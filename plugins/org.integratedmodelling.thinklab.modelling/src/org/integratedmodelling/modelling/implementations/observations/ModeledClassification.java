package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.ClassData;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;
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
	implements MediatingObservation {
	
	protected ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	IConcept cSpace = null;
	double[] continuousDistribution = null;

	protected boolean hasNilClassifier = false;

	@Override
	public String toString() {
		return ("classification(" + getObservableClass() + "): " + cSpace);
	}

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
		public Object getValue(int idx, Object[] registers) {
			Object o = getDataSource().getValue(index++, registers);
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
		public boolean notifyDependencyObservable(IObservation o,
				IConcept observable, String formalName)
				throws ThinklabException {
			return !(o instanceof Topology);
		}

		@Override
		public void notifyDependencyRegister(IObservation observation,
				IConcept observable, int register, IConcept stateType)
				throws ThinklabException {
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext) throws ThinklabException  {
			// TODO Auto-generated method stub
			
		}
	}
	

	public class ClassificationMediator implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(int idx, Object[] registers) {

			Object o = registers[index];
			
			if (o == null && !hasNilClassifier)
				return null;
			
			for (Pair<GeneralClassifier, IConcept> p : classifiers) {
				if (p.getFirst().classify(o))
					return p.getSecond();
			}

			// null means "no data"; it can be caught using with a nil classifier						
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

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext)  throws ThinklabException {
			// TODO Auto-generated method stub
			
		}
	}

	
	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new ClassificationAccessor();
	}

	@Override
	public IConcept getStateType() {
		return cSpace;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		metadata.put(Metadata.CONTINUOUS, Boolean.FALSE);
		
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
		
		/*
		 * check if we have a nil classifier; if we don't we don't bother classifying nulls
		 */
		this.hasNilClassifier = false;
		for (Pair<GeneralClassifier, IConcept> cl : classifiers) {
			if (cl.getFirst().isNil()) {
				this.hasNilClassifier = true;
				break;
			}
		}
		
		IValue def = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (def != null)
			cSpace = def.getConcept();

		def = i.get("modeltypes:encodesContinuousDistribution");
		if (def != null)
			continuousDistribution = MiscUtilities.parseDoubleVector(def.toString());

		// TODO remove?
		if (continuousDistribution != null && getDataSource() != null && (getDataSource() instanceof IState))
			((IState)getDataSource()).getMetadata().put(
					Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS, 
					continuousDistribution); 

		if (continuousDistribution != null)
			metadata.put(Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS, 
					continuousDistribution);
		

		if (classifiers != null) {

			metadata.put(Metadata.CLASSIFIERS, classifiers);
			
			IConcept[] rnk = null;
			/*
			 * remap the values to ranks and determine how to rewire the input
			 * if necessary, use classifiers instead of lexicographic order to
			 * infer the appropriate concept order
			 */
			ArrayList<GeneralClassifier> cla = new ArrayList<GeneralClassifier>();
			ArrayList<IConcept> con = new ArrayList<IConcept>();
			for (Pair<GeneralClassifier, IConcept> op : classifiers) {
				cla.add(op.getFirst());
				con.add(op.getSecond());
			}

			Pair<double[], IConcept[]> pd = Metadata
					.computeDistributionBreakpoints(cSpace, cla, con);
			if (pd != null) {
				if (pd.getSecond()[0] != null) {
					rnk = pd.getSecond();
				}
			}

			HashMap<IConcept, Integer> ranks = null;
			if (rnk == null) {	
				ranks = Metadata.rankConcepts(cSpace, metadata);
			} else {
				ranks = Metadata.rankConcepts(cSpace, rnk, metadata);
			}

		}
		
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add("observation:Classification");
		arr.add(Polylist.list(CoreScience.HAS_CONCEPTUAL_SPACE, Polylist.list(cSpace)));
		arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, Polylist.list(cSpace)));

		if (getFormalName() != null) {
			arr.add(Polylist.list(CoreScience.HAS_FORMAL_NAME, getFormalName()));			
		}
		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		
		IState ret = new ClassData(cSpace, size, classifiers, (ObservationContext)context);
//
//		/*
//		 * TODO other metadata
//		 */
//		if (continuousDistribution != null)
//			ret.getMetadata().put(
//					Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS, 
//					continuousDistribution); 
//		
		return ret;
	}

	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {
		return new ClassificationMediator();
	}
}