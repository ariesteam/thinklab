package org.integratedmodelling.corescience.literals;

import java.util.Vector;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.AlgorithmValue;
import org.integratedmodelling.thinklab.literals.IntervalValue;

public class GeneralClassifier {

	Vector<GeneralClassifier> classifiers = null;
	Double number = null;
	IntervalValue interval = null;
	IConcept concept = null;
	AlgorithmValue code = null;
	String string = null;
	private boolean catchAll = false;
	
	public GeneralClassifier(String s) throws ThinklabException {
		parse(s);
	}
	
	public GeneralClassifier() {
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
			// TODO
		} else if (selector.equals("str:")) {
			string = def;
		} else if (selector.equals("tru:")) {
			catchAll = true;
		}
	}
	
	public boolean isUniversal() {
		return catchAll;
	}
	
	public boolean classify(Object o) {

		if (catchAll) {
			return true;
		}
		
		if (number != null) {
			
			return number.equals(asNumber(o));
			
		} else if (classifiers != null) {
			
			for (GeneralClassifier cl : classifiers) {
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

		} else if (code != null) {
		
			/*
			 * TODO
			 */
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
	
	public void addClassifier(GeneralClassifier c) {
		if (classifiers == null)
			classifiers = new Vector<GeneralClassifier>();
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
			for (GeneralClassifier c : classifiers) {
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
		} else if (catchAll)
			ret = "tru:true";
		return ret;
	}

	public void setCatchAll() {
		this.catchAll = true;
	}

	public void setString(String classifier) {
		string = classifier;
	}
	
}
