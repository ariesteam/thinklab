package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceByte;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
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
 * @deprecated
 */
public class IntegerClassifier extends Observation implements IndirectObservation {

	IConcept observationSpace = null;
	HashMap<Integer,IConcept> valueMap = null;
	IConcept defaultValue = null;
	
	public class ClassificationAccessor implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(int cidx, Object[] registers) {
			
			Integer idx = (Integer)getDataSource().getValue(index++, registers);
			return valueMap.get(idx);
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IObservation o,
				IConcept observable, String formalName)
				throws ThinklabException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation,
				IConcept observable, int register, IConcept stateType)
				throws ThinklabException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext)  throws ThinklabException {

		}

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
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		return new IndexedContextualizedDatasourceByte<IConcept>(observationSpace, size, 
				(ObservationContext)context);
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new ClassificationAccessor();
	}

}
