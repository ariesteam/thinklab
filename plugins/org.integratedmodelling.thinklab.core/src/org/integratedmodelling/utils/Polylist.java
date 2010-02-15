/**
 * Polylist.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 Robert Keller and www.integratedmodelling.org 
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
 * @copyright 2008 Robert Keller and www.integratedmodelling.org
 * @author    Robert Keller (original, polya package)
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/

package org.integratedmodelling.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.xml.XML;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.integratedmodelling.utils.xml.XML.XmlNode;

/**   
 <pre>
 Polylist is the basic unit for constructing an open linked list of Objects.
 A Polylist can be either :
 empty, or
 non-empty, in which case it consists of:
 a Object as the first thing in the list
 a rest which is a Polylist.
 A Polylist can also be a Incremental, which may be converted into an
 ordinary Polylist by applying methods such as isEmpty(), first(), and
 rest(). 
 </pre>
 */
public class Polylist {
	/**
	 *  nil is the empty-list constant
	 */
	public static final Polylist nil = new Polylist();

	private ConsCell ptr;

	/*
	 * simple functor that transforms tokens into objects when lists are read from strings.
	 */
	public interface TokenTransformer {
		public abstract Object transformString(String string);
		public abstract Object transformDouble(String string);
		public abstract Object transformQuote();
	}
	
	/**
	 *  construct empty Polylist
	 */

	public Polylist() {
		ptr = null;
	}

	/**
	 *  construct non-empty Polylist from a First and Rest
	 */

	Polylist(Object First, Polylist Rest) {
		ptr = new ConsCell(First, Rest);
	}

	/**
	 *  construct non-empty Polylist from a First and a Seed for the Rest
	 */

	Polylist(Object First, Seed Rest) {
		ptr = new ConsCell(First, Rest);
	}

	/**
	 *  isEmpty() tells whether the Polylist is empty.
	 */

	public boolean isEmpty() {
		return ptr == null;
	}

	/**
	 *  nonEmpty() tells whether the Polylist is non-empty.
	 */

	public boolean nonEmpty() {
		return ptr != null;
	}

	/**
	 *  first() returns the first element of a non-empty Polylist.
	 * @exception NullPointerException Can't take first of an empty Polylist.
	 *
	 */

	public Object first() {
		return ptr.first();
	}

	/**
	 *  setFirst() sets the first of a list to an object
	 * @exception NullPointerException Can't take first of an empty Polylist.
	 *
	 */

	public void setFirst(Object ob) {
		ptr.setFirst(ob);
	}

		
	
	public static String prettyPrint(Polylist list) {
		return prettyPrintInternal(list, 0, 2);
	}

	public static String prettyPrint(Polylist list, int indent) {
		return prettyPrintInternal(list, indent, 2);
	}
	
	private static String prettyPrintInternal(Polylist list, int indentLevel, int indentAmount) {

		String ret = "";
		String inds = "";
		
		for (int i = 0; i < indentLevel*indentAmount; i++)
			inds += ' ';

		if (list == null)
			return "(nil)";
		
		boolean wrote = false;

		ret += inds + "(";
		
		for (Object o : list.array()) {
			
			if (o instanceof Polylist) {
				ret += "\n" + prettyPrintInternal((Polylist)o, indentLevel+1, indentAmount);
			} else {
				String sep = " ";
				String z = o == null ? "*null*" : o.toString();
				if (z.contains(" ")) {
					z = "'" + z + "'";
				}
				if (z.contains("\n")) {
					z = "'" + z.replaceAll("\n", inds + "\n") + "'";
					sep = "\n";
				}
				
				if (wrote) ret += sep;
				ret += z;
				wrote = true;
			}
		}
		
		ret += ")";
		
		return ret;
	}

	/**
	 * TBC an utility function that should either be generalized or moved to another class. If
	 * passed object is a list and the string equivalent of its first element equals to the passed string,
	 * the second object in the list is returned. Otherwise null is returned. Note that there is no
	 * way to distinguish when the element is there and the second is null, or the element is not there
	 * at all.
	 * @param o
	 * @param s
	 * @return
	 */
	public static Object declares(Object o, String what) {

		Object ret = null;

		if (o.getClass() == Polylist.class && ((Polylist) o).nonEmpty()
				&& ((Polylist) o).first().getClass() == String.class
				&& ((Polylist) o).first().toString().equals(what)) {
			ret = ((Polylist) o).second();
		}

		return ret;
	}

	/**
	 *  rest() returns the rest of a non-empty Polylist.
	 * @exception NullPointerException Can't take rest of an empty Polylist.
	 */

	public Polylist rest() {
		return ptr.rest();
	}

	/**
	 *  toString() converts Polylist to string, e.g. for printing
	 */

	public String toString() {

		StringBuffer buff = new StringBuffer();

		buff.append("(");

		// See if this is an incremental list; if so, show ...

		if (this instanceof Incremental && !((Incremental) this).grown()) {
			buff.append("...");
		}

		else if (nonEmpty()) {

			buff.append(first());
			Polylist L = rest();

			// print the rest of the items

			for (;;) {
				if (L instanceof Incremental && !((Incremental) L).grown()) {
					buff.append(" ...");
					break;
				}
				if (L.isEmpty())
					break;
				buff.append(" ");
				buff.append(L.first().toString());
				L = L.rest();
			}
		}
		buff.append(")");
		return buff.toString();
	}

	/**
	 * cons returns a new Polylist given a First and this as a Rest
	 */

	public Polylist cons(Object First) {
		return new Polylist(First, this);
	}

	/**
	 *  static cons returns a new Polylist given a First and a Rest.
	 */

	public static Polylist cons(Object First, Polylist Rest) {
		return Rest.cons(First);
	}

	/**
	 *  This variant of cons takes a Seed instead of a Polylist as rest, so
	 *  the list can be grown incrementally.
	 */

	public static Polylist cons(Object First, Seed Rest) {
		return new Polylist(First, Rest);
	}

	/**
	 *  PolylistFromEnum makes a Polylist out of any Enumeration.
	 */

	public static Polylist PolylistFromEnum(java.util.Enumeration<?> e) {
		if (e.hasMoreElements())
			return cons(e.nextElement(), PolylistFromEnum(e));
		else
			return nil;
	}

	/**
	 *  return a list of no elements
	 */

	public static Polylist list() {
		return nil;
	}

	public static Polylist list(Object ... objs) {
		return PolylistFromArray(objs);
	}

	public static Polylist listNotNull(Object ... objs) {
		return PolylistFromArrayNotNull(objs);
	}

	/**
	 *  return the length of this list
	 */

	public int length() {
		int len = 0;
		for (Enumeration<?> e = elements(); e.hasMoreElements(); e.nextElement()) {
			len++;
		}
		return len;
	}

	/**
	 *  elements() returns a PolylistEnum object, which implements the
	 *  interface java.util.Enumeration.
	 */

	public PolylistEnum elements() {
		return new PolylistEnum(this);
	}

	/**
	 *  first(L) returns the first element of its argument.
	 * @exception NullPointerException Can't take first of empty List.
	 */

	static public Object first(Polylist L) {
		return L.first();
	}

	/**
	 *  rest(L) returns the rest of its argument.
	 * @exception NullPointerException Can't take rest of empty Polylist.
	 */

	static public Polylist rest(Polylist L) {
		return L.rest();
	}

	/**
	 *  reverse(L) returns the reverse of this
	 */

	public Polylist reverse() {
		Polylist rev = nil;
		for (Enumeration<?> e = elements(); e.hasMoreElements();) {
			rev = cons(e.nextElement(), rev);
		}
		return rev;
	}

	/**
	 *  append(M) returns a Polylist consisting of the elements of this
	 *  followed by those of M.
	 */

	public Polylist append(Polylist M) {
		if (isEmpty())
			return M;
		else
			return cons(first(), rest().append(M));
	}

	/**
	 *  member(A, L) tells whether A is a member of this
	 */

	public boolean member(Object A) {
		for (Enumeration<?> e = elements(); e.hasMoreElements();)
			if (Arith.equal(e.nextElement(), A))
				return true;
		return false;
	}

	/**
	 *  range(M, N) returns a Polylist of the form (M M+1 .... N)
	 */

	public static Polylist range(long M, long N) {
		if (M > N)
			return nil;
		else
			return cons(new Long(M), range(M + 1, N));
	}

	/**
	 *  range(M, N, S) returns a Polylist of the form (M M+S .... N)
	 */

	public static Polylist range(long M, long N, long S) {
		if (S >= 0)
			return rangeUp(M, N, S);
		else
			return rangeDown(M, N, S);
	}

	/**
	 *  rangeUp(M, N, S) is an auxiliary function for range
	 */

	static Polylist rangeUp(long M, long N, long S) {
		if (M > N)
			return nil;
		else
			return cons(new Long(M), rangeUp(M + S, N, S));
	}

	/**
	 *  rangeDown(M, N, S) is auxiliary function for range
	 */

	static Polylist rangeDown(long M, long N, long S) {
		if (M < N)
			return nil;
		else
			return cons(new Long(M), range(M + S, N, S));
	}

	/**
	 *  second selects the second element of a Polylist.
	 * @exception NullPointerException Can't take second of Polylist.
	 */

	public Object second() {
		return rest().first();
	}

	/**
	 *  third selects the third element of a Polylist.
	 * @exception NullPointerException Can't take third of Polylist.
	 */

	public Object third() {
		return rest().rest().first();
	}

	/**
	 *  fourth selects the fourth element of a Polylist.
	 * @exception NullPointerException Can't take fourth of Polylist.
	 */

	public Object fourth() {
		return rest().rest().rest().first();
	}

	/**
	 *  fifth selects the fifth element of a Polylist.
	 * @exception NullPointerException Can't take fifth of Polylist
	 */

	public Object fifth() {
		return rest().rest().rest().rest().first();
	}

	/**
	 *  sixth selects the sixth element of a Polylist.
	 * @exception NullPointerException Can't take sixth of Polylist
	 */

	public Object sixth() {
		return rest().rest().rest().rest().rest().first();
	}

	/**
	 *  nth selects Polylist item by index (0, 1, 2, ...).
	 * @exception NullPointerException Can't select from an empty Polylist.
	 */

	public Object nth(long n) {
		Polylist L = this;
		while (n-- > 0)
			L = L.rest();
		return L.first();
	}

	/**
	 *   prefix creates the length-n prefix of a Polylist.
	 */

	public Polylist prefix(long n) {
		if (n <= 0 || isEmpty())
			return nil;
		else
			return cons(first(), rest().prefix(n - 1));
	}

	/**
	 *   coprefix creates the Polylist with all but the length-n prefix of 
	 *   a Polylist
	 */

	public Polylist coprefix(long n) {
		if (n <= 0 || isEmpty())
			return this;
		else
			return rest().coprefix(n - 1);
	}

	/**
	 *  equals(L, M) tells whether Polylists L and M are equal
	 */

	public static boolean equals(Polylist L, Polylist M) {
		Enumeration<?> e = L.elements();
		Enumeration<?> f = M.elements();

		while (e.hasMoreElements() && f.hasMoreElements()) {
			if (!Arith.equal(e.nextElement(), f.nextElement()))
				return false;
		}
		return !(e.hasMoreElements() || f.hasMoreElements());
	}

	/**
	 *  equals(M) tells whether this Polylist is equal to some other Object
	 */

	public boolean equals(Object M) {
		if (M instanceof Polylist)
			return equals(this, (Polylist) M);
		else
			return false;
	}

	
	/**
	 * Read the next list from the passed inputstream, stop when list is complete.
	 * 
	 * @param input
	 * @return a new list, or null if the stream finishes without a list. 
	 * @throws MalformedListException 
	 * @throws IOException 
	 */
	public static Polylist read(InputStream input) throws MalformedListException, IOException {
		
		String s = "";
		
		int c = input.read();
		int pcount = 0;
		boolean inString = false;
		
		while (c != -1) {
		
			s = s + (char)c;
			
			if (c == '"') {
				inString = !inString;
			} else if (!inString) {
				if (c == '(')
					pcount ++;
				else if (c == ')') {
					pcount --;
					if (pcount == 0)
						break;
				}
			}
			c = input.read();
		}
		
		if (s.trim().startsWith("("))
			return Polylist.parse(s);
		
		return null;
	}
	
	
	/**
	 * Create a list from a string. Quoted strings are handled properly. Numbers are
	 * stored in the list as doubles; all other objects are stored as strings.
	 * @param s
	 * @return
	 * @throws MalformedListException
	 */
	public static Polylist parse(String s) throws MalformedListException {

		final class DummyFunctor implements TokenTransformer {

			public Object transformDouble(String string) {
				return string;
		}

			public Object transformString(String string) {
				return string;
			}

			@Override
			public Object transformQuote() {
				return "'";
			}
			
		}

		return parseWithFunctor(s, new DummyFunctor());
	}
	
	/**
	 * Create a list from a string, but replace every token starting with $ with the
	 * correspondent value from the passed map (the key should not contain the $ sign).
	 * @param t
	 * @param vmap
	 * @return
	 */
	public static Polylist parseWithTemplate(String s, Hashtable<String, Object> vmap) throws MalformedListException {

		final class SubstitutionFunctor implements TokenTransformer {

			private Hashtable<String,Object> map = null;

			public SubstitutionFunctor(Hashtable<String, Object> vmap) {
				this.map = vmap ;
			}

			public Object transformDouble(String string) {
				return string;
			}

			public Object transformString(String string) {

				Object ret = string;
				
				if (ret == null)
					return ret;
				
				/* replace whole string with object if we have perfect match */
				if (string.startsWith("$") && map.containsKey(string.substring(1))) {
					ret = map.get(string.substring(1));
				} else if (string.contains("$")) {
					/* replace in-string tokens with their string representation */
					for (String k : map.keySet()) {
						string = 
							string.replaceAll(
								Pattern.quote("$") + k, 
								Matcher.quoteReplacement(map.get(k).toString()));
					}
					ret = string;
				}
				return ret;
			}
			
			@Override
			public Object transformQuote() {
				return "'";
			}

		}

		return parseWithFunctor(s, new SubstitutionFunctor(vmap));
	}
	
	/**
	 * Parses a list from a string. Generates a nested polylist of String objects or other Polylists.
	 * It's trivial to add one that recognizes Integers and Doubles as such, or even other types, but I'm not sure it would be useful. On the
	 * other hand, a parameterized one that parses as one specified type (e.g. integer) would probably be useful in the future.
	 * @param s a string representing a list; e.g. (a b (c d e) (19 22))
	 * @param functor a functor that handles the transformation between token and stored object.
	 * @return the generated list.
	 */
	public static Polylist parseWithFunctor(String s, TokenTransformer functor) throws MalformedListException {

		StreamTokenizer scanner = new StreamTokenizer(new StringReader(s));

		scanner.wordChars(':', ':');
		scanner.wordChars('/', '/');
		scanner.wordChars('\\', '\\');
		scanner.wordChars('~', '~');
		scanner.wordChars('%', '%');
		scanner.wordChars('&', '&');
		scanner.wordChars('?', '?');
		scanner.wordChars('_', '_');
		scanner.wordChars('#', '#');
		scanner.wordChars('$', '$');
		scanner.wordChars('*', '*');		
		scanner.ordinaryChar('\'');

		int token = 0;
		try {
			token = scanner.nextToken();
		} catch (IOException e) {
			throw new MalformedListException(s);
		}

		if (token != '(')
			throw new MalformedListException(s);

		return parseStringWithFunctor(scanner, functor);
	}

	static Polylist parseStringWithFunctor(StreamTokenizer scanner, TokenTransformer functor)
	throws MalformedListException {

		Polylist ret = new Polylist();

		int token = StreamTokenizer.TT_EOF;
		Object tok = "";

		while (true) {

			try {
				token = scanner.nextToken();
			} catch (IOException e) {
				throw new MalformedListException();
			}

			if (token == StreamTokenizer.TT_NUMBER)
				tok = functor.transformDouble(new Double(scanner.nval).toString());
			else if (token == StreamTokenizer.TT_WORD)
				tok = functor.transformString(scanner.sval);
			else if (token == '\'')
				tok = functor.transformQuote();
			else if (token == StreamTokenizer.TT_EOF
					|| token == StreamTokenizer.TT_EOL) {
				throw new MalformedListException();
			} else {
				if (token != '(' && token != ')')
					// TBC a quoted string gets here, but what is returned is not TT_WORD, and no other fields
					// are there. Docs suck, to boot. 
					tok = functor.transformString(scanner.sval);
			}

			if (token == '(')
				// economical, it's not
				ret = ret.append(list(parseStringWithFunctor(scanner, functor)));
			else if (token == ')')
				break;
			else {
				ret = ret.append(list(functor.transformString(tok == null ? ("" + (char)token) : tok.toString())));
			}
		}

		return ret;

	}


	/**
	 *  test program for Polylists etc.
	 */
	static public void main(String args[]) {
		System.out.println("Testing Polylist");

		Polylist x = list(new Long(3), new Long(4));

		// x = (3 4)

		x = list(new Long(2), x);

		// x = (2 (3 4))

		x = list(list(new Long(1)), x);

		// x = ((1) (2 (3 4)))

		x = cons(new Long(0), x);

		// x = (0 (1) (2 (3 4)))

		x = x.append(range(5, 9));

		// x = (0 (1) (2 (3 4)) 5 6 7 8 9)

		// print using the default style (S expression)

		System.out.println(x);

		// prints (0 (1) (2 (3 4)) 5 6 7 8 9)

		// print only the third element

		System.out.println(x.third());

		// prints (2 (3 4))

		// print only the fourth element

		System.out.println(x.nth(3));

		// prints 5

		Polylist q = x.reverse();

		System.out.println(q);

		// prints (9 8 7 6 5 (2 (3 4)) (1) 0)

		System.out.println(x.nth(2).equals(q.nth(5)));

		// prints true

		// print first 2 elements

		System.out.println(q.prefix(2));

		// prints (9 8)

		// check out getting enumeration from a list

		System.out.println("Printed using an enumeration:");

		for (Enumeration<?> e = q.elements(); e.hasMoreElements();) {
			System.out.print(e.nextElement() + "  ");
		}
		System.out.println();

		// check out ListFromEnum

		Stack<Integer> s = new Stack<Integer>();
		for (int i = 0; i < 20; i++)
			s.push(new Integer(i));

		Polylist z = PolylistFromEnum(s.elements());

		System.out.println(z);

		System.out.println(z.member(new Long(9)));

		System.out.println("From array: ");

		Object[] a = z.array();

		for (int i = 0; i < a.length; i++)
			System.out.print(a[i] + " ");

		System.out.println();

		System.out.println(PolylistFromArray(a));

		Polylist nitt = explode("Now is the time");

		System.out.println(nitt);

		System.out.println(nitt.implode());

		System.out.println(list("foo", "bar", new Long(123)).implode());

		try {
			System.out.println(nil.first());
		} catch (NullPointerException e) {
			System.out.println("NullPointerException check on first() passed");
		}

		try {
			System.out.println(nil.rest());
		} catch (NullPointerException e) {
			System.out.println("NullPointerException check on rest() passed");
		}

		System.out.println("Type in S expressions for analysis");

//		Tokenizer in = new Tokenizer(System.in);
//
//		Object ob;
//		while ((ob = in.nextSexp()) != Tokenizer.eof) {
//			System.out.println(analysis(ob));
//		}
//
//		System.out.println("Type in R expressions for analysis");
//
//		in = new Tokenizer(System.in);
//
//		while ((ob = in.nextRexp()) != Tokenizer.eof) {
//			System.out.println(analysis(ob));
//		}
//		System.out.println("Test completed");
		
		/* define commands from user input */
		while(true) {
			
			System.out.print("> ");
			Polylist l = null;
			
			try {
				l = Polylist.read(System.in);
			} catch (MalformedListException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (l != null)
				System.out.println(Polylist.prettyPrint(l));
			
//		     //  open up standard input 
//		      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//		      String input = null;
//
//		      //  read the username from the command-line; need to use try/catch with the 
//		      //  readLine() method 
//		      try { 
//		         input = br.readLine(); 
//		      } catch (IOException ioe) { 
//		         System.out.println("IO error"); 
//		         System.exit(1); 
//		      }		
//		      
//			if ("exit".equals(input)) {
//				System.out.println("shell terminated");
//				System.exit(0);
//				break;
//			} else if (!input.trim().equals("")) {
//
//					Polylist l = null;
//					try {
//						l = Polylist.parse(input.trim());
//					} catch (MalformedListException e) {
//						System.out.println("Invalid list input!");
//					}
//					
//					if (l != null)
//						System.out.println("\n" + prettyPrint(l));
//				
//			}
		}
		
	}

	/**
	 *  analysis produces a string analyzing objects, especially Polylists
	 */

	public static String analysis(Object Ob) {
		return analysis(Ob, 0);
	}

	/**
	 *  produce an analysis of this Polylist
	 */

	String analysis() {
		return analysis(0);
	}

	/**
	 *  produce an analysis of this Polylist, indenting N spaces
	 */

	String analysis(int N) {
		if (isEmpty())
			return spaces(N) + "The empty Polylist\n";
		StringBuffer buff = new StringBuffer();
		buff.append(spaces(N));
		int len = length();
		buff.append("A Polylist consisting of " + len + " element"
				+ (len > 1 ? "s" : "") + ": \n");

		for (Enumeration<?> e = elements(); e.hasMoreElements();) {
			buff.append(analysis(e.nextElement(), N + 1));
		}
		return buff.toString();
	}

	/**
	 *  produce an analysis of the first argument, indenting N spaces
	 */

	static String analysis(Object Ob, int N) {
		if (Ob instanceof Polylist)
			return ((Polylist) Ob).analysis(N);
		else
			return spaces(N) + Ob.toString() + " (class "
					+ Ob.getClass().getName() + ")\n";
	}

	/**
	 * Indent N spaces.
	 */

	static String spaces(int N) {
		StringBuffer buff = new StringBuffer();
		while (N > 0) {
			buff.append("  ");
			N--;
		}
		return buff.toString();
	}

	/**
	 * array() returns an array of elements in list
	 */

	public Object[] array() {
		Object[] result = new Object[length()];
		int i = 0;
		for (Enumeration<?> e = elements(); e.hasMoreElements();) {
			result[i++] = e.nextElement();
		}
		return result;
	}

	/**
	 * PolylistFromArray makes a list out of an array of objects
	 */
	public static Polylist PolylistFromArray(Object array[]) {
		Polylist result = nil;
		for (int i = array.length - 1; i >= 0; i--)
			result = cons(array[i], result);
		return result;
	}
	
	/**
	 * PolylistFromArrayNotNull makes a list out of an array of objects where
	 * any nulls are not included in the list
	 */
	public static Polylist PolylistFromArrayNotNull(Object array[]) {
		Polylist result = nil;
		for (int i = array.length - 1; i >= 0; i--)
			if (array[i] != null)
				result = cons(array[i], result);
		return result;
	}
	
	
	/**
	 * PolylistFromArray makes a list out of an array of objects
	 */
	public static Polylist PolylistFromArrayList(ArrayList<Object> array) {
		Polylist result = nil;
		for (int i = array.size() - 1; i >= 0; i--)
			result = cons(array.get(i), result);
		return result;
	}

	/**
	 * explode(String S) converts a string into a Polylist of Character
	 */

	public static Polylist explode(String S) {
		Polylist result = nil;
		for (int i = S.length() - 1; i >= 0; i--)
			result = cons(new Character(S.charAt(i)), result);
		return result;
	}

	/**
	 * implode() creates a String from a Polylist of items
	 */

	public String implode() {
		StringBuffer buff = new StringBuffer();
		for (Enumeration<?> e = elements(); e.hasMoreElements();) {
			buff.append(e.nextElement().toString());
		}
		return buff.toString();
	}

	/**
	 * map maps an object of class Function1 over a Polylist returning a 
	 * Polylist
	 */

	public Polylist map(Function1 F) {
		if (isEmpty())
			return nil;
		else
			return cons(F.apply(first()), rest().map(F));
	}

	/**
	 * reduce reduces a Polylist by a Function2 object, with unit
	 * 
	 */

	public Object reduce(Function2 F, Object unit) {
		Object result = unit;
		Polylist L = this;

		for (; L.nonEmpty(); L = L.rest()) {
			result = F.apply(result, L.first());
		}

		return result;
	}

	public boolean hasMemberOfClass(Class<?> cls) {
		boolean ret = false;
		for (Object o : array())
			if (ret = (o.getClass().equals(cls)))
				break;
		return ret;
	}

	public ArrayList<Object> toArrayList() {
		
		ArrayList<Object> ret = new ArrayList<Object>();

		for (Enumeration<?> e = elements(); e.hasMoreElements();) {
			ret.add(e.nextElement());
		}
		return ret;
	}
	
	/**
	 * Return a new list like this with the passed object appended at the end of the list.
	 * 
	 * @param o
	 * @return
	 */
	public Polylist appendElement(Object o) {
		ArrayList<Object> ls = toArrayList();
		ls.add(o);
		return Polylist.PolylistFromArrayList(ls);
	}
	
	public XML.XmlNode createXmlNode() {
		XmlNode n = new XmlNode(first().toString());
		for (Object o : rest().array()) {
			if (o instanceof Polylist)
				n.add(((Polylist)o).createXmlNode());
			else 
				n.text(o.toString());
		}
		return n;
	}
	
	/**
	 * Return a nice XML document that can be written to file or shown.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public XMLDocument asXml() throws ThinklabException {
		return XML.document(createXmlNode());
	}

} // class Polylist
