package org.integratedmodelling.modelling.interfaces;

import java.util.ArrayList;

import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

public interface ClassifyingObservation {

	public ArrayList<Pair<GeneralClassifier, IConcept>> getClassifiers();
	
}

