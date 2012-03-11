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
package org.integratedmodelling.thinklab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/** 
 * A helper class providing patterns to retrieve concepts that match conditions or have
 * external content associated across the subclass hierarchy. Conditions must be expressed as 
 * objects implementing the inner interface ConceptMatcher.
 * @author villa
 */
public class ConceptVisitor<T> {

    public interface ConceptMatcher {
        abstract boolean match(IConcept c);
    }

    private void allMatchesDownwards(Collection<T> coll, Map<String, T> where, ConceptMatcher matcher, IConcept start) {
        T obj = matcher.match(start) ? where.get(start.toString()) : null;
        if (obj != null)
            coll.add(obj); 
        synchronized (start) {
        	for (IConcept c : start.getChildren())
        		allMatchesDownwards(coll, where, matcher, c);
        }
    }

    private void allMatchesUpwards(Collection<T> coll, Map<String, T> where, ConceptMatcher matcher, IConcept start) {
        T obj = matcher.match(start) ? where.get(start.toString()) : null;
        if (obj != null)
            coll.add(obj);
        synchronized (start) {
        	for (IConcept c : start.getParents())
        		allMatchesUpwards(coll, where, matcher, c);
        }
    }

    
    /** 
     * Returns the first object of type T that matches the passed function object in parent hierarchy.
     * @param where The match to search for
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found.
     */
    static public IConcept findMatchUpwards(ConceptMatcher matcher, IConcept start) {
        
    	IConcept ret = null;
    	synchronized (start) {
    		
    		ret = matcher.match(start) ? start : null;
        
    		if (ret == null) {
    			for (IConcept c : start.getParents())
    				if ((ret = findMatchUpwards(matcher, c)) != null)
    					break;
    		}   
    	}
        return ret;
    }

    /** 
     * Returns the first object of type T that matches the passed function object in children hierarchy.
     * @param where The match to search for
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found.
     */
    static public IConcept findMatchDownwards(ConceptMatcher matcher, IConcept start) {
        
    	IConcept ret = null;
    	synchronized (start) {
    		
    		ret = matcher.match(start) ? start : null;
        
    		if (ret == null) {
    			for (IConcept c : start.getChildren())
    				if ((ret = findMatchDownwards(matcher, c)) != null)
    					break;
    		} 
    	}
        
        return ret;
    }


    /** 
     * Returns the object of type T that maps to the semantic type of the passed concept or any of its parents in passed map.
     * @param where The map to search into
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found.
     */
    public T findInMapUpwards(Map<String, T> where, IConcept start) {
    	
        T ret = where.get(start.toString());
        
        if (ret == null) {
            for (IConcept c : start.getParents())
                if ((ret = this.findInMapUpwards(where, c)) != null)
                    break;
        }   
        
        return ret;
    }

    /** 
     * Returns the object of type T that maps to the semantic type of the passed concept or any of its children in passed map.
     * @param where The map to search into
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found.
     */
    public T findInMapDownwards(Map<String, T> where, IConcept start) {
    	
        T ret = where.get(start.toString());
        
        if (ret == null) {
            for (IConcept c : start.getChildren())
                if ((ret = this.findInMapDownwards(where, c)) != null)
                    break;
        }   
        
        return ret;
    }


    /** 
     * Returns the object of type T that maps to the semantic type of the passed concept or any of its parents in passed map.
     * @param where The map to search into
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found.
     */
    public T findMatchingInMapUpwards(Map<String, T> where, ConceptMatcher matcher, IConcept start) {

        T ret = matcher.match(start) ? where.get(start.toString()) : null;
        
        if (ret == null) {
            for (IConcept c : start.getParents())
                if ((ret = this.findMatchingInMapUpwards(where, matcher, c)) != null)
                    break;
        }   
        
        return ret;
    }

    /** 
     * Returns the object of type T that maps to the semantic type of the passed concept or any of its children in passed map.
     * @param where The map to search into
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found.
     */
    public T findMatchingInMapDownwards(Map<String, T> where, ConceptMatcher matcher, IConcept start) {

        T ret = matcher.match(start) ? where.get(start.toString()) : null;
        
        if (ret == null) {
            for (IConcept c : start.getChildren())
                if ((ret = this.findMatchingInMapDownwards(where, matcher, c)) != null)
                    break;
        }   
        
        return ret;
    }

    /** 
     * Returns all objects of type T that maps to the semantic type of the passed concept or any of its children in passed map.
     * @param where The map to search into
     * @param start The starting concept
     * @return The objects we're looking for. Multiple matches across multiple inheritance chains will
     *         return the first match found. We return a List so the order can be maintained.
     */
    public ArrayList<T> findAllMatchesInMapDownwards(Map<String, T> where, ConceptMatcher matcher, IConcept start) {

        ArrayList<T> ret = new ArrayList<T>();
        allMatchesDownwards(ret, where, matcher, start);
        return ret;
    }


    /** 
     * Returns all objects of type T that map to the semantic type of the passed concept or any of its parents in passed map.
     * @param where The map to search into
     * @param start The starting concept
     * @return The object we're looking for or null if not found. Multiple matches across multiple inheritance chains will
     *         return the first match found. We return a List so the order can be maintained.
     */
    public ArrayList<T> findAllMatchesInMapUpwards(Map<String, T> where, ConceptMatcher matcher, IConcept start) {
        ArrayList<T> ret = new ArrayList<T>();
        allMatchesUpwards(ret, where, matcher, start);
        return ret;
    }
    
    
}
