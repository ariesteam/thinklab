package org.integratedmodelling.corescience.literals;

import java.util.HashSet;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.AlgorithmValue;
import org.integratedmodelling.thinklab.literals.IntervalValue;

public class GeneralClassifier {

	GeneralClassifier[] classifiers = null;
	
	IntervalValue interval = null;
	HashSet<IConcept> concepts = null;
	AlgorithmValue code = null;
	IConcept target = null;
	
	public boolean classify(Object o) {
		
		if (classifiers != null) {
			for (GeneralClassifier cl : classifiers) {
				if (cl.classify(o))
					return true;
			}
		} else if (interval != null) {
			
			Double d = asNumber(o);
			if (d != null)
				return interval.contains(d);
			
		} else if (concepts != null) {
			
			IConcept co = asConcept(o);
			for (IConcept c : concepts) {
				if (c.is(co))
					return true;
			}
		} else if (code != null) {
			
		}
		
		return false;
	}

	private IConcept asConcept(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	private Double asNumber(Object o) {

		return null;
	}

	public void setClass(IConcept c) {
		target = c;
	}
	

	
}
