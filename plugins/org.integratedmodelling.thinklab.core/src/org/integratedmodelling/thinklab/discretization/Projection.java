/**
 * Projection.java
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

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
public interface Projection
{
    public abstract void addClassifier(Object subdomainSpecifier, Comparable rangeElement) throws ThinklabValidationException;

    public abstract void removeClassifier(Object subdomainSpecifier) throws ThinklabValidationException;

    public abstract void removeClassifier(Comparable rangeElement);

    public abstract Map<Set<Object>, Comparable> getClassifiers();

    public abstract Set<Object> getDomain();

    public abstract Set<Comparable> getRange();

    public abstract Comparable project(Comparable value);
    
    public abstract int getNumberOfClasses();
}


