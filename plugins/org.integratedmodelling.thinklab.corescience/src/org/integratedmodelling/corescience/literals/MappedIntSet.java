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
package org.integratedmodelling.corescience.literals;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

/**
 * A mapped classification remaps any concept within a specified set of concepts to a single concept.
 * This one can be specified as a literal value of the form c<-c1,c2,c3. The concept of the value
 * is the mapped concept. 
 * 
 * @author Ferdinando
 *
 */
public class MappedIntSet extends ParsedLiteralValue {

	Set<Integer> cset = new HashSet<Integer>();
	Integer mapped = null;
	String sform = null;
	
	public MappedIntSet(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {

		sform = s;
		
		int idx = s.indexOf(":");
		if (idx < 0)
			throw new ThinklabValidationException("invalid mapped classification syntax: " + s);
		
		String iset = s.substring(0, idx).trim();
		String[] classes = s.substring(idx+1).trim().split(",");
		
		for (String ss : classes) {
			cset.add(Integer.parseInt(ss));
		}
		
		mapped = Integer.parseInt(iset);

	}

	public boolean contains(Integer c) {
		return cset.contains(c);
	}

	public int getValue() {
		return mapped;
	}
	
	public String toString() {
		return sform;
	}
}
