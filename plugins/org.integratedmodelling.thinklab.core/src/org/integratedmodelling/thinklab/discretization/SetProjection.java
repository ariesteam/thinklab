/**
 * SetProjection.java
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
package org.integratedmodelling.thinklab.discretization;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

public class SetProjection implements Projection
{
    private HashMap<Comparable,Comparable> valueToValue;
    private HashMap<Comparable,Set<Comparable>> rangeToDomain;

    public SetProjection()
    {
        this.valueToValue = new HashMap<Comparable,Comparable>();
        this.rangeToDomain = new HashMap<Comparable,Set<Comparable>>();
    }

    public void addClassifier(Object subdomainSpecifier, Comparable rangeElement) throws ThinklabValidationException
    {
        Set<Comparable> subdomain;
        try {
            subdomain = (Set<Comparable>) subdomainSpecifier;
        } catch (Exception e) {
            throw new ThinklabValidationException("Subdomain specifier must be of type Set<Comparable>");
        }
        if (!this.rangeToDomain.containsKey(rangeElement)) {
            this.rangeToDomain.put(rangeElement, subdomain);
        } else {
            Set<Comparable> curSubdomain = this.rangeToDomain.get(rangeElement);
            curSubdomain.addAll(subdomain);
            this.rangeToDomain.put(rangeElement, curSubdomain);
        }
        for (Comparable domainElement : subdomain) {
            this.valueToValue.put(domainElement, rangeElement);
        }
    }

    public void removeClassifier(Object subdomainSpecifier) throws ThinklabValidationException
    {
        Set<Comparable> subdomain;
        Set<Comparable> subrange = new HashSet<Comparable>();
        try {
            subdomain = (Set<Comparable>) subdomainSpecifier;
        } catch (Exception e) {
            throw new ThinklabValidationException("Subdomain specifier must be of type Set<Comparable>");
        }
        for (Comparable domainElement : subdomain) {
            subrange.add(this.valueToValue.get(domainElement));
            this.valueToValue.remove(domainElement);
        }
        for (Comparable rangeElement : subrange) {
            Set<Comparable> curSubdomain = this.rangeToDomain.get(rangeElement);
            curSubdomain.removeAll(subdomain);
            if (curSubdomain.size() == 0)
                this.rangeToDomain.remove(rangeElement);
            else
                this.rangeToDomain.put(rangeElement, curSubdomain);
        }
    }

    public void removeClassifier(Comparable rangeElement)
    {
        if (this.rangeToDomain.containsKey(rangeElement)) {
            for (Comparable domainElement : rangeToDomain.get(rangeElement)) {
                this.valueToValue.remove(domainElement);
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

    public Set<Object> convertToObjectSet(Set<Comparable> comparableSet)
    {
        HashSet<Object> objectSet = new HashSet<Object>();
        for (Comparable element : comparableSet) {
            objectSet.add((Object) element);
        }
        return objectSet;
    }

    public Set<Object> getDomain()
    {
        return convertToObjectSet(this.valueToValue.keySet());
    }

    public Set<Comparable> getRange()
    {
        return this.rangeToDomain.keySet();
    }

    public Comparable project(Comparable value)
    {
        return this.valueToValue.get(value);
    }

	public int getNumberOfClasses() {
		return rangeToDomain.size();
	}
}


