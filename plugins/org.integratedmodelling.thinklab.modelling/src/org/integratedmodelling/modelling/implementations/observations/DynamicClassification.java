package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Classification;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.modelling.data.adapters.ClojureAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@InstanceImplementation(concept="modeltypes:DynamicClassification")
public class DynamicClassification extends Classification {

	String code = null;
	String lang = "clojure";
	
	@Override
	public IStateAccessor getAccessor() {
		return new ClojureAccessor(code, false);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Measurement#initialize(org.integratedmodelling.thinklab.interfaces.knowledge.IInstance)
	 */
	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);
		this.code = i.get("modeltypes:hasStateFunction").toString();
		IValue lng = i.get("modeltypes:hasExpressionLanguage");
		if (lng != null)
			this.lang = lng.toString().toLowerCase();
	}

}
