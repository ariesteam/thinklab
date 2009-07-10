package org.integratedmodelling.thinklab;

import java.util.Hashtable;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A map indexed by concepts, whose get() method will select the entry that best corresponds to the passed
 * concept using reasoning instead of equality.
 * 
 * @author Ferdinando
 *
 * @param <T>
 */
public class IntelligentTable<T> {

	Hashtable<IConcept, T> _data = new Hashtable<IConcept, T>();
	
	public T get(IConcept concept) {
		
        class Matcher implements ConceptVisitor.ConceptMatcher {

            Hashtable<IConcept, T> coll;
            T ret = null;
            
            public Matcher(Hashtable<IConcept,T> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                ret = coll.get(c);
                return(ret != null);	
            }    
        }
        
        Matcher matcher = new Matcher(_data);
        IConcept cms = ConceptVisitor.findMatchUpwards(matcher, concept);
        return cms == null ? null : matcher.ret;
	}
	
	public void put(IConcept concept, T data) {
		_data.put(concept, data);
	}
	
}
