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

package org.integratedmodelling.utils.cformat;         


/**
  * Format object for scanning input in the same way as the 
  * C <code>scanf</code> function.
  * 
  * <p>
  * A <code>scanf</code> style format string is specified in the
  * constructor. Once instantiated, objects of this class may
  * be passed as arguments to the <code>scan</code> methods
  * of the <code>ScanfReader</code> class.
  *
  * @see ScanfReader
  * @author John E. Lloyd, Fall 2000
  * Copied from John Lloyd's site and repackaged for Thinklab, FV 2007
  *
  */
public class ScanfFormat
{
	int width;
	int type;
	String prefix;
	String suffix;

	String cmatch;

	class Cmatch
	 { char cupper;
	   char clower;
	 }

	/** 
	  * Checks to see if a character matches the sequence specified by
	  * the character set cmatch.
	  * 
	  * @param c Character to test
	  * @return True if c is a member of the character set specified by
	  * the string cmatch (or not a member, if the string begins with
	  * a '^').
	  */
	boolean matchChar(char c)
	 {
	   int i0 = 0;
	   int len;
	   boolean negate = false;
	   char c0, c1;

	   if (cmatch == null)
	    { return false;
	    }
	   len = cmatch.length();

	   if (cmatch.charAt(0) == '^')
	    { negate = true;
	      i0 = 1;
	    }
	   for (int i=i0; i<len; i++)
	    { c0 = cmatch.charAt(i);
	      if (i<len-2 && cmatch.charAt(i+1) == '-')
	       { c1 = cmatch.charAt(i+2);
		 if (c0 < c1)
		  { if (c0 <= c && c <= c1)
		     { return !negate;
		     }
		  }
		 else
		  { if (c1 <= c && c <= c0)
		     { return !negate;
		     }
		  }
		 i++;
	       }
	      else
	       { if (c == c0)
		  { return !negate;
		  }
	       }
	    }
	   return negate;
	 }

	private static String validTypes = new String ("dioxfsc[bqS");

	/** 
	  * Constructs a ScanfFormat class from a format string.
	  * The structure of the format string is described
	  * in the documentation for the <code>set</code> method.
	  * 
	  * @param fmt	Format string
	  * @throws IllegalArgumentException Malformed format string
	  * @see ScanfReader
	  */
	public ScanfFormat (String fmt)
	 {
	   set (fmt);
	 }

	/** 
	  * Sets the contents of the object according to
	  * the information provided in the format string.
	  * 
	  * 
	  * <p>
	  * The format string describes what input to expect for a
	  * ScanfReader, and its form closely matches that for the C
	  * <code>scanf</code> function, expect that multiple conversions
	  * cannot be specified. 
	  * 
	  * <p>
	  * A conversion sequence is introduced by the '%' character; valid
	  * conversion sequences are described below. Other characters may
	  * appear in the format string. A white space character requests
	  * a match of any amount of white space, including none. Other
	  * characters request an exact match of themselves. The character
	  * sequence "%%" requests a match of the '%' character.
	  *
	  * <p>
	  * The '%' character introducing a conversion sequence
	  * may be followed by an unsigned decimal integer
	  * indicating the field width, which is the maximum number of
	  * characters used for a particular conversion. Field widths must
	  * be greater than 0. 
	  * 
	  * <p>
	  * The optional field width is followed by one of the following
	  * <em>conversion characters</em>, which specifies the primitive
	  * type to be scanned: 
	  * 
	  * <dl>
	  * <dt> f
	  * <dd> floating point (double).
	  * <dt> d
	  * <dd> signed decimal integer (long or int).
	  * <dt> o
	  * <dd> unsigned octal integer (long or int), formed from the digits
	  * [0-7].
	  * <dt> x
	  * <dd> unsigned hex integer (long or int), formed from the
	  * characters [0-9a-fA-F].
	  * <dt> i
	  * <dd> signed integer (long or int). If the digit sequence
	  * begins with "<code>0x</code>", then a hex value is scanned; if
	  * the digit sequence begins with "<code>0</code>", then an octal
	  * value is scanned; and otherwise a decimal value is scanned.
	  * <dt> c
	  * <dd> character (char).
	  * <dt> [
	  * <dd> matches any character in the sequence specified between 
	  * the '[' and a closing ']', unless the first character in the
	  * sequence is a '^', in which case any character is matched which
	  * does <em>not</em> appear in the following sequence. To include
	  * ']' in the sequence, it should be placed immediately after the
	  * initial '[' or '[^'. The character '-' is also special; if it
	  * appears between any two characters in the sequence, then all
	  * characters within the numeric range specified by the two
	  * characters are implicitly incorported into the sequence. 
	  * Consecutive '-' characters are not permitted in the sequence.
	  * A lone '-' character may be specified by placing it at the
	  * end of the sequence, before the closing ']'.
	  * <dt> s
	  * <dd> matches a string delimited by white space.
	  * <dt> q
	  * <dd> matches a string delimited by double quotes.
	  * <dt> S
	  * <dd> matches a string delimited by either white space or
	  * double quotes.
	  * <dt> b
	  * <dd> matches a boolean ("true" or "false").
	  * </dl>
	  *
	  * @param fmt	Format string
	  * @throws IllegalArgumentException Malformed format string
	  * @see ScanfReader
	  */
	public void set (String fmt)
	 {
	   type = -1;
	   width = -1;
	   prefix = null;
	   suffix = null;
	   cmatch = null;
	   char[] buf = new char[fmt.length()];
	   int i, n, c = 0;

	   if (fmt.length()==0)
	    { return;
	    }
	   n = 0;
	   for (i=0; i<fmt.length(); i++)
	    { if ((c = fmt.charAt(i)) == '%')
	       { i++;
		 if (i==fmt.length())
		  { throw new IllegalArgumentException (
"Format string terminates with '%'");
		  }
		 if ((c = fmt.charAt(i)) != '%')
		  { break;
		  }
	       }
	      buf[n++] = (char)c;
	    }
	   if (n > 0)
	    { prefix = new String(buf, 0, n);
	    }
	   if (i==fmt.length())
	    { return;
	    }
	   if (Character.isDigit((char)c))
	    { int w = c - '0';
	      for (i++; i<fmt.length(); i++)
	       { if (Character.isDigit((char)(c = fmt.charAt(i)) ))
		  { w = w*10 + c - '0';
		  }
		 else
		  { break;
		  }
	       }
	      if (i==fmt.length())
	       { throw new IllegalArgumentException (
"Premature end of format string");
	       }
	      if (w == 0)
	       { throw new IllegalArgumentException (
"Zero field width specified");
	       }
	      width = w;
	    }
	   if (validTypes.indexOf(c) == -1)
	    { throw new IllegalArgumentException (
"Illegal conversion character '" + (char)c + "'");
	    }
	   type = c;
	   if (type == '[')
	    { n = 0;
	      // first scan the cmatch string ...
	      for (i++; i<fmt.length(); i++)
	       { if ((c = fmt.charAt(i)) == ']' &&
		      n != 0 && !(buf[0] == '^' && n == 1))
		  { break;
		  }
		 buf[n++] = (char)c;
	       }
	      if (i==fmt.length())
	       { throw new IllegalArgumentException (
"Premature end of format string");
	       }
	      // and now make sure it's legal ....
	      for (int k=0; k<n; k++)
	       { if (buf[k] == '-')
		  { if (k < n-1 && buf[k+1] == '-')
		     { throw new IllegalArgumentException (
"Misplaced '-' in character match spec '[" + new String(buf, 0, n) + "]'");
		     }
		  }
	       }
	      cmatch = new String(buf, 0, n);
	    }
	   n = 0;
	   for (i++; i<fmt.length(); i++)
	    { if ((c = fmt.charAt(i)) == '%')
	       { i++;
		 if (i<fmt.length() && fmt.charAt(i) == '%')
		  { buf[n++] = '%';
		  }
		 else
		  { throw new IllegalArgumentException (
"Extra '%' in format string");
		  }
	       }
	      else
	       {
		 buf[n++] = (char)c;
	       }
	    }
	   if (n > 0)
	    { suffix = new String(buf, 0, n);
	    }
	 }
}
