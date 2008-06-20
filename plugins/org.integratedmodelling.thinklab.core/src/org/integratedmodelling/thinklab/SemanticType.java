/**
 * SemanticType.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab;

import java.io.Serializable;

import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IResource;

/**
 * <p>All relevant knowledge objects accessed through the Knowledge Manager are identified by a semantic type, whose form is 
 * a string with two names separated by a colon. The first part identifies the "concept space", the second the local ID within
 * the concept space (local name). Each semantic type must be unique in the KM.</p>
 * 
 * <p>Objects that are identified by semantic types are: Concepts, Properties, and Instances. Note that Ontologies as such are not
 * exposed by the KM interface, and the equivalence between the concept space ID and an ontology is not to be relied upon. Even
 * if in the current implementation a concept space ID maps directly to an ontology URI, this may change and more sophisticated
 * and useful notions of a concept space may emerge. Ontologies are only a concern within the physical model of the knowledge
 * base, which should not be seen by users.</p>
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 * 
 * @see IConcept
 * @see IProperty
 * @see IInstance
 * @see KnowledgeManager
 */
public final class SemanticType implements Serializable{
	private static final long serialVersionUID = -647901196357082946L;

	String conceptSpace;
	String localName;
	
	private void assign(String s) throws ThinklabMalformedSemanticTypeException {
		String[] ss = s.split(":");
		if (ss.length != 2 || ss[0].trim().equals("") || ss[1].trim().equals(""))
			throw new ThinklabMalformedSemanticTypeException(s);
		
		conceptSpace = ss[0];
		localName = ss[1];
		// be sure the Concepts Spaces are known to the KnowledgeManager
		// probably this will slow down the things a bit
		// TODO fv: temporarily disabled - does not seem to work and I'm worried it's not right to not be able to
		// name concepts we don't have. Check this w/Ioannis
		//		try {
//			if (!KnowledgeManager.KM().cs2uri.containsKey(conceptSpace))
//				throw new ThinklabMalformedSemanticTypeException(s);
//		} catch (ThinklabNoKMException e) {
//			throw new ThinklabMalformedSemanticTypeException(s + " (Knowledge Manager not present.)");
//		}
		
	}
	
	
	/**
	 * Returns a Semantic Type with the same content as the given one.
	 * @param s
	 */
	public SemanticType(SemanticType s) {
		conceptSpace = s.conceptSpace;
		localName = s.localName;
	}
	
	/**
	 * Returns a Semantic Type with the same content as the one of the given IResource.
	 *  (Note that an IResource can be an IConcept, IProperty or a IInstance).
	 * <br/>
	 * <b>HINT</b>: To retrieve the SemanticType of an IResource use the method IResource.getSemanticType()
	 * 
	 *  @see IResource
	 *  @param res
	 */
	public SemanticType(IKnowledge res) {
		 try {
			this.assign(res.getSemanticType().toString());
		} 
		 // supposedly the IResource should be correct. 
		 // if something goes wrong send it to the log
		 catch (ThinklabMalformedSemanticTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Default constructor from a String. Note that the String has to be in the form: 
	 * conceptSpace:LocalName 
	 * @param s
	 * @throws ThinklabMalformedSemanticTypeException
	 */
	public SemanticType(String s) throws ThinklabMalformedSemanticTypeException {
		assign(s);
	}

	/**
	 * Default constructor from two Strings: first is the Concept Space, second is the Local Name.
	 * Both arguments should not contain a semicolon.
	 * @param conceptSpace 
	 * @param localName
	 */
	public SemanticType(String conceptSpace,String localName)  {
		this.conceptSpace = conceptSpace;
		this.localName = localName;
	}

	public String toString() {
		return conceptSpace+":"+localName;
	}
	
	public String getConceptSpace() {
		return conceptSpace;
	}
	
	public String getLocalName() {
		return localName;
	}
    
    /** 
     * Use to check if the string is a valid semantic type. Only checks syntax; ontology or concept may not exist.
     * @param t a string to validate
     * @return true if valid token in the form "ontology:resource_id"
     */
    public static boolean validate(String t) {
        boolean ret = true;
        try {
            new SemanticType(t);
        } catch (ThinklabMalformedSemanticTypeException e) {
            ret = false;
        }
        return ret;
    }
	
   public boolean equals(Object s){
    	if (s instanceof SemanticType) {
			SemanticType st = (SemanticType) s;
			return st.toString().equals(toString());
		} else return false;
    }
   public int hashCode(){
	return 0;
   }
}
