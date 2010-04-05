package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.agents.ThinkAgent;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabDuplicateNameException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.time.literals.TemporalExtentValue;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;

/**
 * A singleton (access from ModellingPlugin) that catalogs models and provides
 * search functions for models of specific observable concepts.
 * 
 * Contains a scheduler that allows executing a number of models simultaneously - 
 * just use enqueue() instead of run - using the thinklab.modelling.concurrentmodels
 * property to define how many models can run at once. Because OWLAPI is not 
 * thread safe, this should for now remain at the default value of 1.
 * 
 * @author Ferdinando Villa
 * 
 */
public class ModelFactory {

	// var ID for auxiliary cache information to differentiate signatures of
	// same context;
	// should not be necessary if context included "conceptual" dimensions but
	// it is for now
	// to implement scenarios
	public static final String AUX_VARIABLE_DESC = "session.aux.model";

	public static final IObservation observation = null;
	public Hashtable<String, Model> modelsById = new Hashtable<String, Model>();
	public Hashtable<String, Scenario> scenariosById = new Hashtable<String, Scenario>();
	public Hashtable<String, ThinkAgent> agentsById = new Hashtable<String, ThinkAgent>();

	class ContextualizingModelResult implements IQueryResult {

		IQueryResult mres = null;
		Collection<IContextualizationListener> listeners = null;
		Topology[] extents;

		public ContextualizingModelResult(IQueryResult r,
				Collection<IContextualizationListener> listeners,
				Topology[] extents2) {
			this.mres = r;
			this.listeners = listeners;
			this.extents = extents2;
		}

		@Override
		public IValue getBestResult(ISession session) throws ThinklabException {
			return mres.getBestResult(session);
		}

		@Override
		public IQueriable getQueriable() {
			return mres.getQueriable();
		}

		@Override
		public IQuery getQuery() {
			return mres.getQuery();
		}

		@Override
		public IValue getResult(int n, ISession session)
				throws ThinklabException {
			IInstance res = session.createObject(getResultAsList(n, null));
			IInstance result = new ObservationFactory().contextualize(res,
					session, listeners, extents);
			return new ObjectReferenceValue(result);
		}

		@Override
		public Polylist getResultAsList(int n,
				HashMap<String, String> references) throws ThinklabException {

			return mres.getResultAsList(n, references);
		}

		@Override
		public int getResultCount() {
			return mres.getResultCount();
		}

		@Override
		public IValue getResultField(int n, String schemaField)
				throws ThinklabException {
			return mres.getResultField(n, schemaField);
		}

		@Override
		public int getResultOffset() {
			return mres.getResultOffset();
		}

		@Override
		public float getResultScore(int n) {
			return mres.getResultScore(n);
		}

		@Override
		public int getTotalResultCount() {
			return mres.getTotalResultCount();
		}

		@Override
		public void moveTo(int currentItem, int itemsPerPage)
				throws ThinklabException {
			mres.moveTo(currentItem, itemsPerPage);
		}

		@Override
		public float setResultScore(int n, float score) {
			return mres.setResultScore(n, score);
		}
	}

	class Listener implements IContextualizationListener {

		ISession session;

		Listener(ISession session) {
			this.session = session;
		}

		private void scan(IObservation observation, ObservationContext context) {

			if (observation.getDependencies().length == 0
					&& !(observation.isMediator()) 
					&& observation.getDataSource() != null
					&& observation.getDataSource() instanceof IState) {
				ModellingPlugin.get().getCache().addObservation(observation,
						context,
						(String) session.getVariable(AUX_VARIABLE_DESC));
			} else if (observation.isMediator()) {
				scan(observation.getMediatedObservation(), context);
			} else {
				for (IObservation d : observation.getDependencies())
					scan(d, context);
			}
		}

		@Override
		public void onContextualization(IObservation original,
				IObservation obs, ObservationContext context) {
			scan(original, context);
			scan(obs, context);
		}

		@Override
		public void postTransformation(IObservation original, IObservation obs,
				ObservationContext context) {
			scan(original, context);
			scan(obs, context);
		}

		@Override
		public void preTransformation(IObservation original, IObservation obs,
				ObservationContext context) {
			scan(original, context);
			scan(obs, context);
		}
	}

	public static ModelFactory get() {
		return ModellingPlugin.get().getModelManager();
	}

	public void clearCache() {
		ObservationCache cache = ModellingPlugin.get().getCache();
		if (cache != null)
			cache.clear();
	}

	/*
	 * called by the defmodel macro.
	 */
	public Model registerModel(Model model, String name) throws ThinklabException {
		
		if (modelsById.containsKey(name))
			throw new ThinklabDuplicateNameException(
					"model manager: a model named " + name + " has already been registered");
		
		modelsById.put(name, model);
		model.setName(name);
		ModellingPlugin.get().logger().info("model " + model + " registered as " + name);
		return model;
	}

	/*
	 * called by the defscenario macro.
	 */
	public Scenario registerScenario(Scenario model, String name) throws ThinklabException {
		
		// FIXME same ID and name
		model.setId(name);
		model.setName(name);
		scenariosById.put(model.id, model);
		ModellingPlugin.get().logger().info("scenario " + model + " registered");
		return model;
	}
	
	/*
	 * called by the defagent macro. Creates a prototype agent that we will use to
	 * cloned agents from.
	 */
	public ThinkAgent registerAgent(ThinkAgent model, String name) throws ThinklabException {
		
		// FIXME same ID and name
		model.setId(name);
		agentsById.put(model.getId(), model);
		ModellingPlugin.get().logger().info("scenario " + model + " registered");
		return model;
	}
	
	public Model retrieveModel(String s) throws ThinklabException {
		return modelsById.get(s);
	}

	public Model requireModel(String s) throws ThinklabException {

		Model ret = retrieveModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no model found for "
					+ s);
		return ret;
	}

	/**
	 * Return a shape (usually a multipolygon) that describes the coverage of a
	 * particular model in a given kbox. It will be the union of the spatial
	 * contexts of all the realized models found in that kbox. Computing this
	 * may be a big deal, and if there are contingencies or complex transformers
	 * may involve computing models, so be careful.
	 * 
	 * @param model
	 * @return a shape or null if the model is not realizable or has no
	 *         spatially explicit realizations in the kbox. If the shape is not
	 *         null, it will be in normalized lat/lon coordinates.
	 * 
	 * @throws ThinklabException
	 */
	public ShapeValue getSpatialCoverage(Model model, IKBox kbox,
			ISession session) throws ThinklabException {

		ShapeValue ret = null;

		IQueryResult res = model.observe(kbox, session, (Object[]) null);

		for (int i = 0; i < res.getResultCount(); i++) {

			IValue rr = res.getResult(i, session);
			IObservation obs = ObservationFactory.getObservation(rr
					.asObjectReference().getObject());

			ObservationContext ctx = new ObservationContext(obs);

			if (!ctx.isEmpty()) {
				ArealExtent ext = (ArealExtent) ctx.getExtent(Geospace.get()
						.SpaceObservable());
				ShapeValue sh = (ShapeValue) ext.getFullExtentValue();
				if (ret == null)
					ret = sh;
				else
					ret = ret.union(sh);
			}
		}

		/*
		 * just in case
		 */
		if (ret != null)
			ret.transform(Geospace.get().getStraightGeoCRS());

		return ret;
	}

	/**
	 * Return a temporal extent that describes the time coverage of a particular
	 * model in a given kbox. It will be the union of the temporal contexts of
	 * all the realized models found in that kbox. Same disclaimers as
	 * getSpatialCoverage.
	 * 
	 * TODO unimplemented
	 * 
	 * @param model
	 * @return
	 */
	public TemporalExtentValue getTemporalCoverage(Model model, IKBox kbox,
			ISession session) {
		return null;
	}

	/**
	 * Get all scenarios that apply to the passed model.
	 * 
	 * @param model
	 * @return
	 */
	public Collection<Scenario> getApplicableScenarios(Model model) {
		
		ArrayList<Scenario> ret = new ArrayList<Scenario>();
		
		for (Scenario s : scenariosById.values()) {
			if (model.getObservable().is(s.getObservable())) {
				ret.add(s);
			}
		}
		
		return ret;
	}
	
	/**
	 * Return a lazy collection of computed result observations for the passed
	 * model. Just take the first one if you want one. This is the simplest way
	 * to "run" a model:
	 * 
	 * Will honor the observation cache if any is configured in the modelling
	 * plugin.
	 * 
	 * @param model
	 * @param kbox
	 * @param extents
	 * @return
	 * @throws ThinklabException
	 */
	public IQueryResult run(Model model, IKBox kbox, ISession session,
			Topology... extents) throws ThinklabException {
		return run(model, kbox, session, null, extents);
	}

	public IQueryResult run(Model model, IKBox kbox, ISession session,
			Collection<IContextualizationListener> listeners,
			Topology... extents) throws ThinklabException {

		if (listeners == null && ModellingPlugin.get().getCache() != null) {
			listeners = new ArrayList<IContextualizationListener>();
		}
		if (ModellingPlugin.get().getCache() != null)
			listeners.add(new Listener(session));

		IQueryResult r = model.observe(kbox, session, (Object[]) extents);
		return new ContextualizingModelResult(r, listeners, extents);
	}
	
	/**
	 * Query a model in the identified contexts, take the first result observation if any, and build
	 * a clojure-compatible map of name->state that uses Clojure keywords for the states (using 
	 * the ID name of the computing model if any was passed in an :as clause, or the local name
	 * of the observable concept if not). If no models were computed, an empty map is returned.
	 * 
	 * @param model
	 * @param kbox
	 * @param session
	 * @param extents
	 * @return
	 * @throws ThinklabException
	 */
	public Map<?,?> eval(Model model, IKBox kbox, ISession session,
			Topology... extents) throws ThinklabException {

		PersistentArrayMap ret = new PersistentArrayMap(new Object[]{});
		IQueryResult res = run(model, kbox, session, null, extents);
		
		if (res.getResultCount() > 0) {
			
			 Map<IConcept, IState> states = 
				 ObservationFactory.getStateMap(res.getResult(0, session).asObjectReference().getObject());
			 
			 for (IState state : states.values()) {
				 
				DefaultAbstractModel mod = (DefaultAbstractModel) state.getMetadata(Metadata.DEFINING_MODEL);
				String name = 
					 (mod == null || (mod.getId() == null)) ? 
						state.getObservableClass().getLocalName() : 
						mod.getId();
 				Keyword kw = Keyword.intern(null, name);
 				ret = (PersistentArrayMap) ret.assoc(kw, state);
			 }
		}
		
		return ret;

	
	}
	
	
	/**
	 * Clones a new agent of the passed type and places it in the context
	 * identified by the topologies. The agent will use the passed session
	 * to store its knowledge and observe the world in the passed kbox.
	 * 
	 * @param agentId
	 * @param kbox
	 * @param session
	 * @param extents
	 * @return
	 * @throws ThinklabException
	 */
	public ThinkAgent placeAgent(String agentId, IKBox kbox, ISession session, 
				Topology ... extents) throws ThinklabException {
		return null;
	}

	public ThinkAgent createAgent(String agentId) throws ThinklabException {

		ThinkAgent ret = agentsById.get(agentId);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no agent found for "
					+ agentId);
		return (ThinkAgent) ret.clone();
	}
	
}
