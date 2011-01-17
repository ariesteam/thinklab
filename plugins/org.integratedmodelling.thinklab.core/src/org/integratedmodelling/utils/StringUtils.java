package org.integratedmodelling.utils;

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
}
