package org.integratedmodelling.thinklab.transformations;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.ConfigurableIntelligentMap;

/**
 * Maps concepts to data transformations to apply when data in states are returned as doubles. 
 * Properties to map concepts to transformations should have the form
 * 
 * thinklab.transformation.mapping.representation-LogDistributedState = log10
 *
 * which assumes a log10 transformation was defined.
 *  
 * @author Ferdinando
 *
 */
public class TransformationChooser extends ConfigurableIntelligentMap<ITransformation> {

	public TransformationChooser() {
		super("thinklab.transformation.mapping.");
	}

	@Override
	protected ITransformation getObjectFromPropertyValue(String pvalue, Object[] parameters) 
		throws ThinklabException {
		return TransformationFactory.get().requireTransformation(pvalue, parameters);
	}
}
