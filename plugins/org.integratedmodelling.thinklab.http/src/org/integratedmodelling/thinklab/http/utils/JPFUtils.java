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
package org.integratedmodelling.thinklab.http.utils;

import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;

public class JPFUtils {

	public static String getParameter(Extension ext, String extName, String defaultValue) {	
		String ret = getParameter(ext, extName);
		return ret == null ? defaultValue : ret;
	}

	public static String getParameter(Extension.Parameter ext, String extName, String defaultValue) {
		String ret = getParameter(ext, extName);
		return ret == null ? defaultValue : ret;
	}
	
	public static String getParameter(Extension ext, String extName) {
		Parameter prm = ext.getParameter(extName);
		return prm == null ? null : prm.valueAsString();
	}

	public static String getParameter(Extension.Parameter ext, String extName) {
		Parameter prm = ext.getSubParameter(extName);
		return prm == null ? null : prm.valueAsString();
	}
}
