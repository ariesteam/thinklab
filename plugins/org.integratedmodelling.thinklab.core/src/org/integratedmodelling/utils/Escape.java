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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
* Convenience methods for escaping special characters related to HTML, XML, 
* and regular expressions.
* 
* <P>To keep you safe by default, WEB4J goes to some effort to escape 
* characters in your data when appropriate, such that you <em>usually</em>
* don't need to think too much about escaping special characters. Thus, you
*  shouldn't need to <em>directly</em> use the services of this class very often. 
* 
* <P><span class='highlight'>For Model Objects containing free form user input, 
* it is highly recommended that you use {@link SafeText}, not <tt>String</tt></span>.
* Free form user input is open to malicious use, such as
* <a href='http://www.owasp.org/index.php/Cross_Site_Scripting'>Cross Site Scripting</a>
* attacks. 
* Using <tt>SafeText</tt> will protect you from such attacks, by always escaping 
* special characters automatically in its <tt>toString()</tt> method.   
* 
* <P>The following WEB4J classes will automatically escape special characters 
* for you, when needed : 
* <ul>
* <li>the {@link SafeText} class, used as a building block class for your 
* application's Model Objects, for modeling all free form user input
* <li>the {@link Populate} tag used with forms
* <li>the {@link Report} class used for creating quick reports
* <li>the {@link Text}, {@link TextFlow}, and {@link Tooltips} custom tags used 
* for translation
* </ul> 
*/
public final class Escape {

  /**
   * Escape characters for text appearing in HTML markup.
   * 
   * <P>This method exists as a defence against Cross Site Scripting (XSS) hacks.
   * This method escapes all characters recommended by the Open Web App
   * Security Project - 
   * <a href='http://www.owasp.org/index.php/Cross_Site_Scripting'>link</a>.  
   * 
   * <P>The following characters are replaced with corresponding HTML 
   * character entities : 
   * <table border='1' cellpadding='3' cellspacing='0'>
   * <tr><th> Character </th><th> Encoding </th></tr>
   * <tr><td> < </td><td> &lt; </td></tr>
   * <tr><td> > </td><td> &gt; </td></tr>
   * <tr><td> & </td><td> &amp; </td></tr>
   * <tr><td> " </td><td> &quot;</td></tr>
   * <tr><td> ' </td><td> &#039;</td></tr>
   * <tr><td> ( </td><td> &#040;</td></tr> 
   * <tr><td> ) </td><td> &#041;</td></tr>
   * <tr><td> # </td><td> &#035;</td></tr>
   * <tr><td> % </td><td> &#037;</td></tr>
   * <tr><td> ; </td><td> &#059;</td></tr>
   * <tr><td> + </td><td> &#043; </td></tr>
   * <tr><td> - </td><td> &#045; </td></tr>
   * </table>
   * 
   * <P>Note that JSTL's {@code <c:out>} escapes <em>only the first 
   * five</em> of the above characters.
   */
   public static String forHTML(String aText){
     final StringBuilder result = new StringBuilder();
     final StringCharacterIterator iterator = new StringCharacterIterator(aText);
     char character =  iterator.current();
     while (character != CharacterIterator.DONE ){
       if (character == '<') {
         result.append("&lt;");
       }
       else if (character == '>') {
         result.append("&gt;");
       }
       else if (character == '&') {
         result.append("&amp;");
      }
       else if (character == '\"') {
         result.append("&quot;");
       }
       else if (character == '\'') {
         result.append("&#039;");
       }
       else if (character == '(') {
         result.append("&#040;");
       }
       else if (character == ')') {
         result.append("&#041;");
       }
       else if (character == '#') {
         result.append("&#035;");
       }
       else if (character == '%') {
         result.append("&#037;");
       }
       else if (character == ';') {
         result.append("&#059;");
       }
       else if (character == '+') {
         result.append("&#043;");
       }
       else if (character == '-') {
         result.append("&#045;");
       }
       else {
         //the char is not a special one
         //add it to the result as is
         result.append(character);
       }
       character = iterator.next();
     }
     return result.toString();
  }
  
  
  /**
   * Synonym for <tt>URLEncoder.encode(String, "UTF-8")</tt>.
   *
   * <P>Used to ensure that HTTP query strings are in proper form, by escaping
   * special characters such as spaces.
   *
   * <P>It is important to note that if a query string appears in an <tt>HREF</tt>
   * attribute, then there are two issues - ensuring the query string is valid HTTP
   * (it is URL-encoded), and ensuring it is valid HTML (ensuring the 
   * ampersand is escaped).
   */
   public static String forURL(String aURLFragment){
     String result = null;
     try {
    	 result = URLEncoder.encode(aURLFragment, "UTF-8");
     }
     catch (UnsupportedEncodingException ex){
       throw new RuntimeException("UTF-8 not supported", ex);
     }
     return result;
   }

   /**
    * Synonym for <tt>URLEncoder.encode(String, "UTF-8")</tt>.
    *
    * <P>Used to ensure that HTTP query strings are in proper form, by escaping
    * special characters such as spaces.
    *
    * <P>It is important to note that if a query string appears in an <tt>HREF</tt>
    * attribute, then there are two issues - ensuring the query string is valid HTTP
    * (it is URL-encoded), and ensuring it is valid HTML (ensuring the 
    * ampersand is escaped).
    */
    public static String fromURL(String aURLFragment){
      String result = null;
      try {
     	 result = URLDecoder.decode(aURLFragment, "UTF-8");
      }
      catch (UnsupportedEncodingException ex){
        throw new RuntimeException("UTF-8 not supported", ex);
      }
      return result;
    }


   
   /**
  * Escape characters for text appearing as XML data, between tags.
  * 
  * <P>The following characters are replaced with corresponding character entities : 
  * <table border='1' cellpadding='3' cellspacing='0'>
  * <tr><th> Character </th><th> Encoding </th></tr>
  * <tr><td> < </td><td> &lt; </td></tr>
  * <tr><td> > </td><td> &gt; </td></tr>
  * <tr><td> & </td><td> &amp; </td></tr>
  * <tr><td> " </td><td> &quot;</td></tr>
  * <tr><td> ' </td><td> &#039;</td></tr>
  * </table>
  * 
  * <P>Note that JSTL's {@code <c:out>} escapes the exact same set of 
  * characters as this method. <span class='highlight'>That is, {@code <c:out>}
  *  is good for escaping to produce valid XML, but not for producing safe HTML.</span>
  */
  public static String forXML(String aText){
    final StringBuilder result = new StringBuilder();
    final StringCharacterIterator iterator = new StringCharacterIterator(aText);
    char character =  iterator.current();
    while (character != CharacterIterator.DONE ){
      if (character == '<') {
        result.append("&lt;");
      }
      else if (character == '>') {
        result.append("&gt;");
      }
      else if (character == '\"') {
        result.append("&quot;");
      }
      else if (character == '\'') {
        result.append("&#039;");
      }
      else if (character == '&') {
         result.append("&amp;");
      }
      else {
        //the char is not a special one
        //add it to the result as is
        result.append(character);
      }
      character = iterator.next();
    }
    return result.toString();
  }

  /**
  * Return <tt>aText</tt> with all <tt>'<'</tt> and <tt>'>'</tt> characters
  * replaced by their escaped equivalents.
  */
  public static String toDisableTags(String aText){
    final StringBuilder result = new StringBuilder();
    final StringCharacterIterator iterator = new StringCharacterIterator(aText);
    char character =  iterator.current();
    while (character != CharacterIterator.DONE ){
      if (character == '<') {
        result.append("&lt;");
      }
      else if (character == '>') {
        result.append("&gt;");
      }
      else {
        //the char is not a special one
        //add it to the result as is
        result.append(character);
      }
      character = iterator.next();
    }
    return result.toString();
  }
  

  /**
  * Replace characters having special meaning in regular expressions
  * with their escaped equivalents, preceded by a '\' character.
  *
  * <P>The escaped characters include :
  *<ul>
  *<li>.
  *<li>\
  *<li>?, * , and +
  *<li>&
  *<li>:
  *<li>{ and }
  *<li>[ and ]
  *<li>( and )
  *<li>^ and $
  *</ul>
  *
  */
  public static String forRegex(String aRegexFragment){
    final StringBuilder result = new StringBuilder();

    final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
    char character =  iterator.current();
    while (character != CharacterIterator.DONE ){
      /*
      * All literals need to have backslashes doubled.
      */
      if (character == '.') {
        result.append("\\.");
      }
      else if (character == '\\') {
        result.append("\\\\");
      }
      else if (character == '?') {
        result.append("\\?");
      }
      else if (character == '*') {
        result.append("\\*");
      }
      else if (character == '+') {
        result.append("\\+");
      }
      else if (character == '&') {
        result.append("\\&");
      }
      else if (character == ':') {
        result.append("\\:");
      }
      else if (character == '{') {
        result.append("\\{");
      }
      else if (character == '}') {
        result.append("\\}");
      }
      else if (character == '[') {
        result.append("\\[");
      }
      else if (character == ']') {
        result.append("\\]");
      }
      else if (character == '(') {
        result.append("\\(");
      }
      else if (character == ')') {
        result.append("\\)");
      }
      else if (character == '^') {
        result.append("\\^");
      }
      else if (character == '$') {
        result.append("\\$");
      }
      else {
        //the char is not a special one
        //add it to the result as is
        result.append(character);
      }
      character = iterator.next();
    }
    return result.toString();
  }
  
  public static String forSQL(String aRegexFragment){
	    final StringBuilder result = new StringBuilder();

	    final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
	    char character =  iterator.current();
	    while (character != CharacterIterator.DONE ){
	      /*
	      * All literals need to have backslashes doubled.
	      */
	      if (character == '\'') {
	        result.append("\\'");
	      }
	      else if (character == '\\') {
	        result.append("\\\\");
	      }  else {
	        //the char is not a special one
	        //add it to the result as is
	        result.append(character);
	      }
	      character = iterator.next();
	    }
	    return result.toString();
	  }


public static String forDoubleQuotedString(String s, boolean removeNewlines){
    final StringBuilder result = new StringBuilder();

    final StringCharacterIterator iterator = new StringCharacterIterator(s);
    char character =  iterator.current();
    while (character != CharacterIterator.DONE ){
      /*
      * All literals need to have backslashes doubled.
      */
      if (character == '"') {
        result.append("\\\"");
      } else if (character == '\n' && removeNewlines) {
    	  result.append(' ');
      }  else {
        //the char is not a special one
        //add it to the result as is
        result.append(character);
      }
      character = iterator.next();
    }
    return result.toString();
  }


   public static String collapseWhitespace(String s) {
	
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character =  iterator.current();
		while (character != CharacterIterator.DONE ){
			/*
			 * All literals need to have backslashes doubled.
			 */
			boolean wasw = false;
			while (Character.isWhitespace(character)) {
				wasw = true;
				character = iterator.next();
			}
			if (wasw) {
				result.append(' ');
			} else {
				result.append(character);
				character = iterator.next();
			}
		}
		return result.toString();
	}
	    
}
 
