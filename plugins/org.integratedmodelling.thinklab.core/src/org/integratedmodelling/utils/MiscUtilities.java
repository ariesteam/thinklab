/**
 * MiscUtilities.java
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
package org.integratedmodelling.utils;
/*
 * MiscUtilities.java - Various miscallaneous utility functions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov
 * Portions copyright (C) 2000 Richard S. Hall
 * Portions copyright (C) 2001 Dirk Moebius
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

//{{{ Imports

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.Vector;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.java.plugin.Plugin;

//}}}

/**
 * Path name manipulation, string manipulation, and more.<p>
 *
 * The most frequently used members of this class are:<p>
 *
 * <b>Some path name methods:</b><p>
 * <ul>
 * <li>{@link #getFileName(String)}</li>
 * <li>{@link #getParentOfPath(String)}</li>
 * <li>{@link #constructPath(String,String)}</li>
 * </ul>
 * <b>String comparison:</b><p>

 * A {@link #compareStrings(String,String,boolean)} method that unlike
 * <function>String.compareTo()</function>, correctly recognizes and handles
 * embedded numbers.<p>
 *
 * This class also defines several inner classes for use with the
 * sorting features of the Java collections API:
 *
 * <ul>
 * <li>{@link MiscUtilities.StringCompare}</li>
 * <li>{@link MiscUtilities.StringICaseCompare}</li>
 * <li>{@link MiscUtilities.MenuItemCompare}</li>
 * </ul>
 *
 * For example, you might call:<p>
 *
 * <code>Arrays.sort(myListOfStrings,
 *     new MiscUtilities.StringICaseCompare());</code>
 *
 * @author Slava Pestov
 * @author John Gellene (API documentation)
 * @version $Id: MiscUtilities.java,v 1.8 2006/11/23 02:47:31 fvilla Exp $
 */
public class MiscUtilities{    
	

	private static Collection<Class<?>> findSubclasses(ArrayList<Class<?>> ret, Class<?> mainClass, String pckgname, ClassLoader cloader) {

		if (ret == null)
			ret = new ArrayList<Class<?>>();

		// Translate the package name into an absolute path
		String name = new String(pckgname).replace('.', '/');

		// Get a File object for the package
		URL url = cloader.getResource(name);
		
		if (url == null)
			return ret;
		
		File directory = new File(Escape.fromURL(url.getFile()));

		if (directory.exists()) {

			// Get the list of the files contained in the package
			String[] files = directory.list();

			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					String classname = files[i].substring(0,
							files[i].length() - 6);
					try {
						Class<?> clls = Class.forName(pckgname + "." + classname, true, cloader);
						if (mainClass.isAssignableFrom(clls)) {
							ret.add(clls);
						}
					} catch (ClassNotFoundException e) {
						Thinklab.get().logger().warn("task class " + pckgname + "." + classname + " could not be created: " + e.getMessage());
					}
				} else {
					
					File ff = new File(Escape.fromURL(url.getFile()) + "/" + files[i]);
					
					if (ff.isDirectory()) {
						String ppk = pckgname + "." + files[i];
						findSubclasses(ret, mainClass, ppk, cloader);
					}
				}				
			}
		}

		return ret;
	}

	
	/**
	 * Return all subclasses of given class in given package. Uses file structure in 
	 * classpath as seen by passed classloader. Loads ALL classes in package in 
	 * the process. Use with caution - it's sort of dirty, but it's the only way to obtain
	 * the class structure without preloading classes.
	 * 
	 * @param mainClass
	 * @param pckgname
	 * @return
	 */
	public static Collection<Class<?>> findSubclasses(Class<?> mainClass, String pckgname, ClassLoader cloader) {
		return findSubclasses(null, mainClass, pckgname, cloader);
	}

	/**
	 * This encoding is not supported by Java, yet it is useful.
	 * A UTF-8 file that begins with 0xEFBBBF.
	 */
	public static final String UTF_8_Y = "UTF-8Y";

	
	 static public boolean deleteDirectory(File path) {

		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	public static String getFileExtension(String s) {

		String ret = "";
		
		int sl = s.lastIndexOf(".");
		if (sl > 0)
			ret = s.substring(sl+1);
			
		return ret;
	}
	
	/**
	 * Return file name with no path or extension
	 * @param ss
	 * @return
	 */
	public static String getFileBaseName(String s) {

		String ret = s;

		int sl = ret.lastIndexOf(".");
		if (sl > 0)
			ret = ret.substring(0, sl);
		sl = ret.lastIndexOf(File.separator);
		if (sl > 0)
			ret = ret.substring(sl+1);
		
		return ret;
	}

	/**
	 * Return URL base name with no path or extension. Just like getFileBaseName but uses / instead
	 * of system separator.
	 * @param ss
	 * @return
	 */
	public static String getURLBaseName(String s) {

		/* just in case */
		String ret = s.replace('\\', '/');

		int sl = ret.lastIndexOf(".");
		if (sl > 0)
			ret = ret.substring(0, sl);
		sl = ret.lastIndexOf("/");
		if (sl >= 0)
			ret = ret.substring(sl+1);
		
		return ret;
	}

	/**
	 * Return file name with no path but with extension
	 * @param ss
	 * @return
	 */
	public static String getFileName(String s) {

		String ret = s;

		int sl = ret.lastIndexOf(File.separator);
		if (sl < 0)
			sl = ret.lastIndexOf('/');
		if (sl > 0)
			ret = ret.substring(sl+1);
		
		return ret;
	}
	
	  /**
	   * Writes InputStream to a given <code>fileName<code>.
	   * And, if directory for this file does not exists,
	   * if createDir is true, creates it, otherwice throws OMDIOexception.
	   *
	   * @param fileName - filename save to.
	   * @param iStream  - InputStream with data to read from.
	   * @param createDir (false by default)
	   * @throws IOException in case of any error.
	   *
	   * @refactored 2002-05-02 by Alexander Feldman
	   * - moved from OMDBlob.
	   *
	   */
	  public static void writeToFile(String fileName, InputStream iStream,
	    boolean createDir)
	    throws IOException
	  {
	    String me = "FileUtils.WriteToFile";
	    if (fileName == null)
	    {
	      throw new IOException(me + ": filename is null");
	    }
	    
	    File theFile = new File(fileName);

	    // Check if a file exists.
	    if (theFile.exists())
	    {
	       String msg =
	         theFile.isDirectory() ? "directory" :
	         (! theFile.canWrite() ? "not writable" : null);
	       if (msg != null)
	       {
	         throw new IOException(me + ": file '" + fileName + "' is " + msg);
	       }
	    }

	    // Create directory for the file, if requested.
	    if (createDir && theFile.getParentFile() != null)
	    {
	      theFile.getParentFile().mkdirs();
	    }

	    // Save InputStream to the file.
	    BufferedOutputStream fOut = null;
	    try
	    {
	      fOut = new BufferedOutputStream(new FileOutputStream(theFile));
	      byte[] buffer = new byte[32 * 1024];
	      int bytesRead = 0;
	      if (iStream != null) {
	    	  while ((bytesRead = iStream.read(buffer)) != -1) {
	    		  fOut.write(buffer, 0, bytesRead);
	    	  }
	      }
	    } catch (Exception e) {
	    	throw new IOException(me + " failed, got: " + e.toString());
	     } finally {
	    	  if (iStream != null)
	    		  iStream.close();
	    	  fOut.close();    	  
	      }
	  }
	
	  /**
	   * Closes InputStream and/or OutputStream.
	   * It makes sure that both streams tried to be closed,
	   * even first throws an exception.
	   *
	   * @throw IOException if stream (is not null and) cannot be closed.
	   *
	   */
	  protected static void close(InputStream iStream, OutputStream oStream)
	    throws IOException
	  {
	    try
	    {
	      if (iStream != null)
	      {
	        iStream.close();
	      }
	    }
	    finally
	    {
	      if (oStream != null)
	      {
	        oStream.close();
	      }
	    }
	  }

	//{{{ getProtocolOfURL() method
	/**
	 * Returns the protocol specified by a URL.
	 * @param url The URL
	 * @since jEdit 2.6pre5
	 */
	public static String getProtocolOfURL(String url)
	{
		return url.substring(0,url.indexOf(':'));
	} //}}}

	//{{{ closeQuietly() method
	/**
	 * Method that will close an {@link InputStream} ignoring it if it is null and ignoring exceptions.
	 *
	 * @param in the InputStream to close.
	 * @since jEdit 4.3pre3
	 */
	public static void closeQuietly(InputStream in)
	{
		if(in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				//ignore
			}
		}
	} //}}}

	//{{{ copyStream() method
	/**
	 * Method that will close an {@link OutputStream} ignoring it if it is null and ignoring exceptions.
	 *
	 * @param out the OutputStream to close.
	 * @since jEdit 4.3pre3
	 */
	public static void closeQuietly(OutputStream out)
	{
		if(out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				//ignore
			}
		}
	} //}}}

	//{{{ fileToClass() method
	/**
	 * Converts a file name to a class name. All slash characters are
	 * replaced with periods and the trailing '.class' is removed.
	 * @param name The file name
	 */
	public static String fileToClass(String name)
	{
		char[] clsName = name.toCharArray();
		for(int i = clsName.length - 6; i >= 0; i--)
			if(clsName[i] == '/')
				clsName[i] = '.';
		return new String(clsName,0,clsName.length - 6);
	} //}}}

	public static String getNameFromURL(String uu) {
		int sl = uu.lastIndexOf('/');
		String name = sl == -1 ? uu : uu.substring(sl+1);
		int dt = name.lastIndexOf('.');
		return dt == -1 ? name : name.substring(0, dt);
	}
	
	//{{{ classToFile() method
	/**
	 * Converts a class name to a file name. All periods are replaced
	 * with slashes and the '.class' extension is added.
	 * @param name The class name
	 */
	public static String classToFile(String name)
	{
		return name.replace('.','/').concat(".class");
	} //}}}

//	//{{{ pathsEqual() method
//	/**
//	 * @param p1 A path name
//	 * @param p2 A path name
//	 * @return True if both paths are equal, ignoring trailing slashes, as
//	 * well as case insensitivity on Windows.
//	 * @since jEdit 4.3pre2
//	 */
//	public static boolean pathsEqual(String p1, String p2)
//	{
//		VFS v1 = VFSManager.getVFSForPath(p1);
//		VFS v2 = VFSManager.getVFSForPath(p2);
//
//		if(v1 != v2)
//			return false;
//
//		if(p1.endsWith("/") || p1.endsWith(File.separator))
//			p1 = p1.substring(0,p1.length() - 1);
//
//		if(p2.endsWith("/") || p2.endsWith(File.separator))
//			p2 = p2.substring(0,p2.length() - 1);
//
//		if((v1.getCapabilities() & VFS.CASE_INSENSITIVE_CAP) != 0)
//			return p1.equalsIgnoreCase(p2);
//		else
//			return p1.equals(p2);
//	} //}}}

	//}}}

	//{{{ Text methods

	//{{{ getLeadingWhiteSpace() method
	/**
	 * Returns the number of leading white space characters in the
	 * specified string.
	 * @param str The string
	 */
	public static int getLeadingWhiteSpace(String str)
	{
		int whitespace = 0;
loop:		for(;whitespace < str.length();)
		{
			switch(str.charAt(whitespace))
			{
			case ' ': case '\t':
				whitespace++;
				break;
			default:
				break loop;
			}
		}
		return whitespace;
	} //}}}

	//{{{ getTrailingWhiteSpace() method
	/**
	 * Returns the number of trailing whitespace characters in the
	 * specified string.
	 * @param str The string
	 * @since jEdit 2.5pre5
	 */
	public static int getTrailingWhiteSpace(String str)
	{
		int whitespace = 0;
loop:		for(int i = str.length() - 1; i >= 0; i--)
		{
			switch(str.charAt(i))
			{
			case ' ': case '\t':
				whitespace++;
				break;
			default:
				break loop;
			}
		}
		return whitespace;
	} //}}}

	//{{{ getLeadingWhiteSpaceWidth() method
	/**
	 * Returns the width of the leading white space in the specified
	 * string.
	 * @param str The string
	 * @param tabSize The tab size
	 */
	public static int getLeadingWhiteSpaceWidth(String str, int tabSize)
	{
		int whitespace = 0;
loop:		for(int i = 0; i < str.length(); i++)
		{
			switch(str.charAt(i))
			{
			case ' ':
				whitespace++;
				break;
			case '\t':
				whitespace += (tabSize - whitespace % tabSize);
				break;
			default:
				break loop;
			}
		}
		return whitespace;
	} //}}}

//	//{{{ getVirtualWidth() method
//	/**
//	 * Returns the virtual column number (taking tabs into account) of the
//	 * specified offset in the segment.
//	 *
//	 * @param seg The segment
//	 * @param tabSize The tab size
//	 * @since jEdit 4.1pre1
//	 */
//	public static int getVirtualWidth(Segment seg, int tabSize)
//	{
//		int virtualPosition = 0;
//
//		for (int i = 0; i < seg.count; i++)
//		{
//			char ch = seg.array[seg.offset + i];
//
//			if (ch == '\t')
//			{
//				virtualPosition += tabSize
//					- (virtualPosition % tabSize);
//			}
//			else
//			{
//				++virtualPosition;
//			}
//		}
//
//		return virtualPosition;
//	} //}}}
//
//	//{{{ getOffsetOfVirtualColumn() method
//	/**
//	 * Returns the array offset of a virtual column number (taking tabs
//	 * into account) in the segment.
//	 *
//	 * @param seg The segment
//	 * @param tabSize The tab size
//	 * @param column The virtual column number
//	 * @param totalVirtualWidth If this array is non-null, the total
//	 * virtual width will be stored in its first location if this method
//	 * returns -1.
//	 *
//	 * @return -1 if the column is out of bounds
//	 *
//	 * @since jEdit 4.1pre1
//	 */
//	public static int getOffsetOfVirtualColumn(Segment seg, int tabSize,
//		int column, int[] totalVirtualWidth)
//	{
//		int virtualPosition = 0;
//
//		for (int i = 0; i < seg.count; i++)
//		{
//			char ch = seg.array[seg.offset + i];
//
//			if (ch == '\t')
//			{
//				int tabWidth = tabSize
//					- (virtualPosition % tabSize);
//				if(virtualPosition >= column)
//					return i;
//				else
//					virtualPosition += tabWidth;
//			}
//			else
//			{
//				if(virtualPosition >= column)
//					return i;
//				else
//					++virtualPosition;
//			}
//		}
//
//		if(totalVirtualWidth != null)
//			totalVirtualWidth[0] = virtualPosition;
//		return -1;
//	} //}}}

	//{{{ createWhiteSpace() method
	/**
	 * Creates a string of white space with the specified length.<p>
	 *
	 * To get a whitespace string tuned to the current buffer's
	 * settings, call this method as follows:
	 *
	 * <pre>myWhitespace = MiscUtilities.createWhiteSpace(myLength,
	 *     (buffer.getBooleanProperty("noTabs") ? 0
	 *     : buffer.getTabSize()));</pre>
	 *
	 * @param len The length
	 * @param tabSize The tab size, or 0 if tabs are not to be used
	 */
	public static String createWhiteSpace(int len, int tabSize)
	{
		return createWhiteSpace(len,tabSize,0);
	} //}}}

	//{{{ createWhiteSpace() method
	/**
	 * Creates a string of white space with the specified length.<p>
	 *
	 * To get a whitespace string tuned to the current buffer's
	 * settings, call this method as follows:
	 *
	 * <pre>myWhitespace = MiscUtilities.createWhiteSpace(myLength,
	 *     (buffer.getBooleanProperty("noTabs") ? 0
	 *     : buffer.getTabSize()));</pre>
	 *
	 * @param len The length
	 * @param tabSize The tab size, or 0 if tabs are not to be used
	 * @param start The start offset, for tab alignment
	 * @since jEdit 4.2pre1
	 */
	public static String createWhiteSpace(int len, int tabSize, int start)
	{
		StringBuffer buf = new StringBuffer();
		if(tabSize == 0)
		{
			while(len-- > 0)
				buf.append(' ');
		}
		else if(len == 1)
			buf.append(' ');
		else
		{
			int count = (len + start % tabSize) / tabSize;
			if(count != 0)
				len += start;
			while(count-- > 0)
				buf.append('\t');
			count = len % tabSize;
			while(count-- > 0)
				buf.append(' ');
		}
		return buf.toString();
	} //}}}

	//{{{ globToRE() method
	/**
	 * Converts a Unix-style glob to a regular expression.<p>
	 *
	 * ? becomes ., * becomes .*, {aa,bb} becomes (aa|bb).
	 * @param glob The glob pattern
	 */
	public static String globToRE(String glob)
	{
		final Object NEG = new Object();
		final Object GROUP = new Object();
		Stack state = new Stack();

		StringBuffer buf = new StringBuffer();
		boolean backslash = false;

		for(int i = 0; i < glob.length(); i++)
		{
			char c = glob.charAt(i);
			if(backslash)
			{
				buf.append('\\');
				buf.append(c);
				backslash = false;
				continue;
			}

			switch(c)
			{
			case '\\':
				backslash = true;
				break;
			case '?':
				buf.append('.');
				break;
			case '.':
			case '+':
			case '(':
			case ')':
				buf.append('\\');
				buf.append(c);
				break;
			case '*':
				buf.append(".*");
				break;
			case '|':
				if(backslash)
					buf.append("\\|");
				else
					buf.append('|');
				break;
			case '{':
				buf.append('(');
				if(i + 1 != glob.length() && glob.charAt(i + 1) == '!')
				{
					buf.append('?');
					state.push(NEG);
				}
				else
					state.push(GROUP);
				break;
			case ',':
				if(!state.isEmpty() && state.peek() == GROUP)
					buf.append('|');
				else
					buf.append(',');
				break;
			case '}':
				if(!state.isEmpty())
				{
					buf.append(")");
					if(state.pop() == NEG)
						buf.append(".*");
				}
				else
					buf.append('}');
				break;
			default:
				buf.append(c);
			}
		}

		return buf.toString();
	} //}}}

	//{{{ escapesToChars() method
	/**
	 * Converts "\n" and "\t" escapes in the specified string to
	 * newlines and tabs.
	 * @param str The string
	 * @since jEdit 2.3pre1
	 */
	public static String escapesToChars(String str)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			switch(c)
			{
			case '\\':
				if(i == str.length() - 1)
				{
					buf.append('\\');
					break;
				}
				c = str.charAt(++i);
				switch(c)
				{
				case 'n':
					buf.append('\n');
					break;
				case 't':
					buf.append('\t');
					break;
				default:
					buf.append(c);
					break;
				}
				break;
			default:
				buf.append(c);
			}
		}
		return buf.toString();
	} //}}}

	//{{{ charsToEscapes() method
	/**
	 * Escapes newlines, tabs, backslashes, and quotes in the specified
	 * string.
	 * @param str The string
	 * @since jEdit 2.3pre1
	 */
	public static String charsToEscapes(String str)
	{
		return charsToEscapes(str,"\n\t\\\"'");
	} //}}}

	//{{{ charsToEscapes() method
	/**
	 * Escapes the specified characters in the specified string.
	 * @param str The string
	 * @param toEscape Any characters that require escaping
	 * @since jEdit 4.1pre3
	 */
	public static String charsToEscapes(String str, String toEscape)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if(toEscape.indexOf(c) != -1)
			{
				if(c == '\n')
					buf.append("\\n");
				else if(c == '\t')
					buf.append("\\t");
				else
				{
					buf.append('\\');
					buf.append(c);
				}
			}
			else
				buf.append(c);
		}
		return buf.toString();
	} //}}}

	//{{{ compareVersions() method
	/**
	 * @deprecated Call <code>compareStrings()</code> instead
	 */
	public static int compareVersions(String v1, String v2)
	{
		return compareStrings(v1,v2,false);
	} //}}}

	//{{{ compareStrings() method
	/**
	 * Compares two strings.<p>
	 *
	 * Unlike <function>String.compareTo()</function>,
	 * this method correctly recognizes and handles embedded numbers.
	 * For example, it places "My file 2" before "My file 10".<p>
	 *
	 * @param str1 The first string
	 * @param str2 The second string
	 * @param ignoreCase If true, case will be ignored
	 * @return negative If str1 &lt; str2, 0 if both are the same,
	 * positive if str1 &gt; str2
	 * @since jEdit 4.0pre1
	 */
	public static int compareStrings(String str1, String str2, boolean ignoreCase)
	{
		char[] char1 = str1.toCharArray();
		char[] char2 = str2.toCharArray();

		int len = Math.min(char1.length,char2.length);

		for(int i = 0, j = 0; i < len && j < len; i++, j++)
		{
			char ch1 = char1[i];
			char ch2 = char2[j];
			if(Character.isDigit(ch1) && Character.isDigit(ch2)
				&& ch1 != '0' && ch2 != '0')
			{
				int _i = i + 1;
				int _j = j + 1;

				for(; _i < char1.length; _i++)
				{
					if(!Character.isDigit(char1[_i]))
					{
						//_i--;
						break;
					}
				}

				for(; _j < char2.length; _j++)
				{
					if(!Character.isDigit(char2[_j]))
					{
						//_j--;
						break;
					}
				}

				int len1 = _i - i;
				int len2 = _j - j;
				if(len1 > len2)
					return 1;
				else if(len1 < len2)
					return -1;
				else
				{
					for(int k = 0; k < len1; k++)
					{
						ch1 = char1[i + k];
						ch2 = char2[j + k];
						if(ch1 != ch2)
							return ch1 - ch2;
					}
				}

				i = _i - 1;
				j = _j - 1;
			}
			else
			{
				if(ignoreCase)
				{
					ch1 = Character.toLowerCase(ch1);
					ch2 = Character.toLowerCase(ch2);
				}

				if(ch1 != ch2)
					return ch1 - ch2;
			}
		}

		return char1.length - char2.length;
	} //}}}

	//{{{ stringsEqual() method
	/**
	 * @deprecated Call <code>objectsEqual()</code> instead.
	 */
	public static boolean stringsEqual(String s1, String s2)
	{
		return objectsEqual(s1,s2);
	} //}}}

	//{{{ objectsEqual() method
	/**
	 * Returns if two strings are equal. This correctly handles null pointers,
	 * as opposed to calling <code>o1.equals(o2)</code>.
	 * @since jEdit 4.2pre1
	 */
	public static boolean objectsEqual(Object o1, Object o2)
	{
		if(o1 == null)
		{
			if(o2 == null)
				return true;
			else
				return false;
		}
		else if(o2 == null)
			return false;
		else
			return o1.equals(o2);
	} //}}}

	//{{{ charsToEntities() method
	/**
	 * Converts &lt;, &gt;, &amp; in the string to their HTML entity
	 * equivalents.
	 * @param str The string
	 * @since jEdit 4.2pre1
	 */
	public static String charsToEntities(String str)
	{
		StringBuffer buf = new StringBuffer(str.length());
		for(int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			switch(ch)
			{
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case '&':
				buf.append("&amp;");
				break;
			default:
				buf.append(ch);
				break;
			}
		}
		return buf.toString();
	} //}}}

	//{{{ formatFileSize() method
	public static final DecimalFormat KB_FORMAT = new DecimalFormat("#.# KB");
	public static final DecimalFormat MB_FORMAT = new DecimalFormat("#.# MB");

	/**
	 * Formats the given file size into a nice string (123 bytes, 10.6 KB,
	 * 1.2 MB).
	 * @param length The size
	 * @since jEdit 4.2pre1
	 */
	public static String formatFileSize(long length)
	{
		if(length < 1024)
			return length + " bytes";
		else if(length < 1024*1024)
			return KB_FORMAT.format((double)length / 1024);
		else
			return MB_FORMAT.format((double)length / 1024 / 1024);
	} //}}}

	//{{{ getLongestPrefix() method
	/**
	 * Returns the longest common prefix in the given set of strings.
	 * @param str The strings
	 * @param ignoreCase If true, case insensitive
	 * @since jEdit 4.2pre2
	 */
	public static String getLongestPrefix(List str, boolean ignoreCase)
	{
		if(str.size() == 0)
			return "";

		int prefixLength = 0;

loop:		for(;;)
		{
			String s = str.get(0).toString();
			if(prefixLength >= s.length())
				break loop;
			char ch = s.charAt(prefixLength);
			for(int i = 1; i < str.size(); i++)
			{
				s = str.get(i).toString();
				if(prefixLength >= s.length())
					break loop;
				if(!compareChars(s.charAt(prefixLength),ch,ignoreCase))
					break loop;
			}
			prefixLength++;
		}

		return str.get(0).toString().substring(0,prefixLength);
	} //}}}

	//{{{ getLongestPrefix() method
	/**
	 * Returns the longest common prefix in the given set of strings.
	 * @param str The strings
	 * @param ignoreCase If true, case insensitive
	 * @since jEdit 4.2pre2
	 */
	public static String getLongestPrefix(String[] str, boolean ignoreCase)
	{
		return getLongestPrefix((Object[])str,ignoreCase);
	} //}}}

	//{{{ getLongestPrefix() method
	/**
	 * Returns the longest common prefix in the given set of strings.
	 * @param str The strings (calls <code>toString()</code> on each object)
	 * @param ignoreCase If true, case insensitive
	 * @since jEdit 4.2pre6
	 */
	public static String getLongestPrefix(Object[] str, boolean ignoreCase)
	{
		if(str.length == 0)
			return "";

		int prefixLength = 0;

		String first = str[0].toString();

loop:		for(;;)
		{
			if(prefixLength >= first.length())
				break loop;
			char ch = first.charAt(prefixLength);
			for(int i = 1; i < str.length; i++)
			{
				String s = str[i].toString();
				if(prefixLength >= s.length())
					break loop;
				if(!compareChars(s.charAt(prefixLength),ch,ignoreCase))
					break loop;
			}
			prefixLength++;
		}

		return first.substring(0,prefixLength);
	} //}}}

	//}}}

	//{{{ Sorting methods

	//{{{ quicksort() method
	/**
	 * Sorts the specified array. Equivalent to calling
	 * <code>Arrays.sort()</code>.
	 * @param obj The array
	 * @param compare Compares the objects
	 * @since jEdit 4.0pre4
	 */
	public static void quicksort(Object[] obj, Comparator compare)
	{
		Arrays.sort(obj,compare);
	} //}}}

	//{{{ quicksort() method
	/**
	 * Sorts the specified vector.
	 * @param vector The vector
	 * @param compare Compares the objects
	 * @since jEdit 4.0pre4
	 */
	public static void quicksort(Vector vector, Comparator compare)
	{
		Collections.sort(vector,compare);
	} //}}}

	//{{{ quicksort() method
	/**
	 * Sorts the specified list.
	 * @param list The list
	 * @param compare Compares the objects
	 * @since jEdit 4.0pre4
	 */
	public static void quicksort(List list, Comparator compare)
	{
		Collections.sort(list,compare);
	} //}}}

	//{{{ quicksort() method
	/**
	 * Sorts the specified array. Equivalent to calling
	 * <code>Arrays.sort()</code>.
	 * @param obj The array
	 * @param compare Compares the objects
	 */
	public static void quicksort(Object[] obj, Compare compare)
	{
		Arrays.sort(obj,compare);
	} //}}}

	//{{{ quicksort() method
	/**
	 * Sorts the specified vector.
	 * @param vector The vector
	 * @param compare Compares the objects
	 */
	public static void quicksort(Vector vector, Compare compare)
	{
		Collections.sort(vector,compare);
	} //}}}

	//{{{ Compare interface
	/**
	 * An interface for comparing objects. This is a hold-over from
	 * they days when jEdit had its own sorting API due to JDK 1.1
	 * compatibility requirements. Use <code>java.util.Comparable</code>
	 * instead.
	 */
	public interface Compare extends Comparator
	{
		int compare(Object obj1, Object obj2);
	} //}}}

	//{{{ StringCompare class
	/**
	 * Compares strings.
	 */
	public static class StringCompare implements Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return compareStrings(obj1.toString(),
				obj2.toString(),false);
		}
	} //}}}

	//{{{ StringICaseCompare class
	/**
	 * Compares strings ignoring case.
	 */
	public static class StringICaseCompare implements Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return compareStrings(obj1.toString(),
				obj2.toString(),true);
		}
	} //}}}

	
	//{{{ buildToVersion() method
	/**
	 * Converts an internal version number (build) into a
	 * `human-readable' form.
	 * @param build The build
	 */
	public static String buildToVersion(String build)
	{
		if(build.length() != 11)
			return "<unknown version: " + build + ">";
		// First 2 chars are the major version number
		int major = Integer.parseInt(build.substring(0,2));
		// Second 2 are the minor number
		int minor = Integer.parseInt(build.substring(3,5));
		// Then the pre-release status
		int beta = Integer.parseInt(build.substring(6,8));
		// Finally the bug fix release
		int bugfix = Integer.parseInt(build.substring(9,11));

		return major + "." + minor
			+ (beta != 99 ? "pre" + beta :
			(bugfix != 0 ? "." + bugfix : "final"));
	} //}}}

//	//{{{ isToolsJarAvailable() method
//	/**
//	 * If on JDK 1.2 or higher, make sure that tools.jar is available.
//	 * This method should be called by plugins requiring the classes
//	 * in this library.
//	 * <p>
//	 * tools.jar is searched for in the following places:
//	 * <ol>
//	 *   <li>the classpath that was used when jEdit was started,
//	 *   <li>jEdit's jars folder in the user's home,
//	 *   <li>jEdit's system jars folder,
//	 *   <li><i>java.home</i>/lib/. In this case, tools.jar is added to
//	 *       jEdit's list of known jars using jEdit.addPluginJAR(),
//	 *       so that it gets loaded through JARClassLoader.
//	 * </ol><p>
//	 *
//	 * On older JDK's this method does not perform any checks, and returns
//	 * <code>true</code> (even though there is no tools.jar).
//	 *
//	 * @return <code>false</code> if and only if on JDK 1.2 and tools.jar
//	 *    could not be found. In this case it prints some warnings on Log,
//	 *    too, about the places where it was searched for.
//	 * @since jEdit 3.2.2
//	 */
//	public static boolean isToolsJarAvailable()
//	{
//		Log.log(Log.DEBUG, MiscUtilities.class,"Searching for tools.jar...");
//
//		Vector paths = new Vector();
//
//		//{{{ 1. Check whether tools.jar is in the system classpath:
//		paths.addElement("System classpath: "
//			+ System.getProperty("java.class.path"));
//
//		try
//		{
//			// Either class sun.tools.javac.Main or
//			// com.sun.tools.javac.Main must be there:
//			try
//			{
//				Class.forName("sun.tools.javac.Main");
//			}
//			catch(ClassNotFoundException e1)
//			{
//				Class.forName("com.sun.tools.javac.Main");
//			}
//			Log.log(Log.DEBUG, MiscUtilities.class,
//				"- is in classpath. Fine.");
//			return true;
//		}
//		catch(ClassNotFoundException e)
//		{
//			//Log.log(Log.DEBUG, MiscUtilities.class,
//			//	"- is not in system classpath.");
//		} //}}}
//
//		//{{{ 2. Check whether it is in the jEdit user settings jars folder:
//		String settingsDir = jEdit.getSettingsDirectory();
//		if(settingsDir != null)
//		{
//			String toolsPath = constructPath(settingsDir, "jars",
//				"tools.jar");
//			paths.addElement(toolsPath);
//			if(new File(toolsPath).exists())
//			{
//				Log.log(Log.DEBUG, MiscUtilities.class,
//					"- is in the user's jars folder. Fine.");
//				// jEdit will load it automatically
//				return true;
//			}
//		} //}}}
//
//		//{{{ 3. Check whether it is in jEdit's system jars folder:
//		String jEditDir = jEdit.getJEditHome();
//		if(jEditDir != null)
//		{
//			String toolsPath = constructPath(jEditDir, "jars", "tools.jar");
//			paths.addElement(toolsPath);
//			if(new File(toolsPath).exists())
//			{
//				Log.log(Log.DEBUG, MiscUtilities.class,
//					"- is in jEdit's system jars folder. Fine.");
//				// jEdit will load it automatically
//				return true;
//			}
//		} //}}}
//
//		//{{{ 4. Check whether it is in <java.home>/lib:
//		String toolsPath = System.getProperty("java.home");
//		if(toolsPath.toLowerCase().endsWith(File.separator + "jre"))
//			toolsPath = toolsPath.substring(0, toolsPath.length() - 4);
//		toolsPath = constructPath(toolsPath, "lib", "tools.jar");
//		paths.addElement(toolsPath);
//
//		if(!(new File(toolsPath).exists()))
//		{
//			Log.log(Log.WARNING, MiscUtilities.class,
//				"Could not find tools.jar.\n"
//				+ "I checked the following locations:\n"
//				+ paths.toString());
//			return false;
//		} //}}}
//
//		//{{{ Load it, if not yet done:
//		PluginJAR jar = jEdit.getPluginJAR(toolsPath);
//		if(jar == null)
//		{
//			Log.log(Log.DEBUG, MiscUtilities.class,
//				"- adding " + toolsPath + " to jEdit plugins.");
//			jEdit.addPluginJAR(toolsPath);
//		}
//		else
//			Log.log(Log.DEBUG, MiscUtilities.class,
//				"- has been loaded before.");
//		//}}}
//
//		return true;
//	} //}}}

	//{{{ parsePermissions() method
	/**
	 * Parse a Unix-style permission string (rwxrwxrwx).
	 * @param s The string (must be 9 characters long).
	 * @since jEdit 4.1pre8
	 */
	public static int parsePermissions(String s)
	{
		int permissions = 0;

		if(s.length() == 9)
		{
			if(s.charAt(0) == 'r')
				permissions += 0400;
			if(s.charAt(1) == 'w')
				permissions += 0200;
			if(s.charAt(2) == 'x')
				permissions += 0100;
			else if(s.charAt(2) == 's')
				permissions += 04100;
			else if(s.charAt(2) == 'S')
				permissions += 04000;
			if(s.charAt(3) == 'r')
				permissions += 040;
			if(s.charAt(4) == 'w')
				permissions += 020;
			if(s.charAt(5) == 'x')
				permissions += 010;
			else if(s.charAt(5) == 's')
				permissions += 02010;
			else if(s.charAt(5) == 'S')
				permissions += 02000;
			if(s.charAt(6) == 'r')
				permissions += 04;
			if(s.charAt(7) == 'w')
				permissions += 02;
			if(s.charAt(8) == 'x')
				permissions += 01;
			else if(s.charAt(8) == 't')
				permissions += 01001;
			else if(s.charAt(8) == 'T')
				permissions += 01000;
		}

		return permissions;
	} //}}}

	//{{{ getEncodings() method
	/**
	 * Returns a list of supported character encodings.
	 * @since jEdit 4.2pre5
	 */
	public static String[] getEncodings()
	{
		List returnValue = new ArrayList();

		Map map = Charset.availableCharsets();
		Iterator iter = map.keySet().iterator();

		returnValue.add(UTF_8_Y);

		while(iter.hasNext())
			returnValue.add(iter.next());

		return (String[])returnValue.toArray(
			new String[returnValue.size()]);
	} //}}}

	/**
	 * Read contents of file into string; return string.
	 * @param file
	 * @return
	 * @throws ThinklabException
	 */
	public static String readFileIntoString(File file) throws ThinklabException {

		String ret = null;
   	 	FileInputStream fis = null;
   	 	
		try {
			fis = new FileInputStream(file.getAbsolutePath());
			int x= fis.available();
			byte b[]= new byte[x];
   	 		fis.read(b);
   	 		ret = new String(b);
		} catch (Exception e) {
			throw new ThinklabException(e);
		}
		return ret;
	}
	
	/**
	 * Analyze the passed string and determine if it specifies an existing file resource
	 * or URL. Return the appropriate object, that must be disambiguated using instanceof.
	 * Meant to (inelegantly) solve problems coming from file name encodings in primitive
	 * OS (e.g. Windows) that cannot be handled properly in file:// URLs. 
	 * @return a valid URL, existing file, or null. No exceptions are thrown.
	 */
	public static Object getSourceForResource(String s) {
		
		File f = new File(s);

		if (f.exists()) 
			return f;
		
		URL url = null;
		try {
			url = new URL(s);
		} catch (MalformedURLException e) {
		}
		
		if (url != null)
			return url;
		
		return null;
	}
	
	/**
	 * Uses getSourceForResource on the passed string and tries to open whatever resource
	 * is passed and return the correspondent open input stream.
	 * @return an open input stream or null. No exceptions are thrown.
	 */
	public static InputStream getInputStreamForResource(String s) {
		
		InputStream ret = null;
		
		Object o = getSourceForResource(s);
		
		if (o != null) {
			
			if (o instanceof File) {
				try {
					ret = new FileInputStream((File)o);
				} catch (FileNotFoundException e) {
				}
			} else if (o instanceof URL) {
				try {
					ret = ((URL)o).openStream();
				} catch (IOException e) {
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Resolve a passed string into an existing file name
	 * @param msource a file name, URL, or string containing a plugin identifier and a resource
	 * 		  separated by :: - in the latter case, we lookup the resource in the named plugin's
	 *        classpath
	 * @return A local file containing the resource
	 * @throws ThinklabResourceNotFoundException
	 */
	public static File resolveUrlToFile(String msource) 
		throws ThinklabException {
		
		File ret = null;
		
		if (msource.contains("::")) {
			
			/* plugin classpath: resolve plugin and get resource */
			int x = msource.indexOf("::");
			String plug = msource.substring(0, x);
			String reso = msource.substring(x+2);
			
			ThinklabPlugin plugin = Thinklab.resolvePlugin(plug, true);
			URL rurl = plugin.getResourceURL(reso);
			ret = CopyURL.getFileForURL(rurl);
		
		} else if (msource.startsWith("http:") || msource.startsWith("file:")) {
			try {
				ret = CopyURL.getFileForURL(new URL(msource));
			} catch (Exception e) {
				throw new ThinklabResourceNotFoundException(
						"resource " +
						msource +
						": invalid URL");	
			}
		} else {			
			ret = new File(msource);
		}
		
		if (!ret.exists())
			throw new ThinklabResourceNotFoundException(
					"file " +
					msource +
					" cannot be read");	
		
		return ret;
	}
	
	/**
	 * Interprets a string as either a valid URL or a file name, and return a proper
	 * URL no matter what, or throw an exception if nothing seems to work. No checking is
	 * done on whether the URL actually resolves to anything.
	 * 
	 * @param msource
	 * @return
	 */
	public static URL getURLForResource(String msource) throws ThinklabIOException {

		URL murl = null;
		
		File f = new File(msource);

		// we may get things like shapefile://host/file.shp; change to http if so,
		// but only if this is not an existing file (may be C:/ in stupid Windows).
		if (!f.exists() && msource.contains(":")) {
			String[] pc = msource.split(":");
			if (!(pc[0].equals("file") || pc[0].equals("http"))) {
				// assume http if it's anything else
				msource = "http:" + pc[1];
			}
		}
		
		try {
			murl = new URL(msource);
		} catch (MalformedURLException e) {
			try {
				murl = f.toURI().toURL();
			} catch (MalformedURLException e1) {
				throw new ThinklabIOException(e1);
			}
		}

		if (murl == null)
			throw new ThinklabIOException("can't open resource " + msource);

		return murl;
	}
	
	//{{{ throwableToString() method
	/**
	 * Returns a string containing the stack trace of the given throwable.
	 * @since jEdit 4.2pre6
	 */
	public static String throwableToString(Throwable t)
	{
		StringWriter s = new StringWriter();
		t.printStackTrace(new PrintWriter(s));
		return s.toString();
	} //}}}

	//{{{ Private members
	private MiscUtilities() {}

	//{{{ compareChars()
	/** should this be public? */
	private static boolean compareChars(char ch1, char ch2, boolean ignoreCase)
	{
		if(ignoreCase)
			return Character.toUpperCase(ch1) == Character.toUpperCase(ch2);
		else
			return ch1 == ch2;
	} //}}}


	/**
	 * This method is used for creating a backup of an existing File.
	 * @param file
	 * @throws ThinklabIOException 
	 */
	public static void backupFile(File file) throws ThinklabIOException {
		File parentFolder = new File(file.getParent() + "/backup");

		if (!parentFolder.exists()) 	parentFolder.mkdir();

		int ver = 0;
		File newFile = new File(parentFolder,file.getName()+"."+ Integer.toString(ver));
		File backupFile = new File(parentFolder,file.getName()+"."+ Integer.toString(ver));
		
		while (newFile.exists()) {
			backupFile = newFile;
			newFile = new File(parentFolder,file.getName() + "."+ Integer.toString(ver++));
			
		}
		
//		TODO: Is this a safe way for comparing when last file was modified?
		if(backupFile.lastModified()<=file.lastModified()){
		try {
			CopyURL.copy(file.toURL(), newFile);
		} catch (Exception e) {
			throw new ThinklabIOException(e.getMessage());
		}
		}
		else newFile.delete();

	}

	// TODO make sure things work correctly when it's a URL with parameters
	public static String changeExtension(String string, String string2) {

		String ret = null;
		int sl = string.lastIndexOf(".");
		if (sl > 0) {
			if (sl == string.length() - 1)
				ret += string2;
			else
				ret = string.substring(0, sl+1) + string2;
		} else {
			ret += "." + string2;
		}
		
		return ret;
	}

	/**
	 * Resource indicated by s can be a file name or a URL. Returns true if it's an
	 * existing file or a URL that can be opened.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean resourceExists(String s) {

		boolean ret = false;
		Object o = getSourceForResource(s);
		
		if (o != null) {
			
			if (o instanceof File) {
				ret = ((File)o).exists();
			} else if (o instanceof URL) {
				try {
					ret = ((URL)o).openStream() != null;
				} catch (IOException e) {
				}
			}
		}
		return ret;
	}

	public static String changeProtocol(String url, String protocol) {
		
		return 
			url.indexOf(':') > 0 ?
					(protocol + url.substring(url.indexOf(':'))) : 
					(protocol + ":" + url);
		
	}

	public static File getPath(String lf) {
		// TODO Auto-generated method stub
		int n = lf.lastIndexOf(File.separator);
		String s = lf;
		if (n > -1) {
			s = lf.substring(0, n);
		}
		return new File(s);
	}

	public static URI removeFragment(URI uri) {
		
		URI ret = uri;
		if (ret.toString().contains("#")) {
			String ut = ret.toString().substring(0, ret.toString().indexOf("#"));
			try {
				ret = new URI(ut);
			} catch (URISyntaxException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		return ret;
	}
	
	public static String printVector(int[] data) {
		String ret = "";
		for (int d: data) {
			ret += (ret.equals("") ? "" : " ") + d;
		}
		return ret;
	}

	public static String printVector(double[] data) {
		String ret = "";
		for (double d: data) {
			ret += (ret.equals("") ? "" : " ") + d;
		}
		return ret;
	}

	public static int[] parseIntVector(String data) {

		String[] ss  = data.split("\\s+");
		int[] ret = new int[ss.length];
		int i = 0;
		for (String s: ss) {
			ret[i++] = Integer.parseInt(s);
		}
		return ret;
	}

	public static int[] parseIntVector(String data, int startAt) {

		String[] ss  = data.split("\\s+");
		int len = ss.length - startAt;
		int[] ret = new int[len];
		int n = 0;
		for (int i = startAt; i < ss.length; i++) {
			ret[n++] = Integer.parseInt(ss[i]);
		}
		return ret;
	}


	public static double[] parseDoubleVector(String data) {

		String[] ss  = data.split("\\s+");
		double[] ret = new double[ss.length];
		int i = 0;
		for (String s: ss) {
			ret[i++] = Double.parseDouble(s);
		}
		return ret;
	}

	public static double[] parseDoubleVector(String data, int startAt) {

		String[] ss  = data.split("\\s+");
		int len = ss.length - startAt;
		double[] ret = new double[len];
		int n = 0;
		for (int i = startAt; i < ss.length; i++) {
			ret[n++] = Double.parseDouble(ss[i]);
		}
		return ret;
	}

	
	/** 
	 * Create a new temporary directory. Use something like 
	 * {@link #recursiveDelete(File)} to clean this directory up since it isn't 
	 * deleted automatically 
	 * @return  the new directory 
	 * @throws IOException if there is an error creating the temporary directory 
	 */ 
	public static File createTempDir() throws ThinklabIOException 
	{ 
	    final File sysTempDir = new File(System.getProperty("java.io.tmpdir")); 
	    File newTempDir; 
	    final int maxAttempts = 9; 
	    int attemptCount = 0; 
	    do 
	    { 
	        attemptCount++; 
	        if(attemptCount > maxAttempts) 
	        { 
	            throw new ThinklabIOException( 
	                    "Failed to create a unique temporary directory after " + 
	                    maxAttempts + " attempts."); 
	        } 
	        String dirName = UUID.randomUUID().toString(); 
	        newTempDir = new File(sysTempDir, dirName); 
	    } while(newTempDir.exists()); 
	 
	    if(newTempDir.mkdirs()) 
	    { 
	        return newTempDir; 
	    } 
	    else 
	    { 
	        throw new ThinklabIOException( 
	                "Failed to create temp dir named " + 
	                newTempDir.getAbsolutePath()); 
	    } 
	} 
	 
	/** 
	 * Recursively delete file or directory 
	 * @param fileOrDir 
	 *          the file or dir to delete 
	 * @return 
	 *          true iff all files are successfully deleted 
	 */ 
	public static boolean recursiveDelete(File fileOrDir) 
	{ 
	    if(fileOrDir.isDirectory()) 
	    { 
	        // recursively delete contents 
	        for(File innerFile: fileOrDir.listFiles()) 
	        { 
	            if(!recursiveDelete(innerFile)) 
	            { 
	                return false; 
	            } 
	        } 
	    } 
	 
	    return fileOrDir.delete(); 
	} 

	
//	//{{{ getPathStart()
//	private static int getPathStart(String path)
//	{
//		int start = 0;
//		if(path.startsWith("/"))
//			return 1;
//		else if(OperatingSystem.isDOSDerived()
//			&& path.length() >= 3
//			&& path.charAt(1) == ':'
//			&& (path.charAt(2) == '/'
//			|| path.charAt(2) == '\\'))
//			return 3;
//		else
//			return 0;
//	} //}}}

	//}}}
}
