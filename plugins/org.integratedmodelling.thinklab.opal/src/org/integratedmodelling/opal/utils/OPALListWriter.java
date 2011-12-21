/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.opal.utils;

import java.util.HashSet;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.opal.profile.OPALProfile;
import org.integratedmodelling.opal.profile.OPALProfileFactory;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.utils.instancelist.InstanceList;
import org.integratedmodelling.utils.instancelist.RelationshipList;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

/**
 * Convert instance lists to OPAL, with no need to create an instance.
 * 
 * @author Ferdinando Villa
 *
 */ 
public class OPALListWriter {
	
	/**
	 * Convenience method to get a new document, if you want to do it all here.
	 * @return a new XML document with a "kbox" parent node.
	 * @throws ThinklabException
	 */
	public static XMLDocument getNewDocument(String profile) throws ThinklabException {

		OPALProfile prof = OPALProfileFactory.get().getProfile(profile, true);
		XMLDocument doc =  new XMLDocument(prof.getDefaultRootNodeID());
		
		if (!prof.isDefault()) {
			/* add processing instruction to set profile  */
			doc.addProcessingInstruction("OPAL", "profile=" + prof.getName());
		}
		
		return doc;
	}

	/**
	 * Append the given list directly to the root node of an XML document.
	 * @param list A list representing an instance
	 * @param document an XML document
	 * @param profile the name of an existing OPAL profile, or null for the default profile
	 * @throws ThinklabException
	 */
	public static void appendOPAL(IList list, XMLDocument document, String profile) throws ThinklabException {
		
		OPALProfile prof = OPALProfileFactory.get().getProfile(profile, true);
		appendOPALInternal(new InstanceList(list), document, document.root(), prof, new HashSet<String>());
	}

	/**
	 * Append the given list to a given node in an XML document.
	 * @param list A list representing an instance
	 * @param document an XML document
	 * @param parentNode the node to append to. Must be in the passed document. 
	 * @param profile the name of an existing OPAL profile, or null for the default profile
	 * @throws ThinklabException
	 */
	public static void appendOPAL(IList list, XMLDocument document, Node parentNode, String profile) throws ThinklabException {
		
		OPALProfile prof = OPALProfileFactory.get().getProfile(profile, true);
		appendOPALInternal(new InstanceList(list), document, parentNode, prof, new HashSet<String>());
	}

	private static void appendOPALInternal(InstanceList instance, XMLDocument document, Node parentNode, OPALProfile profile,  HashSet<String> refs) throws ThinklabException {
		
		/* 
		 * if this has been written before, just write out a ref for it. Make sure
		 * we have all prefixes right.
		 */
		if (refs.contains(instance.getLocalName())) {
			 document.appendTextNode(
					profile.getDefaultReferenceTag(),
					instance.getLocalName(), 
					parentNode);
			 return;
		}
		
		/* have profile determine node name, ID etc. appropriately */
		Node ret = 
			document.createNode(
					profile.getOPALConceptID(instance.getDirectType(),document),
					parentNode);
		
		document.addAttribute(
				ret, 
				profile.getDefaultNameTag(), 
				instance.getLocalName());
		
		refs.add(instance.getLocalName());
		
		/* write labels and comments if any, plus any annotations */
		String label = instance.getLabel();
		
		if (label != null && !label.equals(""))
			document.appendTextNode(profile.getDefaultLabelTag(), label, ret);
		
		label = instance.getDescription();
		
		if (label != null && !label.equals(""))
			document.appendTextNode(profile.getDefaultDescriptionTag(), label, ret);
		
		/* see if the list specifies the instance using a literal */
		Object o = instance.getLiteralContent();
		if (o != null) {
			ret.setTextContent(o.toString());
		}
		
		/* scan properties */
		for (RelationshipList r : instance.getRelationships()) {

			/* 
			 * see the relationship id, and ignore the whole thing if it's one of those 
			 * that don't appear in the KM, such as protege metadata.
			 */
			String cid = profile.getOPALConceptID(r.getProperty(), document);
			
			if (cid == null)
				continue;

			/*
			 * object relationship: write rel node unless implicit, and recurse
			 */
			if (r.isObject()) {
				
				/* see if we can default the relationship */
				if (profile.getDefaultRelationship(
						instance.getDirectType(),
						r.getValue().asInstanceList().getDirectType())
						== null) {
				
					/* nope, write it up as is */
					Node reln = document.createNode(cid, ret);
				
					appendOPALInternal(
						r.getValue().asInstanceList(),
						document,
						reln,
						profile,
						refs);
					
				} else {
					
					/* 
					 * default relationship, just write the instance directly
					 * within the main instance.
					 */
					appendOPALInternal(
							r.getValue().asInstanceList(),
							document,
							ret,
							profile,
							refs);
				}
				
				
			} else if (r.isClassification()) {
				
				document.appendTextNode(
						cid,
						r.getValue().asString(), 
						ret);
				
			} else {

				document.appendTextNode(
						cid,
						r.getValue().asString(), 
						ret);
			}
		}		
	}
}
