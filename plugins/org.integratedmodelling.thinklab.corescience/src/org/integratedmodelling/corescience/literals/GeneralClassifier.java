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
			
		} else if (concepts != null) {
			
		} else if (code != null) {
			
		}
		
		return false;
	}

	public void setClass(IConcept c) {
		target = c;
	}
	

	
}
