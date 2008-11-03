/**
 * KeyValueMap.java
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

import java.util.HashMap;

import org.integratedmodelling.thinklab.value.BooleanValue;

/**
 * A simple class that parses a key-value string and initializes a map to its values.
 * TODO uses stupid "split" method and won't work with embedded spaces in values. Should be
 * redone with smarter tokenizer.
 * 
 * @author Ferdinando Villa
 *
 */
public class KeyValueMap extends HashMap<String, String> {

	private static final long serialVersionUID = 1123680512640721726L;

	private void initialize(String s) {
		
		String[] pairs = s.trim().split(" ");
		for (String p : pairs) {
			addPair(p);
		}
	}
	
	public void addPair(String p) {

		String[] kv = p.split("=");
		if (kv.length == 2) {
			String v = kv[1];
			if (v.startsWith("\"") || v.startsWith("'")) {
				v = v.substring(1);
			}
			if (v.endsWith("\"") || v.endsWith("'")) {
				v = v.substring(0, v.length()-1);
			}
			put(kv[0], v);
		}
	}
	
	public KeyValueMap() {
		
	}
	
	public KeyValueMap(String string) {
		initialize(string);
	}
	
	public static void main(String[] args) {
		
		KeyValueMap kv = new KeyValueMap(" dio=\"ciao\" ostia=\"1.0\"" );
		
		for (String entry : kv.keySet()) {
			
			System.out.println(entry + " = " + kv.get(entry));
			
		}
	}
	
	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public float getFloat(String key) {
		return Float.parseFloat(get(key));
	}
	
	public double getDouble(String key) {
		return Double.parseDouble(get(key));
	}
	
	public boolean getBoolean(String key) {
		return BooleanValue.parseBoolean(get(key));
	}
	
}
