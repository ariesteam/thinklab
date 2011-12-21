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

import java.io.OutputStream;
import java.io.PrintStream;

/**
  * PrintStream which outputs primitive types using C <code>printf</code>
  * style formatting. For each primitive type (float, double, char, int, long,
  * String), there is a <code>printf</code> method which takes (as a first
  * argument) either a <code>printf</code> style format string, or a
  * PrintfFormat object.
  * Using the latter can be more efficient because it permits an
  * application to prorate the overhead of parsing a format string.
  * 
  * <p>
  * Because Java does not permit variable numbers of arguments, each
  * <code>printf</code> function accepts only one primitive type, and the
  * format can correspondingly contain only one conversion
  * sequence.
  * 
  * @see PrintfFormat
  * @see PrintfWriter
  * @author John E. Lloyd, Fall 2000
  * Copied from John Lloyd's site and repackaged for Thinklab, FV 2007
  */
public class PrintfStream extends PrintStream
{
	/** 
	  * Creates a PrintfStream, without automatic line flushing,
	  * from an existing OutputStream.
	  * 
	  * @param out An output stream
	  */
	public PrintfStream (OutputStream out)
	 { super (out);
	 }

	/**
	  * Creates a PrintfStream from an existing OutputStream.
	  * 
	  * @param out An output stream
	  * @param autoFlush If true, specifies that output flushing will
	  * automatically occur when the println() methods are called,
	  * a byte array is written, or a new line character or byte is
	  * encountered in the output.
	  */
	public PrintfStream (OutputStream out, boolean autoFlush)
	 { super (out, autoFlush);
	 }

	/** 
	  * Prints a double in accordance with the supplied format string.
	  * 
	  * @param fs Format string
	  * @param x Double to output
	  * @throws IllegalArgumentException Malformed format string
	  */
	public void printf (String fs, double x)
	 { print (new PrintfFormat(fs).tostr(x));
	 }

	/** 
	  * Prints a float in accordance with the supplied format string.
	  * 
	  * @param fs Format string
	  * @param x Float to output
 	  * @throws IllegalArgumentException Malformed format string
	  */
	public void printf (String fs, float x)
	 { print (new PrintfFormat(fs).tostr(x));
	 }

	/** 
	  * Prints a long in accordance with the supplied format string.
	  * 
	  * @param fs Format string
	  * @param x Long to output
	  * @throws IllegalArgumentException Malformed format string
	  */
	public void printf (String fs, long x)
	 { print (new PrintfFormat(fs).tostr(x));
	 }

	/** 
	  * Prints an int in accordance with the supplied format string.
	  * 
	  * @param fs Format string
	  * @param x Int to output
	  * @throws IllegalArgumentException Malformed format string
	  */
	public void printf (String fs, int x)
	 { print (new PrintfFormat(fs).tostr(x));
	 }

	/** 
	  * Prints a String in accordance with the supplied format string.
	  * 
	  * @param fs Format string
	  * @param x String to output
	  * @throws IllegalArgumentException Malformed format string
	  */
	public void printf (String fs, String x)
	 { print (new PrintfFormat(fs).tostr(x));
	 }

	/** 
	  * Prints a char in accordance with the supplied format string.
	  * 
	  * @param fs Format string
	  * @param x Char to output
	  * @throws IllegalArgumentException Malformed format string
	  */
	public void printf (String fs, char x)
	 { print (new PrintfFormat(fs).tostr(x));
	 }

	/** 
	  * Prints a double in accordance with the supplied 
	  * PrintfFormat object.
	  * 
	  * @param fmt Formatting object
	  * @param x Double to output
	  * @see PrintfFormat
	  */
	public void printf (PrintfFormat fmt, double x)
	 { print (fmt.tostr(x));
	 }

	/** 
	  * Prints a float in accordance with the supplied 
	  * PrintfFormat object.
	  * 
	  * @param fmt Formatting object
	  * @param x Float to output
	  * @see PrintfFormat
	  */
	public void printf (PrintfFormat fmt, float x)
	 { print (fmt.tostr(x));
	 }

	/** 
	  * Prints a long in accordance with the supplied 
	  * PrintfFormat object.
	  * 
	  * @param fmt Formatting object
	  * @param x Long to output
	  * @see PrintfFormat
	  */
	public void printf (PrintfFormat fmt, long x)
	 { print (fmt.tostr(x));
	 }

	/** 
	  * Prints an int in accordance with the supplied 
	  * PrintfFormat object.
	  * 
	  * @param fmt Formatting object
	  * @param x Int to output
	  * @see PrintfFormat
	  */
	public void printf (PrintfFormat fmt, int x)
	 { print (fmt.tostr(x));
	 }

	/** 
	  * Prints a String in accordance with the supplied 
	  * PrintfFormat object.
	  * 
	  * @param fmt Formatting object
	  * @param x String to output
	  * @see PrintfFormat
	  */
	public void printf (PrintfFormat fmt, String x)
	 { print (fmt.tostr(x));
	 }

	/** 
	  * Prints a char in accordance with the supplied 
	  * PrintfFormat object.
	  * 
	  * @param fmt Formatting object
	  * @param x Char to output
	  * @see PrintfFormat
	  */
	public void printf (PrintfFormat fmt, char x)
	 { print (fmt.tostr(x));
	 }
}
