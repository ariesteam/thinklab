/**
 * RangeProjection.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: June 08, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ARIES.
 * 
 * ARIES is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ARIES is distributed in the hope that it will be useful,
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
 * FIXME use proper strong typing; fix all the 1985-like code, casts everywhere, 
 * nested types and collections of collections of collections of collections.
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Gary Johnson (gwjohnso@uvm.edu)
 * @date      June 08, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.discretization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map.Entry;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Pair;

public class RangeProjection implements Projection
{
    private HashMap<Pair<Comparable,Comparable>,Comparable> domainToRange;
    private HashMap<Comparable,Set<Pair<Comparable,Comparable>>> rangeToDomain;

    public RangeProjection()
    {
        this.domainToRange = new HashMap<Pair<Comparable,Comparable>,Comparable>();
        this.rangeToDomain = new HashMap<Comparable,Set<Pair<Comparable,Comparable>>>();
    }

    public void addClassifier(Object subdomainSpecifier, Comparable rangeElement) throws ThinklabValidationException
    {
        Pair<Comparable,Comparable> subdomain;
        try {
            subdomain = (Pair<Comparable,Comparable>) subdomainSpecifier;
        } catch (Exception e) {
            throw new ThinklabValidationException("Subdomain specifier must be of type Pair<Comparable,Comparable>");
        }

        this.domainToRange.put(subdomain, rangeElement);

        if (!this.rangeToDomain.containsKey(rangeElement)) {
            HashSet<Pair<Comparable,Comparable>> subdomainContainer = new HashSet<Pair<Comparable,Comparable>>();
            subdomainContainer.add(subdomain);
            this.rangeToDomain.put(rangeElement, subdomainContainer);
        } else {
            Set<Pair<Comparable,Comparable>> curSubdomain = this.rangeToDomain.get(rangeElement);
            curSubdomain.add(subdomain);
            this.rangeToDomain.put(rangeElement, curSubdomain);
        }
    }

    public void removeClassifier(Object subdomainSpecifier) throws ThinklabValidationException
    {
        Pair<Comparable,Comparable> subdomain;
        Comparable rangeElement;
        try {
            subdomain = (Pair<Comparable,Comparable>) subdomainSpecifier;
        } catch (Exception e) {
            throw new ThinklabValidationException("Subdomain specifier must be of type Pair<Comparable,Comparable>");
        }

        rangeElement = this.domainToRange.get(subdomain);
        this.domainToRange.remove(subdomain);

        Set<Pair<Comparable,Comparable>> curSubdomain = this.rangeToDomain.get(rangeElement);
        curSubdomain.remove(subdomain);
        if (curSubdomain.size() == 0)
            this.rangeToDomain.remove(rangeElement);
        else
            this.rangeToDomain.put(rangeElement, curSubdomain);
    }

    public void removeClassifier(Comparable rangeElement)
    {
        if (this.rangeToDomain.containsKey(rangeElement)) {
            for (Pair<Comparable,Comparable> subdomainElement : rangeToDomain.get(rangeElement)) {
                this.domainToRange.remove(subdomainElement);
            }
            rangeToDomain.remove(rangeElement);
        }
    }

    public Map<Set<Object>,Comparable> getClassifiers()
    {
        HashMap<Set<Object>,Comparable> classifiers = new HashMap<Set<Object>,Comparable>();
        for (Comparable rangeElement : this.rangeToDomain.keySet()) {
            classifiers.put(convertToObjectSet(this.rangeToDomain.get(rangeElement)), rangeElement);
        }
        return classifiers;
    }


    public Set<Object> convertToObjectSet(Set<Pair<Comparable,Comparable>> pairSet)
    {
        HashSet<Object> objectSet = new HashSet<Object>();
        for (Pair<Comparable,Comparable> pair : pairSet) {
            objectSet.add((Object) pair);
        }
        return objectSet;
    }

    public Set<Object> getDomain()
    {
        return convertToObjectSet(this.domainToRange.keySet());
    }

    public Set<Comparable> getRange()
    {
        return this.rangeToDomain.keySet();
    }

    public Comparable project(Comparable value)
    {
        for (Pair<Comparable,Comparable> subdomain : this.domainToRange.keySet()) {
        	
        	Comparable d = null;
        	
        	try {
        		d = Double.parseDouble(value.toString());
        	} catch (Exception e) {
        		return null;
        	}
        	
            if (d.compareTo(subdomain.getFirst()) >= 0
                && d.compareTo(subdomain.getSecond()) <= 0)
                return this.domainToRange.get(subdomain);
        }
        return null;
    }

    /*
     * Sort the intervals and give me the order of the interval where the 
     * passed var is. Also give me the total number of intervals.
     * 
     */
    public int getSortingOrder(Comparable value) {
    	
    	class Sorter implements Comparable {
    		
    		Comparable val;
			Pair<Comparable, Comparable> range;

			public Sorter(Pair<Comparable, Comparable> range, Comparable val) {
    			this.range = range;
    			this.val = val;
    		}

			public int compareTo(Object o) {
				int ret =
					range.getSecond().compareTo(((Sorter)o).range.getFirst());
				return ret == 0 ? 1 : ret; 
			}
    	}
    	
    	ArrayList<Sorter> sorter = new ArrayList<Sorter>();
    	for (Entry<Pair<Comparable, Comparable>, Comparable> rs : domainToRange.entrySet()) {
    		sorter.add(new Sorter(rs.getKey(), rs.getValue()));
		}

    	Collections.sort(sorter);
    	
    	int ret = 0;
    	for (Sorter s : sorter) {
    		if (s.val.compareTo(value) == 0) {
    			break;
    		}
    		ret++;
    	}
    	
    	return ret;

    }

	public int getNumberOfClasses() {
		return domainToRange.size();
	}
    
}


