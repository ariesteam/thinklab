package org.integratedmodelling.modelling.data.transformation;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.ConfigurableIntelligentMap;

/**
 * Maps concepts to data transformations to apply when data in states are returned as doubles.
 * 
 * @author Ferdinando
 *
 */
public class TransformationChooser extends ConfigurableIntelligentMap<Transformation> {

	public TransformationChooser() {
		super("thinklab.data.transformation.");
	}

	@Override
	protected Transformation getObjectFromPropertyValue(String pvalue, Object[] parameters) 
		throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
}
