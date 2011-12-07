package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.observations.Ranking;
import org.integratedmodelling.corescience.interfaces.IMergingObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IProbabilisticObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;

@InstanceImplementation(concept="modeltypes:ProbabilisticRanking")
public class ProbabilisticRanking extends Ranking implements IProbabilisticObservation {

	// set through reflection - must be public
	public ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	IConcept cSpace = null;
	double[] continuousDistribution = null;

	protected boolean hasNilClassifier = false;
	
	class ProbabilisticRankingAccessor extends RankingStateAccessor {
		
		public Object getValue(int idx, Object[] registers) {
			
			Object o = super.getValue(idx, registers);
			
			if (o instanceof Number && Double.isNaN(((Number)o).doubleValue()))
				o = null;
			
			if (o == null && !hasNilClassifier)
				return null;

			for (Pair<GeneralClassifier, IConcept> p : classifiers) {
				if (p.getFirst().classify(o)) {
					/*
					 * create distribution, set 100% evidence for classified concept.
					 */
					return p.getSecond();
				}
			}

			// null means "no data"; it can be caught using with a nil classifier						
			return null;
		}
	}
	
	class ProbabilisticRankingMediator extends RankingMediator {

		public ProbabilisticRankingMediator() {
			super();
		}
		
		public ProbabilisticRankingMediator(IndirectObservation other) throws ThinklabException {
			super(other);
		}
		
		public Object getValue(int idx, Object[] registers) {
			
			Object o = super.getValue(idx, registers);
			if (o instanceof Number && Double.isNaN(((Number)o).doubleValue()))
				o = null;
			
			if (o == null && !hasNilClassifier)
				return null;

			for (Pair<GeneralClassifier, IConcept> p : classifiers) {
				if (p.getFirst().classify(o)) {
					/*
					 * create distribution, set 100% evidence for classified concept.
					 */
					return p.getSecond();
				}
			}
			// null means "no data"; it can be caught using with a nil classifier						
			return null;
		}
	}
	
	@Override
	public IConcept getStateType() {
		return cSpace;
	}

	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {

		/*
		 * if we're mediating an observation that merges others, we assume that its first dependency will
		 * describe all of them. 
		 */
		if (observation instanceof IMergingObservation)
			observation = (IndirectObservation) observation.getDependencies()[0];
		
		if ( ! (observation instanceof Ranking))
			throw new ThinklabValidationException("rankings can only mediate other rankings");
		return new ProbabilisticRankingMediator(((Ranking)observation));
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new ProbabilisticRankingAccessor();
	}
	
	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {

		IConcept[] vmaps = new IConcept[classifiers.size()];
		for (int i = 0; i < classifiers.size(); i++)
			vmaps[i] = classifiers.get(i).getSecond();
		
		IState ret =
			new CategoricalDistributionDatasource(cSpace, size, vmaps, classifiers, (ObservationContext) context);

		ret.getMetadata().merge(this.metadata);
						
		return ret;
	
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
	
		/* 
		 * these should be in already through reflection, but let's keep
		 * the OWL way supported just in case.
		 */
		for (IRelationship r : i.getRelationships("modeltypes:hasClassifier")) {
			String[] rz = r.getValue().toString().split("->");
			Pair<GeneralClassifier, IConcept> cls = 
				new Pair<GeneralClassifier, IConcept>(
					new GeneralClassifier(rz[1]), 
					KnowledgeManager.get().requireConcept(rz[0]));
			classifiers.add(cls);					
		}
		
		/*
		 * we have no guarantee that the universal classifier, if there,
		 * will be last, given that it may come from an OWL multiproperty where
		 * the orderding isn't guaranteed.
		 * 
		 * scan the classifiers and if we have a universal classifier make sure
		 * it's the last one, to avoid problems.
		 */
		int unidx = -1; int iz = 0;
		for (Pair<GeneralClassifier, IConcept> cls : classifiers) {
			if (cls.getFirst().isUniversal()) {
				unidx = iz;
			}
			iz++;
		}
		
		if (unidx >= 0 && unidx < classifiers.size() -1) { 
			ArrayList<Pair<GeneralClassifier, IConcept>> nc =
				new ArrayList<Pair<GeneralClassifier,IConcept>>();
			for (iz = 0; iz < classifiers.size(); iz++) {
				if (iz != unidx)
					nc.add(classifiers.get(iz));
			}
			nc.add(classifiers.get(unidx));
			classifiers = nc;
		}
		
		/*
		 * check if we have a nil classifier; if we don't we don't bother classifying
		 * nulls and save some work.
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

			Pair<double[], IConcept[]> pd = 
				Metadata
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
	public List<Pair<GeneralClassifier, IConcept>> getClassifiers() {
		return classifiers;
	}

}
