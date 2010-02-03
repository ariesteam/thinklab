/**
 * OPALProfile.java
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
package org.integratedmodelling.opal.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.opal.OPALValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.xml.XMLDocument;

public class OPALProfile {

    String name;
	private boolean allowExplicit = true;        
    private HashMap<String, ArrayList<String> > prefixMappings;
    private HashMap<String, String> dictionary;
    private HashMap<String, String> drelMapping;
    private ArrayList<String> nameTags;
    private ArrayList<String> referenceTags;
    private ArrayList<Pair<String, String>> urlPrefixes;
    private ArrayList<String> labelTags;
    private ArrayList<String> descriptionTags;
    private ArrayList<String> rootNodeIDs;
    
    /* readProfile will set this to false if it is called. */
	boolean isDefault = true;
    
    public OPALProfile() {
    	prefixMappings = new HashMap<String, ArrayList<String> >();
    	dictionary = new HashMap<String, String>();
    	drelMapping = new HashMap<String, String>();
    	nameTags = new ArrayList<String>();
    	referenceTags = new ArrayList<String>();
    	urlPrefixes = new ArrayList<Pair<String,String>>();
    	labelTags = new ArrayList<String>();
    	descriptionTags = new ArrayList<String>();
    	rootNodeIDs = new ArrayList<String>();
    }
    
    public void setName(String profileName) {
        name = profileName;
    }
    
    public String getName() {
    	return name;
    }

    public String getDefaultReferenceTag() {
    	return referenceTags.get(0);
    }
    
    public String getDefaultNameTag() {
    	return nameTags.get(0);
    }
    
    public IProperty getDefaultRelationship(IConcept c1, IConcept c2) {

    	IProperty ret = null;
    	String s = drelMapping.get(c1+"|"+c2);

        if (s != null) {

        	if (s.equals("__NONE__"))
        		return null;
        	
        	try {
                ret = KnowledgeManager.get().retrieveProperty(s);
            } catch (ThinklabRuntimeException e) {
                // no KM, no property, that's it
            	return null;
            }
        }

        if (ret == null) {
        	
        	/* this is quite expensive, so we want to do it only once */
        	Collection<IConcept> cp1 = c1.getAllParents();
        	Collection<IConcept> cp2 = c2.getAllParents();
        	cp1.add(c1);
        	cp2.add(c2);
        	
        	for (IConcept cc1 : cp1) {
        		for (IConcept cc2 : cp2) {

        			s = drelMapping.get(cc1+"|"+cc2);
        			
        			if (s != null) {
        	            try {
        	            	ret = KnowledgeManager.get().retrieveProperty(s);
        	                break;
        	            } catch (ThinklabRuntimeException e) {
        	                // no KM, no property, that's it
        	            }
        			}
        		}
        		if (ret != null) {
        			/* cache it for next time */
        			drelMapping.put(c1+"|"+c2, ret.toString());
        			break;
        		}
        	}
        }
        
        /* don't search again if we know it's not there */
        if (ret == null) {
        	drelMapping.put(c1+"|"+c2, "__NONE__");
        }

        return ret;
    }
    
    public void addDefaultRelationships(String c1, String c2, String p) {
        drelMapping.put(c1+"|"+c2, p);
    }

    public void addReferenceUrlMapping(String url, String context) {
    	urlPrefixes.add(new Pair<String, String>(context, url));
    }
    
	public void allowExplicitPrefix(boolean b) {
		allowExplicit = b;
	}
	
	public IConcept locateConcept(String s) throws ThinklabException {
        
        if (dictionary.containsKey(s))
            s = dictionary.get(s);
        
        String prefix = "";
        String cid = s;
        
	    if (SemanticType.validate(s)) {
	    	
            SemanticType st = new SemanticType(s);            
            prefix = st.getConceptSpace();
            cid = st.getLocalName();
            
            if (!allowExplicit)
                throw new OPALValidationException("profile " + name + " does not allow explicit type: " + s);
        }

        /* fix prefix to name of profile if we have no prefix and no default set of
         * ontologies. 
         */
        if ((prefix == null || prefix.equals("")) && !prefixMappings.containsKey("__NOPREFIX__"))
            prefix = name;
       
        if (prefix == null || prefix.equals(""))
        	prefix = "__NOPREFIX__";
        
        ArrayList<String> aa = prefixMappings.get(prefix);
        
        if (aa == null) {
            return KnowledgeManager.get().retrieveConcept(prefix + ":" + cid);
        }
        
        IConcept c = null;
        
        for (String cs : aa) {
            if (( c = KnowledgeManager.get().retrieveConcept(cs + ":" + cid)) != null)
                break;
        }
        
        return c;
    }

	public IProperty locateProperty(String s) throws ThinklabException {

		if (dictionary.containsKey(s))
            s = dictionary.get(s);
        
        String prefix = "";
        String cid = s;
        
        if (SemanticType.validate(s)) {

            SemanticType st = new SemanticType(s);            
            prefix = st.getConceptSpace();
            cid = st.getLocalName();

            if (!allowExplicit)
                throw new OPALValidationException("profile " + name + " does not allow explicit property: " + s);
        }

        /* fix prefix to name of profile if we have no prefix and no default set of
         * ontologies. 
         */
        if ((prefix == null || prefix.equals("")) && !prefixMappings.containsKey("__NOPREFIX__"))
            prefix = name;
        
        if (prefix == null || prefix.equals(""))
        	prefix = "__NOPREFIX__";
        
        ArrayList<String> aa = prefixMappings.get(prefix);
        
        if (aa == null) {
        	return KnowledgeManager.get().retrieveProperty(prefix + ":" + cid);
        }
        
        IProperty c = null;
        
        for (String cs : aa) {
            if (( c = KnowledgeManager.get().retrieveProperty(cs + ":" + cid)) != null)
                break;
        }
        
        return c;

	}

	
    public void addOntologyForPrefix(String id, String ontology) {
    	
        ArrayList<String> aa = prefixMappings.get(id);
        if (aa == null) {
            aa = new ArrayList<String>();
            aa.add(ontology);
            prefixMappings.put(id, aa);
        } else {
            aa.add(ontology);
        }
    }

    public String getDefaultSynonim(String id) {
    	
    	String ret = id;
    	
    	for (String ids : dictionary.values()) {
    		if (id.equals(ids)) {
    			ret = ids;
    			break;
    		}
    	}
    	
    	return ret;
    }
    
    /**
     * If the passed semantic type is in one of the ontologies that we are 
     * defaulting to, return its bare ID without the concept space; otherwise
     * return the full semantic type and add the correspondent namespace specs to
     * the passed XML document.
     * 
     * @param semanticType
     * @param document
     * @return
     * @throws ThinklabException 
     */
    public String getOPALConceptID(SemanticType semanticType, XMLDocument document) throws ThinklabException {
    	
    	/* first of all, check that we are not aliasing it to something */
    	String syn = getDefaultSynonim(semanticType.toString());
    	
    	if (!syn.equals(semanticType.toString())) {
    		return syn;
    	}
    	
    	/*
    	 * then check if it's one of those concepts where we don't want any prefix 
    	 */
    	ArrayList<String> aa = prefixMappings.get("__NOPREFIX__");
    	
    	if (aa != null)
    		for (String pr : aa) {
    			if (semanticType.getConceptSpace().equals(pr))
    				return semanticType.getLocalName();
    		}
    	
    	String ret = semanticType.toString();
    	
    	/* TODO now see if we want a specific translation. This requires the 
    	 * namespace somewhere, so it's inconsistent for now. */
    	
    	/* no more choices, add namespace prefix to document */
    	String uri = 
    		KnowledgeManager.get().getURIFromConceptSpace(
    				semanticType.getConceptSpace());
    	
    	/* if null, it's one of those things we don't want to see. */
    	if (uri == null)
    		return null;
    	
    	document.addNamespace(semanticType.getConceptSpace(), uri);
    	
    	return ret;
    }
    
    public void addSynonym(String from, String to) {
       dictionary.put(from, to);        
    }

	public boolean isNameTag(String anam) {
		if (nameTags.contains(anam))
			return true;
		return false;
	}
	
	public boolean isReferenceTag(String anam) {
		if (referenceTags.contains(anam))
			return true;
		return false;
	}

	public void addNameTag(String string) {
		nameTags.add(string);
	}

	public void addReferenceTag(String string) {
		referenceTags.add(string);
	}

	public void checkDefaults() {
		
		// TODO see what other defaults we need to check
		if (nameTags.size() == 0) {
			nameTags.add("id");
		}
		if (referenceTags.size() == 0) {
			referenceTags.add("reference");
		}
		if (labelTags.size() == 0) {
			labelTags.add("rdfs:label");
			labelTags.add("label");
		}
		if (descriptionTags.size() == 0) {
			descriptionTags.add("rdfs:comment");
			descriptionTags.add("description");
		}
		if (rootNodeIDs.size() == 0) {
			rootNodeIDs.add("kbox");
		}
	}

	public Collection<Pair<String, String> > getReferenceDefaults() {
		return urlPrefixes;
	}
	
	public boolean isLabelTag(String s) {
		return labelTags.contains(s);
	}
	
	public String getDefaultLabelTag() {
		return labelTags.get(0);
	}
	
	public boolean isDescriptionTag(String s) {
		return descriptionTags.contains(s);
	}
	
	public String getDefaultDescriptionTag() {
		return descriptionTags.get(0);
	}

	public void addDescriptionTag(String trim) {
		descriptionTags.add(trim);
	}

	public void addLabelTag(String trim) {
		labelTags.add(trim);
	}

	public String getDefaultRootNodeID() {
		return rootNodeIDs.get(0);
	}
	
	public boolean isRootNodeID(String s) {
		return rootNodeIDs.contains(s);
	}

	public void addRootNodeID(String trim) {
		rootNodeIDs.add(trim);
	}

	/**
	 * Returns true if the profile has been created by the system without a 
	 * profile specification from the user.
	 * @return
	 */
	public boolean isDefault() {
		return isDefault;
	}
    
}
