package org.integratedmodelling.thinklab.modelling.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.factories.IModelManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.lang.parsing.IConceptDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.ILanguageDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.INamespaceDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IPropertyDefinition;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IAgentModel;
import org.integratedmodelling.thinklab.api.modelling.ICategorizingObserver;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IMeasuringObserver;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IRankingObserver;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.IStoryline;
import org.integratedmodelling.thinklab.api.modelling.IUnit;
import org.integratedmodelling.thinklab.api.modelling.IValuingObserver;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.proxy.ModellingModule;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.MiscUtilities;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A model manager that can parse the Thinklab language and build a model map, without actually being 
 * capable of running the model objects.
 * 
 * @author Ferd
 *
 */
public class ModelManager implements IModelManager {


	private static ModelManager _this = null;

	private Hashtable<String, IModel> modelsById = new Hashtable<String, IModel>();
	private Hashtable<String, IScenario> scenariosById = new Hashtable<String, IScenario>();
	private Hashtable<String, IContext> contextsById = new Hashtable<String, IContext>();
	private Hashtable<String, IAgentModel> agentsById = new Hashtable<String, IAgentModel>();
	private Hashtable<String, INamespace> namespacesById = new Hashtable<String, INamespace>();

	class FunctionDescriptor {
		public FunctionDescriptor(String id, String[] parameterNames,
				Class<?> cls) {
			this._id = id;
			this._parameterNames = parameterNames;
			this._class = cls;
		}
		String   _id;
		String[] _parameterNames;
		Class<?> _class;
	}

	private HashMap<String, FunctionDescriptor> _functions =
			new HashMap<String, ModelManager.FunctionDescriptor>();

	/**
	 * This one resolves namespace source files across imported plugins and handles errors.
	 * @author Ferd
	 *
	 */
	class Resolver implements IResolver {

		ArrayList<Pair<String,Integer>> errors = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> warnings = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> infos = new ArrayList<Pair<String,Integer>>();
		String resourceId = "(not set)";
		IProject project;

		public Resolver(Object resource) {
			this.resourceId = resource.toString();
		}

		@Override
		public boolean onException(Throwable e, int lineNumber)
				throws ThinklabException {
			errors.add(new Pair<String, Integer>(e.getMessage(), lineNumber));
			Thinklab.get().logger().error(resourceId + ": " + lineNumber + ": " + e.getMessage());
			return true;
		}

		@Override
		public boolean onWarning(String warning, int lineNumber) {
			warnings.add(new Pair<String, Integer>(warning, lineNumber));
			Thinklab.get().logger().warn(resourceId + ": " + lineNumber + ": " + warning);
			return true;
		}

		@Override
		public boolean onInfo(String info, int lineNumber) {
			infos.add(new Pair<String, Integer>(info, lineNumber));
			Thinklab.get().logger().info(resourceId + ": " + lineNumber + ": " + info);
			return true;
		}

		@Override
		public InputStream resolveNamespace(String namespace, String reference)
				throws ThinklabException {

//			Plugin plugin = null;
//			if (project instanceof ThinklabProject) {
//				plugin = ((ThinklabProject)project).getPlugin();
//			}

			/*
			 * TODO
			 * if we have both namespace and reference, push a non-void resolver context so that next import can use
			 * the same location in a relative ref; pop the resolving context after the namespace has been read.
			 * Otherwise, push a void resolver context
			 */

			/*
			 * reference trumps namespace; if both are specified, the name check is done later in validateNamespace
			 */
			if (reference != null) {

				try {
					File f = new File(reference);

					if (f.exists() && f.isFile() && f.canRead()) {
						return new FileInputStream(f);
					} else if (reference.contains(":/")) {
						URL url = new URL(reference);						
						return url.openStream();
					}

					/*
					 * plugin resource has precedence even over local file with same path
					 */
//					if (plugin != null) {
//						URL url = plugin.getManager().
//								getPluginClassLoader(plugin.getDescriptor()).
//								getResource(reference);
//						if (url != null) {
//							return url.openStream();
//						}
//					}

				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}

				/*
				 * if we get here we haven't found it, look it up in all DIRECTLY imported projects (non-recursively)
				 */
//				if (plugin != null) {
//					for (PluginPrerequisite pr : plugin.getDescriptor().getPrerequisites()) {
//						try {
//							Plugin dpp = plugin.getManager().getPlugin(pr.getPluginId());
//							URL url = dpp.getManager().
//									getPluginClassLoader(dpp.getDescriptor()).
//									getResource(reference);
//							if (url != null) {
//								return url.openStream();
//							}
//						} catch (Exception e) {
//							throw new ThinklabIOException(e);
//						}
//					}
//				}
			} else if (namespace != null) {

				/*
				 * find resource using path corresponding to namespace, either in plugin classpath or
				 * relative filesystem.
				 */
				String fres = namespace.replace('.', '/');
//				if (plugin != null) {
//					URL url = plugin.getManager().
//							getPluginClassLoader(plugin.getDescriptor()).
//							getResource(reference);
//					if (url != null) {
//						try {
//							return url.openStream();
//						} catch (IOException e) {
//							throw new ThinklabIOException(e);
//						}
//					}
//				}

				/*
				 * TODO try with the (non-existent yet) pushed resolver context first
				 */

				/*
				 * dumb (i.e., null resolver context)
				 */
				File f = new File(fres);
				if (f.exists() && f.isFile() && f.canRead()) {
					try {
						return new FileInputStream(f);
					} catch (FileNotFoundException e) {
						throw new ThinklabIOException(e);
					}
				}
			}

			/*
			 * throw exception here - CHECK We don't get here if it was found, but I'm unsure if this should be
			 * handled in the caller instead.
			 */
			String message = "";
			if (namespace == null)
				message = "cannot read model resource from " + reference;
			else if (reference == null) 
				message = "cannot find source for namespace " + namespace;
			else 
				message = "cannot read namespace " + namespace + " from resource " + reference;

			throw new ThinklabResourceNotFoundException(message);

		}

		@Override
		public void onNamespaceDeclared(String namespaceId, INamespace namespace) {
			
			if (namespacesById.get(namespaceId) != null) {
				/*
				 * warn only for now
				 */
				Thinklab.get().logger().warn("warning: namespace " + namespaceId + " is being redefined");
				releaseNamespace(namespaceId);
			}
		}

		@Override
		public void onNamespaceDefined(INamespace namespace) throws ThinklabException {
			
			/*
			 * store it first, which publishes the knowledge so that the next ones will work
			 */
			if (!namespacesById.containsKey(namespace.getId())) {
				namespacesById.put(namespace.getId(), namespace);
			} else {
				onException(new ThinklabValidationException("cannot redefine namespace: " + namespace.getId()), -1);
			}

			((Namespace)namespace).initialize();
			/*
			 * TODO pop resolver context
			 */
		}

		@Override
		public void validateNamespaceForResource(String resource,
				String namespace) throws ThinklabException {

//			Plugin plugin = null;
//			if (project instanceof ThinklabProject) {
//				plugin = ((ThinklabProject)project).getPlugin();
//			}
//
//			if (plugin != null) {
//				
//				/*
//				 * check that resource was read from same file path
//				 */
//			} else {
//				
//				/*
//				 * check that filename path is same in namespace
//				 */
//			}
//			

		}

		public void setProject(IProject project) {
			this.project = project;
		}

		@Override
		public IConceptDefinition resolveExternalConcept(String id, INamespace namespace, int line) throws ThinklabException {

			if (Thinklab.get().getConcept(id) == null) {
				onException(new ThinklabValidationException("concept " + id + " unknown"), line);
			}

			ConceptObject co = new ConceptObject();
			co.setId(id);

			/*
			 * TODO decide how to handle the import with the namespace
			 */

			return co;

		}

		@Override
		public IPropertyDefinition resolveExternalProperty(String id, INamespace namespace, int line) throws ThinklabException {

			if (Thinklab.get().getProperty(id) == null) {
				onException(new ThinklabValidationException("concept " + id + " unknown"), line);
			}

			PropertyObject co = new PropertyObject();
			co.setId(id);

			/*
			 * TODO decide how to handle the import with the namespace
			 */

			return co;
		}

		@Override
		public IExpression resolveFunction(String functionId,
				Collection<String> parameterNames) {
			return ModelManager.get().resolveFunction(functionId, parameterNames);
		}

		@Override
		public void onModelObjectDefined(INamespace namespace, IModelObject ret) {
			// TODO Auto-generated method stub

		}

		@Override
		public ILanguageDefinition newLanguageObject(Class<?> cls) {
			
			if (cls.equals(INamespace.class)) {
				return new Namespace();
			} else if (cls.equals(ICategorizingObserver.class)) {
				return new Categorization();
			} else if (cls.equals(IClassifyingObserver.class)) {
				return new Classification();
			} else if (cls.equals(IMeasuringObserver.class)) {
				return new Measurement();
			} else if (cls.equals(IRankingObserver.class)) {
				return new Ranking();
			} else if (cls.equals(IValuingObserver.class)) {
				return new Value();
			} else if (cls.equals(IModel.class)) {
				return new Model();
			} else if (cls.equals(IContext.class)) {
				return new Context();
			} else if (cls.equals(IDataSource.class)) {
				return new DataSourceDefinition();
			} else if (cls.equals(IStoryline.class)) {
				return new Storyline();
			} else if (cls.equals(IScenario.class)) {
				return new Scenario();
			} else if (cls.equals(IAgentModel.class)) {
				return new AgentModel();
			} else if (cls.equals(IConcept.class)) {
				return new ConceptObject();
			} else if (cls.equals(IProperty.class)) {
				return new PropertyObject();
			} else if (cls.equals(IObservation.class)) {
				return new Observation();
			} else if (cls.equals(IUnit.class)) {
				return new UnitDefinition();
			} else if (cls.equals(IMetadata.class)) {
				return new Metadata();
			}
			
			return null;
		}

	}

	private Resolver _resolver = null;

	Resolver getResolver(IProject project, Object resource) {
		if (_resolver  == null) {
			_resolver = new Resolver(resource);
		}
		_resolver.setProject(project);
		return _resolver;
	}

	public IExpression resolveFunction(String functionId,
			Collection<String> parameterNames) {

		/*
		 * TODO see if we want to check or validate parameters
		 */
		IExpression exp = null;
		FunctionDescriptor fd = _functions.get(functionId);
		if (fd != null) {
			try {
				exp = (IExpression) fd._class.newInstance();
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return exp;
	}

	/*
	 * we put all model observable instances here.
	 */
	ISession _session = null;

	/**
	 * Return the singleton model manager. Use injection to modularize the
	 * parser/interpreter.
	 * 
	 * @return
	 */
	public static ModelManager get() {

		if (_this == null) {
			_this = new ModelManager();
		}
		return _this;
	}

	private ModelManager() {
	}

	public IModel retrieveModel(String s) {
		return getModel(s);
	}

	public IModel requireModel(String s) throws ThinklabException {
		IModel ret = getModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("model " + s + " not found");
		return ret;
	}

	public IAgentModel retrieveAgentModel(String s) {
		return getAgentModel(s);
	}

	public IAgentModel requireAgentModel(String s) throws ThinklabException {
		IAgentModel ret = getAgentModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("agent model " + s + " not found");
		return ret;
	}

	public IScenario retrieveScenario(String s) {
		return getScenario(s);
	}

	public IScenario requireScenario(String s) throws ThinklabException {
		IScenario ret = getScenario(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("scenario " + s + " not found");
		return ret;
	}

	public IContext retrieveContext(String s) {
		return getContext(s);
	}

	public IContext requireContext(String s) throws ThinklabException {
		IContext ret = getContext(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("context " + s + " not found");
		return ret;
	}

	@Override
	public void releaseNamespace(String namespace) {
		
		namespacesById.remove(namespace);
		ArrayList<String> toRemove = new ArrayList<String>();
		for (String s : agentsById.keySet()) {
			if (s.startsWith(namespace + "/")) {
				toRemove.add(s);
			}
		}
		for (String s : toRemove) {
			agentsById.remove(s);
		}	
		toRemove.clear();
		for (String s : modelsById.keySet()) {
			if (s.startsWith(namespace + "/")) {
				toRemove.add(s);
			}
		}
		for (String s : toRemove) {
			modelsById.remove(s);
		}
		toRemove.clear();
		for (String s : scenariosById.keySet()) {
			if (s.startsWith(namespace + "/")) {
				toRemove.add(s);
			}
		}
		for (String s : toRemove) {
			contextsById.remove(s);
		}
		toRemove.clear();
		for (String s : contextsById.keySet()) {
			if (s.startsWith(namespace + "/")) {
				toRemove.add(s);
			}
		}
		for (String s : toRemove) {
			contextsById.remove(s);
		}
	}

	@Override
	public String getSource(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModelObject> getDependencies(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<INamespace> getNamespaces() {
		return namespacesById.values();
	}

	@Override
	public IContext getCoverage(IModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IScenario> getApplicableScenarios(IModel model,
			IContext context, boolean isPublic) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized INamespace loadFile(String resourceId, String namespaceId, IProject project) throws ThinklabException {

		INamespace ret = null;

		if (resourceId.endsWith(".tql")) {

			Injector injector = Guice.createInjector(new ModellingModule());
			ModelGenerator thinkqlParser = injector.getInstance(ModelGenerator.class);
			ret = thinkqlParser.parse(resourceId, getResolver(project, resourceId));
			
			if (namespaceId != null && !namespaceId.equals(ret.getId())) {
				throw new ThinklabValidationException(
						"resource "+ resourceId + " declares namespace: " + ret.getId() + 
						" when " + namespaceId + " was expected");
			}

		} else if (resourceId.endsWith(".clj")) {

			/*
			 * TODO we need to rewrite the clojure modeling interface to produce
			 * beans compatible with ModelAdapter.
			 */

		} else if (resourceId.endsWith(".owl")) {

			File ofile = new File(resourceId);
			try {
				Thinklab.get().getKnowledgeRepository().
					refreshOntology(ofile.toURI().toURL(), namespaceId, false);
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}

			IOntology ontology = Thinklab.get().getKnowledgeRepository().requireOntology(namespaceId);
			ret = new Namespace();
			((INamespaceDefinition)ret).setId(namespaceId);
			((INamespaceDefinition)ret).setResourceUrl(resourceId);
			((INamespaceDefinition)ret).setTimeStamp(ofile.lastModified());
			((Namespace)ret).setOntology(ontology);
			namespacesById.put(namespaceId, ret);
		}

		return ret;
	}

	@Override
	public IModel getModel(String s) {
		return modelsById.get(s);
	}

	@Override
	public IAgentModel getAgentModel(String s) {
		return agentsById.get(s);
	}

	@Override
	public IScenario getScenario(String s) {
		return scenariosById.get(s);
	}

	@Override
	public IContext getContext(String s) {
		return contextsById.get(s);
	}

	@Override
	public INamespace getNamespace(String ns) {
		return namespacesById.get(ns);
	}

	@Override
	public IModelObject getModelObject(String object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<INamespace> load(IProject project)
			throws ThinklabException {

		ArrayList<INamespace> ret = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();

		loadInternal(project.getSourceDirectory(), read, ret, "", project);

		return ret;
	}

	public Collection<INamespace> loadSourceDirectory(File sourcedir) throws ThinklabException {

		ArrayList<INamespace> ret = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();

		loadInternal(sourcedir, read, ret, null, null);

		return ret;
	}

	private void loadInternal(File f, HashSet<File> read, ArrayList<INamespace> ret, String path,
			IProject project) throws ThinklabException {

		String pth = 
				path == null ? 
					"" : 
					(path + (path.isEmpty() ? "" : ".") + CamelCase.toLowerCase(MiscUtilities.getFileBaseName(f.toString()), '-'));

		INamespace ns = null;

		if (f.isDirectory()) {
			for (File fl : f.listFiles()) {
				loadInternal(fl, read, ret, pth, project);
			}

		} else if (f.toString().endsWith(".owl")) {
			ns = loadFile(f.toString(), pth, project);
		} else if (f.toString().endsWith(".tql") || f.toString().endsWith(".clj")) {			
			ns = loadFile(f.toString(), pth, project);
		}

		if (ns != null) {
			Thinklab.get().logger().info("namespace " + ns.getId() + " created from " + f);
			ret.add(ns);
		}

	}


	public void registerFunction(String id, String[] parameterNames,
			Class<?> cls) {
		_functions.put(id, new FunctionDescriptor(id, parameterNames, cls));
	}

}