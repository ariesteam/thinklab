package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.implementations.datasources.ClassData;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

/**
 * Built by the classification model. Fairly nasty to use otherwise, but very flexible and 
 * beautifully defined in Clojure.
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept="modeltypes:ModeledClassification")
public class ModeledClassification 
	extends Observation 
	implements IConceptualModel, MediatingConceptualModel, IConceptualizable {
	
	ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	IConcept cSpace = null;

	private IDataSource<?> ds;
	
	/**
	 * TODO 
	 * FIXME
	 * this may not be necessary; this is intended as a mediator class only
	 * @author Ferdinando Villa
	 *
	 */
	public class ClassificationAccessor implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(Object[] registers) {
			Object o = ds.getValue(index++, registers);
			for (Pair<GeneralClassifier, IConcept> p : classifiers) {
				if (p.getFirst().classify(o))
					return p.getSecond();
			}
			
			ModellingPlugin.get().logger().warn(
					"value " + o + " does not classify as a valid " + getObservableClass() +
					": datasource will have null values");
			
			return null;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
				throws ThinklabException {
			return !Obs.isExtent(o);
		}

		@Override
		public void notifyDependencyRegister(IObservation observation, IConcept observable,
				int register, IConcept stateType) throws ThinklabException {	
		}
	}
	

	public class ClassificationMediator implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(Object[] registers) {

			Object o = registers[index];
			
			for (Pair<GeneralClassifier, IConcept> p : classifiers) {
				if (p.getFirst().classify(o))
					return p.getSecond();
			}
						
			ModellingPlugin.get().logger().warn(
					"value " + o + " does not classify as a valid " + getObservableClass() +
					": datasource will have null values");

			return null;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
				throws ThinklabException {
			return true;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation, IConcept observable,
				int register, IConcept stateType) throws ThinklabException {	
			index = register;
		}
		
		@Override
		public String toString() {
			return "[Classifier " + classifiers + " @ " + index + " ]";
		}
	}

	
	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		return new ClassificationAccessor();
	}

	@Override
	public IConcept getStateType() {
		return cSpace;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		Pair<GeneralClassifier, IConcept> universal = null;
		Pair<GeneralClassifier, IConcept> cls = null;
		
		/*
		 * we have no guarantee that the universal classifier will be last, given that it
		 * comes from an OWL multiproperty
		 */
		for (IRelationship r : i.getRelationships("modeltypes:hasClassifier")) {
			String[] rz = r.getValue().toString().split("->");
			cls = new Pair<GeneralClassifier, IConcept>(
					new GeneralClassifier(rz[1]), 
					KnowledgeManager.get().requireConcept(rz[0]));
			if (cls.getFirst().isUniversal())
				universal = cls;
			else 
				classifiers.add(cls);					
		}
		if (universal != null) 
			classifiers.add(universal);
		
		IValue def = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (def != null)
			cSpace = def.getConcept();

		ds = getDataSource();
	}
	
	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		ArrayList<Object> arr = new ArrayList<Object>();
		
		/*
		 * FIXME
		 * TODO
		 * TLC-42: ModeledClassification should conceptualize to observation:Classification
		 * http://ecoinformatics.uvm.edu/jira/browse/TLC-42
		 * ------------------------------------------------------------------------------
		 */
		arr.add("modeltypes:ModeledClassification");
		arr.add(Polylist.list(CoreScience.HAS_CONCEPTUAL_SPACE, Polylist.list(cSpace)));
		arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, Polylist.list(cSpace)));

		if (getFormalName() != null) {
			arr.add(Polylist.list(CoreScience.HAS_FORMAL_NAME, getFormalName()));			
		}
		
		for (int i = 0; i < classifiers.size(); i++) {
			arr.add(Polylist.list(
						"modeltypes:hasClassifier", 
						classifiers.get(i).getSecond() + "->" + classifiers.get(i).getFirst()));
		}
		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public IStateAccessor getMediator(IConceptualModel conceptualModel,
			IConcept stateType, IObservationContext context)
			throws ThinklabException {
		return new ClassificationMediator();
	}

	@Override
	public IContextualizedState createContextualizedStorage(int size)
			throws ThinklabException {
		return new ClassData(cSpace, size);
	}
}