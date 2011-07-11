/**
 * IDistrictingAlgorithmConstructor.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 05, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDistrictingPlugin.
 * 
 * ThinklabDistrictingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDistrictingPlugin is distributed in the hope that it will be useful,
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
 * @author    Gary Johnson (gwjohnso@uvm.edu)
 * @date      Feb 05, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.districting.interfaces;

import org.integratedmodelling.exceptions.ThinklabException;


/**
 * Interface for a districting algorithm constructor to implement.
 *
 * The only requirement currently is being able to generate an
 * instance of any class which implements IDistrictingAlgorithm.
 * 
 * @author Gary Johnson
 *
 */
public interface IDistrictingAlgorithmConstructor {

    public abstract IDistrictingAlgorithm createDistrictingAlgorithm() throws ThinklabException;

}