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
package org.integratedmodelling.thinklab.interfaces.knowledge;

/**
 * If an instance implementation implements OntoBean, all fields whose name
 * start with an underscore will be automatically wired to property values,
 * using the has/have prefix as a key to find the corresponding relationship
 * and standard camelcase translation. The cardinality of the relationships
 * is expected to match: arrays will be expected for all multiple properties.
 * 
 * TODO unimplemented
 * 
 * @author Ferdinando Villa
 *
 */
public interface OntoBean {

}
