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
package org.integratedmodelling.thinklab.exception;

/**
 * Thrown when a method supposed to return one resource is called in a context that
 * returns multiple ones.
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class ThinklabMultipleResourcesException extends ThinklabException {


	private static final long serialVersionUID = 5280213816096568419L;

	public ThinklabMultipleResourcesException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThinklabMultipleResourcesException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ThinklabMultipleResourcesException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ThinklabMultipleResourcesException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
