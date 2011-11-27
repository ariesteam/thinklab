package org.integratedmodelling.corescience.interfaces;

import java.util.List;

import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

public interface IProbabilisticObservation {

	List<Pair<GeneralClassifier, IConcept>> getClassifiers();

}
