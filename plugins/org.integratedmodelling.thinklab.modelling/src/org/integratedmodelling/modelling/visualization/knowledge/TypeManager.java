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
package org.integratedmodelling.modelling.visualization.knowledge;

import java.util.ArrayList;
import java.util.Hashtable;

import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;

/**
 * A singleton that manages decorations, labels, icons etc related to types. It is 
 * configured using the plugin.xml file and the main typedecorations.xml in WEB-INF
 * of the Thinklab application.
 * 
 * @author Ferdinando Villa
 *
 */
public class TypeManager {
	
	static TypeManager _this = new TypeManager();
	
	Hashtable<String, VisualConcept> visualConcepts = 
		new Hashtable<String, VisualConcept>();
	Hashtable<String, VisualProperty> visualProperties = 
		new Hashtable<String, VisualProperty>();
	
	public static TypeManager get() {
		return _this;
	}
	
	public VisualConcept getVisualConcept(String conceptID) throws ThinklabException {		
		return getVisualConcept(KnowledgeManager.get().requireConcept(conceptID));
	}

	public synchronized VisualConcept getVisualConcept(IConcept concept) {
		
		VisualConcept ret = visualConcepts.get(concept.toString());
		if (ret == null) {
			ret = new VisualConcept(concept, this);
			visualConcepts.put(concept.toString(), ret);
		}
		return ret;
	}
	
	
	public VisualProperty getVisualProperty(IProperty p) {
		
		VisualProperty ret = visualProperties.get(p.toString());
		if (ret == null) {
			ret = new VisualProperty(p, this);
			visualProperties.put(p.toString(), ret);
		}
		return ret;
	}
	
	
	public class TypeDecoration {
		
		String id;
		public boolean ignored;
		public String seealso;
		public String comment;
		public String label;
		public String icon;
		public Hashtable<String, String> pIcons = new Hashtable<String, String>();
		boolean isConcept;
	
		public TypeDecoration(String id, boolean isC) {
			this.id = id;
			isConcept = isC;
		}

		boolean isConcept() {
			return isConcept;
		}
		
		boolean isProperty() {
			return !isConcept;
		}

		public String getId() {
			return id;
		}

		public boolean isIgnored() {
			return ignored;
		}

		public String getSeeAlso() {
			return seealso;
		}

		public String getComment() {
			return comment;
		}

		public String getLabel() {
			return label;
		}

		public String getIcon(String context) {
			
			String ret = icon;
			if (context != null) {
				// look for specific icon; will return default if not found
				String ic = pIcons.get(context);
				if (ic != null)
					ret = ic;
			}
			return ret;
		}
	}
	
	private Hashtable<String, TypeDecoration> typeDescription = 
		new Hashtable<String, TypeDecoration>();

	/**
	 * Just return the record if any, without going through the concept
	 * hierarchy at all.
	 * 
	 * @param concept
	 * @return
	 */
	public TypeDecoration getTypeDecoration(IConcept concept) {
		return typeDescription.get(concept.toString());
	}
	
	
	/**
	 * Return the closest result visualizer for the given concept.
	 * @param concept
	 * @return
	 */
	public ArrayList<TypeDecoration> getAllTypeDecorations(IConcept concept) {
		
        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, TypeDecoration> coll;
            
            public TypeDecoration result = null;
            public vmatch(Hashtable<String, TypeDecoration> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
            	
            	boolean ret = false;
                TypeDecoration td = coll.get(c.getSemanticType().toString());
                if (td != null) {
                	result =  td;
                	ret = (result != null);
                }
            	return ret;
            }    
        }
        
        vmatch matcher = new vmatch(typeDescription);
        
        return 
            new ConceptVisitor<TypeDecoration>().
            	findAllMatchesInMapUpwards(typeDescription, matcher, concept);
		
	}

	public VisualInstance getVisualInstance(IInstance obj) {
		// TODO Auto-generated method stub
		return new VisualInstance(obj);
	}


}
