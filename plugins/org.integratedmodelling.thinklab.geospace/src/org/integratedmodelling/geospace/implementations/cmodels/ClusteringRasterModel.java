package org.integratedmodelling.geospace.implementations.cmodels;

import java.util.Map;

import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.implementations.datasources.MemValueContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.observations.Measurement;
import org.integratedmodelling.corescience.implementations.observations.Ranking;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.districting.algorithms.ISODATAAlgorithm;
import org.integratedmodelling.geospace.districting.algorithms.KMeansAlgorithm;
import org.integratedmodelling.geospace.districting.utils.DistrictingResults;
import org.integratedmodelling.geospace.exceptions.ThinklabDistrictingException;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * The conceptual model that implements a transformation between a contextualized multiple
 * raster grid observation and a classified raster with as many states as cluster types.
 * 
 * @author Ferdinando
 */
public class ClusteringRasterModel implements IConceptualModel, TransformingConceptualModel {

	private int initialK;
	private double stoppingThreshold = 0.2;
	private double varianceRatio = 9.0;
	private double membershipRatio = 0.1;
	private double separationRatio = 0.25;
	private String method = null;
	IInstance observable;

	/**
	 * Use ISODATA if this one is used
	 * 
	 * @param initialK
	 * @param stoppingThreshold
	 * @param varianceRatio
	 * @param membershipRatio
	 * @param separationRatio
	 */
	public ClusteringRasterModel(IInstance observable,
			  int initialK, double stoppingThreshold,
		      double varianceRatio, double membershipRatio,
		      double separationRatio) {
		
		this.observable = observable;
		this.initialK = initialK;
		this.stoppingThreshold = stoppingThreshold;
		this.varianceRatio = varianceRatio;
		this.membershipRatio = membershipRatio;
		this.separationRatio = separationRatio;
		this.method = "isodata";
	}
	
	/**
	 * Use basic k-means if this one is used.
	 * @param initialK
	 */
	public ClusteringRasterModel(IInstance observable, int initialK) {
		this.observable = observable;
		this.initialK = initialK;
		this.method = "k-means";
	}
	
	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		return null;
	}

	@Override
	public IConcept getStateType() {
		return null;
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
	public IInstance transformObservation(IInstance inst)
			throws ThinklabException {
		
		/* get all spatial dependencies of obs and set their states as cluster */
		IObservation obs = Obs.getObservation(inst);
		IObservationContext ctx = obs.getObservationContext();
		GridExtent extent = (GridExtent) ctx.getExtent(Geospace.get().
				RasterGridObservable());

		if (extent == null) {
			throw new ThinklabValidationException(
					"clustering model: cannot use non-grid observations");
		}
		
		/* cluster */
		Map<IConcept, IContextualizedState> states = Obs.getStateMap(obs);
		
		if (states.size() < 1) {
			throw new ThinklabDistrictingException(
				"clustering: can't find any state to work with in " + obs);
		}

		double[][] dset = new double[states.size()][]; int i = 0;
		IConcept[] oos = new IConcept[states.size()];
		
		for (Map.Entry<IConcept, IContextualizedState> state : states.entrySet()) {
			oos[i] = state.getKey();
			dset[i++] = state.getValue().getDataAsDoubles();
		}

		DistrictingResults results =
			method.equals("isodata") ? 
					new ISODATAAlgorithm().createDistricts(dset, initialK,
							stoppingThreshold, varianceRatio, membershipRatio,
							separationRatio) :
					new KMeansAlgorithm().createDistricts(dset, initialK);
		
		
		/*
		 * each dependency gets a new Measurement or Ranking (can't work with
		 * anything else) with its own datasource, each with as many distributions
		 * of the given observable as there are clusters
		 */
		for (IConcept co : oos) {
			
			IObservation origObs = Obs.findObservation(obs, co);
			if (origObs instanceof Measurement) {
				
			} else if (origObs instanceof Ranking) {
				
			} else 
				throw new ThinklabValidationException(
						"clustering: can only generate distributions from ranking or measurements: "
						+ origObs);
			
			for (int k = 0; k < results.getFinalK(); k++) {
//				ds.addValue(new DistributionValue(
//						DistributionValue.Distributions.NORMAL,
//						results.get));
			}
			
			
		}
					
		/* process the results of clustering into a datasource per observable */
		MemValueContextualizedDatasource ds = 
			new MemValueContextualizedDatasource(
					Geospace.get().GridClassifier(), results.getFinalK());
		
		
		/* make a conceptual model for the spatial extent */
		ClassifiedRasterConceptualModel cm =
			new ClassifiedRasterConceptualModel(
					results.getTypeset(),
					extent.getXCells(),
					extent.getYCells(),
					results.getFinalK(),
					null);
		
		/* build final transformed observation, turning all observations we have
		 * clustered into distributions of their values per cluster. */
		
		
		/* build main identification, shares our observable */
		
		
		/* connect spatial extent obs */
		
		/* connect all observables */
		
		return null;
	}


}
