/**
 * MeasurementModel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.corescience.implementations.cmodels;

import java.util.Properties;

import javax.measure.unit.Unit;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.data.ResamplingDataSource;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.UnitValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IParseable;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.utils.multidimensional.MultidimensionalArray;
import org.jscience.mathematics.number.Rational;

import sun.security.x509.IssuingDistributionPointExtension;


/**
 * Extends a Unit value to make it a suitable conceptual model for a measurement. That
 * encapsulates all the unit conversion functions that happen automatically whenever two
 * measurement values are viewed through a different model.
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="measurement:Unit")
public class MeasurementModel extends UnitValue implements 
		IConceptualModel, MediatingConceptualModel, IParseable, IInstanceImplementation {
	
	public enum PhysicalNature {
		EXTENSIVE,
		INTENSIVE
	}
	
	String id = null;
	IDataSource<?> dataSource = null;
	int[] dimensions = null;
	MultidimensionalArray<NumberValue> data =  null;
	PhysicalNature physicalNature = null;
	
	protected IKnowledgeSubject observable;

	/*
	 * if not null, a value has been passed and we have no datasource
	 */
	Double inlineValue = null;
	private IRandomValue inlineRandom = null;
	
	/**  
	 * Simple aggregator uses physical nature to decide how to aggregate properties.
	 */ 
	public class MeasurementAggregator implements IValueAggregator<Double> {

		double val = 0.0;
		boolean isnew = true;
		PhysicalNature nature = null;
		
		public MeasurementAggregator(PhysicalNature nature) {
			this.nature = nature;
		}
		
		public void addValue(Double value, IObservationContextState contextState) throws ThinklabException {
			
			if (nature == PhysicalNature.EXTENSIVE) {
				val += (Double)value;
			} else {
				val = 
					isnew ? 
							(Double)value : 
							(val + (Double)value)/2;
			}
			
			isnew = false;
		}

		public Double aggregateAndReset() throws ThinklabException {
			
			double ret = val;
			val = 0.0;
			return ret;
		}


		@Override
		public Double partition(Double originalValue, Rational ratio) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public MeasurementModel() throws ThinklabException {
	}
	
	public MeasurementModel(IConcept c, String s) throws ThinklabException {
		super(c, s);
	}
	
	public IConcept getStateType() {
		return inlineRandom == null ? KnowledgeManager.Double() : CoreScience.get().RandomValue();
	}

	public void validate(IObservation observation) throws ThinklabValidationException {

		/*
		 * TODO we should analyze the dimensionality vs. the context here,
		 * mapping units below the fraction to context dimensions. This would
		 * enable us to properly aggregate/disaggregate, as well as (e.g.)
		 * "decontextualize" quantities when a compatible non-distributed model
		 * is passed to one of the functions with a foreignModel (dimensional folding).
		 * 
		 * E.g. if we are kg/m^2 and we are
		 * passed kg of a compatible observable, we should know that the m^2
		 * unit is linked to the spatial context and aggregate over it, reducing
		 * the dimensionality as necessary. This is a little tough but we'll get
		 * there.
		 */
		this.observable = observation.getObservable();
		
		/*
		 * validate observable. This should be redundant if OWL validation is
		 * working, but no big deal to add it.
		 */
		if (!observable.is(CoreScience.PHYSICAL_PROPERTY)) {
			throw new ThinklabValidationException("measurements can only be of physical properties: " + observable);
		}
		
		/*
		 * determine if intensive or extensive from the semantics of the observable,
		 * and communicate to the conceptual model. 
		 */
		physicalNature = 
			observable.is(CoreScience.EXTENSIVE_PHYSICAL_PROPERTY) ?
			PhysicalNature.EXTENSIVE :
			PhysicalNature.INTENSIVE;
		
	}


	public void setObjectName(String name) {
		id = name;
	}

	public String getObjectName() {
		return id;
	}

	public void setInlineValue(double val) {
		inlineValue = val;
	}
	
	public void setInlineValue(IRandomValue val) {
		inlineRandom = val;
	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context) {

		if (inlineValue != null)
			return new MeasurementStateAccessor(inlineValue);
		else if (inlineRandom != null)
			return new MeasurementStateAccessor(inlineRandom);
		else if (dataSource != null) 
			return new MeasurementStateAccessor(dataSource);
		
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {

		/*
		 * ensure we understand each other's dimensionalities; if the DS is interpolating,
		 * communicate how to interpolate and ask it to do so.
		 */
		
		/*
		 * store DS for accessor
		 */
		this.dataSource = dataSource;
		
		if (dataSource instanceof ResamplingDataSource) {
			// TODO we want to tell the ds how to resample according to what we represent
		}
		
	}

	@Override
	public IStateAccessor getMediator(IConceptualModel oc,
			IConcept stateType, IObservationContext context)
			throws ThinklabException {
		return new MeasurementStateMediator((MeasurementModel)oc, this);	
	}

	@Override
	public void parseSpecifications(IInstance inst, String literal) {
		this.unit = Unit.valueOf(literal);
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
	}


}
