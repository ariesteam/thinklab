package org.integratedmodelling.modelling.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

/**
 * Built by the classification model. Fairly nasty to use otherwise, but very flexible and 
 * beautifully defined in Clojure.
 * 
 * @author Ferdinando
 *
 */
public class ModeledClassification 
	extends Observation 
	implements IConceptualModel, MediatingConceptualModel, IConceptualizable {
	
	ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	public class ClassificationAccessor implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(Object[] registers) {

			try {
				Object o = getDataSource().getValue(index++, registers);
				for (Pair<GeneralClassifier, IConcept> p : classifiers) {
					if (p.getFirst().classify(o))
						return p.getSecond();
				}
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
			return null;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IConcept observable)
				throws ThinklabValidationException {
			return false;
		}

		@Override
		public void notifyDependencyRegister(IConcept observable, int register,
				IConcept stateType) throws ThinklabValidationException {			
		}
	}
	
	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		return new ClassificationAccessor();
	}

	@Override
	public IConcept getStateType() {
		// must be computed
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		return null;
	}

	@Override
	public IStateAccessor getMediator(IConceptualModel conceptualModel,
			IConcept stateType, IObservationContext context)
			throws ThinklabException {
		return new ClassificationAccessor();
	}
}