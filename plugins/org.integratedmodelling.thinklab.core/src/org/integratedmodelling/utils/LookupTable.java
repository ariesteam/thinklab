/**
 * LookupTable.java
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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabValidationException;

/**
 * A lookup table that can be initialized from a string using PERL syntax, extended to support numeric ranges for
 * values.
 * 
 * Pseudo-grammar for table:
 * 
 * table := pair*
 * pair := {value|range} => value 
 * value := IDENTIFIER | 'IDENTIFIER' // quotes are literals
 * range := [NUMBER:NUMBER] // []- are literals
 * 
 * @author Ferdinando Villa
 * @date October 1, 2007
 */
public class LookupTable {
	
	private boolean hasRange = false;
	String literal = null;
	private Hashtable<String, String> translation = null;
	// now that's fun
	private ArrayList<Pair<Pair<Double, Double>, String>> ranges = null;
	
	private void mustBe(StreamTokenizer lex, int token) throws ThinklabException {
		
		int tok = 0;
		try {
			tok = lex.nextToken();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		if (tok != token) {
			throw new ThinklabValidationException("lookup table: syntax error in string: " + literal);
		}
		
	}
	
	protected void initialize(String s) throws ThinklabException {
	
		StringReader input = new StringReader(s);
		StreamTokenizer lex = new StreamTokenizer(input);
				
		lex.wordChars('=', '=');
		lex.wordChars('>','>');

		int token = 0;
		
		try {
			for (token = lex.nextToken(); token != StreamTokenizer.TT_EOF; token = lex.nextToken()) {
				
				String key = null;
				Pair<Double, Double> range = null;
				
				if (token == ';') {
					token = lex.nextToken();
					if (token == StreamTokenizer.TT_EOF)
						break;
				}
				
				if (token == '[') {
					Double min = null;
					Double max = null;
					
					token = lex.nextToken();
					if (token == StreamTokenizer.TT_NUMBER) {
						min = new Double(lex.nval);
						token = lex.nextToken();
					}

					if (token == ':') 
						token = lex.nextToken();

					if (token == StreamTokenizer.TT_NUMBER) {
						max = new Double(lex.nval);
						token = lex.nextToken();
					}
					
					if (token != ']')
						throw new ThinklabValidationException("range specification should be [min-max]");
		
					range = new Pair<Double, Double>(min, max);
						
				} else {
					key = lex.sval;
				}
				
				
				lex.nextToken(); 
				if (!lex.sval.equals("=>")) {
					throw new ThinklabValidationException("lookup table: each pair should be separated by =>");
				}
				
				token = lex.nextToken();
				String value = 
					token ==
						StreamTokenizer.TT_NUMBER ?
								removeZero(lex.nval):
								lex.sval;
								
				if (key != null) {
					
					if (translation == null)
						translation = new Hashtable<String, String>();
					
					translation.put(key, value);
					
				} else if (range != null) {
					
					if (ranges == null)
						ranges = new ArrayList<Pair<Pair<Double,Double>,String>>();
					
					ranges.add(new Pair<Pair<Double, Double>, String>(range, value));
				}
			}
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		/* check that we don't have strange mixes of keys and ranges */
		if (ranges != null && translation != null)
			throw new ThinklabValidationException(
					"lookup table: ranges are mixed with keys: only keys or ranges should be specified");
	}
	
	private String removeZero(double nval) {
		
		String s = Double.toString(nval);
		if (s.endsWith(".0"))
			s = s.substring(0,s.length() - 2);
		return s;
	}

	public LookupTable(String s) throws ThinklabException {
		initialize(s);
	}
	
	/**
	 * Look value up in table. If value matches one of the keys or is a number in one of the ranges, the
	 * correspondent value is returned. If no match is found, the original value is returned with no
	 * modification.
	 * 
	 * @param value
	 * @return
	 */
	public String lookup(String value) {
		
		String ret = value;
		
		if (ranges != null) {

			Double val = Double.parseDouble(value);
			for (Pair<Pair<Double, Double>, String> ks : ranges) {
				
				Double min = ks.getFirst().getFirst();
				Double max = ks.getFirst().getSecond();
				
				boolean ok = min == null || (val >= min);
				if (ok)
					ok = max == null || (val < max);
				
				if (ok) {
					ret = ks.getSecond();
					break;
				}
			}
			
		} else if (translation != null) {
			
			if (translation.contains(value))
				ret = translation.get(value);
			
		}
		
		return ret;
	}
	
	/**
	 * Lookup a double value in table with ranges.
	 * 
	 * @param val
	 * @return the associated result, or null if no range matches.
	 * @throws ThinklabValidationException if no ranges are defined.
	 */
	public String lookup(double val) throws ThinklabValidationException {
		
		String ret = null;
		
		if (ranges == null) {
			throw new ThinklabValidationException("lookup table does not contain ranges");
		}
		for (Pair<Pair<Double, Double>, String> ks : ranges) {
			
			Double min = ks.getFirst().getFirst();
			Double max = ks.getFirst().getSecond();
			
			boolean ok = min == null || (val >= min);
			if (ok)
				ok = max == null || (val < max);
					
				if (ok) {
					ret = ks.getSecond();
					break;
				}
			}
		 
		
		return ret;
	}
	
	public static void main(String[] args) {
		
		try {
			LookupTable t1 = new LookupTable("[-100:] => 1; [1:20] => 'ont:concept'; [21:] => 2");
			LookupTable t2 = new LookupTable("key1 => value1; key2 => value2");
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
