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
package org.integratedmodelling.corescience.observation.measurement;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.exceptions.ThinklabInconsistentConceptualModelException;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.corescience.values.UnitValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.multidimensional.MultidimensionalArray;
import org.jscience.mathematics.number.Rational;


/**
 * Extends a Unit value to make it a suitable conceptual model for a measurement. That
 * encapsulates all the unit conversion functions that happen automatically whenever two
 * measurement values are viewed through a different model.
 * @author UVM Affiliate
 *
 */
public class MeasurementModel extends UnitValue implements IConceptualModel {
	
	String id = null;
	
	/**  
	 * Simple aggregator uses physical nature to decide how to aggregate properties.
	 */ 
	public class MeasurementAggregator implements IValueAggregator {

		double val = 0.0;
		boolean isnew = true;
		IUncertainty unc = null;
		PhysicalNature nature = null;
		
		public MeasurementAggregator(PhysicalNature nature) {
			this.nature = nature;
		}
		
		// FIXME uncertainty is thrown in without even thinking.
		public void addValue(IValue value, IUncertainty uncertainty, IObservationContextState contextState) throws ThinklabException {

			if (!value.isNumber()) 
				throw new ThinklabValidationException("non-numerical ranking cannot be aggregated");
			
			if (uncertainty != null) {
				if (unc != null)
					unc.compound(uncertainty);
				else unc = uncertainty;
			}
			
			if (nature == PhysicalNature.EXTENSIVE) {
				val += value.asNumber().asDouble();
			} else {
				val = 
					isnew ? 
							value.asNumber().asDouble() : 
							(val + value.asNumber().asDouble())/2;
			}
			
			isnew = false;
		}

		public Pair<IValue, IUncertainty> aggregateAndReset() throws ThinklabException {
			
			Pair<IValue,IUncertainty> ret = 
				new Pair<IValue, IUncertainty>(new NumberValue(val), null);
			val = 0.0;
			unc = null;
			return ret;
		}
		
	}

	public MeasurementModel(IConcept c, String s) throws ThinklabException {
		super(c, s);
		// TODO Auto-generated constructor stub
	}

	int[] dimensions = null;
	MultidimensionalArray<NumberValue> data =  null;
	PhysicalNature physicalNature = null;
	
	protected IKnowledgeSubject observable;
	
	/*
	 * TODO this one should be smarter, also checking the possibility of dimensional
	 * folding along extents.
	 */
	private void checkModels(IConceptualModel foreignModel) throws ThinklabInconsistentConceptualModelException {
		if (
			!(foreignModel instanceof MeasurementModel) ||
			!((MeasurementModel)foreignModel).observable.is(observable))
			// FIXME better error message
		throw new ThinklabInconsistentConceptualModelException("measurement observables are incompatible");
		
		/* units must also be compatible */
//		if (!unit.isCompatible(((MeasurementModel)foreignModel).unit)) {
//			throw new ThinklabInconsistentConceptualModelException("cannot convert " + toString() + 
//					"  to " + foreignModel + ": incompatible units");			
//		}
	}

	public IConcept getStateType() {
		return KnowledgeManager.Double();
	}


//	public Object createValues(IDataSource dataSource, ObservationContext context) throws ThinklabValidationException {
//		
//		/* create storage */
//		data = new MultidimensionalArray<NumberValue>(dimensions);
//
//		/* expose the datasource to the OC just in case */
////		dataSource.notifyObservationContext(context);
//		
//		/* and load the values using a double model */
//		try {
//			for (int i = 0; i < data.size(); i++) {
//				data.set(i, dataSource.getValue(i, getStorageModel()).asNumber());
//			}
//		} catch (ThinklabValueConversionException e) {
//			throw new ThinklabValidationException(e);
//		}
//
//		return data;
//
//	}
		
//		/*
//		 * TODO we should analyze the dimensionality vs. the context here,
//		 * mapping units below the fraction to context dimensions. This would
//		 * enable us to properly aggregate/disaggregate, as well as (e.g.)
//		 * "decontextualize" quantities when a compatible non-distributed model
//		 * is passed to one of the functions with a foreignModel (dimensional folding).
//		 * 
//		 * E.g. if we are kg/m^2 and we are
//		 * passed kg of a compatible observable, we should know that the m^2
//		 * unit is linked to the spatial context and aggregate over it, reducing
//		 * the dimensionality as necessary. This is a little tough but we'll get
//		 * there.
//		 */
//		this.observable = observable;
//		this.observationContext = observationContext;
//
//		/*
//		 * validate observable. This should be redundant if OWL validation is
//		 * working, but no big deal to add it.
//		 */
//		if (!observable.is(OntologyBridge.PhysicalProperty)) {
//			throw new ThinklabValidationException("measurements can only be of physical properties: " + observable);
//		}
//		
//		/*
//		 * determine if intensive or extensive from the semantics of the observable,
//		 * and communicate to the conceptual model. 
//		 */
//		physicalNature = 
//			observable.is(OntologyBridge.ExtensivePhysicalProperty) ?
//			PhysicalNature.EXTENSIVE :	
//			PhysicalNature.INTENSIVE;
//
//		/*
//		 * process the context for all required info
//		 */
//		dimensions = observationContext.getDimensionality();
//		
//		/*
//		 * TODO this is also the place to ensure that the unit we represent is
//		 * compatible with the nature of the observable. Meaning the unit above
//		 * the fraction must reflect the base dimension in the Obs ontology, and,
//		 * that any contextualization is compatible with the extensive/intensive
//		 * nature of the observable.
//		 */
		
//	public IValueMediator getContextAdaptor(IObservationContext overallContext, IDataSource datasource) {

//		if ( !(otherModel instanceof MeasurementModel))
//			throw new ThinklabInconsistentConceptualModelException("cannot convert " + toString() + "  to " + otherModel);
//
//
//		checkModels(otherModel);
//
//		UnitConverter converter = unit.getConverterTo(((MeasurementModel)otherModel).unit);
//		
//		for (int i = 0; i < data.size(); i++) {
//			
//			NumberValue v = data.get(i);
//			v.value = converter.convert(v.value);
//			data.set(i, v);		}
//		

//		return null;
//	}

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

	public IValueMediator getMediator(IConceptualModel conceptualModel, IObservationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue partition(IValue originalValue, Rational ratio) {
		// TODO Auto-generated method stub
		return null;
	}

	public IValueAggregator getAggregator(IObservationContext ownContext, IObservationContext overallContext) {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		
		// TODO check boundaries and stuff if any required
		return new NumberValue(Double.parseDouble(value));
	}

	public void setObjectName(String name) {
		id = name;
	}

	public String getObjectName() {
		return id;
	}

	public IValue validateValue(IValue value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO more validation
		return value;
	}

	public IValue validateData(byte b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(int b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(long b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(float b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(double b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	@Override
	public IConcept getUncertaintyType() {
		// FIXME this should change obviously.
		return KnowledgeManager.Nothing();
	}

}
