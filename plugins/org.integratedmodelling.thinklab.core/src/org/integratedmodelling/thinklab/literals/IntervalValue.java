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
package org.integratedmodelling.thinklab.literals;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.knowledge.SemanticLiteral;

/**
 * A numeric interval parsed from conventional syntax (e.g. "[12 34)" )
 * @author Ferdinando Villa
 *
 */
@Literal(concept=NS.NUMERIC_INTERVAL, datatype="", javaClass=IntervalValue.class)
public class IntervalValue extends SemanticLiteral implements IParseable {

	double lowerBound = 0.0;
	double upperBound = 0.0;
	boolean isLowerOpen = false;
	boolean isUpperOpen = false;
	boolean isLowerUndefined = true;
	boolean isUpperUndefined = true;
	
    public void wrap(Object o) {
//    	value = (Boolean)o;
    }

	public IntervalValue() {
		try {
			setConcept(KnowledgeManager.get().requireConcept(NS.NUMERIC_INTERVAL));
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	public IntervalValue(String intvs) throws ThinklabException {
		parse(intvs);
	}

	public IntervalValue(Double left, Double right, boolean leftOpen, boolean rightOpen) {

		if (!( isLowerUndefined = (left == null))) 
			lowerBound = left;
		if (!( isUpperUndefined = (right == null))) 
			upperBound = right;

		isLowerOpen = leftOpen;
		isUpperOpen = rightOpen;
	}
	
	@Override
	public void parse(String s) throws ThinklabException {

		StreamTokenizer scanner = new StreamTokenizer(new StringReader(s));
		int token = 0;
		double high = 0.0, low = 0.0;
		int nnums = 0;
		boolean lowdef = false, highdef = false;
		
		while (true) {

			try {
				token = scanner.nextToken();
			} catch (IOException e) {
				throw new ThinklabValidationException("invalid interval syntax: " + s);
			}

			if (token == StreamTokenizer.TT_NUMBER) {
			
				if (nnums > 0) {
					high = scanner.nval;
				} else {
					low = scanner.nval;
				}
				nnums ++;
				
			} else if (token == StreamTokenizer.TT_EOF || token == StreamTokenizer.TT_EOL) {
				break;
			} else  if (token == '(') {
				if (nnums > 0) 
					throw new ThinklabValidationException("invalid interval syntax: " + s);
				lowdef = true;
				isLowerOpen = true;
			} else  if (token == '[') {
				if (nnums > 0) 
					throw new ThinklabValidationException("invalid interval syntax: " + s);
				lowdef = true;
				isLowerOpen = false;
			} else  if (token == ')') {
				if (nnums == 0) 
					throw new ThinklabValidationException("invalid interval syntax: " + s);
				highdef = true;
				isUpperOpen = true;
			} else  if (token == ']') {
				if (nnums == 0) 
					throw new ThinklabValidationException("invalid interval syntax: " + s);
				highdef = true;
				isUpperOpen = false;
			} else  if (token == ',') {
				/* accept and move on */
			} else {
				throw new ThinklabValidationException("invalid interval syntax: " + s);
			}			
		}
		
		/*
		 * all read, assemble interval info
		 */
		if (lowdef && highdef && nnums == 2) {
			isLowerUndefined = isUpperUndefined = false;
			lowerBound = low;
			upperBound = high;
		} else if (lowdef && !highdef && nnums == 1) {
			isLowerUndefined = false;
			lowerBound = low;
		} else if (highdef && !lowdef && nnums == 1) {
			isUpperUndefined = false;
			upperBound = low;
		} else {
			throw new ThinklabValidationException("invalid interval syntax: " + s);
		}
	}

	public int compare(IntervalValue i) {
		
		if (isLowerUndefined == i.isLowerUndefined &&
				isLowerOpen == i.isLowerOpen &&
				isUpperUndefined == i.isUpperUndefined &&
				isUpperOpen == i.isUpperOpen &&
				lowerBound == i.lowerBound &&
				upperBound == i.upperBound)
			return 0;
		
		if (this.upperBound <= i.lowerBound)
			return -1;

		if (this.lowerBound >= i.upperBound)
			return 1;
		
		throw new ThinklabRuntimeException("error: trying to sort overlapping numeric intervals");
		
	}
	
	public boolean isRightInfinite() {
		return isUpperUndefined;
	}

	public boolean isLeftInfinite() {
		return isLowerUndefined;
	}

	/**
	 * true if the upper boundary is closed, i.e. includes the limit
	 * @return
	 */
	public boolean isRightBounded() {
		return !isUpperOpen;
	}

	/**
	 * true if the lower boundary is closed, i.e. includes the limit
	 * @return
	 */
	public boolean isLeftBounded() {
		return !isLowerOpen;
	}
	
	public double getMinimumValue() {
		return lowerBound;
	}

	public double getMaximumValue() {
		return upperBound;
	}

	public boolean contains(double d) {

		if (isLowerUndefined)
			return (isUpperOpen ? d < upperBound : d <= upperBound);
		else if (isUpperUndefined)
			return (isLowerOpen ? d > lowerBound : d >= lowerBound);
		else 
			return
				(isUpperOpen ? d < upperBound : d <= upperBound) &&
				(isLowerOpen ? d > lowerBound : d >= lowerBound);	
	}

	@Override
	public String toString() {
	
		String ret = "";
		
		if (!isLowerUndefined) {
			ret += isLowerOpen ? "(" : "[";
			ret += lowerBound;
		}
		if (!isUpperUndefined) {
			if (!isLowerUndefined) 
				ret += " ";
			ret += upperBound;
			ret += isUpperOpen ? ")" : "]";
		}
		
		return ret;
	}

	@Override
	public Semantics getSemantics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean is(ISemanticObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return null;
	}

}
