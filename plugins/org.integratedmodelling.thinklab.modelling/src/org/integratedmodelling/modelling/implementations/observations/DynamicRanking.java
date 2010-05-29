package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.implementations.observations.Ranking;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.modelling.data.adapters.ClojureAccessor;
import org.integratedmodelling.modelling.data.adapters.MVELAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

import clojure.lang.IFn;

@InstanceImplementation(concept="modeltypes:DynamicRanking")
public class DynamicRanking extends Ranking {

	public Object code = null;
	public Object change = null;
	public Object derivative = null;

	String lang = "clojure";
	
	class ClojureRankingAccessor extends ClojureAccessor {

		RankingMediator mediator = null;
		
		public ClojureRankingAccessor(IFn code, Observation obs,
				boolean isMediator, IndirectObservation mediated, 
				IObservationContext context, IFn change, IFn derivative) {
			super(code, obs, isMediator, context, change, derivative);
			if (mediated != null)
				try {
					mediator = new RankingMediator(mediated);
				} catch (ThinklabValidationException e) {
					throw new ThinklabRuntimeException(e);
				}
				
			if (isConstant)
				setInitialValue(value);
			else if (distribution != null)
				setInitialValue(distribution);

		}

		@Override
		protected Object processMediated(Object object) {
			return mediator == null ? object : mediator.convert(((Number)object).doubleValue());
		}
		
	}
	
	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		if (lang.equals("clojure"))
			return new ClojureRankingAccessor((IFn)code, this, false, null, context, (IFn)change, (IFn)derivative);
		else
			return new MVELAccessor((String)code, false);
	}

	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {
		if (lang.equals("clojure"))
			return new ClojureRankingAccessor((IFn)code, this, true, null, context, (IFn)change, (IFn)derivative);
		else
			return new MVELAccessor((String)code, true);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Measurement#initialize(org.integratedmodelling.thinklab.interfaces.knowledge.IInstance)
	 */
	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);

		IValue cd = i.get("modeltypes:hasStateFunction");
		if (cd != null) {
			this.code = cd.toString();
			this.lang = "MVEL";
		}
		IValue lng = i.get("modeltypes:hasExpressionLanguage");
		if (lng != null)
			this.lang = lng.toString().toLowerCase();
	}

}
