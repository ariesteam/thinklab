package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Classification;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.modelling.data.adapters.ClojureAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

import clojure.lang.IFn;

@InstanceImplementation(concept="modeltypes:DynamicClassification")
public class DynamicClassification extends Classification {

	public IFn code = null;
	public Object change = null;
	public Object derivative = null;

	String lang = "clojure";
	
	class ClojureClassificationAccessor extends ClojureAccessor {

		public ClojureClassificationAccessor(IFn code, Observation obs,
				boolean isMediator, IObservationContext context, IFn change, IFn derivative) {
			super(code, obs, isMediator, context, change, derivative);
		}

		@Override
		protected Object processMediated(Object object) {
			// TODO this should have its own classifiers and mediate to them before calling the code
			return object;
		}
	}
	
	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new ClojureClassificationAccessor(code, this, false, context, (IFn)observation, (IFn)context);
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
	}

}
