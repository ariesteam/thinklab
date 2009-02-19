/**
 * IConceptualizable.java
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
package org.integratedmodelling.thinklab.interfaces.knowledge;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;


/**
 * Objects implementing this one are capable of being converted into an instance. In order to allow that,
 * they return a list representation of the instance that represents them. Instance implementations are
 * not automatically IConceptualizable, but if they are, they should be able to produce an instance that
 * will create a clone of themselves as implementation, or if the implementation wasn't created with 
 * an instance, may use themselves as implementation.
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public interface IConceptualizable {
	
	Polylist conceptualize() throws ThinklabException;
}
