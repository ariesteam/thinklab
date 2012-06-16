package org.integratedmodelling.thinklab.modelling.lang;


import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.IUnit;
import org.integratedmodelling.thinklab.api.modelling.parsing.IMeasuringObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IUnitDefinition;
import org.integratedmodelling.thinklab.modelling.Unit;
import org.integratedmodelling.thinklab.modelling.states.NumberState;

@Concept(NS.MEASURING_OBSERVER)
public class Measurement extends Observer<Measurement> implements IMeasuringObserverDefinition {

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
	 * accessor - it's always a mediator, either to another measurement or to a datasource
	 * whose content was defined explicitly to conform to our semantics
	 * -----------------------------------------------------------------------------------
	 */
	public class MeasurementAccessor 
		implements ISerialAccessor, IMediatingAccessor, IComputingAccessor {

		MeasurementAccessor _mediated;
		
		@Override
		public IConcept getStateType() {
			return Thinklab.DOUBLE;
		}

		@Override
		public void notifyMediatedAccessor(IAccessor accessor)
				throws ThinklabException {
			
			/*
			 * must be another measurement accessor, or a direct datasource.
			 */
			if (accessor instanceof MeasurementAccessor) {

				/*
				 * TODO
				 * check unit compatibility
				 */
				
				_mediated = (MeasurementAccessor) accessor;
			}
			
		}
		
		@Override
		public String toString() {
			return "[measurement: " + _unit + "]";
		}
		
		public IUnit getUnit() {
			return _unit;
		}

		@Override
		public Object mediate(Object object) throws ThinklabException {
			
			if (object == null || (object instanceof Number && Double.isNaN(((Number)object).doubleValue())))
				return Double.NaN;
			
			return _mediated == null ?
					object :
					_unit.convert(((Number)object).doubleValue(), _mediated.getUnit());
		}

		@Override
		public Object getValue(int idx) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void notifyDependency(ISemanticObject<?> observable, String key) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyExpectedOutput(ISemanticObject<?> observable,
				String key) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void process(int stateIndex) throws ThinklabException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setValue(String inputKey, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getValue(String outputKey) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	

	@Override
	public IAccessor getAccessor() {
		/*
		 * TODO should produce a subclassed ComputingMeasurementAccessor if there are
		 * expressions. This way the compiler will catch misplaced dependencies.
		 */
		return new MeasurementAccessor();
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		return new NumberState(observable, context);
	}
	
	
}
