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
package org.integratedmodelling.thinklab.modelling.classification;

import java.util.Vector;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassifier;
import org.integratedmodelling.thinklab.literals.IntervalValue;

import clojure.lang.IFn;

/**
 * A powerful classifier of objects meant to be defined from a Clojure classification model.
 * Can be serialized to an ugly string so that observations can be built easily, but it is
 * not meant to be used outside of a model context.
 */
public class Classifier implements IClassifier {

	Vector<Classifier> classifiers = null;
	Double number = null;
	IntervalValue interval = null;
	IConcept concept = null;
	IExpression code = null;
	String string = null;
	
	/*
	 * if true, this is an :otherwise classifier, that needs to be known
	 * by the classification
	 */
	private boolean catchAll = false;
	
	/* if true, this is a classifier specifically meant to reclassify nil/null;
	 * normally, nil does not reclassify unless there is one of these in a 
	 * classification.
	 */
	private boolean isNil = false;
	
	/**
	 * @deprecated find a way to use an IExpression
	 */
	private IFn closure = null;
	
	public Classifier(String s) throws ThinklabException {
		parse(s);
	}
	
	public Classifier() {
	}

	public void parse(String s) throws ThinklabException {
		
		String selector = s.substring(0,4);
		String def = s.substring(4);
		
		if (selector.equals("num:")) {
			number = Double.parseDouble(def);
		} else if (selector.equals("int:")) {
			interval = new IntervalValue(def);
		} else if (selector.equals("con:")) {
			concept = KnowledgeManager.get().requireConcept(def);
		} else if (selector.equals("mul:")) {
			parseMultiple(def);
		} else if (selector.equals("str:")) {
			string = def;
		} else if (selector.equals("tru:")) {
			catchAll = true;
		} else if (selector.equals("nil:")) {
			isNil = true;
		}
	}
	
	private void parseMultiple(String def) throws ThinklabException {
		
		/*
		 * first character must be a square bracket; read up until matching closing bracket
		 */
		if (def.charAt(0) != '[') {
			throw new ThinklabValidationException("syntax error in multiple classifier: classifiers must appear in square brackets");
		}
		
		int level = 0;
		int len = def.length();

		StringBuffer buf = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			char c = def.charAt(i);
			if (c == '[') {
				if (level > 0) {
					buf.append(c);
				}
				level++;
			} else if (c == ']') {
				level--;
				if (level == 0) {
					addClassifier(new Classifier(buf.toString()));
					buf = new StringBuffer(len);
				} else {
					buf.append(c);
				}
			} else {
				buf.append(c);
			}
		}
	}

	@Override
	public boolean isUniversal() {
		return catchAll;
	}
	
	@Override
	public boolean classify(Object o) {

		if (catchAll && o != null && !(o instanceof Double && Double.isNaN((Double)o))) {
			return true;
		}

		if (o == null)
			return isNil;
		
		if (number != null) {
			
			return number.equals(asNumber(o));
			
		} else if (classifiers != null) {
			
			for (Classifier cl : classifiers) {
				if (cl.classify(o))
					return true;
			}
			
		} else if (interval != null) {
			
			Double d = asNumber(o);
			if (d != null)
				return interval.contains(d);
			
		} else if (concept != null) {

			return asConcept(o).is(concept);

		} else if (string != null) { 

			return string.equals(o.toString());

		} else if (closure != null) {
		
			try {
				/*
				 * TODO find an elegant way to communicate external
				 * parameter maps, and set :self = o in it.
				 */
				//return (Boolean)closure.invoke(o);
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		return false;
	}

	private IConcept asConcept(Object o) {
		
		if (o instanceof IConcept)
			return (IConcept)o;

		IConcept ret = null;
		try {
			ret = KnowledgeManager.get().requireConcept(o.toString());
		} catch (Exception e) {
			throw new ThinklabRuntimeException("cannot match " + o + " to a concept name for classification");
		}
		
		return ret;
	}

	private Double asNumber(Object o) {

		Double ret = null;
		if (o instanceof Integer) {
			ret = (double)((Integer)o);
		} else if (o instanceof Double) {
			ret = (Double)o;
		} else if (o instanceof Float) {
			ret = (double)((Float)o);
		} else if (o instanceof Long) {
			ret = (double)((Long)o);
		} 
		return ret;
	}
	
	public void addClassifier(Classifier c) {
		if (classifiers == null)
			classifiers = new Vector<Classifier>();
		classifiers.add(c);
	}

	public void setConcept(IConcept c) {
			concept = c;
	}

	public void setInterval(IntervalValue interval) {
		this.interval = interval;
	}

	public void setNumber(Object classifier) {
		number = asNumber(classifier);
	}
	
	public String toString() {
		String ret = null;
		if (classifiers != null) {
			ret = "mul:";
			for (Classifier c : classifiers) {
				ret += "[" + c + "]";
			}
		} else if (number != null) {
			ret = "num:" + number;
		} else if (interval != null) {
			ret = "int:" + interval;
		} else if (concept != null) {
			ret = "con:" + concept;
		} else if (string != null) {
			ret = "str:" + string;
		} else if (catchAll) {
			ret = "tru:true";
		} else if (isNil) {
			ret = "nil:true";
		}
		return ret;
	}

	public void setCatchAll() {
		this.catchAll = true;
	}

	public void setString(String classifier) {
		this.string = classifier;
	}

	public void setNil() {
		this.isNil = true;
	}

	@Override
	public boolean isInterval() {
		return interval != null;
	}

	public IntervalValue getInterval() {
		return interval;
	}

	@Override
	public boolean isNil() {
		return this.isNil;
	}

	public void setClosure(IFn closure) {
		this.closure = closure;
	}

	@Override
	public String asText() {
		return toString();
	}
	
}
