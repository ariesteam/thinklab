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
import java.io.PrintWriter;
import java.io.Writer;

/**
  * Writer class to output primitive types using C <code>printf</code> style
  * formatting. For each primitive type (float, double, char, int, long,
  * String), there is a <code>printf</code> method which takes (as a first
  * argument) a <code>printf</code> style format string.
  * Because Java does not permit variable numbers of arguments, each
  * <code>printf</code> function accepts only one primitive type, and the
  * formats can correspondingly contain only one conversion
  * sequence.
  *
  * <p>
  * For example, suppose we wish to print a list of names and numbers
  * according to the printf format <code>"%s, %8.2f"</code>. This can
  * be done as follows:
  * <pre>
  *    String[] names = ...
  *    double[] score = ...
  *    PrintfWriter w = new PrintfWriter (writer);
  *    for (int i=0; i<1000; i++)
  *     { w.printf ("%s, ", names[i]);
  *       w.printf ("%8.2f", score[i]);
  *     }
  * </pre>
  * <p>
  * Instead of a format string, one can instead use a pre-parsed
  * {@link cformat.PrintfFormat PrintfFormat} object which
  * is created from a format string. This will be more efficient
  * when a lot of output is required:
  * <pre>
  *    double[] bigVector = new double[1000];
  *    PrintfWriter w = new PrintfWriter (writer);
  *    PrintfFormat fmt = new PrintfFormat ("%8.2f");
  *    for (int i=0; i<1000; i++)
  *     { w.printf (fmt, bigVector[i]);
  *     }
  * </pre>
  * <p>
  * Finally, {@link cformat.PrintfFormat PrintfFormat} can be used
  * to perform the conversion directly, via its
  * {@link cformat.PrintfFormat#tostr tostr} method:
  * <pre>
  *    PrintfFormat fmt = new PrintfFormat ("%8.2f");
  *    for (int i=0; i<1000; i++)
  *     { System.out.println (fmt.tostr(bigVector[i]));
  *     }
  * </pre>
  * @see PrintfFormat
  * @see PrintfStream
  * @author John E. Lloyd, Fall 2000
  * Copied from John Lloyd's site and repackaged for Thinklab, FV 2007
  *
  */
public class PrintfWriter extends PrintWriter
{
	/** 
	  * Creates a PrintfWriter, without automatic line flushing,
	  * from an existing OutputStream.
	  * 
	  * @param out An output stream
	  */
	public PrintfWriter (OutputStream out)
	 { super (out);
	 }

	/**
	  * Creates a PrintfWriter from an existing OutputStream.
	  * 
	  * @param out An output stream
	  * @param autoFlush If true, specifies that output flushing will
	  * automatically occur when the println() methods are called.
	  */
	public PrintfWriter (OutputStream out, boolean autoFlush)
	 { super (out, autoFlush);
	 }

	/** 
	  * Creates a PrintfWriter, without automatic line flushing,
	  * from an existing Writer.
	  * 
	  * @param out A writer
	  */
	public PrintfWriter (Writer out)
	 { super (out);
	 }

	/** 
	  * Creates a PrintfWriter from an existing Writer.
	  * 
	  * @param out A writer
	  * @param autoFlush If true, specifies that output flushing will
	  * automatically occur when the println() methods are called.
	  */
	public PrintfWriter (Writer out, boolean autoFlush)
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
