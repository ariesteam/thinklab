/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.modelling.corescience;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.CoreScience.PhysicalNature;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.model.DefaultStatefulAbstractModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

public class ProbabilisticMeasurementModel extends ClassificationModel {

	String unitSpecs;
	
	public ProbabilisticMeasurementModel(String namespace) {
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return ("probabilistic-measurement");
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {		
		return CoreScience.Measurement();
	}
	
	@Override
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
		unitSpecs = ((ProbabilisticMeasurementModel)model).unitSpecs;
	}
		
	public void setUnits(Object unitSpecs) throws ThinklabException {
		
		this.unitSpecs = unitSpecs.toString();
		this.metadata.put(Metadata.UNIT_SPECS, this.unitSpecs);
		this.metadata.put(Metadata.UNIT, new Unit(this.unitSpecs));
		this.metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
		PhysicalNature physicalNature = 
			CoreScience.getPhysicalNature(getObservableClass());		
		this.metadata.put(Metadata.PHYSICAL_NATURE, physicalNature);
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, IContext context, int flags) throws ThinklabException {

		IConcept theState =
			KnowledgeManager.get().getLeastGeneralCommonConcept(concepts);

		if (theState /* still */ == null)
			theState = observable;
						
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add((dynSpecs == null && changeSpecs == null && derivativeSpecs == null) ?
					"modeltypes:ProbabilisticMeasurement" : 
					"modeltypes:DynamicProbabilisticMeasurement");
		
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
		} else {
			throw new ThinklabValidationException(
					"probabilistic measurement " + 
					getName() + 
					" must discretize a continuous distribution with no numeric gaps and finite boundaries.");			
		}
		
		// units!
		arr.add(Polylist.list(CoreScience.HAS_UNIT, unitSpecs));
		
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
		ret = ObservationFactory.addReflectedField(ret, "modelObservable", observableSpecs);
		
		return ret;
	}

}
