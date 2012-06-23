package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IClassification;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IClassificationDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IClassifyingObserverDefinition;
import org.integratedmodelling.thinklab.modelling.states.ObjectState;

@Concept(NS.CLASSIFYING_OBSERVER)
public class Classification extends Observer<Classification> implements IClassifyingObserverDefinition {

	@Property(NS.HAS_CLASSIFICATION)
	IClassification _classification;
	
	@Override
	public IClassification getClassification() {
		return _classification;
	}

	@Override
	public Classification demote() {
		return this;
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		return new ObjectState(observable, context, this);
	}

	@Override
	public IAccessor getNaturalAccessor(IContext context) {
		if (getDependencies().size() > 0 || /* TODO check if expressions have been defined */ false)
			return new ComputingClassificationAccessor();
		
		return new ClassificationAccessor();
	}

	@Override
	public void setClassification(IClassification classification) {
		_classification = classification;
	}
	
	@Override
	public void initialize() throws ThinklabException {
		super.initialize();
		((IClassificationDefinition)_classification).initialize();
	}

	/*
	 * -----------------------------------------------------------------------------------
	 * accessor - it's always a mediator, either to another measurement or to a datasource
	 * whose content was defined explicitly to conform to our semantics
	 * -----------------------------------------------------------------------------------
	 */
	public class ClassificationAccessor 
		implements ISerialAccessor, IMediatingAccessor {

		ISerialAccessor _mediated;
		
		@Override
		public IConcept getStateType() {
			return Thinklab.DOUBLE;
		}

		@Override
		public void notifyMediatedAccessor(IAccessor accessor)
				throws ThinklabException {
			_mediated = (ISerialAccessor) accessor;
		}
		
		@Override
		public String toString() {
			return "[classification: " + _classification.getConceptSpace() + "]";
		}
		
		@Override
		public Object mediate(Object object) throws ThinklabException {
			
			if (object == null || (object instanceof Number && Double.isNaN(((Number)object).doubleValue())))
				return Double.NaN;
			
			return _mediated == null ?
					object :
					_classification.classify(object);
		}

		@Override
		public Object getValue(int idx) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	
	class ComputingClassificationAccessor extends ClassificationAccessor implements IComputingAccessor {

		@Override
		public void notifyDependency(ISemanticObject<?> observable, String key) {
		}

		@Override
		public void notifyExpectedOutput(ISemanticObject<?> observable,
				String key) {
		}

		@Override
		public void process(int stateIndex) throws ThinklabException {
		}

		@Override
		public void setValue(String inputKey, Object value) {
		}

		@Override
		public Object getValue(String outputKey) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
