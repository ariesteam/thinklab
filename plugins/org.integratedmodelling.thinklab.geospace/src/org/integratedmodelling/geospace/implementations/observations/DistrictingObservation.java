package org.integratedmodelling.geospace.implementations.observations;

import java.util.Properties;

import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.cmodels.ClusteringRasterModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * A districting observation transforms a gridded context into a spatial classification, and
 * turns the states of its dependencies into a distribution of state values for each class.
 *  
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept=Geospace.CLASSIFIED_GRID)
public class DistrictingObservation extends Observation {

	private String method = "k-means";
	private int initialK;
	private double stoppingThreshold = 0.2;
	private double varianceRatio = 9.0;
	private double membershipRatio = 0.1;
	private double separationRatio = 0.25;

	@Override
	protected IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		return 
			this.method .equals("k-means") ?
					new ClusteringRasterModel(getObservable(), initialK) :
					new ClusteringRasterModel(
							getObservable(),
							initialK, stoppingThreshold, varianceRatio, 
							membershipRatio, separationRatio);
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		
		super.initialize(i);
		
		/*
		 * recover clustering parameters, "infer" method
		 */
		initialK = i.get("geospace:hasInitialK").asNumber().asInteger();
		
		IValue st = i.get("geospace:hasStoppingThreshold");
		IValue vr = i.get("geospace:hasVarianceRatio");
		IValue mr = i.get("geospace:hasMembershipRatio");
		IValue sr = i.get("geospace:hasSeparationRatio");
		
		if (st != null) {
			stoppingThreshold = st.asNumber().asDouble();
			method = "isodata";
		}
		if (vr != null) {
			varianceRatio = st.asNumber().asDouble();
			method = "isodata";
		}
		if (mr != null) {
			membershipRatio = st.asNumber().asDouble();
			method = "isodata";
		}
		if (sr != null) {
			separationRatio = st.asNumber().asDouble();
			method = "isodata";
		}
		
	}

}
