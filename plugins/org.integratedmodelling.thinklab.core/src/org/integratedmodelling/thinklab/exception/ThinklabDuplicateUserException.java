/**
 * ThinklabDuplicateUserException.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabAuthenticationPlugin.
 * 
 * ThinklabAuthenticationPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabAuthenticationPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.exception;


public class ThinklabDuplicateUserException extends ThinklabException {

	private static final long serialVersionUID = -531398720073851507L;

	public ThinklabDuplicateUserException() {
		// TODO Auto-generated constructor stub
	}

	public ThinklabDuplicateUserException(String arg0, Throwable arg1) {
		super("duplicate user: " + arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ThinklabDuplicateUserException(String arg0) {
		super("duplicate user: " + arg0);
		// TODO Auto-generated constructor stub
	}

	public ThinklabDuplicateUserException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
