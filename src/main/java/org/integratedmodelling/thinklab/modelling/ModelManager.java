package org.integratedmodelling.thinklab.modelling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.factories.IModelManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IPrototype;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.ICategorizingObserver;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IContext;
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
import org.integratedmodelling.thinklab.api.modelling.parsing.IClassificationDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IConceptDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionCall;
import org.integratedmodelling.thinklab.api.modelling.parsing.ILanguageDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelObjectDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.INamespaceDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IPropertyDefinition;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer;
import org.integratedmodelling.thinklab.modelling.compiler.ModelResolver;
import org.integratedmodelling.thinklab.modelling.datasets.FileDataset;
import org.integratedmodelling.thinklab.modelling.interfaces.IExpressionContextManager;
import org.integratedmodelling.thinklab.modelling.lang.Categorization;
import org.integratedmodelling.thinklab.modelling.lang.Classification;
import org.integratedmodelling.thinklab.modelling.lang.ConceptObject;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.modelling.lang.FunctionCall;
import org.integratedmodelling.thinklab.modelling.lang.Measurement;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;
import org.integratedmodelling.thinklab.modelling.lang.Model;
import org.integratedmodelling.thinklab.modelling.lang.ModelObject;
import org.integratedmodelling.thinklab.modelling.lang.Namespace;
import org.integratedmodelling.thinklab.modelling.lang.Observation;
import org.integratedmodelling.thinklab.modelling.lang.PropertyObject;
import org.integratedmodelling.thinklab.modelling.lang.Ranking;
import org.integratedmodelling.thinklab.modelling.lang.Scenario;
import org.integratedmodelling.thinklab.modelling.lang.Storyline;
import org.integratedmodelling.thinklab.modelling.lang.UnitDefinition;
import org.integratedmodelling.thinklab.modelling.lang.Value;
import org.integratedmodelling.thinklab.modelling.lang.expressions.GroovyExpressionManager;
import org.integratedmodelling.thinklab.proxy.ModellingModule;
import org.integratedmodelling.thinklab.query.Queries;
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

	public static final String DEFAULT_EXPRESSION_LANGUAGE = "groovy";

	/*
	 * we provide a default namespace for models that come right out of a kbox.
	 */
	static INamespace _defaultModelNamespace = null;
	
	private Hashtable<String, IModel> modelsById = new Hashtable<String, IModel>();
	private Hashtable<String, IScenario> scenariosById = new Hashtable<String, IScenario>();
	private Hashtable<String, IContext> contextsById = new Hashtable<String, IContext>();
	private Hashtable<String, INamespace> namespacesById = new Hashtable<String, INamespace>();

	/*
	 * FIXME type handling just a stub, no optional vs. mandatory distinction, no
	 * description.
	 */
	class FunctionDescriptor implements IPrototype {
		public FunctionDescriptor(String id, String[] parameterNames,
				Class<?> cls) {
			this._id = id;
			this._parameterNames = parameterNames;
			this._class = cls;
		}
		String   _id;
		String[] _parameterNames;
		Class<?> _class;

		@Override
		public String getId() {
			return _id;
		}
		@Override
		public IConcept getReturnType() {
			return Thinklab.THING;
		}
		@Override
		public List<String> getMandatoryArgumentNames() {
			return Arrays.asList(_parameterNames);
		}
		@Override
		public List<String> getOptionalArgumentNames() {
			return new ArrayList<String>();
		}
		@Override
		public IConcept getArgumentType(String argumentName) {
			return Thinklab.THING;
		}
		@Override
		public String getDescription() {
			return "";
		}
	}

	private HashMap<String, FunctionDescriptor> _functions =
			new HashMap<String, ModelManager.FunctionDescriptor>();

	/**
	 * This one resolves namespace source files across imported plugins and handles errors.
	 * @author Ferd
	 *
	 */
	public class Resolver implements IResolver {

		ArrayList<Pair<String,Integer>> infos = new ArrayList<Pair<String,Integer>>();
		String resourceId = "";
		IProject project;
		Namespace namespace;
		URL resourceUrl; 
		
		IContext currentContext = null;
		
		/* this will be set to the resource's timestamp if the namespace is read 
		 * from a resource whose timestamp can be determined. Otherwise any
		 * storeable resource will be refreshed.
		 */
		long _timestamp = new Date().getTime();
		
		boolean _isInteractive = false;
		IModelObject _lastProcessed = null;
		
		/*
		 * timestamp of kbox-stored namespace, if this is < _timestamp and neither is 0 we
		 * need to refresh the kbox with the contents of the namespace.
		 */
		long _storedTimestamp = 0l;
		private InputStream _interactiveInput;
		private PrintStream _interactiveOutput;
		private HashMap<String, IModelObjectDefinition> symbolTable = 
				new HashMap<String, IModelObjectDefinition>();

		public Resolver(IProject project) {
			this.project = project;
		}

		/*
		 * create a resolver for interactive use.
		 */
		public Resolver(InputStream input, PrintStream output) {
			_isInteractive = true;
			_interactiveInput = input;
			_interactiveOutput = output;
			currentContext = new Context();
		}

		@Override
		public boolean onException(Throwable e, int lineNumber) {

			/*
			 * add error directly to namespace if ns isn't null and interactive is false.
			 */
			if (namespace != null && e instanceof ThinklabException) {
				namespace.addError(0, e.getMessage(), lineNumber);
			}
			Thinklab.get().logger().error(resourceId + ": " + lineNumber + ": " + e.getMessage());
			if (_interactiveOutput != null) {
				_interactiveOutput.println("error: " + e.getMessage());
			}
			return true;
		}
		
		@Override
		public boolean isInteractive() {
			return _isInteractive;
		}
		
		@Override
		public boolean onWarning(String warning, int lineNumber) {

			/*
			 * add warning directly to namespace
			 */
			if (namespace != null) {
				namespace.addWarning(warning, lineNumber);
			}

			Thinklab.get().logger().warn(resourceId + ": " + lineNumber + ": " + warning);
			if (_isInteractive) {
				_interactiveOutput.println("warning: " + warning);
			}
			return true;
		}

		@Override
		public boolean onInfo(String info, int lineNumber) {
			infos.add(new Pair<String, Integer>(info, lineNumber));
			Thinklab.get().logger().info(resourceId + ": " + lineNumber + ": " + info);
			if (_isInteractive) {
				_interactiveOutput.println("info: " + info);
			}
			return true;
		}

		@Override
		public void onNamespaceDeclared() {
			
			if (namespace.hasErrors()) {
				return;
			}
			
			/*
			 * if we have stored this namespace previously, retrieve its record and set the
			 * previous modification date.
			 */
			IQuery query = Queries.select(NS.NAMESPACE).restrict(NS.HAS_ID, Queries.is(namespace.getId()));
			try {
				IKbox kbox = Thinklab.get().getStorageKboxForNamespace(namespace);
				List<ISemanticObject<?>> res = kbox.query(query);
				if (res.size() > 0) {
					Namespace ns = (Namespace)res.get(0);
					_storedTimestamp = ns.getTimeStamp();
				}
				
				/*
				 * if we have stored something and we are younger than the stored ns, remove
				 * all models coming from it so we can add our new ones.
				 */
				if (_storedTimestamp != 0l && _timestamp > _storedTimestamp) {
					Thinklab.get().logger().info(
						"refreshing permanent storage for namespace " + namespace.getId() + " in kbox " + kbox.getUri());
					kbox.removeAll(Queries.select(NS.MODEL).restrict(NS.HAS_NAMESPACE_ID, Queries.is(namespace.getId())));
				}
			} catch (ThinklabException e) {
				/*
				 * TBC
				 * do nothing
				 */
			}
		}
		
		@Override
		public void onNamespaceDefined() {

			if (namespace.hasErrors()) {
				return;
			}
			
			/*
			 * at this point, this should be moot as we define everything incrementally, 
			 * but leave it here for any final tasks we may want to implement.
			 */
			try {
				namespace.initialize();
			} catch (Exception e) {
				onException(e, 0);
			}
			
			/*
			 * TODO pop resolver context
			 */
			
			
			/*
			 * if was stored and not changed, do nothing
			 */
			if (_storedTimestamp == 0l || _timestamp > _storedTimestamp) {
				
				try {
					IKbox kbox = Thinklab.get().getStorageKboxForNamespace(namespace);
					if (_storedTimestamp != 0l) {
						IQuery query = Queries.select(NS.NAMESPACE).restrict(NS.HAS_ID, Queries.is(namespace.getId()));
						kbox.removeAll(query);
					}
					kbox.store(namespace);
				} catch (Exception e) {
					// -1 flags exception that don't come from the parser
					onException(e, -1);
				}
			}
			
			Thinklab.get().logger().info("namespace " + namespace.getId() + " created from " + resourceId);

		}

		@Override
		public IConceptDefinition resolveExternalConcept(String id, int line)  {
			
			if (Thinklab.get().getConcept(id) == null) {
				onException(new ThinklabValidationException("concept " + id + " unknown"), line);
				return null;
			}

			ConceptObject co = new ConceptObject();
			co.setId(id);

			/*
			 * TODO decide how to handle the import with the namespace
			 */

			return co;

		}

		@Override
		public IPropertyDefinition resolveExternalProperty(String id, int line)  {

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

//		public IExpression resolveFunction(String functionId,
//				Collection<String> parameterNames) {
//			IExpression ret = ModelManager.this.resolveFunction(functionId, parameterNames);
//			ret.setProjectContext(project);
//			return ret;
//		}

		@Override
		public void onModelObjectDefined(IModelObject ret)  {

			if (ret.getNamespace().hasErrors()) {
				return;
			}

			/*
			 * actualize all knowledge so that the object is complete and we can create observables
			 * as required.
			 */
			try {
				namespace.flushKnowledge();
				/*
				 * this creates any remanining knowledge.
				 */
				((ModelObject<?>)ret).initialize();

			} catch (Exception e) {
				onException(e, ret.getFirstLineNumber());
			}
			
			/*
			 * store anything that reports storage metadata.
			 */
			IMetadata md = ((ModelObject<?>)ret).getStorageMetadata();
			if (md != null && (_storedTimestamp == 0l || (_timestamp != 0l && _timestamp > _storedTimestamp))) {
				ret.getMetadata().merge(md);
				IKbox kbox = null;
				try {
					kbox = Thinklab.get().getStorageKboxForNamespace(namespace);
				} catch (ThinklabException e1) {
					onException(e1, -1);
				}
				if (kbox != null) {
					try {
						kbox.store(ret);
					} catch (ThinklabException e) {
						onException(e, ret.getLastLineNumber());
					}
				}
			}
			
			_lastProcessed = ret;
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
			} else if (cls.equals(IStoryline.class)) {
				return new Storyline();
			} else if (cls.equals(IScenario.class)) {
				return new Scenario();
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
			} else if (cls.equals(IFunctionCall.class)) {
				return new FunctionCall();
			} else if (cls.equals(IClassificationDefinition.class)) {
				return new org.integratedmodelling.thinklab.modelling.Classification();
			}
			
			return null;
		}

		@Override
		public boolean isGeneratedId(String id) {
			return ModelManager.isGeneratedId(id);
		}

		@Override
		public String generateId(IModelObject o) {
			return ModelManager.generateId(o);
		}

//		@Override
//		public Object runFunction(IFunctionDefinition function) {
//			IExpression f = resolveFunction(function.getId(), function.getParameters().keySet());
//			if (f != null) {
//				try {
//					return f.eval(function.getParameters());
//				} catch (ThinklabException e) {
//				}
//			}
//			return null;
//		}

		@Override
		public IModelObject getLastProcessedObject() {
			IModelObject ret = _lastProcessed;
			_lastProcessed = null;
			return ret;
		}

		@Override
		public void handleObserveStatement(Object observable, IContext ctx, boolean resetContext,
				int lineNumber)  {

			try {

				/*
				 * actualize all knowledge so that the object is complete and we can create observables
				 * as required.
				 */
				((Namespace)namespace).flushKnowledge();
			
				/*
				 * switch to another context or to a new one if requested.
				 */
				if (ctx != null) {
					((Context)currentContext).clear();
					currentContext.merge(ctx);
				} else	if (resetContext) {
					((Context)currentContext).clear();
				}
			
				Object obs = null;
				if (observable instanceof IModel) {
					obs = observable;
				} else if (observable instanceof IList) {
					obs = Thinklab.get().entify((IList)observable);
				} else if (observable instanceof IContext) {
					((Context)currentContext).clear();
					currentContext.merge((IContext)observable);
				} else if (observable instanceof IConceptDefinition) {
					obs = Thinklab.get().entify(PolyList.list(Thinklab.c(((IConceptDefinition)observable).getName())));
				} else if (observable instanceof IFunctionCall) {
				
					/*
					 * must eval to extent, to be merged with current context directly.
					 */
					IFunctionCall function = (IFunctionCall)observable;
				
					Observation o = null;
				
					// run function and store observation
					try {
						Object obj = (Observation) function.call();
						if ( !(obj instanceof Observation))
							throw new ThinklabValidationException("function " + function.getId() + " does not return any value");
						o  = (Observation) obj;
					} catch (ThinklabException e) {
						throw new ThinklabValidationException(e);
					}
				
					currentContext.merge(o);
				
					return;
				}
			
				if (obs != null) {
				
					IObservation observation = Thinklab.get().observe(obs, currentContext);
					if (observation != null) {
						currentContext.merge(observation.getContext());
						/*
						 * trick to visualize only if we're using the shell
						 */
						if (_interactiveInput != null)
							visualizeContext(currentContext);
					}
				}
				
			} catch (Exception e) {
				onException(e, lineNumber);
			}
		}

		@Override
		public IResolver getImportResolver(IProject project) {
			Resolver ret = new Resolver(project);
			ret._interactiveInput = _interactiveInput;
			ret._interactiveOutput = _interactiveOutput;
			ret._isInteractive = _isInteractive;
			ret.currentContext = currentContext;
			return ret;
		}

		@Override
		public INamespace getNamespace(String id, int lineNumber) {

			INamespace ns = namespacesById.get(id);
			
			if (ns == null && this.project != null && this.project.providesNamespace(id)) {
				
				/*
				 * preload it
				 */
				try {
					ns = loadFile(this.project.findResourceForNamespace(id).toString(), id, this.project, this);
				} catch (ThinklabException e) {
					onException(e, lineNumber);
				}
			}
			
			return ns;
		}

		@Override
		public IResolver getNamespaceResolver(String namespace, String resource) {
			
			/*
			 * in interactive use, we allow the namespace to be defined incrementally
			 */
			if (namespacesById.get(namespace) != null && !isInteractive()) {
				Thinklab.get().logger().warn("warning: namespace " + namespace + " is being redefined");
				releaseNamespace(namespace);
			}

			/*
			 * create namespace
			 */
			Namespace ns = (Namespace) (isInteractive() ? namespacesById.get(namespace) : null);
			long timestamp = new Date().getTime();
			URL url = null;
			
			if (ns == null) {
				
				ns = new Namespace();
				ns.setId(namespace);
				ns.setResourceUrl(resource);
			
				/*
				 * resolve the resource ID to an openable URL
				 */
				if (resource != null) {
				
					File f = new File(resource);

					try {
						if (f.exists() && f.isFile() && f.canRead()) {
							timestamp = f.lastModified();
							url = f.toURI().toURL();					
						} else if (resource.contains(":/")) {
							url = new URL(resource);
							if (url.toString().startsWith("file:")) {
								f = new File(url.getFile());
								timestamp = f.lastModified();
							}
						}
					} catch (Exception e) {
						onException(e, -1);
					}
				}
				
				if (isInteractive()) {
					/*
					 * define NS for the context so it won't complain
					 */
					((Context)currentContext).setNamespace(ns);
				} else {
					namespacesById.put(namespace, ns);
				}
			}
			
			ns.setTimeStamp(timestamp);
			
			/*
			 * create new resolver with same project and new namespace
			 */
			Resolver ret = new Resolver(project);
			ret.namespace = ns;
			ret.resourceId = resource;
			ret.resourceUrl = url;
			ret._interactiveInput = _interactiveInput;
			ret._interactiveOutput = _interactiveOutput;
			ret._isInteractive = _isInteractive;
			ret.currentContext = currentContext;
			
			return ret;
		}

		@Override
		public INamespaceDefinition getNamespace() {
			return namespace;
		}

		@Override
		public InputStream openStream() {
			
			if (resourceUrl == null)
				onException(
						new ThinklabInternalErrorException(
							"internal error: namespace " + namespace.getId() + " has no associated resource"), -1);

			try {
				return resourceUrl.openStream();
			} catch (IOException e) {
				onException(e, -1);
			}
			
			return null;
		}

		@Override
		public HashMap<String, IModelObjectDefinition> getSymbolTable() {
			return this.symbolTable;
		}

		public IContext getCurrentContext() {
			return currentContext;
		}

		@Override
		public IProject getProject() {
			return project;
		}

//		@Override
//		public IExpression resolveFunction(String functionId,
//				Collection<String> parameterNames) {
//			// TODO Auto-generated method stub
//			return null;
//		}

		@Override
		public boolean validateFunctionCall(IFunctionCall ret) {
			// TODO check function agains known prototypes
			return true;
		}
	}

	public Resolver getResolver(IProject project) {
		return new Resolver(project);
	}

	public void visualizeContext(IContext context) {
		
		FileDataset dset = new FileDataset(context);
		try {
			dset.persist(Thinklab.get().getWorkspace("context").toString());
		} catch (ThinklabException e) {
			Thinklab.get().logger().warn("error visualizing context: " + e.getMessage());
		}
	}

	public Resolver getInteractiveResolver(InputStream input, PrintStream output) {
		return new Resolver(input, output);
	}

	public static boolean isGeneratedId(String id) {
		return id.endsWith("___");
	}

	public static String generateId(IModelObject o) {
		return UUID.randomUUID().toString() + "___";
	}

	public static INamespace getDefaultModelNamespace() {
		return _defaultModelNamespace;
	}
	
	public IExpression getExpressionForFunctionCall(IFunctionCall functionCall) throws ThinklabInternalErrorException {

		IExpression exp = null;
		FunctionDescriptor fd = _functions.get(functionCall.getId());
		if (fd != null) {
			try {
				exp = (IExpression) fd._class.newInstance();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		return exp;
	}

	/*
	 * we put all model observable instances here.
	 */
	ISession _session = null;

	public ModelManager() {
		if (_defaultModelNamespace == null) {
			_defaultModelNamespace = new Namespace();
			((Namespace)_defaultModelNamespace).setId("org.integratedmodelling.ks.models");
		}
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
		
		Namespace ns = (Namespace) getNamespace(namespace);
		
		ns.releaseKnowledge();
		
		namespacesById.remove(namespace);
		
		ArrayList<String> toRemove = new ArrayList<String>();
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
		return loadFile(resourceId, namespaceId, project, getResolver(project));
	}

	public synchronized INamespace loadFile(String resourceId, String namespaceId, IProject project, IResolver resolver) throws ThinklabException {

		INamespace ret = null;

		if (resourceId.endsWith(".tql")) {

			Thinklab.get().logger().info("loading " + resourceId + " for " + namespaceId);
			
			IResolver res = resolver.getNamespaceResolver(namespaceId, resourceId);
			
			Injector injector = Guice.createInjector(new ModellingModule());
			ModelGenerator thinkqlParser = injector.getInstance(ModelGenerator.class);
			ret = thinkqlParser.parse(namespaceId, resourceId, res);
			
			if (namespaceId != null && !namespaceId.equals(ret.getId())) {
				throw new ThinklabValidationException(
						"resource "+ resourceId + " declares namespace: " + ret.getId() + 
						" when " + namespaceId + " was expected");
			}

		} else if (resourceId.endsWith(".owl")) {

			Thinklab.get().logger().info("loading " + resourceId + " for " + namespaceId);

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
		return null;
	}

	@Override
	public Collection<INamespace> load(IProject project)
			throws ThinklabException {

		ArrayList<INamespace> ret = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();

		loadInternal(new File(project.getLoadPath() + File.separator + project.getSourceDirectory()), read, ret, "", project);

		return ret;
	}

	@Override
	public Collection<INamespace> loadSourceDirectory(File sourcedir, IProject project) throws ThinklabException {

		ArrayList<INamespace> ret = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();

		loadInternal(sourcedir, read, ret, null, project);

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
			if (!namespacesById.containsKey(pth)) {
				ns = loadFile(f.toString(), pth, project);
			}
		} else if (f.toString().endsWith(".tql") || f.toString().endsWith(".clj")) {
			if (!namespacesById.containsKey(pth)) {
				ns = loadFile(f.toString(), pth, project);
			}
		}

		if (ns != null) {			
			ret.add(ns);
		}

	}

	public void registerFunction(String id, String[] parameterNames,
			Class<?> cls) {
		_functions.put(id, new FunctionDescriptor(id, parameterNames, cls));
	}

	@Override
	public IObservation observe(Object object, IContext context)
			throws ThinklabException {
		
		ISemanticObject<?> so = null;
		
		if (object instanceof IConcept) {
			so = Thinklab.get().entify(PolyList.list(object));
		} else {
			so = Thinklab.get().annotate(object);
		}
		
		IObservation ret = null;
		IContext ctx = new Context((Context) context);
		ModelResolver resolver = new ModelResolver(so.getNamespace(), ctx);
		IModel root = resolver.resolve(so);
		if (root != null) {
			Contextualizer ctxer = new Contextualizer(resolver.getModelStructure());
			ret = ctxer.run(root, ctx);
		}
		return ret;
	}
	
	/**
	 * TODO make this configurable - won't be needed for quite some time, if ever.
	 * 
	 * @param language
	 * @return
	 */
	public IExpressionContextManager getExpressionManager(String language) {

		if (language.equals("groovy"))
			return new GroovyExpressionManager();
		
		throw new ThinklabRuntimeException("unknown expression language: " + language);
	}

	public Collection<IPrototype> getFunctionPrototypes() {
		
		/*
		 * one day I'll understand why it can't just cast the f'ing collection.
		 */
		ArrayList<IPrototype> ret = new ArrayList<IPrototype>();
		for (FunctionDescriptor f :  _functions.values())
			ret.add(f);
		return ret;
	}

	/**
	 * TODO modularize eventually (the way the client library does it).
	 * @param fileExtension
	 * @return
	 */
	public boolean canParseExtension(String fileExtension) {
		return fileExtension.equals("tql") || fileExtension.equals("owl");
	}

}
