package org.integratedmodelling.modelling.corescience;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.model.DefaultStatefulAbstractModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

public class ProbabilisticRankingModel extends ClassificationModel {

	String unitSpecs;
	
	public ProbabilisticRankingModel(String namespace) {
		super(namespace);
	}

	@Override
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, IContext context, int flags) throws ThinklabException {

		IConcept theState =
			KnowledgeManager.get().getLeastGeneralCommonConcept(concepts);

		if (theState /* still */ == null)
			theState = observable;
						
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add((dynSpecs == null && changeSpecs == null && derivativeSpecs == null) ?
					"modeltypes:ProbabilisticRanking" : 
					"modeltypes:DynamicProbabilisticRanking");
		
		arr.add(Polylist.list(CoreScience.HAS_CONCEPTUAL_SPACE, Polylist.list(theState)));			
		arr.add(Polylist.list(CoreScience.HAS_FORMAL_NAME, getLocalFormalName()));					
		
		if (dynSpecs != null) {
			arr.add(Polylist.list(":code", dynSpecs));
		}
		if (changeSpecs != null) {
			arr.add(Polylist.list(":change", changeSpecs));
		}
		if (derivativeSpecs != null) {
			arr.add(Polylist.list(":derivative", derivativeSpecs));
		}
		
		if (dynSpecs != null || changeSpecs != null || derivativeSpecs != null)
			arr.add(Polylist.list("modeltypes:hasExpressionLanguage", 
				this.lang.equals(language.CLOJURE) ? "clojure" : "mvel"));

		double[] breakpoints = null;
		Pair<double[], IConcept[]> pd = Metadata.computeDistributionBreakpoints(observable, classifiers, null);		
		if (pd != null)
			breakpoints = pd.getFirst();
		
		if (breakpoints != null) {
			arr.add(Polylist.list(
					"modeltypes:encodesContinuousDistribution",
					MiscUtilities.printVector(breakpoints)));
		}
		
		if (!isMediating() || (flags & FORCE_OBSERVABLE) != 0)
			arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, this.observableSpecs));
		
		ArrayList<Pair<GeneralClassifier,IConcept>> clsf = 
			new ArrayList<Pair<GeneralClassifier,IConcept>>();
		
		// TODO only necessary in one special case, should be revised
		if (concepts.size() < classifiers.size())
			validateSemantics(session);
		
		for (int i = 0; i < classifiers.size(); i++) {
			clsf.add(new Pair<GeneralClassifier,IConcept>(
						classifiers.get(i), concepts.get(i)));
		}
		
		Polylist ret = addImplicitExtents(Polylist.PolylistFromArrayList(arr), context);
		ret = ObservationFactory.addReflectedField(ret, "classifiers", clsf);
		
		return ret;
	}

}
