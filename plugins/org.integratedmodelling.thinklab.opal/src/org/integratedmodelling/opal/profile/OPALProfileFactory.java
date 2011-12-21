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
package org.integratedmodelling.opal.profile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

public class OPALProfileFactory {

    static OPALProfileFactory _PF = null;
    static OPALProfile defaultProfile = null;
    
    File profilePath;
    
    HashMap<String, OPALProfile> profiles;
    
    public OPALProfileFactory() {

    	profilePath =  new File(LocalConfiguration.getSystemPath() + "/opal/profiles");
    	profiles    = new HashMap<String, OPALProfile>();
    	defaultProfile = new OPALProfile();
    	defaultProfile.checkDefaults();
    }
    
    public static OPALProfileFactory get() {

    	if (_PF == null)
            _PF = new OPALProfileFactory();
        return _PF;
    }
    
    public OPALProfile getProfile(String profileName, boolean create) throws ThinklabIOException {
        
        OPALProfile ret = null;
        
        if (profileName == null)
        	return defaultProfile;
        else {
        	
        	ret = profiles.get(profileName);
        	
            /* try to read from file; create anyway if not there */
        	if (ret == null && create) {        		
        		ret = getProfileFromDefinition(profileName);
        	} 
        }

        return ret;      
    }
    
    public void readProfile(URL profile) throws ThinklabIOException {
 
    	OPALProfile ret = new OPALProfile();

    	String pname = 
    		profile.toString().substring(
    				profile.toString().lastIndexOf("/") + 1, 
    				profile.toString().lastIndexOf("."));
    	
    	ret.setName(pname);

        readProfile(ret, profile);
        
        ret.checkDefaults();
        
        /* add profile to catalog */
        profiles.put(pname, ret);
    }
    
    private OPALProfile getProfileFromDefinition(String pname) throws ThinklabIOException {

        OPALProfile ret = new OPALProfile();
        ret.setName(pname == null ? NameGenerator.newName("OPAL") : pname);
        
        /* check if we have XML definition of profile */
        if (pname != null) {
        	
        	File pfile = new File(profilePath.toString() + "/" + pname + ".opf");

        	/* if so, read it in */
        	if (pfile.exists()) {
        		try {
        			readProfile(ret, pfile.toURI().toURL());
        		} catch (MalformedURLException e) {
        			throw new ThinklabIOException(e);
        		}
        	}
        }

        ret.checkDefaults();
        
        /* add profile to catalog */
        if (pname != null)
        	profiles.put(pname, ret);
        
        return ret;
    }
    
    private void readProfile(OPALProfile profile, URL in) throws ThinklabIOException {

		XMLDocument doc = null;
		
		profile.isDefault = false;
		
    	try {
			doc = new XMLDocument(in);
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		 // loop through child nodes
		 Node child;
		 Node next = (Node)doc.root().getFirstChild();
		 
		 /*
		  * TODO
		  * ?? Allow "type" keywords so that the node name can be decoupled from the class, or specialized
		  * later?
		  */
		 
		 while ((child = next) != null) {

			 next = child.getNextSibling();
			 
			 if (child.getNodeName().equals("dictionary")) {

                 for (Node nn = (Node)child.getFirstChild(); nn != null; nn = nn.getNextSibling()) {
                     if (nn.getNodeName().equals("synonym")) {
                         profile.addSynonym(XMLDocument.getAttributeValue(nn, "name"), 
                                            XMLDocument.getAttributeValue(nn, "translation"));
                     }
                 }
                 
			 } else if (child.getNodeName().equals("object-name-tag")) {

				 profile.addNameTag(child.getTextContent().trim());
				 
			 } else if (child.getNodeName().equals("reference-tag")) {
				 
				 profile.addReferenceTag(child.getTextContent().trim());

				 
             } else if (child.getNodeName().equals("description-tag")) {
				 
				 profile.addDescriptionTag(child.getTextContent().trim());

				 
             } else if (child.getNodeName().equals("label-tag")) {
				 
				 profile.addLabelTag(child.getTextContent().trim());

				 
             } else if (child.getNodeName().equals("root-node-id")) {
				 
				 profile.addRootNodeID(child.getTextContent().trim());

				 
             }else if (child.getNodeName().equals("concept-space")) {
				 
				 Node cchild;
				 Node nnext = (Node)child.getFirstChild();

				 while ((cchild = nnext) != null) {
					 nnext = cchild.getNextSibling();

					 if (cchild.getNodeName().equals("prefix")) {

                         String id = XMLDocument.getAttributeValue(cchild, "id").trim();
                         
                         /* hashing with empty strings may still work, but I just don't like it. */
                         if (id.equals(""))
                        	 id = "__NOPREFIX__";
                         
                         for (Node nn = (Node)cchild.getFirstChild(); nn != null; nn = nn.getNextSibling()) {
                             if (nn.getNodeName().equals("ontology")) {
                                 profile.addOntologyForPrefix(id, XMLDocument.getAttributeValue(nn, "id"));
                                 // TODO handle ontology version attribute
                             }
                         }
                         
					 } else if (cchild.getNodeName().equals("allow-explicit-concept-space")) {
						 String aval = XMLDocument.getAttributeValue(cchild, "value").trim();
						 profile.allowExplicitPrefix(BooleanValue.parseBoolean(aval));
					 }
				 }
				 
			 } else if (child.getNodeName().equals("default-relationships")) {

				 Node cchild;
				 Node nnext = (Node)child.getFirstChild();

				 while ((cchild = nnext) != null) {
					 nnext = cchild.getNextSibling();

					 if (cchild.getNodeName().equals("relationship")) {
					     profile.addDefaultRelationships(XMLDocument.getAttributeValue(cchild, "from").trim(),
                                                         XMLDocument.getAttributeValue(cchild, "to").trim(),
                                                         cchild.getTextContent().trim());
                     }
				 }
				 
			 } else if (child.getNodeName().equals("default-reference-url")) {

				 Node cchild;
				 Node nnext = (Node)child.getFirstChild();

				 while ((cchild = nnext) != null) {
					 nnext = cchild.getNextSibling();

					 /*
					  * a way to autocomplete the URL prefix of a reference, either globally or
					  * locally to a specific relationship context.
					  */
					 
					 if (cchild.getNodeName().equals("reference-prefix")) {

						 String context = XMLDocument.getAttributeValue(cchild, "context", "*");			 
						 profile.addReferenceUrlMapping(cchild.getTextContent().trim(), context);
                     }
				 }
				 
			 } else if (child.getNodeName().equals("groups")) {
				 // TODO handle groups
			 }
			 
		 }
    }

	public OPALProfile getDefaultProfile() {
		return defaultProfile;
	}
}
