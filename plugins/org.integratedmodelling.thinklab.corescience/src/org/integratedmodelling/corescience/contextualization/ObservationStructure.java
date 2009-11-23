package org.integratedmodelling.corescience.contextualization;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.contextualization.Compiler.MediatedDependencyEdge;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Polylist;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class ObservationStructure {
	
		public class ObservationContents {
			
			public IConcept observationType = null;
			public IConcept observable = null;
			public Polylist cmDesc = null;
			public Polylist observableDesc = null;
			public IDataSource<?> datasource = null;
			public Polylist wholeObs = null;
			public boolean isExtent = false;
			
			String id;
						
			public ObservationContents(IObservation obs) throws ThinklabException {
				
				id = NameGenerator.newName("osds");
				observable = obs.getObservableClass();
				
				if (obs instanceof IConceptualizable) {
					
					wholeObs = ((IConceptualizable)obs).conceptualize();
					
				} else {
				
					observationType = 
						(obs.getConceptualModel() instanceof TransformingConceptualModel) ? 
								((TransformingConceptualModel)obs.getConceptualModel()).
								getTransformedObservationClass() :
								obs.getObservationClass();
				
					if (obs.getConceptualModel() != null && 
							(obs.getConceptualModel() instanceof IConceptualizable || obs.getConceptualModel() instanceof TransformingConceptualModel)) {
						cmDesc = 
							(obs.getConceptualModel() instanceof TransformingConceptualModel) ?
									((TransformingConceptualModel)obs.getConceptualModel()).getTransformedConceptualModel() :
									((IConceptualizable)obs.getConceptualModel()).conceptualize();
									
					} else if (obs.getConceptualModel() != null) 
						throw new ThinklabValidationException(
							"conceptual model " + 
							obs.getConceptualModel() + 
							" cannot be reconstructed in contextualized observation");
					
					if (obs.getObservable().getImplementation() == null) {
						observableDesc = obs.getObservable().toList(null);
					} else if (obs.getObservable().getImplementation() instanceof IConceptualizable) {
						observableDesc = ((IConceptualizable)obs.getObservable().getImplementation()).conceptualize();
					} else throw new ThinklabValidationException(
							"observable of class " + 
							obs.getObservableClass() + 
							" cannot be reconstructed in contextualized observation");
				}
			}
			
			public ObservationContents(Polylist l, boolean isExtent) {
				wholeObs = l;
				this.isExtent = isExtent;
				id = NameGenerator.newName("osds");
			}

			public void dump(PrintStream stream) {
				
				stream.println("---- Observation descriptor ---");
				if (wholeObs != null) {
					stream.println("observation desc: " + wholeObs);					
				} else {
					stream.println("observation type: " + observationType);
					stream.println("conceptual model: " + cmDesc);
					stream.println("observable descr: " + observableDesc);
				}
			}

			
			
			@Override
			public int hashCode() {
				return id.hashCode();
			}


			@Override
			public boolean equals(Object obj) {
				return id.equals(((ObservationContents)obj).id);
			}

			public Polylist getList() {

				if (wholeObs != null) {
					return wholeObs;
				} else {
					return 
						cmDesc == null ?
								Polylist.list(
										observationType,
										Polylist.list(CoreScience.HAS_OBSERVABLE, observableDesc)) :
								Polylist.list(
										observationType,
										Polylist.list(CoreScience.HAS_CONCEPTUAL_MODEL, cmDesc),
										Polylist.list(CoreScience.HAS_OBSERVABLE, observableDesc));
				}
			}
		}
		
		/*
		 * represent a dependency (or a contingency? or both?) - we may also want to add the property so
		 * we reconstruct it as is in the source.
		 */
		public static class DependencyEdge extends DefaultEdge {

			private static final long serialVersionUID = 5926757404834780955L;
			
			public ObservationContents getSourceObservation() {
				return (ObservationContents)getSource();
			}
			
			public ObservationContents getTargetObservation() {
				return (ObservationContents)getTarget();
			}
		}
		
		/*
		 * the dependency structure. Excludes extents and mediated obs.
		 */
		DefaultDirectedGraph<ObservationContents, DependencyEdge> structure = 
			new DefaultDirectedGraph<ObservationContents, DependencyEdge>(DependencyEdge.class);
		ObservationContents root = null;
		HashMap<IConcept, ObservationContents> contents = 
			new HashMap<IConcept, ObservationContents>();
		
		private ObservationContents makeDesc(IObservation o) throws ThinklabException {
			
			ObservationContents ret = new ObservationContents(o);			
			structure.addVertex(ret);

			if (!o.isMediator()) {
				for (IObservation dep : o.getDependencies()) {
					if (! (dep.getConceptualModel() instanceof ExtentConceptualModel)) {
						ObservationContents newdesc = makeDesc(dep);
						structure.addVertex(newdesc);
						structure.addEdge(ret, newdesc);
					}
				}
			}
			
			/*
			 * store for later
			 */
			contents.put(o.getObservableClass(), ret);
			
			return ret;
		}
		
		private ObservationContents makeDesc(IObservation o, DefaultDirectedGraph<IObservation, MediatedDependencyEdge> deps) 
			throws ThinklabException {
			
			ObservationContents ret = new ObservationContents(o);			
			structure.addVertex(ret);

			if (!o.isMediator()) {
				for (MediatedDependencyEdge edge : deps.outgoingEdgesOf(o)) {
					IObservation dep = edge.getTargetObservation();
					if (! (dep.getConceptualModel() instanceof ExtentConceptualModel)) {
						ObservationContents newdesc = makeDesc(dep, deps);
						structure.addVertex(newdesc);
						structure.addEdge(ret, newdesc);
					}
				}
			}
			
			/*
			 * store for later
			 */
			contents.put(o.getObservableClass(), ret);
			
			return ret;
		}
		/**
		 * Builds a structure for the passed observable. Extents and mediated observations are not
		 * stored. Extents can be created for individual observables using setContext().
		 * 
		 * @param obs
		 * @param context
		 * @throws ThinklabException
		 */
		public ObservationStructure(IObservation obs) 
			throws ThinklabException {
			root = makeDesc(obs);
		}
		
		/**
		 * build structure from observation and its dependencies
		 * @param observation
		 * @param context
		 * @throws ThinklabException
		 */
		public ObservationStructure(IObservation observation,
				IObservationContext context) throws ThinklabException {
			this(observation);
			setOverallContext(context);
		}

		/**
		 * build structure from observation, taking dependencies from the passed tree
		 * @param root
		 * @param dependencies
		 * @param context
		 * @throws ThinklabException
		 */
		public ObservationStructure(
				IObservation root,
				DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies,
				IObservationContext context) throws ThinklabException {
			this.root = makeDesc(root, dependencies);
			setOverallContext(context);
		}

		/**
		 * Define the observation context for the given observable. All extents in the context
		 * must be conceptualizable.
		 * 
		 * @param observable
		 * @param context
		 */
		public void setContext(IConcept observable, IObservationContext context) throws ThinklabException {

			/*
			 * find the obs descriptor
			 */
			ObservationContents desc = contents.get(observable);
			
			if (desc == null)
				return;
//				throw new ThinklabResourceNotFoundException(
//						"observation structure: can't set context of non-existent observation of " +
//						observable);
			
			for (IConcept dimension : context.getDimensions()) {
				
				Polylist l = context.conceptualizeExtent(dimension);
				if (l != null) { // which shouldn't happen 
					
					ObservationContents cd = new ObservationContents(l, true);
					structure.addVertex(cd);
					structure.addEdge(desc, cd);
				}	
			}
		}
		
		/*
		 * set the overall context
		 */
		public void setOverallContext(IObservationContext context) throws ThinklabException {
			setContext(getRootObservable(), context);
		}
		
		/**
		 * Build a whole observation structure that replicates the structure we represent, but containing the
		 * data in the passed datasources (indexed by observable). 
		 * @param session
		 * @param data
		 * @return
		 * @throws ThinklabException 
		 */
		public IInstance buildObservation(ISession session, Map<IConcept, IDataSource<?>> data) throws ThinklabException {
			
			Polylist l = buildObservationList(root, data);

			// TODO remove
			System.out.println(
					"\n ------------------------ \n" + 
					Polylist.prettyPrint(l) + 
					"\n ------------------------ \n");
			
			return session.createObject(l);
		}
		
		private Polylist buildObservationList(ObservationContents od,
				Map<IConcept, IDataSource<?>> data) throws ThinklabException {

			ArrayList<Object> adl = od.getList().toArrayList();

			for (DependencyEdge de : structure.outgoingEdgesOf(od)) {
				adl.add(
					Polylist.list(
							(de.getTargetObservation().isExtent ? 
									CoreScience.HAS_EXTENT : CoreScience.DEPENDS_ON),
							buildObservationList(de.getTargetObservation(), data)));
			}
			
			/* 
			 * add datasource 
			 */
			IDataSource<?> ds = data.get(od.observable);
			if (ds != null) {
				adl.add(
					Polylist.list(
						CoreScience.HAS_DATASOURCE,
						((IContextualizedState)ds).conceptualize()));
			}
			
			return Polylist.PolylistFromArrayList(adl);
		}


		public IConcept getRootObservable() {
			return root.observable;
		}
}
