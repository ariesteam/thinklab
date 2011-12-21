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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author villa
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ThinklabException extends Exception {

	private static final long serialVersionUID = 5999457326224959271L;

	public ThinklabException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThinklabException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ThinklabException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ThinklabException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	 public String stackTrace()
	  {
	    StringWriter sw = new StringWriter();
	    printStackTrace(new PrintWriter(sw));
	    return sw.toString();
	  }
}
