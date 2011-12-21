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
package org.integratedmodelling.thinklab.interfaces.literals;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;

/**
 * An IOperator is a specialized instance implementation capable of returning a value given a set
 * of IValue arguments. Should be also capable of validating the arguments, although this is not 
 * enforced currently.
 * 
 * Operators are declared in ontologies and the correspondent instance objects can be used in 
 * constraints.
 * 
 * FIXME this whole thing is unnecessarily complicated and should be simplified.
 * 
 * @author Ferdinando
 *
 */
public interface IOperator extends IInstanceImplementation {

	public static final String NO_OP = "nop";
	public static final String SUM = "+";
	public static final String MUL = "*";
	public static final String SUB = "-";
	public static final String DIV = "/";
	public static final String MOD = "%";
	
	public static final String AVG = "mean";
	public static final String STD = "std";
	public static final String CV  = "cv";
	public static final String VAR = "var";

	
	public abstract IValue eval(Object ... arg) throws ThinklabException;
	public abstract String getOperatorId();

}
