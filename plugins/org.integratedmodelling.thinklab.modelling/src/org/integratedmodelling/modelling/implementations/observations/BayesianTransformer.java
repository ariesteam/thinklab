package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceByte;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
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
public class BayesianTransformer 
	extends Observation 
	implements IConceptualModel, TransformingConceptualModel, IConceptualizable {
	
	ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	IConcept cSpace = null;

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		throw new ThinklabRuntimeException("internal: bayesian transformer: get accessor called");
	}

	@Override
	public IConcept getStateType() {
		return cSpace;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		
		for (IRelationship r : i.getRelationships("modeltypes:hasClassifier")) {
			String[] rz = r.getValue().toString().split("->");
			classifiers.add(
				new Pair<GeneralClassifier, IConcept>(
					new GeneralClassifier(rz[1]), 
					KnowledgeManager.get().requireConcept(rz[0])));
					
		}
		
		IValue def = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (def != null)
			cSpace = def.getConcept();

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
				
		for (int i = 0; i < classifiers.size(); i++) {
			arr.add(Polylist.list(
						"modeltypes:hasClassifier", 
						classifiers.get(i).getSecond() + "->" + classifiers.get(i).getFirst()));
		}
		return Polylist.PolylistFromArrayList(arr);
	}


	@Override
	public IContextualizedState createContextualizedStorage(int size)
			throws ThinklabException {
		return new IndexedContextualizedDatasourceInt<IConcept>(cSpace, size);
	}

	@Override
	public Polylist getTransformedConceptualModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getTransformedObservationClass() {
		return CoreScience.Observation();
	}

	@Override
	public IInstance transformObservation(IInstance inst)
			throws ThinklabException {
		
		IObservation orig = Obs.getObservation(inst);
		Map<IConcept, IContextualizedState> smap =
			Obs.getStateMap(orig);
		
		/*
		 * build the network
		 */
		
		/*
		 * make new state
		 */
		
		/*
		 * new obs has same extents as the former, so just copy them.
		 */
//		for (IObservation ext : orig.get)
		// TODO Auto-generated method stub
		return null;
	}
}