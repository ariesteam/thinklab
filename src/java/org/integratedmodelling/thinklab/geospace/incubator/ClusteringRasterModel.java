package org.integratedmodelling.thinklab.geospace.incubator;


/**
 * The conceptual model that implements a transformation between a contextualized multiple
 * raster grid observation and a classified raster with as many states as cluster types.
 * 
 * @author Ferdinando
 */
//public class ClusteringRasterModel implements IConceptualModel, TransformingConceptualModel {
//
//	private int initialK;
//	private double stoppingThreshold = 0.2;
//	private double varianceRatio = 9.0;
//	private double membershipRatio = 0.1;
//	private double separationRatio = 0.25;
//	private String method = null;
//	IInstance observable;
//
//	/**
//	 * Use ISODATA if this one is used
//	 * 
//	 * @param initialK
//	 * @param stoppingThreshold
//	 * @param varianceRatio
//	 * @param membershipRatio
//	 * @param separationRatio
//	 */
//	public ClusteringRasterModel(IInstance observable,
//			  int initialK, double stoppingThreshold,
//		      double varianceRatio, double membershipRatio,
//		      double separationRatio) {
//		
//		this.observable = observable;
//		this.initialK = initialK;
//		this.stoppingThreshold = stoppingThreshold;
//		this.varianceRatio = varianceRatio;
//		this.membershipRatio = membershipRatio;
//		this.separationRatio = separationRatio;
//		this.method = "isodata";
//	}
//	
//	/**
//	 * Use basic k-means if this one is used.
//	 * @param initialK
//	 */
//	public ClusteringRasterModel(IInstance observable, int initialK) {
//		this.observable = observable;
//		this.initialK = initialK;
//		this.method = "k-means";
//	}
//	
//	@Override
//	public IStateAccessor getStateAccessor(IConcept stateType,
//			IObservationContext context) {
//		return null;
//	}
//
//	@Override
//	public IConcept getStateType() {
//		return null;
//	}
//
//	@Override
//	public void handshake(IDataSource<?> dataSource,
//			IObservationContext observationContext,
//			IObservationContext overallContext) throws ThinklabException {
//	}
//
//	@Override
//	public void validate(IObservation observation)
//			throws ThinklabValidationException {
//	}
//
//	@Override
//	public IInstance transformObservation(IInstance inst, ISession session)
//			throws ThinklabException {
//		
//		/* get all spatial dependencies of obs and set their states as cluster */
//		IObservation obs = Obs.getObservation(inst);
//		IObservationContext ctx = obs.getObservationContext();
//		GridExtent extent = (GridExtent) ctx.getExtent(Geospace.get().
//				RasterGridObservable());
//
//		if (extent == null) {
//			throw new ThinklabValidationException(
//					"clustering model: cannot use non-grid observations");
//		}
//		
//		/* cluster */
//		Map<IConcept, IContextualizedState> states = Obs.getStateMap(obs);
//		
//		if (states.size() < 1) {
//			throw new ThinklabDistrictingException(
//				"clustering: can't find any state to work with in " + obs);
//		}
//
//		double[][] dset = new double[states.size()][]; int i = 0;
//		IConcept[] oob = new IConcept[states.size()];
//		
//		for (Map.Entry<IConcept, IContextualizedState> state : states.entrySet()) {
//			dset[i] = state.getValue().getDataAsDoubles();
//			oob[i++] = state.getKey();
//		}
//
//		DistrictingResults results =
//			method.equals("isodata") ? 
//					new ISODATAAlgorithm().createDistricts(dset, initialK,
//							stoppingThreshold, varianceRatio, membershipRatio,
//							separationRatio) :
//					new KMeansAlgorithm().createDistricts(dset, initialK);
//		
//		
//		ArrayList<Polylist> deps = new ArrayList<Polylist>();			
//		/*
//		 * each dependency gets a new Measurement or Ranking (can't work with
//		 * anything else) with its own datasource, each with as many distributions
//		 * of the given observable as there are clusters
//		 */
//		int obsidx = 0;
//		for (IConcept co : oob) {
//			
//			IObservation origObs = Obs.findObservation(obs, co);
//			
//			/* process the results of clustering into a datasource per observable */
//			MemValueContextualizedDatasource ds = 
//				new MemValueContextualizedDatasource(
//						Geospace.get().GridClassifier(), results.getFinalK());
//
//			for (int k = 0; k < results.getFinalK(); k++) {
//				ds.addValue(new DistributionValue(
//						DistributionValue.Distributions.NORMAL,
//						results.getCentroids(k)[obsidx],
//						results.getStdevs()[k].get(obsidx)));
//			}
//			
//			if (origObs instanceof Measurement) {
//				
//				deps.add(Obs.makeObservation(
//						CoreScience.Measurement(),
//						origObs.getObservable(),
//						origObs.getConceptualModel(),
//						ds));
//				
//			} else if (origObs instanceof Ranking) {
//
//				deps.add(Obs.makeObservation(
//						CoreScience.Ranking(),
//						origObs.getObservable(),
//						origObs.getConceptualModel(),
//						ds));
//				
//			} else {
//				throw new ThinklabValidationException(
//						"clustering: can only generate distributions from ranking or measurements: "
//						+ origObs);
//			}
//			obsidx++;
//		}
//		
//		/* make a conceptual model for the spatial extent */
//		ClassifiedRasterConceptualModel cm =
//			new ClassifiedRasterConceptualModel(
//					results.getTypeset(),
//					extent.getXCells(),
//					extent.getYCells(),
//					results.getFinalK(),
//					null);
//		
//		/* build final transformed observation, turning all observations we have
//		 * clustered into distributions of their values per cluster. 
//		 * 
//		 * TODO add back the non-spatial extents of the original obs
//		 * 
//		 * */
//		
//		
//		/* main identification, shares our observable */
//		Polylist orobs = obs.getObservable().toList(null);
//		
//		/* connect spatial extent obs: this needs to be a ClassifiedRasterCM */
//		
//		
//		return null;
//	}
//
//	@Override
//	public Polylist getTransformedConceptualModel() {
//		// we want to contextualize this as if it was an identification, so this should be fine.
//		return null;
//	}
//
//	@Override
//	public IConcept getTransformedObservationClass() {
//		// we operate as an identification, i.e. a simple observation with no CM.
//		return CoreScience.Observation();
//	}
//
//	@Override
//	public IContextualizedState createContextualizedStorage(IObservation observation, int size)
//			throws ThinklabException {
//		throw new ThinklabUnimplementedFeatureException("storage of clustered states not implemented");
//	}
//
//
//}
