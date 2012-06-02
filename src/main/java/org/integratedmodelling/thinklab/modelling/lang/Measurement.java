package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IMediatingObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.IUnit;
import org.integratedmodelling.thinklab.api.modelling.parsing.IMeasuringObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IUnitDefinition;
import org.integratedmodelling.thinklab.modelling.Unit;

@Concept(NS.MEASURING_OBSERVER)
public class Measurement extends Observer<Measurement> implements IMeasuringObserverDefinition, IMediatingObserver {

	@Property(NS.HAS_UNIT_DEFINITION)
	IUnitDefinition _unitDefinition;
	
	IUnit _unit;

	public IUnit getUnit() {
		return _unit;
	}

	@Override
	public void setUnit(IUnitDefinition unit) {
		_unitDefinition = unit;
	}

	@Override
	public Measurement demote() {
		return this;
	}

	@Override
	public void initialize() throws ThinklabException {
		super.initialize();		
		_unit = new Unit(_unitDefinition.getStringExpression());
	}
	
	
	/*
	 * -----------------------------------------------------------------------------------
	 * accessor
	 * -----------------------------------------------------------------------------------
	 */
	
	public class MeasurementAccessor implements IMediatingAccessor {

		@Override
		public IConcept getStateType() {
			return Thinklab.DOUBLE;
		}

		@Override
		public IState createState(int size, IContext context)
				throws ThinklabException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object get(String key) throws ThinklabException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void notifyDependency(String key, IAccessor accessor) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addMediatedAccessor(IAccessor accessor)
				throws ThinklabException {

			/*
			 * must be another measurement accessor, or a direct datasource.
			 */

			/*
			 * if another measurement, check if we're identical and switch off 
			 * conversion if so.
			 */
		}
		
	}


	@Override
	public IAccessor getAccessor() {
		return new MeasurementAccessor();
	}
	
	
}
