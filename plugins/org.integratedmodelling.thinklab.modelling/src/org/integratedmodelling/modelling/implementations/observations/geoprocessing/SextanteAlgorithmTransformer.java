/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.modelling.implementations.observations.geoprocessing;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.InlineAccessor;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.geotools.GTOutputFactory;
import es.unex.sextante.outputs.Output;

/**
 * Facilitates using a Sextante algorithm in a model. Implement each algorithm as
 * a child of this one.
 * 
 * @author Ferdinando
 *
 */
public abstract class SextanteAlgorithmTransformer extends Observation implements
	IndirectObservation, TransformingObservation {

	private ObservationContext context;
	private IState outputState = null;

	protected abstract GeoAlgorithm getParameterizedAlgorithm();
	
	protected abstract String getResultID();

	protected abstract IState createOutputState(ObservationContext context);
	
	protected IState processOutput(Object o) {
		if (o instanceof IVectorLayer) {
			setStateFromVectorLayer(outputState, (IVectorLayer)o);
		} else if (o instanceof IRasterLayer) {
			setStateFromRasterLayer(outputState, (IRasterLayer)o);
		} 
		return outputState;
	}
	
	
	private void setStateFromRasterLayer(IState state, IRasterLayer layer) {
		Object dioporco = layer.getBaseDataObject();
		System.out.println("fobject is " + dioporco);
	}

	private void setStateFromVectorLayer(IState state, IVectorLayer layer) {
		Object dioporco = layer.getBaseDataObject();
		System.out.println("fobject is " + dioporco);		
	}

	@Override
	public IContext transform(IObservationContext inputContext,
			ISession session, IContext context) throws ThinklabException {

		GeoAlgorithm alg = getParameterizedAlgorithm();
		OutputFactory out = new GTOutputFactory();
		OutputObjectsSet outs = alg.getOutputObjects();
		Output result = null;
		try {
			result = outs.getOutput(getResultID());
		} catch (WrongOutputIDException e) {
			throw new ThinklabValidationException(e);
		}
		
		try {
			alg.execute(null, out);
		} catch (GeoAlgorithmExecutionException e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		ObservationContext ret = (ObservationContext)context.cloneExtents();;
		ret.setObservation(this);
		ret.addState(processOutput(result.getOutputObject()));
		return ret;
	}

	@Override
	public IConcept getTransformedObservationClass() {
		return CoreScience.Observation();
	}


	@Override
	public IConcept getStateType() {
		return outputState.getObservableClass();
	}

	@Override
	public void preContextualization(ObservationContext context,
			ISession session) throws ThinklabException {

		this.context = context;
		this.outputState = createOutputState(this.context);
		this.outputState.getMetadata().merge(this.metadata);
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		return outputState;
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return outputState == null ? null : new InlineAccessor(outputState);
	}
	
}

