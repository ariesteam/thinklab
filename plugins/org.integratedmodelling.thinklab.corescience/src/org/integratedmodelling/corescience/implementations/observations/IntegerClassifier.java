package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceByte;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

/**
 * FIXME
 * TODO
 * Classifies integers into concepts. 
 * 
 * Not quite sure this is still necessary - the classification model form takes care of this and
 * more.
 * 
 * @author Ferdinando
 *
 */
public class IntegerClassifier extends Observation implements IConceptualModel, IConceptualizable {

	IConcept observationSpace = null;
	HashMap<Integer,IConcept> valueMap = null;
	IConcept defaultValue = null;
	
	public class ClassificationAccessor implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(Object[] registers) {

			try {
				Integer idx = (Integer)getDataSource().getValue(index++, registers);
				return valueMap.get(idx);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IConcept observable, String formalName)
				throws ThinklabValidationException {
			return false;
		}

		@Override
		public void notifyDependencyRegister(IConcept observable, int register,
				IConcept stateType) throws ThinklabValidationException {			
		}
	}
	
	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		return new ClassificationAccessor();
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);

		observationSpace =
			KnowledgeManager.get().requireConcept(
					i.get(CoreScience.HAS_CONCEPTUAL_SPACE).toString());

		IConcept cc = null;
		for (IRelationship r : i.getRelationships("measurement:hasMapping")) {
			
			String[] s = r.getValue().toString().split(":");
			if (s.length != 2)
				throw new ThinklabValidationException(
						"class mapping invalid: " + 
						r.getValue());
			valueMap.put(
					Integer.parseInt(s[1].trim()),
					cc = KnowledgeManager.get().requireConcept(s[0]));
			
			if (!cc.is(observationSpace))
				throw new ThinklabValidationException(
						"mapping: " +
						cc + 
						" is not a type of " +
						observationSpace);
		}
		
		IValue def = i.get("measurement:hasDefaultValue");
		if (def != null)
			defaultValue = 
				KnowledgeManager.get().requireConcept(def.toString().trim());
		
	}

	@Override
	public IConcept getStateType() {
		return observationSpace;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		
		/*
		 * should be a provider of integers
		 */
		if (!dataSource.getValueType().is(KnowledgeManager.Integer()))
			throw new ThinklabValidationException(
					"an integer classifier can only work with an integer datasource");
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {		
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add(this.getObservationClass());

		arr.add(Polylist.list(CoreScience.HAS_CONCEPTUAL_SPACE, 
				observationSpace.toString()));
		arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, 
				getObservable().toList(null)));
		
		for (Integer n : valueMap.keySet()) {
			arr.add(Polylist.list(
						"measurement:hasMapping", 
						valueMap.get(n) + ": " + n));
			
		if (defaultValue != null)
			arr.add(Polylist.list("measurement:hasDefaultValue", defaultValue.toString()));
		}
		
		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public IContextualizedState createContextualizedStorage(int size)
			throws ThinklabException {
		return new IndexedContextualizedDatasourceByte<IConcept>(observationSpace, size);
	}

}
