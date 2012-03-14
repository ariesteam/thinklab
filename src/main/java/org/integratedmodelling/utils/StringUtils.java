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

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang.StringUtils {
	
	
	/**
	 * Remove all leading and trailing whitespace; pack whitespace in between to
	 * single space; leave a blank line if there are at least two newlines in the
	 * original whitespace. Good for formatting indented and bullshitted text like
	 * what you put in XML files into something more suitable for text processing or
	 * wiki translation.
	 * 
	 * @param s
	 * @return
	 */
	static public String pack(String s) {
		
		if (s == null)
			return "";
		
		StringBuffer ret = new StringBuffer(s.length());
		
		s = s.trim();
		
		for (int i = 0; i < s.length(); i++) {
			
			int nlines = 0;
			int wp = 0;
			while (Character.isWhitespace(s.charAt(i))) {
				if (s.charAt(i) == '\n')
					nlines++;
				i++;
				wp++;
			}
			if (wp > 0)
				ret.append(nlines > 1 ? "\n\n" : " ");
			ret.append(s.charAt(i));
		}
		
		return ret.toString();
	}
	
	/**
	 * Divide up a string into tokens, correctly handling double quotes.
	 * 
	 * @param s
	 * @return
	 */
	static public Collection<String> tokenize(String s) {
		
		ArrayList<String> ret = new ArrayList<String>();
	    String regex = "\"([^\"]*)\"|(\\S+)";
	    Matcher m = Pattern.compile(regex).matcher(s);
	    while (m.find()) {
	        if (m.group(1) != null) {
	        	ret.add(m.group(1));
	        } else {
	        	ret.add(m.group(2));
	        }
	    }
	    return ret;
	}
}
