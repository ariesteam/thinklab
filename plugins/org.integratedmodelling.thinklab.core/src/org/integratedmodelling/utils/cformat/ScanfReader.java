 /**
 * ScanfReader.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/

package org.integratedmodelling.utils.cformat;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;

/**
  * A Reader which implements C <code>scanf</code> functionality.
  * It allows an application to read various primitive types 
  * from the underlying stream using <code>scan</code> methods 
  * that implement <code>scanf</code> type input formatting.
  *
  * <p>
  * There are <code>scan</code> methods to read float, double,
  * long, int, char, char[], and Strings, and each can accept a format
  * string with the same syntax as that accepted by the C <code>scanf()</code>
  * function (the exact syntax is described in the
  * documentation for {@link cformat.ScanfFormat ScanfFormat}).
  *
  * <p> Because Java does not permit variable-length argument lists,
  * only one conversion can be performed per method call.
  * As an example, suppose we wish to parse the input
  * <pre>
  * 00010987JohnLloyd     0032,09999.99
  * </pre>
  * with the equivalent of
  * <pre>
  *    scanf ("%10d%4s%10s%d,%f", &i, name1, name2, &j, &price);
  * </pre>
  * This can be done as follows:
  * <pre>
  *    ScanfReader scanner = new ScanfReader (reader);
  *
  *    int i = scanner.scanInt ("%10d");
  *    String name1 = scanner.scanString ("%4s");
  *    String name2 = scanner.scanString ("%10s");
  *    int j = scanner.scanInt ("%d");
  *    double price = scanner.scanDouble (",%f");
  * </pre>
  *
  * <p>The scan methods can also be called without an argument,
  * in which case a default format string is used. One can also
  * pre-parse the format string into an object of type 
  * {@link cformat.ScanfFormat ScanfFormat},
  * which is more efficient when a lot of input is being read:
  * <pre>
  *    ScanfFormat fmt = new ScanfFormat ("%f");
  *    for (int i=0; i<100000; i++){
  *     { buf[i] = scanner.scanDouble (fmt);
  *     }
  * </pre>
  *
  * <p> If the input does not match the specified format, then a
  * {@link cformat.ScanfMatchException ScanfMatchException} is thrown. A
  * <code>java.io.EOFException</code> is thrown if the end of input is
  * reached before the scan can complete successfully, and errors in
  * the underlying input will throw a
  * <code>java.io.IOException</code>.  Finally, an invalid format
  * string (or {@link cformat.ScanfFormat ScanfFormat} object) will trigger an
  * <code>InvalidArgumentException</code>.
  * In the case of a <code>ScanfMatchException</code>,
  * scanning stops at the first character from which it can be
  * determined that the match will fail. This character is remembered by
  * the stream (see the discussion of the look-ahead character, below)
  * and will be the first character seen by the next <code>scan</code> or
  * <code>read</code> method which is called.
  * 
  * <p>
  * The class keeps track of the current line number (accessible with
  * {@link #getLineNumber getLineNumber} and
  * {@link #setLineNumber setLineNumber}),
  * as well as the number of characters which have been consumed
  * (accesible with {@link #getCharNumber getCharNumber}
  * and {@link #setCharNumber setCharNumber}).
  *
  * <p>
  * The class usually keeps one character of look-ahead which has been
  * read from the underlying reader but not yet consumed by any scan
  * method. If the underlying reader is used later in some other capacity,
  * this look-ahead character may have to be taken into account. If a
  * look-ahead character is actually being stored, the
  * {@link #lookAheadCharValid lookAheadCharValid} method
  * will return <code>true</code>,
  * and the look-ahead character itself can then be obtained using
  * {@link #getLookAheadChar getLookAheadChar}. The look-ahead character can 
  * be cleared using {@link #clearLookAheadChar clearLookAheadChar}.  */
public class ScanfReader extends Reader
{
	private Reader reader = null;
	private int charCnt = 0;
	private int lineCnt = 1;
	private int lastChar;
	private int curChar;
	private boolean curCharValid = false;
	private char[] buffer;
	private int bcnt;
	private static final int NO_COMMENT = -2;
	private int commentChar = NO_COMMENT;

	private static String hexChars = new String("0123456789abcdefABCDEF");
	private static String octChars = new String("01234567");

	private static final int BUFSIZE = 1024;

	private static ScanfFormat defaultDoubleFmt =
	   new ScanfFormat ("%f");

	private static ScanfFormat defaultIntFmt =
	   new ScanfFormat ("%i");

	private static ScanfFormat defaultDecFmt =
	   new ScanfFormat ("%d");

	private static ScanfFormat defaultHexFmt =
	   new ScanfFormat ("%x");

	private static ScanfFormat defaultOctFmt =
	   new ScanfFormat ("%o");

	private static ScanfFormat defaultStringFmt =
	   new ScanfFormat ("%s");

	private static ScanfFormat defaultCharFmt =
	   new ScanfFormat ("%c");

	private static ScanfFormat defaultBooleanFmt =
	   new ScanfFormat ("%b");

	private final void initChar()
	   throws IOException
	 {
	   if (!curCharValid)
	    { curChar = reader.read();
	      curCharValid = true;
	    }
//	   charCnt = 0;
	 }

	private final void consumeAndReplaceChar()
	   throws IOException
	 {
	   if (curChar != -1)
	    { charCnt++;
	      if (curChar == '\r' || (curChar == '\n' && lastChar != '\r'))
	       { lineCnt++;
	       }
	    }
	   lastChar = curChar;
	   curChar = reader.read();
	 }

	private final void consumeChar()
	   throws IOException
	 {
	   if (curChar != -1)
	    { charCnt++;
	      if (curChar == '\r' || (curChar == '\n' && lastChar != '\r'))
	       { lineCnt++;
	       }
	    }
	   lastChar = curChar;
	   curCharValid = false;
	 }

	/** 
	  * Closes the stream. 
	  * 
	  * @throws IOException An I/O error occurred
	  */
	public void close()
	   throws IOException
	 {
	   reader.close();
	 }

	/** 
	  * Reads characters into a portion of a character array.
	  * The method will block until input is available, an I/O
	  * error occurs, or the end of the stream is reached.
	  * 
	  * @param cbuf Buffer to write characters into
	  * @param off Offset to start writing at
	  * @param len Number of characters to read
	  * @return The number of characters read, or -1 if the end of
	  * the stream is reached.
	  * @throws IOException An I/O error occurred
	  */
	public int read (char[] cbuf, int off, int len)
	   throws IOException
	 {
	   int n, c;
	   int n0 = 0;

	   if (curCharValid)
	    { consumeChar();
	      if (curChar != -1)
	       { cbuf[off++] = (char)curChar;
		 len--;
		 n0 = 1;
	       }
	      else
	       { 
		 return -1;
	       }
	    }
	   if (len > 0)
	    { n = reader.read (cbuf, off, len);
	    }
	   else
	    { return n0;
	    }
	   if (n == -1)
	    { return -1;
	    }
	   else
	    { for (int i=0; i<n; i++)
	       { c = cbuf[off+i];
		 if (c == '\r' || (c == '\n' && lastChar != '\r'))
		  { lineCnt++;
		  }
		 lastChar = c;
	       }
	      charCnt += n;
	      return n + n0;
	    }
	 }
	
	private final void checkTypeAndScanPrefix (ScanfFormat fmt, 
						   String type)
	   throws IOException, IllegalArgumentException
	 {
	   if (fmt.type == -1)
	    { throw new IllegalArgumentException (
"No conversion character");
	    }
	   if (type.indexOf (fmt.type) == -1)
	    { throw new IllegalArgumentException (
"Illegal conversion character '" + (char)fmt.type + "'");
	    }
	   if (fmt.prefix != null)
	    { matchString (fmt.prefix);
	    }
	 }

	private final void scanPrefix (ScanfFormat fmt)
	   throws IOException
	 {
	   if (fmt.prefix != null)
	    { matchString (fmt.prefix);
	    }
	 }

	private final void scanSuffix (ScanfFormat fmt)
	   throws IOException
	 {
	   if (fmt.suffix != null)
	    { matchString (fmt.suffix);
	    }
	 }

	private void matchString (String s)
	   throws IOException, ScanfMatchException
	 {
	   initChar();
	   for (int i=0; i<s.length(); i++)
	    { char c = s.charAt(i);
	      if (Character.isWhitespace(c))
	       { skipWhiteSpace();
//		 if (skipWhiteSpace() == false)
//		  { throw new ScanfMatchException (
//"No white space to match white space in format");
//		  }
	       }
	      else if (curChar == -1)
	       { throw new EOFException("EOF");
	       }
	      else
	       { if (curChar != (int)c)
		  { throw new ScanfMatchException (
"Char '" + (char)curChar + "' does not match char '" + 
			  c + "' in format");
		  }
		 consumeAndReplaceChar();
	       }
	    }	   
	 }

	private void matchKeyword (String s)
	   throws IOException, ScanfMatchException
	 {
	   initChar();
	   for (int i=0; i<s.length(); i++)
	    { char c = s.charAt(i);
	      if (curChar == -1)
	       { throw new EOFException("EOF");
	       }
	      if (curChar != (int)c)
	       { throw new ScanfMatchException (
"Expecting keyword \"" + s + "\"");
	       }
	      consumeAndReplaceChar();
	    }	   
	 }

	/** 
	  * Skip white space and count line numbers.
	  */
	private boolean skipWhiteSpace()
	   throws IOException
	 {
	   boolean encounterdWhiteSpace = false;

	   initChar();
	   while (true)
	    { if (Character.isWhitespace ((char)curChar))
	       { consumeAndReplaceChar();
		 encounterdWhiteSpace = true;
	       }
	      else if (curChar == commentChar)
	       { while (curChar != '\n') 
		  { consumeAndReplaceChar();
		  }
		 consumeAndReplaceChar();
		 encounterdWhiteSpace = true;
	       }
	      else
	       { break;
	       }
	    }
	   return encounterdWhiteSpace ? true : false;
	 }

	/** 
	  * Create a new ScanfReader from the given reader.
	  * 
	  * @param reader 	Underlying Reader
	  */
	public ScanfReader (Reader in)
	 {
	   super();
	   reader = in;
	   curCharValid = false;
	   charCnt = 0;
	   lineCnt = 1;
	   curChar = 0;
	   lastChar = 0;
	   // XXX XXX XXX fixed size buffer is a big hack
	   buffer = new char[BUFSIZE];
	   bcnt = 0;
	 }

	private final boolean acceptDigits(int width)
	   throws IOException
	 { boolean matched = false;
	   while (Character.isDigit((char)curChar) && bcnt<width)
	    { buffer[bcnt++] = (char)curChar;
	      matched = true;
	      if (bcnt<width)
	       { consumeAndReplaceChar();
	       }
	      else
	       { consumeChar();
	       }
	    }
	   return matched;
	 }

	private final boolean acceptChar(char c, int width)
	   throws IOException
	 { if (curChar == c && bcnt<width)
	    { buffer[bcnt++] = (char)curChar;
	      if (bcnt<width)
	       { consumeAndReplaceChar();
	       }
	      else
	       { consumeChar();
	       }
	      return true;
	    }
	   else
	    { return false;
	    }
	 }

	/** 
	  * Scan and return a double. 
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion character 'f'. The number itself
	  * may consist of (a) an optional sign ('+' or '-'), (b) a sequence
	  * of decimal digits, with an optional decimal point, (c) an
	  * optional exponent ('e' or 'E'), which must by followed by an
	  * optionally signed sequence of decimal digits. White space
	  * immediately before the number is skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned double value
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public double scanDouble (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanDouble (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a double, using the default format string "%f".
	  * 
	  * @return Scanned double value
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDouble(String)
	  */
	public double scanDouble ()
	   throws IOException, ScanfMatchException
	 {
	   double val = 0;
	   try
	    { val = scanDouble (defaultDoubleFmt);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return a double, using a pre-allocated
	  * <code>ScanfFormat</code> object. 
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned double value
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDouble(String)
	  */
	public double scanDouble (ScanfFormat fmt)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   // parse [-][0-9]*[.][0-9]*[eE][-][0-9]*
	   boolean hasDigits = false;
	   boolean signed;
	   double value = 0;
	   int w;

	   checkTypeAndScanPrefix (fmt, "f");
	   skipWhiteSpace();
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   bcnt = 0;
	   w = (fmt.width == -1 ? 1000000000 : fmt.width);

	   signed = (acceptChar ('-', w) || 
		     acceptChar ('+', w));
	   if (acceptDigits(w))
	    { hasDigits = true;
	    }
	   acceptChar ('.', w);
	   if (!hasDigits && (bcnt==w || !Character.isDigit((char)curChar)))
	    { if (curCharValid && curChar == -1)
	       { throw new EOFException("EOF");
	       }
	      else
	       { throw new ScanfMatchException (
"Malformed floating point number: no digits");
	       }
	    }
	   acceptDigits(w);
	   if (acceptChar ('e', w) || 
	       acceptChar ('E', w))
	    { signed = (acceptChar ('-', w) || 
			acceptChar ('+', w));
	      if (bcnt==w || !Character.isDigit((char)curChar))
	       { if (curCharValid && curChar == -1)
		  { throw new EOFException("EOF");
		  }
		 else
		  { throw new ScanfMatchException (
"Malformed floating point number: no digits in exponent");
		  }
	       }
	      acceptDigits (w);
	    }
	   try
	    { value = Double.parseDouble(new String(buffer, 0, bcnt));
	    }
	   catch (NumberFormatException e)
	    { throw new ScanfMatchException (
"Malformed floating point number");
	    }
	   scanSuffix (fmt);
	   return value;
	 }
	
	/** 
	  * Scan and return a float. The format string <code>s</code> 
	  * takes the same form as that described in the documentation 
	  * for <code>scanDouble(String)</code>.
	  * 
	  * @param s	Format string
	  * @return Scanned float value
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDouble(String)
	  */
	public float scanFloat (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return (float)scanDouble (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a float, using the default format string "%f".
	  * 
	  * @return Scanned float value
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDouble(String)
	  */
	public float scanFloat ()
	   throws IOException, ScanfMatchException
	 {
	   return (float)scanDouble();
	 }

	/** 
	  * Scan and return a float, using a pre-allocated
	  * <code>ScanfFormat</code> object. 
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned float value
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDouble(String)
	  */
	public float scanFloat (ScanfFormat fmt)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return (float)scanDouble (fmt);
	 }

	/** 
	  * Scan and return a <code>String</code>.
	  * 
	  * <p> The format string <code>s</code> must have the form
	  * described by the documentation for the class
	  * <code>ScanfFormat</code>, and must contain either the
	  * conversion character 's', '[', 'q', 'S', or no conversion
	  * character.  If the conversion character is 's', then the
	  * returned string corresponds to the next non-white-space
	  * sequence of characters found in the input, with preceding
	  * white space skipped.  If the conversion character is '[',
	  * then the returned string corresponds to the next sequence
	  * of characters which match those specified between the '['
	  * and the closing ']' (see the documentation for
	  * <code>ScanfFormat</code>).  If the conversion character is
	  * 'q', then the returned string corresponds to the next
	  * sequence of characters delimited by double quotes, with
	  * preceding white space skipped.  If the conversion
	  * character is 'S', then the returned string corresponds to
	  * the next sequence of characters delimited by either
	  * white-space or double quotes, with preceding white space 
	  * skipped.  If there is no conversion character, then the
	  * input must match the format string, except that white
	  * space in the format may be matched by any amount of white
	  * space, including none, in the input.
	  * 
	  * @param s	Format string
	  * @return Scanned <code>String</code>
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat */
	public String scanString (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanString (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a <code>String</code>, using the default format
	  * string "%s".
	  * 
	  * @param s	Format string
	  * @return Scanned <code>String</code>
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanString(String)
	  */
	public String scanString ()
	   throws IOException, ScanfMatchException
	 {
	   String val = null;
	   try
	    { val = scanString (defaultStringFmt);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	private void getQuotedString(int blimit)
	   throws IOException, ScanfMatchException
	 { 
	   consumeAndReplaceChar();
	   while (curChar != '"' &&
		  curChar != '\n' &&
		  curChar != -1)
	    { if (bcnt<blimit)
	       { buffer[bcnt++] = (char)curChar;
	       }
	      consumeAndReplaceChar();
	    }
	   if (curChar == '\n')
	    { throw new ScanfMatchException (
"New line detected before end of quoted string");
	    }
	   else if (curChar == -1)
	    { throw new ScanfMatchException (
"End of file detected before end of quoted string");
	    }
	   else // curChar == '"'
	    { consumeAndReplaceChar();
	    }
	 }

	private void getWhiteSpaceString(int blimit)
	   throws IOException, ScanfMatchException
	 { 
	   while (!Character.isWhitespace((char)curChar) &&
		  curChar != commentChar &&
		  curChar != -1 && bcnt<blimit)
	    { buffer[bcnt++] = (char)curChar;
	      consumeAndReplaceChar();
	    }
	 }

	/** 
	  * Scan and return a <code>String</code>, using a pre-allocated
	  * <code>ScanfFormat</code> object. 
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned <code>String</code>
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanString(String)
	  */
	public String scanString (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   if (fmt.type == -1)
	    { // then we just match the prefix and return null
	      if (fmt.prefix != null)
	       { matchString (fmt.prefix);
	       }
	      return null;
	    }
	   int blimit = BUFSIZE;
	   if (fmt.width != -1 && fmt.width < blimit)
	    { blimit = fmt.width;
	    }
	   checkTypeAndScanPrefix (fmt, "s[qS");
	   skipWhiteSpace();
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   bcnt = 0;
	   if (fmt.type == 's')
	    { getWhiteSpaceString (blimit);
	    }
	   else if (fmt.type == 'q')
	    { if (curChar != '"')
	       { throw new ScanfMatchException (
"Quoted string not found");
	       }
	      getQuotedString(blimit);
	    }
	   else if (fmt.type == 'S')
	    { if (curChar == '"') 
	       { getQuotedString(blimit);
	       }
	      else
	       { getWhiteSpaceString (blimit); 
	       }
	    }
	   else // fmt.type == '['
	    { while (fmt.matchChar((char)curChar) &&
		     curChar != commentChar &&
		     curChar != -1 && bcnt<blimit)
	       { buffer[bcnt++] = (char)curChar;
		 consumeAndReplaceChar();
	       }
	      if (bcnt==0)
	       { throw new ScanfMatchException (
"Input does not match '[" + fmt.cmatch + "]'"); 
	       }
	    }
	   scanSuffix (fmt);
	   return new String(buffer, 0, bcnt);
	 }

	/** 
	  * Scan and return a <code>boolean</code>.
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion character 'b'. White space
	  * before the conversion is skipped. The function returns true
	  * if the input matches the string <code>"true"</code>, and false
	  * if the input matches the string <code>"false"</code>. Other
	  * inputs will cause a ScanfMatchException to be thrown. Any
	  * field width specification in the format is ignored.
	  * 
	  * @param s	Format string
	  * @return Scanned <code>boolean</code>
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public boolean scanBoolean (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanBoolean (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a <code>boolean</code>, using the default format
	  * string "%b".
	  * 
	  * @param s	Format string
	  * @return Scanned <code>boolean</code>
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanBoolean(String)
	  */
	public boolean scanBoolean ()
	   throws IOException, ScanfMatchException
	 {
	   boolean val = false;
	   try
	    { val = scanBoolean (defaultBooleanFmt);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return a <code>boolean</code>, using a pre-allocated
	  * <code>ScanfFormat</code> object. 
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned <code>boolean</code> value
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanBoolean(String)
	  */
	public boolean scanBoolean (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   boolean retval = false;
	   checkTypeAndScanPrefix (fmt, "b");
	   skipWhiteSpace();
	   if (curChar == 't')
	    { matchKeyword ("true");
	      retval = true;
	    }
	   else if (curChar == 'f')
	    { matchKeyword ("false");
	      retval = false;
	    }
	   else
	    { throw new ScanfMatchException (
"Expecting keywords \"true\" or \"false\"");
	    }
	   scanSuffix (fmt);
	   return retval;
	 }

	/** 
	  * Scan and return a character array, whose size is determined
	  * by the field width specified in the format string (with a
	  * default width of 1 being assumed if no width is specified).
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion characters 'c' or '['. If the
	  * conversion character is '[', then each character scanned must
	  * match the sequence specified between the '[' and the closing
	  * ']' (see the documentation for <code>ScanfFormat</code>).
	  * 
	  * <p>
	  * White space preceding the character sequence is not skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned character array
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public char[] scanChars (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanChars (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a character array, using the default format
	  * string "%c", with the field width (number of characters to 
	  * read) supplanted by the argument <code>n</code>.
	  * 
	  * @param n	Number of characters to read
	  * @return Scanned character array
	  * @throws IllegalArgumentException <code>n</code> not a positive
	  * number
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanChars(String)
	  */
	public char[] scanChars (int n)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   if (n <= 0)
	    { throw new IllegalArgumentException ("n is non-positive");
	    }
	   return scanChars (defaultCharFmt, n);
	 }

	/** 
	  * Scan and return a character array, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned character array
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanChars(String)
	  */
	public char[] scanChars (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   return scanChars (fmt, fmt.width);
	 }

	/** 
	  * Implementing function for scanChars.
	  * 
	  * @see ScanfReader#scanChars(String)
	  */
	private char[] scanChars (ScanfFormat fmt, int w)
	   throws IOException, IllegalArgumentException
	 {
	   if (w == -1)
	    { w = 1;
	    }
	   char[] value = new char[w];
	   checkTypeAndScanPrefix (fmt, "c[");
	   initChar();
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   for (int i=0; i<w; i++)
	    { value[i] = (char)curChar;
	      if (fmt.type == '[' && !fmt.matchChar(value[i]))
	       { throw new ScanfMatchException
("Input char '" + value[i] + "' does not match '[" + fmt.cmatch + "]'");
	       }
	      consumeAndReplaceChar();
	    }
	   scanSuffix (fmt);
	   return value;
	 }

	/** 
	  * Scan and return a single character.
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion character 'c' or '['. If the
	  * conversion character is '[', then each character scanned must
	  * match the sequence specified between the '[' and the closing
	  * ']' (see the documentation for <code>ScanfFormat</code>).
	  * 
	  * <p>
	  * White space preceding the character is not skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned character
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public char scanChar (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanChar (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a single character, using the default format
	  * string "%c".
	  * 
	  * @param s	Format string
	  * @return Scanned character
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanChar(String)
	  */
	public char scanChar ()
	   throws IOException, ScanfMatchException
	 {
	   char val = 0;
	   try
	    { val = scanChar (defaultCharFmt);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return a single character, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned character
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanChar(String)
	  */
	public char scanChar (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   char value = 0;

	   checkTypeAndScanPrefix (fmt, "c[");
	   initChar();
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   value = (char)curChar;
	   if (fmt.type == '[' && !fmt.matchChar(value))
	    { throw new ScanfMatchException
("Input char '" + value + "' does not match '[" + fmt.cmatch + "]'");
	    }
	   if (fmt.suffix == null && value == '\n')
	    { // hack so that we won't block if we've just scanned
	      // a new line from an interactive session
	      consumeChar();
	    }
	   else
	    { consumeAndReplaceChar();
	      scanSuffix (fmt);
	    }
	   return value;
	 }

	/** 
	  * Scan and return a hex (long) integer.
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion character 'x'. The integer itself
	  * must be formed from the characters [0-9a-fA-F], and white space
	  * which immediately precedes it is skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public long scanHex (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanHex (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a hex (long) integer, using the default format
	  * string "%x".
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanHex(String)
	  */
	public long scanHex ()
	   throws IOException, ScanfMatchException
	 {
	   long val = 0;
	   try
	    { val = scanHex (defaultHexFmt, -1);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return a hex (long) integer, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanHex(String)
	  */
	public long scanHex (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   return scanHex (fmt, fmt.width);
	 }

	/** 
	  * Implementing function for scanHex.
	  * 
	  * @see ScanfReader#scanHex(String)
	  */
	private long scanHex (ScanfFormat fmt, int width)
	   throws IOException, IllegalArgumentException
	 {
	   if (width == -1)
	    { width = 1000000000;
	    }
	   long val;
	   int k, i;

	   checkTypeAndScanPrefix (fmt, "x");
	   skipWhiteSpace();
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   if (hexChars.indexOf(curChar) == -1)
	    { throw new ScanfMatchException(
"Malformed hex integer");
	    }
	   val = 0;
	   i = 0;
	   while ((k=hexChars.indexOf(curChar)) != -1 && i<width)
	    { if (k > 15)
	       { k -= 6;
	       }
	      val = val*16+k;
	      consumeAndReplaceChar();
	      i++;
	    }
	   scanSuffix (fmt);
	   return val;
	 }

	/** 
	  * Scan and return an octal (long) integer.
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion character 'o'. The integer itself
	  * must be composed of the digits [0-7], and white space which 
	  * immediately precedes it is skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public long scanOct (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanOct (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return an octal (long) integer, using the default format
	  * string "%o".
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanOct(String)
	  */
	public long scanOct ()
	   throws IOException, ScanfMatchException
	 {
	   long val = 0;
	   try
	    { val = scanOct (defaultOctFmt, -1);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return an octal (long) integer, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanOct(String)
	  */
	public long scanOct (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   return scanOct (fmt, fmt.width);
	 }

	/** 
	  * Implementing function for scanOct.
	  * 
	  * @see ScanfReader#scanOct(String)
	  */
	private long scanOct (ScanfFormat fmt, int width)
	   throws IOException, IllegalArgumentException
	 {
	   if (width == -1)
	    { width = 1000000000;
	    }
	   long val;
	   int k, i;

	   checkTypeAndScanPrefix (fmt, "o");
	   skipWhiteSpace();
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   if (octChars.indexOf(curChar) == -1)
	    { throw new ScanfMatchException(
"Malformed octal integer");
	    }
	   val = 0;
	   i = 0;
	   while ((k=octChars.indexOf(curChar)) != -1 && i<width)
	    { val = val*8+k;
	      consumeAndReplaceChar();
	      i++;
	    }
	   scanSuffix (fmt);
	   return val;
	 }

	/** 
	  * Scan and return a signed decimal (long) integer.
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain the conversion character 'd'.
	  * The integer itself must consist of an optional sign 
	  * ('+' or '-') followed by a sequence of digits. White space
	  * preceding the number is skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  */
	public long scanDec (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanDec (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a signed decimal (long) integer, using the default format
	  * string "%d".
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDec(String)
	  */
	public long scanDec ()
	   throws IOException, ScanfMatchException
	 {
	   long val = 0;
	   try
	    { val = scanDec (defaultDecFmt, -1);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return a signed decimal (long) integer, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDec(String)
	  */
	public long scanDec (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   return scanDec (fmt, fmt.width);
	 }

	/** 
	  * Implementing function for scanDec.
	  * 
	  * @see ScanfReader#scanDec(String)
	  */
	private long scanDec (ScanfFormat fmt, int width)
	   throws IOException, IllegalArgumentException
	 {
	   if (width == -1)
	    { width = 1000000000;
	    }
	   long val;
	   int k, i;

	   boolean negate = false;

	   checkTypeAndScanPrefix (fmt, "d");
	   skipWhiteSpace();
	   if (curChar == '-' || curChar == '+')
	    { negate = (curChar == '-');
	      consumeAndReplaceChar();
	    }
	   if (curChar == -1)
	    { throw new EOFException("EOF");
	    }
	   if (!Character.isDigit((char)curChar))
	    { throw new ScanfMatchException(
"Malformed decimal integer");
	    }
	   val = 0;
	   i = 0;
	   while (Character.isDigit((char)curChar) && i<width)
	    { val = val*10+(curChar-'0');
	      consumeAndReplaceChar();
	      i++;
	    }
	   if (negate)
	    { val = -val;
	    }
	   scanSuffix (fmt);
	   return val;
	 }

	/** 
	  * Scan and return a signed integer.
	  * 
	  * <p>
	  * The format string <code>s</code> must have the form described
	  * by the documentation for the class <code>ScanfFormat</code>,
	  * and must contain one of the conversion characters "doxi".
	  *
	  * <p>
	  * Specifying the conversion characters 'd', 'o', or 'x' is 
	  * equivalent to calling (int versions of) <code>scanDec</code>,
	  * <code>scanOct</code>, and <code>scanHex</code>, respectively.
	  * 
	  * <p>
	  * If the conversion character is 'i', then after an optional
	  * sign ('+' or '-'), if the number begins with an
	  * <code>0x</code>, then it is scanned as a hex number; 
	  * if it begins with an <code>0</code>, then it is scanned as an
	  * octal number, and otherwise it is scanned as a decimal number.
	  * White space preceding the number is skipped.
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanDec(String)
	  * @see ScanfReader#scanOct(String)
	  * @see ScanfReader#scanHex(String)
	  */
	public int scanInt (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return (int)scanLong (s);
	 }

	/** 
	  * Scan and return a signed integer, using the default format
	  * string "%i".
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanInt(String)
	  */
	public int scanInt ()
	   throws IOException, ScanfMatchException
	 {
	   return (int)scanLong();
	 }

	/** 
	  * Scan and return a signed integer, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * This saves the overhead of parsing the format from a string.
	  * 
	  * @param fmt	Format object
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanInt(String)
	  */
	public int scanInt (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   return (int)scanLong (fmt);
	 }

	/** 
	  * Scan and return a signed (long) integer. Functionality
	  * is identical to that for <code>scanInt(String)</code>.
	  *
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfReader#scanInt(String)
	  */
	public long scanLong (String s)
	   throws IOException, ScanfMatchException, IllegalArgumentException
	 {
	   return scanLong (new ScanfFormat(s));
	 }

	/** 
	  * Scan and return a signed (long) integer, using the default format
	  * string "%i".
	  * 
	  * @param s	Format string
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfFormat
	  * @see ScanfReader#scanInt(String)
	  */
	public long scanLong ()
	   throws IOException, ScanfMatchException
	 {
	   long val = 0;
	   try
	    { val = scanLong (defaultIntFmt);
	    }
	   catch (IllegalArgumentException e)
	    { // can't happen
	    }
	   return val;
	 }

	/** 
	  * Scan and return a signed (long) integer, using a pre-allocated
	  * <code>ScanfFormat</code> object.
	  * 
	  * @param fmt	Format object
	  * @return Scanned integer
	  * @throws ScanfMatchException Input did not match format
	  * @throws IllegalArgumentException Error in format specification
	  * @throws java.io.EOFException End of file
	  * @throws java.io.IOException Other input error
	  * @see ScanfReader#scanInt(String)
	  */
	public long scanLong (ScanfFormat fmt)
	   throws IOException, IllegalArgumentException
	 {
	   if (fmt.type == 'd')
	    { return scanDec (fmt);
	    }
	   else if (fmt.type == 'x')
	    { return scanHex (fmt);
	    }
	   else if (fmt.type == 'o')
	    { return scanOct (fmt);
	    }
	   else // fmt.type == 'i';
	    { long val = 0;
	      int sign = 1;
	      int ccnt = 0;
	      int width = fmt.width;
	      if (width == -1)
	       { width = 1000000000;
	       }

	      checkTypeAndScanPrefix (fmt, "i");
	      skipWhiteSpace();
	      if (curChar == '-' || curChar == '+')
	       { if (width == 1)
		  { throw new ScanfMatchException("Malformed integer");
		  }
		 if (curChar == '-')
		  { sign = -1;
		  }
		 consumeAndReplaceChar();
		 ccnt++;
	       }
	      if (curChar == -1)
	       { throw new EOFException("EOF");
	       }
	      if (curChar == '0')
	       { consumeAndReplaceChar();
		 ccnt++;
		 if (ccnt == width)
		  { val = 0;
		  }
		 else
		  { if (curChar == 'x' || curChar == 'X')
		     { if (ccnt+1 == width)
			{ throw new ScanfMatchException(
"Malformed hex integer");
			}
		       else
			{ consumeAndReplaceChar();
			  ccnt++;
			  if (Character.isWhitespace((char)curChar))
			   { throw new ScanfMatchException(
"Malformed hex integer");
			   }
			  else
			   { val = scanHex (defaultHexFmt, width-ccnt);
			   }
			}
		     }
		    else
		     { if (Character.isWhitespace((char)curChar))
			{ val = 0;
			}
		       else
			{ val = scanOct (defaultOctFmt, width-ccnt);
			}
		     }
		  }
	       }
	      else
	       { // scan unsigned decimal integer
		 int i = 0;
		 val = 0;

		 if (!Character.isDigit((char)curChar))
		  { throw new ScanfMatchException(
"Malformed decimal integer");
		  }
		 while (Character.isDigit((char)curChar) && i<width-ccnt)
		  { val = val*10+(curChar-'0');
		    consumeAndReplaceChar();
		    i++;
		  }
	       }
	      scanSuffix (fmt);
	      return sign*val;
	    }
	 }

	/** 
	  * Gets the current character number (equal to the
	  * number of characters that have been consumed by
	  * the stream). 
	  * 
	  * @return Current character number
	  * @see ScanfReader#setCharNumber
	  */
	public int getCharNumber()
	 { return charCnt;
	 }

	/** 
	  * Sets the current character number.
	  * 
	  * @param n New character number
	  * @see ScanfReader#getCharNumber
	  */
	public void setCharNumber(int n)
	 { charCnt = n;
	 }

	/** 
	  * Gets the current line number. The initial
	  * value (when the Reader is created) is 1.
	  * A new line is recorded upon reading a carriage return, a line
	  * feed, or a carriage return immediately followed by a line feed.
	  * 
	  * @return Current line number
	  * @see ScanfReader#setLineNumber
	  */
	public int getLineNumber()
	 { return lineCnt;
	 }

	/** 
	  * Sets the current line number.
	  * 
	  * @param n New line number
	  * @see ScanfReader#setLineNumber
	  */
	public void setLineNumber(int n)
	 { lineCnt = n;
	 }

	/** 
	  * Returns whether or not a look-ahead character
	  * is currently begin stored.
	  * 
	  * @return True if a look-ahead character is being stored.
	  */
	public boolean lookAheadCharValid()
	 {
	   return curCharValid;
	 }

	/** 
	  * Returns the look-ahead character.
	  * 
	  * @return Look-ahead character, -1 if EOF has been reached, or 0
	  * if no look-ahead character is being stored.
	  */
	public int getLookAheadChar()
	 {
	   if (curCharValid)
	    { return curChar;
	    }
	   else
	    { return 0;
	    }
	 }

	/** 
	  * Clears the look-ahead character.
	  */
	public void clearLookAheadChar()
	 {
	   curCharValid = false;
	 }

	/**
	 * Gets the comment character. A value of
	 * -1 means that no comment character is set.
	 *
	 * @return Comment character
	 * @see ScanfReader#setCommentChar
	 */
	public int getCommentChar ()
	 { return (commentChar == NO_COMMENT) ? -1 : commentChar;
	 }

	/**
	 * Sets the comment character. When encountered between
	 * scanned object, a comment character causes all input
	 * from itself to the end of the current line to be ignored as
	 * though it were whitespace. A comment character will also
	 * terminate the scanning of a string. Otherwise, comment
	 * characters are ignored within scanned objects, although
	 * if typical comment characters such as <code>%</code>
	 * or <code>#</code> are used, this is unlikely ot be of
	 * consequence. A value of -1 disables
	 * the comment character.
	 *
         * @param c New comment character
	 * @see ScanfReader#getCommentChar
	 */
	public void setCommentChar (int c)
	 {
	   commentChar = (c == -1) ? NO_COMMENT : c;
	 }
}
