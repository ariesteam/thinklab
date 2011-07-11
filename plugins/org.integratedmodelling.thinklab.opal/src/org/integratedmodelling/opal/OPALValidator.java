/**
 * OPALValidator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabOPALPlugin.
 * 
 * ThinklabOPALPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabOPALPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.opal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.opal.profile.OPALProfile;
import org.integratedmodelling.opal.profile.OPALProfileFactory;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * Validator normalizes instances defined in an XML document into list representations, which are later 
 * passed to a session. 
 * @author Ferdinando Villa
 */
public class OPALValidator {

	String rootPrefix;
	OPALProfile profile = null; 
	
	 /* log4j logger used for this class */
	 private static  Logger log = Logger.getLogger(OPALValidator.class);

    
	/* table of references between ID and instances to resolve internal references */
	HashMap<String, Polylist> reftable;
	
	public OPALValidator() {

		reftable = new HashMap<String, Polylist>();
	}
	
	private boolean contextMatch(String currentContext, String contextToMatch) {

		boolean ret = true;
		
		if (contextToMatch.equals("*"))
			return true;
		
		/* match piece by piece */
		String[] a = currentContext.split("/");
		String[] b = contextToMatch.split("/");
		
		if (a.length != b.length)
			return false;
		
		for (int i = 0; i < a.length; i++) {
			
			if (!a[i].equals(b[i]) || 
				!a[i].matches(b[i]))
				return false;
		}
		
		return ret;
	}
	
	private Node openOPALDocument(URL opalDocument) throws ThinklabException {
		
		XMLDocument xdoc = new XMLDocument(opalDocument);
		reftable.clear();
		Element root = xdoc.root();
		rootPrefix = root.getPrefix();
		
		 // TODO handle version request if any
		/* first see if document overrides profile with a processing instruction */
		for (ProcessingInstruction pi : xdoc.getProcessingInstructions()) {

			if (pi.getTarget().equals("opal")) {
				
				if (profile != null)
					throw new OPALValidationException("ambiguous profile definition in " + 
						opalDocument + 
						" (rename document to .xml?)");
				
				KeyValueMap kv = new KeyValueMap(pi.getData());
				profile = OPALProfileFactory.get().getProfile(kv.get("profile"), true);
				log.info("profile for XML document " + opalDocument + " set to " + profile.getName());


			} else if (pi.getTarget().equals("plugin")) {

				// TODO handle version request if any
				KeyValueMap kv = new KeyValueMap(pi.getData());
				Thinklab.get().requirePlugin(kv.get("id"), true);
			}
		}
		
        /* if we have no default namespace prefix in the XML file, use the file extension. 
         * TODO define an optional configurable mapping between file extensions and ontologies to look into. May be more than one
         * ontology for one prefix, and we search all until we find the concept or property.
         */ 
        if (rootPrefix == null && opalDocument.toString().contains(".")) {
            int ext = opalDocument.toString().lastIndexOf('.');
            rootPrefix = opalDocument.toString().substring(ext+1);
        }

        /* if .xml, profile declaration should be in XML file */
		if (rootPrefix != null && !rootPrefix.equals("xml")) {
            profile = OPALProfileFactory.get().getProfile(rootPrefix, true);						
		}
        
		/* if we still don't have a profile, create one */
		if (profile == null) {
			profile = OPALProfileFactory.get().getDefaultProfile();
		}
		
		/* check that we have a root node we know how to handle */
		if (!profile.isRootNodeID(root.getNodeName())) {
			throw new OPALValidationException("unrecognize root node tag: " + 
					root.getNodeName());
		}
		
		return root;
	}
	
	/**
	 * Validate an OPAL document into a set of instances, and put them in the passed session.
	 * @param opalDocument
	 * @param destination a Session to put the instances into. If only OPAL validation  is desired, pass 
	 * null. Note that IMA/OWL validation is not performed in this case - only OPAL errors are caught.
	 * @param kbox a kbox to add the object to (may be null)
	 * @throws OPALValidationException if any OPAL error happens.
	 * @throws ThinklabException if anything else goes wrong.
	 */
	public Collection<IInstance> validate(URL opalDocument, ISession destination, IKBox kbox) throws OPALValidationException, ThinklabException {
	
		ArrayList<IInstance> ret = new ArrayList<IInstance>();

		// loop through child nodes
		Node child;
		Node next = (Node)openOPALDocument(opalDocument).getFirstChild();
		
		while ((child = next) != null) {
			
			/* this works even if we change the tree structure, which we don't */
			next = child.getNextSibling();
			
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				
				/* they should all be element nodes at this level, and each should represent an instance */
				 ret.add(validateInstance(child, destination, kbox));
			}	
			
			/* TBC just ignore any other element */
		 }
		 
		return ret;
	}
	
	/**
	 * Validate an OPAL document to a collection of lists, each representing the
	 * fully qualified description of an instance.
	 * 
	 * @param opalDocument
	 * @return
	 * @throws OPALValidationException
	 * @throws ThinklabException
	 */
	public Collection<Polylist> validateToLists(URL opalDocument) throws OPALValidationException, ThinklabException {
		
		ArrayList<Polylist> ret = new ArrayList<Polylist>();

		// loop through child nodes
		Node child;
		Node next = (Node)openOPALDocument(opalDocument).getFirstChild();

		while ((child = next) != null) {
			
			/* this works even if we change the tree structure, which we don't */
			next = child.getNextSibling();
			
			Stack<String> contextStack = new Stack<String>();
			contextStack.push("");
			
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				
				/* they should all be element nodes at this level, and each should represent an instance */
				 ret.add(validateInstanceToList(child, contextStack));
			}	
		 }
		 
		return ret;
	}
	
	/**
	 * Turn XML into normalized list. Catch all validation errors that may happen at this stage (OPAL related);
	 * leave it to the standard validator to catch literal interpretation, cardinality and functional property
	 * errors.
	 * @param contextStack 
	 * @param inst an XML node representing an instance.
	 * @return
	 * @throws ThinklabException mostly if there are validation errors or others
	 */
	private Polylist validateInstanceToList(Node n, Stack<String> contextStack)  throws ThinklabException {

		ArrayList<Object> alist = new ArrayList<Object>();

		String name = null;
		String reference = null;
		String label = null;
		String description = null;
		
		IConcept c = profile.locateConcept(n.getNodeName());
		
		if (c == null) {

			System.out.println("dio capitone");
			throw new OPALValidationException("node " + n.getNodeName() + " does not identify a known concept");
				
		}
		
		if (c.isAbstract())
			throw new OPALValidationException("can't create an instance of abstract concept " + c);
		
		alist.add(c);
			
		contextStack.push(contextStack.peek() + "/" + c.toString());
		
		/*
		 * remember index of concept node name so we can substitute it later if name or reference
		 * are given
		 */
		int mainIdx = alist.size()-1;
		
		/* check all relationships defined in attributes and look for OPAL ID and other attrs */
		NamedNodeMap nm = n.getAttributes();

		for (int i = 0; i < nm.getLength(); i++) {

			String anam = nm.item(i).getNodeName();
			String aval = nm.item(i).getTextContent();
			
			if (profile.isNameTag(anam)) {
				name = aval;
			} else if (profile.isReferenceTag(anam)) {
				reference = aval;
			} else if (profile.isLabelTag(anam)) {
				label = aval;
			} else if (profile.isDescriptionTag(anam)) {
				description = aval;
			}
			else {
	
				IProperty prop = profile.locateProperty(anam);
				if (prop == null)
					throw new OPALValidationException("attribute " + anam + " does not identify any known property of " + c);
				
				contextStack.push(contextStack.peek() + "/" + prop.toString());
				
				alist.add(validateLiteralProperty(prop, aval));
				
				contextStack.pop();

			}			
		}
		
		if (reference != null) {
			
			/*
			 * check if we have a URL prefix for the current context
			 */
			for (Pair<String, String> p : profile.getReferenceDefaults()) {
				if (contextMatch(contextStack.peek(), p.getFirst())) {
					reference = p.getSecond() + (p.getSecond().endsWith("#") ? "" : "#") + reference;
					break;
				}
			}
			
			alist.set(mainIdx, "#" + reference);
		}
		
		if (name != null) {
			String cname = c + "#" + name;
			alist.set(mainIdx, cname);
		}
		
		if (label != null) {
			alist.add(Polylist.list("rdfs:label", label));
		}
		
		if (description != null) {
			alist.add(Polylist.list("rdfs:comment", label));
		}
		
		/* scan subnodes. Must identify properties, either data or other. 
		 * Node may be a new instance related through default property. */
		 Node child;
		 Node next = (Node)n.getFirstChild();

		 while ((child = next) != null) {

			 next = child.getNextSibling();
			 
			 if (child.getNodeType() == Node.ELEMENT_NODE) {

				 /* filter out annotations */
				 if (profile.isLabelTag(child.getNodeName())) {
					 
					 alist.add(Polylist.list("rdfs:label", child.getTextContent()));
					 continue;
					 
				 } else if (profile.isDescriptionTag(child.getNodeName())) {
					 
					alist.add(Polylist.list("rdfs:comment", child.getTextContent()));
					continue;
				 }
				 
				 /* find out what property we're using */
				 IProperty prop = profile.locateProperty(child.getNodeName());

				 if (prop == null) {
                     
					 /* must be a concept, and we must have a default property for it */
					 IConcept conc = profile.locateConcept(child.getNodeName());
                     
                     if (conc == null) {
                         throw new OPALValidationException(child.getNodeName() + " does not identify a concept or a property");
                     }
                     
					 prop = uniqueImplicitRelationship(c, conc);

					 contextStack.push(contextStack.peek() + "/" + prop.toString());
					 contextStack.push(contextStack.peek() + "/" + conc.toString());
					 					 
					 /* validate instance */
					 Polylist linst = validateInstanceToList(child, contextStack);

					 contextStack.pop();
					 contextStack.pop();
					 
					 /* add to results */
					 alist.add(Polylist.list(prop, linst));

				 } else {

					 /* we have a property. If text content, validate as literal */
				     String s = XMLDocument.getNodeValue(child).trim();
                     if (s.length() > 0) {

                    	 contextStack.push(contextStack.peek() + "/" + prop.toString());
                         alist.add(validateLiteralProperty(prop, s));
    					 contextStack.pop();
    					 
                     } 
                     
                     /* TBC validate all children as instances. Note that a node can have both text and subnodes content
                      * in this implementation. I think it's a good choice but this may change.
                      */
                     if (child.getFirstChild() != null) {
                         
					    /* must link to instances or references */
                        ArrayList<Polylist> aa = new ArrayList<Polylist>();
                        Node cchild;
                        Node nnext = (Node)child.getFirstChild();
                        while ((cchild = nnext) != null) {
                        	nnext = cchild.getNextSibling();	
                        	if (cchild.getNodeType() == Node.ELEMENT_NODE) {
                        		contextStack.push(contextStack.peek() + "/" + prop.toString());
                        		aa.add(validateInstanceToList(cchild, contextStack));
                        		contextStack.pop();
                        	}
                        }
                        
                        for (Polylist l : aa) {
                            alist.add(Polylist.list(prop, l));
                        }
                     }
				 }
			 }
			 
			 /* TBC just ignore any other element */
			 
		 }
		 
		 /* 
		  * if concept node has text content, what we want is to define a concept
		  * with a literal. Just add the content. There should be no relevant property, but we
		  * let the instance validator catch any errors.
		  * 
		  * Solves TLC-23: Enhance OPAL syntax with specific fields to support mixing literal definition with other fields
http://ecoinformatics.uvm.edu/jira/browse/TLC-23
		  */
		 String gg = XMLDocument.getNodeValue(n);
		 
		 if (gg != null && !gg.trim().equals("")) {			
			 alist.add(gg.trim());
		 }

		 contextStack.pop();
			 
		return Polylist.PolylistFromArray(alist.toArray());
	}
	
	private Polylist validateLiteralProperty(IProperty p, String value) {
		ArrayList<Object> alist = new ArrayList<Object>();

        // no validation at all actually. We let the list validator do the rest.
		alist.add(p);
		alist.add(value);
				
		return Polylist.PolylistFromArray(alist.toArray());
	}
	
	private IInstance validateInstance(Node inst, ISession sess, IKBox kbox) throws ThinklabException {
		
		IInstance ret = null;
		
		Stack<String> contextStack = new Stack<String>();
		
		contextStack.push("");
		
		Polylist ls = validateInstanceToList(inst, contextStack);

        if (sess != null) {
        	ret = sess.createObject(ls);
        	if (kbox != null) {
        		kbox.storeObject(ret, null, null, sess);
        	}
        }
        
        return ret;
    }

	private IProperty uniqueImplicitRelationship(IConcept c1, IConcept c2) throws ThinklabException {

        /* check if there is a configured default relationship in the OPAL profile selected */
	    IProperty ret = profile == null ? null : profile.getDefaultRelationship(c1, c2);

        /* if we get here we MUST have an implicit rel, so complain */
        if (ret == null)
            throw new OPALValidationException("instances of " + c1 + " don't have a default relationship with " + c2);
        
		return ret;
	}
		
}
