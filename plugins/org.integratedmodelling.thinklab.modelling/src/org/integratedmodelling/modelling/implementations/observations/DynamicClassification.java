package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.data.adapters.ClojureAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;

import clojure.lang.IFn;

@InstanceImplementation(concept="modeltypes:DynamicClassification")
public class DynamicClassification extends ModeledClassification {

	public IFn code = null;
	public Object change = null;

	String lang = "clojure";
	
	class ClojureClassificationAccessor extends ClojureAccessor {

		public ClojureClassificationAccessor(IFn code, Observation obs,
				boolean isMediator, IObservationContext context, IFn change) {
			super(code, obs, isMediator, context, change, null);
		}

		@Override
		protected Object processMediated(Object object) {
			
			if (object == null && !hasNilClassifier)
				return null;
			
			for (Pair<GeneralClassifier, IConcept> p : classifiers) {
				if (p.getFirst().classify(object))
					return p.getSecond();
			}

			return object;
		}
	}
	
	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new ClojureClassificationAccessor(code, this, false, context, (IFn)change);
	}
		
	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {
		return (code == null && change == null) ? 
					new ClassificationMediator() :
				    new ClojureClassificationAccessor(code, this, true, context, (IFn)change);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Measurement#initialize(org.integratedmodelling.thinklab.interfaces.knowledge.IInstance)
	 */
	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);
		IValue lng = i.get("modeltypes:hasExpressionLanguage");
		if (lng != null)
			this.lang = lng.toString().toLowerCase();
		if (change != null) {
			acceptsDiscontinuousTopologies = false;
		}
	}

}
