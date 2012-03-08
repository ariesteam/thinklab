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
package org.integratedmodelling.thinklab.modelling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.integratedmodelling.lang.model.ConceptObject;
import org.integratedmodelling.lang.model.ModelObject;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.lang.model.PropertyObject;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.modelling.IAgentModel;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelManager;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.project.ThinklabProject;
import org.integratedmodelling.thinklab.proxy.ModellingModule;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.Plugin;
import org.java.plugin.registry.PluginPrerequisite;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModelManager implements IModelManager, IModelFactory {

	private static ModelManager _this = null;
	
	private Hashtable<String, IModel> modelsById = new Hashtable<String, IModel>();
	private Hashtable<String, IScenario> scenariosById = new Hashtable<String, IScenario>();
	private Hashtable<String, IContext> contextsById = new Hashtable<String, IContext>();
	private Hashtable<String, IAgentModel> agentsById = new Hashtable<String, IAgentModel>();
	private Hashtable<String, INamespace> namespacesById = new Hashtable<String, INamespace>();
	
	// API source beans for all the model objects
	private Hashtable<String, Namespace> namespaceBeans = new Hashtable<String, Namespace>();

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

			Plugin plugin = null;
			if (project instanceof ThinklabProject) {
				plugin = ((ThinklabProject)project).getPlugin();
			}
			
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
					} else if (reference.contains("://")) {
						URL url = new URL(reference);						
						return url.openStream();
					}

					/*
					 * plugin resource has precedence even over local file with same path
					 */
					if (plugin != null) {
						URL url = plugin.getManager().
								getPluginClassLoader(plugin.getDescriptor()).
								getResource(reference);
						if (url != null) {
							return url.openStream();
						}
					}

				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
				
				/*
				 * if we get here we haven't found it, look it up in all DIRECTLY imported projects (non-recursively)
				 */
				if (plugin != null) {
					for (PluginPrerequisite pr : plugin.getDescriptor().getPrerequisites()) {
						try {
							Plugin dpp = plugin.getManager().getPlugin(pr.getPluginId());
							URL url = dpp.getManager().
									getPluginClassLoader(dpp.getDescriptor()).
									getResource(reference);
							if (url != null) {
								return url.openStream();
							}
						} catch (Exception e) {
							throw new ThinklabIOException(e);
						}
					}
				}
			} else if (namespace != null) {
				
				/*
				 * find resource using path corresponding to namespace, either in plugin classpath or
				 * relative filesystem.
				 */
				String fres = namespace.replace('.', '/');
				if (plugin != null) {
					URL url = plugin.getManager().
							getPluginClassLoader(plugin.getDescriptor()).
							getResource(reference);
					if (url != null) {
						try {
							return url.openStream();
						} catch (IOException e) {
							throw new ThinklabIOException(e);
						}
					}
				}
				
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
		public void onNamespaceDeclared(String namespaceId, String resourceId,
				org.integratedmodelling.lang.model.Namespace namespace) {
		}

		@Override
		public void onNamespaceDefined(
				org.integratedmodelling.lang.model.Namespace namespace) throws ThinklabException {

			if (!namespacesById.containsKey(namespace.getId())) {
				INamespace ret;
				try {
					ret = new ModelAdapter().createNamespace(namespace);
					namespacesById.put(namespace.getId(), ret);
					namespaceBeans.put(namespace.getId(), namespace);
				} catch (ThinklabException e) {
					onException(e, 0);
				}		
			}
			
			/*
			 * TODO pop resolver context
			 */
		}

		@Override
		public void validateNamespaceForResource(String resource,
				String namespace) throws ThinklabException {
			
			Plugin plugin = null;
			if (project instanceof ThinklabProject) {
				plugin = ((ThinklabProject)project).getPlugin();
			}

			if (plugin != null) {
				
				/*
				 * check that resource was read from same file path
				 */
			} else {
				
				/*
				 * check that filename path is same in namespace
				 */
			}
			
			
		}

		public void setProject(IProject project) {
			this.project = project;
		}

		@Override
		public ConceptObject resolveExternalConcept(String id,
				Namespace namespace, int line) throws ThinklabException {

			if (KnowledgeManager.get().retrieveConcept(id) == null) {
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
		public PropertyObject resolveExternalProperty(String id,
				Namespace namespace, int line) throws ThinklabException {

			if (KnowledgeManager.get().retrieveProperty(id) == null) {
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
		public void onModelObjectDefined(Namespace namespace, ModelObject ret) {
			// TODO Auto-generated method stub
			
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
	ModelMap _map = null;
	
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
		_map = new ModelMap();
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
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
			Namespace nbean = thinkqlParser.parse(resourceId, getResolver(project, resourceId));
			/*
			 * namespace has been put here by the resolver's callback.
			 */
			return namespacesById.get(nbean.getId());			
			
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
			Namespace ns = new Namespace();
			ns.setId(namespaceId);
			ns.setSourceFile(ofile);
			ns.setTimeStamp(ofile.lastModified());
			ret = new NamespaceImpl(ns);
			((NamespaceImpl)ret).setOntology(ontology);
			
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
		
		for (File dir : project.getSourceFolders()) {
		
			if (!dir.isDirectory() || !dir.canRead()) {
				throw new ThinklabIOException("source directory " + dir + " is unreadable");
			}	 
		
			loadInternal(dir, read, ret, "", project);
		}
		
		return ret;
	}

	public Collection<INamespace> loadSourceDirectory(File sourcedir) throws ThinklabException {

		ArrayList<INamespace> ret = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();
		
		loadInternal(sourcedir, read, ret, "", null);

		return ret;
	}
	
	private void loadInternal(File f, HashSet<File> read, ArrayList<INamespace> ret, String path,
			IProject project) throws ThinklabException {


		if (f.isDirectory()) {

			String pth = path.isEmpty() ? "" : (path + "." + MiscUtilities.getFileBaseName(f.toString()));

			for (File fl : f.listFiles()) {
				loadInternal(fl, read, ret, pth, project);
			}
			
		} else if (f.toString().endsWith(".owl")) {

			INamespace ns = loadFile(f.toString(), path + "." + MiscUtilities.getFileBaseName(f.toString()), project);
			if (ns != null) {
				ret.add(ns);
			}
			
		} else if (f.toString().endsWith(".tcl") || f.toString().endsWith(".clj")) {
			
			INamespace ns = loadFile(f.toString(), path + "." + MiscUtilities.getFileBaseName(f.toString()), project);
			if (ns != null) {
				ret.add(ns);
			}
		}
		
	}

	@Override
	public INamespace processNamespace(Namespace namespace) throws ThinklabException {
		return new ModelAdapter().createNamespace(namespace);
	}

	public void registerFunction(String id, String[] parameterNames,
			Class<?> cls) {
		_functions.put(id, new FunctionDescriptor(id, parameterNames, cls));
	}


}
