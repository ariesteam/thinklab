package org.integratedmodelling.modelling.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.ModelMap.NamespaceEntry;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.ObservationCache;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.agents.ThinkAgent;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.knowledge.NamespaceOntology;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabDuplicateNameException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.time.literals.TemporalExtentValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.sexpr.FormReader;
import org.integratedmodelling.utils.sexpr.FormReader.FormListener;

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
	public Hashtable<String, Context> contextsById = new Hashtable<String, Context>();
	public Hashtable<String, ThinkAgent> agentsById = new Hashtable<String, ThinkAgent>();

	// relevant properties from ontology
	public static final String RETAINS_STATES  = "modeltypes:retainsState";
	public static final String REQUIRES_STATES = "modeltypes:requiresState";
	
	
	// used only during registration to recognize what was built by the last code
	// evaluated.
	public IModelForm lastRegistered = null;

	class ContextualizingModelResult implements IQueryResult {

		IQueryResult mres = null;
		Collection<IContextualizationListener> listeners = null;
		IContext context;

		public ContextualizingModelResult(IQueryResult r,
				Collection<IContextualizationListener> listeners,
				IContext context) {
			this.mres = r;
			this.listeners = listeners;
			this.context = context;
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
			IObservationContext result = 
				ObservationFactory.contextualize(res, session, listeners, context);
			return new ContextValue(result);
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
		public void onContextualization(IObservation original, ObservationContext context) {
			scan(original, context);
		}

		@Override
		public void postTransformation(IObservation original, ObservationContext context) {
			scan(original, context);
		}

		@Override
		public void preTransformation(IObservation original, ObservationContext context) {
			scan(original, context);
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
		lastRegistered = model;
		return model;
	}

	/*
	 * called by the defscenario macro.
	 */
	public Scenario registerScenario(Scenario model, String name) throws ThinklabException {
		
		model.setName(name);
		scenariosById.put(model.getName(), model);
		ModellingPlugin.get().logger().info("scenario " + model + " registered");
		lastRegistered = model;
		return model;
	}
	
	/*
	 * called by the defcontext macro.
	 */
	public Context registerContext(Context model, String name) throws ThinklabException {
		
		model.setName(name);
		contextsById.put(model.getName(), model);
		ModellingPlugin.get().logger().info("context " + model + " registered");
		lastRegistered = model;
		return model;
	}
	
	/*
	 * called by the defagent macro. Creates a prototype agent that we will use to
	 * cloned agents from.
	 */
	public ThinkAgent registerAgent(ThinkAgent model, String name) throws ThinklabException {
		
		// FIXME same ID and name
		model.setName(name);
		agentsById.put(model.getName(), model);
		ModellingPlugin.get().logger().info("scenario " + model + " registered");
		lastRegistered = model;
		return model;
	}
	
	public Model retrieveModel(String s) {
		return modelsById.get(s);
	}

	public Model requireModel(String s) throws ThinklabException {

		Model ret = retrieveModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no model found for "
					+ s);
		return ret;
	}

	public Scenario retrieveScenario(String s) {
		return scenariosById.get(s);
	}

	public Scenario requireScenario(String s) throws ThinklabException {

		Scenario ret = retrieveScenario(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no scenario found for "
					+ s);
		return ret;
	}

	public Context retrieveContext(String s) {
		return contextsById.get(s);
	}

	public Context requireContext(String s) throws ThinklabException {

		Context ret = retrieveContext(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no context found for "
					+ s);
		return ret;
	}

	public static IConcept annotateConcept(String namespace, String conceptId) throws ThinklabException {
		if (conceptId.contains(":"))
			return KnowledgeManager.getConcept(conceptId);
		IOntology o = ModelMap.getNamespaceOntology(namespace);
		if (o == null)
			throw new ThinklabInternalErrorException("no ontology for namespace " + namespace);
		IConcept ret = o.getConcept(conceptId);
		
		if (ret == null) {
			
			/*
			 * TODO - should we create it? I'd say not until the context can tell us which
			 * specific parents it should have.
			 */
			throw new ThinklabInternalErrorException(
					"no concept " + conceptId + 
					" in namespace " + namespace + 
					": add it in a namespace-ontology form");
		}
		
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

		for (int i = 0; i < res.getTotalResultCount(); i++) {

			IValue rr = res.getResult(i, session);
			IObservation obs = ObservationFactory.getObservation(rr
					.asObjectReference().getObject());

			ObservationContext ctx = null;
			try {
				ctx = new ObservationContext(obs, null, true);	
			} catch (ThinklabException e) {
				// if we get here, this does not contextualize properly because of
				// thinklab limitations. Just ignore it.
			}
			
			if (ctx != null && !ctx.isEmpty()) {
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
			ret = ret.transform(Geospace.get().getStraightGeoCRS());

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

			Set<IConcept> intersec = new HashSet<IConcept>(model.getObservables());
			intersec.retainAll(s.getObservables());
			if (!intersec.isEmpty()) {
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
			IContext context) throws ThinklabException {
		return run(model, kbox, session, null, context);
	}

	public IQueryResult run(Model model, IKBox kbox, ISession session,
			Collection<IContextualizationListener> listeners,
			IContext context) throws ThinklabException {

		if (listeners == null && ModellingPlugin.get().getCache() != null) {
			listeners = new ArrayList<IContextualizationListener>();
		}
		if (ModellingPlugin.get().getCache() != null)
			listeners.add(new Listener(session));

		IQueryResult r = model.observe(kbox, session, context);
		return new ContextualizingModelResult(r, listeners, context);
	}
	
	/**
	 * Query a model in the identified contexts, take the first result observation if any, and build
	 * a name->state that uses the ID name of the computing model if any was passed in an :as clause, 
	 * or the local name of the observable concept if not. If no models were computed, an empty map 
	 * is returned.
	 * 
	 * @param model
	 * @param kbox
	 * @param session
	 * @param extents
	 * @return
	 * @throws ThinklabException
	 */
	public IObservationContext eval(Model model, IKBox kbox, ISession session, IContext context)
		throws ThinklabException {

		IObservationContext ret = null;
		IQueryResult res = run(model, kbox, session, null, context);
		
		if (res.getResultCount() > 0) {			
			 ret = ((ContextValue)res.getResult(0, session)).getObservationContext();
		}
		
		return ret;
	}
	
	public void loadModelFiles(File dir) throws ThinklabException {
		
		/*
		 * load all recognized model files 
		 */
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				loadModelFiles(f);
			} else if (f.toString().endsWith(".clj")) {
				loadModelFile(f.toString());
			}
		}
	}

	public void loadStorylines(File dir) {
		
	}
	
	
	/**
	 * Load all models passed in the given Clojure file, creating a source code map
	 * and associating each model and namespace to its node in the source map so that
	 * the source can be reconstructed exactly.
	 *  
	 * @param resourceId
	 * @throws ThinklabIOException 
	 */
	public void loadModelFile(final String resourceId) throws ThinklabException {
		
		ModellingPlugin.get().logger().info("model subsystem reading " + resourceId);
		
		URL rurl = MiscUtilities.getURLForResource(resourceId);
		final long lmo = MiscUtilities.getLastModificationForResource(resourceId);
		FormReader f;
		lastRegistered = null;
		
		/*
		 * create entry for resource in source map
		 */
		
		try {

			final ClojureInterpreter intp = new ClojureInterpreter();
			
			f = new FormReader(rurl.openStream());
			f.read(new FormListener() {

				String namespace = null;
				ModelMap.Entry previousFragment = null;
				ModelMap.Entry resource = null;
				ModelMap.NamespaceEntry nsentry = null;
				
				String checkNamespace(String code) {
					
					String ret = null;
					String pattern = ".*\\(ns ([a-z\\-\\_\\.]+)\\s*.*";
					Pattern p = Pattern.compile(pattern);
					Matcher m = p.matcher(code.trim());
					if (m.find()) {
						ret = code.substring(m.start(1), m.end(1));
					}
					return ret;
				}
				
				@Override
				public void onFormRead(String s) throws ThinklabException {

					s = s.trim();
					
					boolean wasunk = false;
					
					/*
					 * get entry for resource
					 */
					if (resource == null) {
						resource = ModelMap.addResource(resourceId, lmo);
					}					

					if (namespace == null) {
						namespace = checkNamespace(s);
						if (namespace != null) {
							intp.eval(s);	
							wasunk = true;
							nsentry = 
								(NamespaceEntry) ModelMap.addNamespace(namespace, resource);
						}
					}
					
					/*
					 * create source node and add to source map
					 */
					ModelMap.Entry codeFrag = ModelMap.addSource(s, resource, previousFragment);
					previousFragment = codeFrag;

					/*
					 * if namespace is still null or this code chunk defined it,
					 * there was nothing else here we care about.
					 */
					if (wasunk || namespace == null)
						return;
					
					IModelForm prev = lastRegistered;
					IModelForm toRegister = null;
					
					/*
					 * eval form in given namespace. This may change lastRegistered
					 * and the associated ontology if a (namespace-ontology) form is
					 * encountered.
					 */
					Object ret = intp.eval("(in-ns '"  + namespace + ")\n" + s);
					
					if (ret instanceof NamespaceOntology) {
						nsentry.defineOntology((NamespaceOntology)ret);
					}
					
					String newName = 
						lastRegistered == null ? 
							null : 
							lastRegistered.getName();
					
					if (prev == null && newName != null || 
						(prev != null && newName != null && 
							!prev.getName().equals(newName))) {
						/*
						 * code has produced new model object, need to register it
						 */
						toRegister = lastRegistered;
					}
					
					if (toRegister != null) {
					
						/*
						 * if it's a model, check dependencies and add
						 * dependency statements
						 */
						ModelMap.Entry[] deps = null;
						if (toRegister instanceof IModel) {
							
							int i = 0;
							Collection<IModel> deeps = ((IModel)toRegister).getDependencies();
							if (deeps.size() > 0) {
								deps = new ModelMap.Entry[deeps.size()];
								for (IModel dep : deeps) {
									deps[i++] = ModelMap.getFormObject(dep.getName());
								}
							}
						}
						
						ModelMap.addForm(toRegister, codeFrag, deps);

					}
				}
			});
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		
		f.close();
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
