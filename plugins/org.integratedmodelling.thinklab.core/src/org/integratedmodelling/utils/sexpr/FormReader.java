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
package org.integratedmodelling.utils.sexpr;

import java.io.IOException;
import java.io.InputStream;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;

/**
 * Wraps an InputStream and reads s-expr by s-expr, using Clojure (and I guess any 
 * Lisp's) reader conventions for strings and quotes. Allows "listening" to all 
 * forms in a stream.
 * 
 * @author Ferdinando
 *
 */
public class FormReader {
	
	private boolean inDquote = false;
	private boolean inSquote = false;
	private boolean inEscape = false;
	private boolean inComment = false;
	
	private InputStream input = null;
	private boolean isEof = false;
	
	StringBuffer ret = new StringBuffer(16000);

	
	public interface FormListener {
		public void onFormRead(String s) throws ThinklabException;
	}
	
	public FormReader(InputStream input) {
		this.input = input;
	}
	
	public void close() throws ThinklabException {
		try {
			this.input.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}
	private boolean quoted(int ch) {
		
		boolean wasEscape = inEscape;
		boolean wasInDquote = inDquote;
		boolean wasInSquote = inSquote;
		boolean wasComment = inComment;
		
		if (wasComment && ch == '\n')
			inComment = false;
			
		if (wasEscape)
			inEscape = false;
		
		if (!wasEscape) {
			if (wasInDquote && ch == '"') {
				inDquote = false;
			}
		}
		
		return wasInDquote || wasInSquote || wasEscape || wasComment;
	}
	
	/**
	 * Read a single form from input.
	 */
	public String readForm() throws ThinklabException {

		int plevel = -2;
		ret.setLength(0);
		
		for (;;) {
			try {

				int ch = this.input.read();
						
				if (ch < 0) {
					this.isEof = true;
					break;
				} 
				
				if (!quoted(ch)) {
					
					switch (ch) {
					
					case '(':
						if (plevel == -2)
							plevel = -1;
						plevel ++;
						break;
					case ')':
						plevel --;
						break;
					case '"':
						inDquote = true;
						break;
//					case '\'':
//						inSquote = true;
//						break;
					case '\\':
						inEscape = true;
						break;
					case ';':
						inComment = true;
						break;
					}
				}
		
				ret.append((char)ch);	
				if (plevel == -1)
					break;
				
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		return ret.toString();
	}
	
	public boolean isEof() {
		return this.isEof;
	}
	
	public void read(FormListener listener) throws ThinklabException {
		while (!isEof()) {
			listener.onFormRead(readForm());
		}
	}
	
}
