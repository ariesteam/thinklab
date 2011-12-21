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
package org.integratedmodelling.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

public class LogAppender {

	private File fname;

	public LogAppender(String file, boolean append) throws ThinklabIOException {
		
		this.fname = new File(file);
		
		if (!append && this.fname.exists()) {
			this.fname.delete();
		}
	}
	
	public void print(String s)  {

		try {
			FileWriter writer = new FileWriter(fname, true);
			writer.write(s + System.getProperty("line.separator"));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
}
