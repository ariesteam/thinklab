/**
 * ArealLocationConceptualModel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.implementations.cmodels;

import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.ShapeExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.LogicalConnector;
import org.jscience.mathematics.number.Rational;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ArealLocationConceptualModel extends SpatialConceptualModel {

	Geometry shape = null;
	
	public ArealLocationConceptualModel(ShapeValue dataSource) {
		shape = dataSource.getGeometry();
	}

	public IExtent getExtent() throws ThinklabException {
		return new ShapeExtent(shape, getCRS(), this);
	}

	public IExtentMediator getExtentMediator(IExtent extent)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtent mergeExtents(IExtent original, IExtent other,
			LogicalConnector connector)
			throws ThinklabContextualizationException, ThinklabException {
		
		/*
		 * FIXME move this to mergeRemainingExtentParameters
		 */
		
		return 
			connector.equals(LogicalConnector.UNION) ?
				new ShapeExtent(((ShapeExtent)original).getShape().union(((ShapeExtent)other).getShape()), getCRS(), this):
				new ShapeExtent(((ShapeExtent)original).getShape().intersection(((ShapeExtent)other).getShape()), getCRS(), this);
	}

	public IValueAggregator getAggregator(IObservationContext ownContext,
			IObservationContext overallContext) {
		// TODO Auto-generated method stub
		return null;
	}


	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validate(IObservation observation)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub

	}

	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue validateValue(IValue value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getObjectName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setObjectName(String name) {
		// TODO Auto-generated method stub

	}

	public IValue partition(IValue originalValue, Rational ratio) {
		return null;
	}

	public IValue validateData(byte b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid areal location from a number");
	}

	public IValue validateData(int b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid areal location from a number");
	}

	public IValue validateData(long b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid areal location from a number");
	}

	public IValue validateData(float b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid areal location from a number");
	}

	public IValue validateData(double b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid areal location from a number");
	}

	public void initialize(IInstance i) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	public void validate(IInstance i) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IExtent createMergedExtent(ArealExtent orextent,
			ArealExtent otextent, CoordinateReferenceSystem crs2,
			Envelope common, boolean isConstraint)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
