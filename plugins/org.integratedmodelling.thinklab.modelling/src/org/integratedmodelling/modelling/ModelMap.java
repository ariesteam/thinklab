package org.integratedmodelling.modelling;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.knowledge.NamespaceOntology;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.exception.ThinklabDuplicateNameException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.graph.GraphViz;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.MiscUtilities;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The holder of the map to the whole model landscape. The map links together 
 * namespaces, models, contexts, storyline and agent objects with their source code and can 
 * reconstruct any of them or all after any change. Model content from external sources can
 * be integrated using the model map and synchronized to files so it can be edited by any
 * application that can read the clojure specs.
 * 
 * The map also maintains indexes of all nodes by ID and can answer dependency questions 
 * about any model node.
 * 
 * @author Ferdinando
 *
 */
public class ModelMap {

	static boolean dirty = false;

	/*
	 * ---------------------------------------------------------------------
	 * boring node/edge types start. An OWL ontology would be simpler.
	 * ---------------------------------------------------------------------
	 */
	public abstract static class Entry {
		
		private boolean modified = false;
		public long lastModification;
		
		public abstract void unlink();
		
		public void setDirty() {
			modified = true;
			lastModification = new Date().getTime();
			ModelMap.dirty = true;
		}
		public boolean isDirty() {
			return modified;
		}
		
		public Entry findSourceEntry() {
			Entry ret = null;
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof HasSourceEdge) {
					ret = e.getTargetObservation();
					break;
				}
			}
			return ret;
		}

		public Entry getFirstSourceEntry() {
			Entry ret = null;
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof SourceEdge) {
					ret = e.getTargetObservation();
					break;
				}
			}
			return ret;
		}
		
		public Entry findResourceEntry() {
			Entry ret = null;
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof HasResourceEdge) {
					ret = e.getTargetObservation();
					break;
				}
			}
			return ret;
		}
		
		public Entry findNextEntry() {
			Entry ret = null;
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof HasNextEdge) {
					ret = e.getTargetObservation();
					break;
				}
			}
			return ret;
		}
		
		public Entry findDependentEntry() {
			Entry ret = null;
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof DependsOnEdge) {
					ret = e.getTargetObservation();
					break;
				}
			}
			return ret;
		}
		
		public Entry findNamespaceEntry() {
			Entry ret = null;
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof HasNamespaceEdge) {
					ret = e.getTargetObservation();
					break;
				}
			}
			return ret;
		}
		
		public String getSource() {
		
			String ret = "";
			
			if (this instanceof FormObjectEntry) {
				
				Entry e = this.findSourceEntry();
				ret = ((CodeFragmentEntry)e).source;
			} else if (this instanceof NamespaceEntry) {
				Entry e = this.findResourceEntry();
				ret = e.getSource();
			} else if (this instanceof ResourceEntry) {
				Entry e = this.getFirstSourceEntry();
				while (e != null) {
					ret += ((CodeFragmentEntry)e).source;
					e = e.findNextEntry();
					if (e != null) {
						ret += "\n\n";						
					}
				}
			}
			
			return ret;
		}
	}
	
	public static class FormObjectEntry extends Entry {

		public IModelForm form;

		public FormObjectEntry(IModelForm form) {
			this.form = form;
		}
	
		@Override
		public String toString() {
			return form.getId();
		}

		@Override
		public void unlink() {
			map.removeVertex(this);
			ModelMap.dirty = true;
		}	
		
		/**
		 * get the list of forms that depend on this one.
		 */
		public DefaultDirectedGraph<IModelForm, DepEdge> getDependencies() {
			
			DefaultDirectedGraph<IModelForm, DepEdge> ret = 
				new DefaultDirectedGraph<IModelForm, DepEdge>(DepEdge.class);
			getDependenciesInternal(ret);
			return ret;
			
		}

		private void getDependenciesInternal(
				DefaultDirectedGraph<IModelForm, DepEdge> mep) {

			mep.addVertex(this.form);
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof DependsOnEdge) {
					Entry ee = e.getTargetObservation();
					if (ee instanceof FormObjectEntry) {
						((FormObjectEntry)ee).getDependenciesInternal(mep);
						mep.addEdge(this.form, ((FormObjectEntry)ee).form);
					}
				}
			}
		}
	}
	
	public static class CodeFragmentEntry extends Entry {

		public String source;

		public CodeFragmentEntry(String source) {
			this.source = source;
		}

		@Override
		public String toString() {
			return "CF";// StringUtils.abbreviate(source, 8);
		}

		@Override
		public void unlink() {
			map.removeVertex(this);
			ModelMap.dirty = true;
		}
	}
	public static class ResourceEntry extends Entry {

		public String resource;

		public ResourceEntry(String resource, long lastModification) {
			this.resource = resource;
			this.lastModification = lastModification;
		}

		@Override
		public String toString() {
			return MiscUtilities.getNameFromURL(resource);
		}
		
		@Override
		public void unlink() {

			ArrayList<Entry> el =  new ArrayList<ModelMap.Entry>();

			for (DepEdge e : map.incomingEdgesOf(this)) {
				if (e instanceof HasSourceEdge) {
					el.add(e.getSourceObservation());
				}
			}
			
			for (Entry e : el)
				e.unlink();
			
			map.removeVertex(this);
			ModelMap.dirty = true;
		}

	}
	public static class NamespaceEntry extends Entry {

		public String namespace;
		private IOntology ontology;
		private HashMap<IConcept, IModel> modelsByObservable = null;
		private NamespaceOntology ontologyDescriptor;

		public NamespaceEntry(String namespace, long lastModification) {
			this.namespace = namespace;
			this.lastModification = lastModification;
		}
		
		@Override
		public void unlink() {
			
			ArrayList<Entry> el =  new ArrayList<ModelMap.Entry>();
			ArrayList<String> rl = new ArrayList<String>();
			for (DepEdge e : map.outgoingEdgesOf(this)) {
				if (e instanceof HasResourceEdge) {
					rl.add(((ResourceEntry)(e.getTargetObservation())).resource);
					el.add(e.getTargetObservation());
				}
			}
			
			for (DepEdge e : map.incomingEdgesOf(this)) {
				if (e instanceof HasNamespaceEdge) {
					el.add(e.getSourceObservation());
				}
			}
			
			for (Entry e : el)
				e.unlink();

			for (String r : rl) {
				resources.remove(r);
			}
			
			map.removeVertex(this);
			ModelMap.dirty = true;
		}

		public Collection<IModelForm> getAllModelObjects() {
			ArrayList<IModelForm> ret = new ArrayList<IModelForm>();
		
			for (DepEdge e : map.incomingEdgesOf(this)) {
				if (e instanceof HasNamespaceEdge) {
					Entry ee = e.getSourceObservation();
					if (ee instanceof FormObjectEntry) {
						ret.add(((FormObjectEntry)ee).form);
					}
				}
			}
			
			return ret;
		}

		// this is lazy also to avoid issues with the annotator
		private synchronized HashMap<IConcept, IModel> getAllModels() {
			
			if (modelsByObservable == null) {
				modelsByObservable = new HashMap<IConcept, IModel>();
				for (DepEdge e : map.incomingEdgesOf(this)) {
					if (e instanceof HasNamespaceEdge) {
						Entry ee = e.getSourceObservation();
						if (ee instanceof FormObjectEntry && 
								((FormObjectEntry)ee).form instanceof IModel) {
							IModel m = (IModel) ((FormObjectEntry)ee).form;
							modelsByObservable.put(m.getObservableClass(), m);
						}
					}
				}
			}
			return modelsByObservable;
		}
		
		@Override
		public String toString() {
			return namespace;
		}
		
		public IModel getModelForObservable(IConcept c) {
			return getAllModels().get(c);
		}

		public void defineOntology(NamespaceOntology ret) {
			this.ontologyDescriptor = ret;
		}
		
		public IOntology getOntology() {
			if (ontology == null) {
				try {
					if (ontologyDescriptor != null) {
						ontology = ontologyDescriptor.getOntology();
					} else {
						ontology = 
							KnowledgeManager.get().getKnowledgeRepository().
								createTemporaryOntology(CamelCase.toLowerCamelCase(namespace, '.'));
					}
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
				

			}
			return ontology;
		}
	}

	static HashMap<String,Entry> namespaces = 
		new HashMap<String, ModelMap.Entry>();
	static HashMap<String,Entry> forms = 
		new HashMap<String, ModelMap.Entry>();
	static HashMap<String,Entry> resources = 
		new HashMap<String, ModelMap.Entry>();
	
	static DefaultDirectedGraph<Entry, DepEdge> map = 
		new DefaultDirectedGraph<Entry, DepEdge>(DepEdge.class);

	/*
	 * generalized dependency specializes to:
	 * 	  FORM_DEPENDENCY (of model, context, agent, scenario to other form)
	 *    NAMESPACE_DEPENDENCY (of model, context, agent)
	 *    SOURCE_DEPENDENCY (of model, context, agent, scenario to source chunk in file)
	 *    FILE_DEPENDENCY (of source chunk and namespace to file)
	 */
	public static class DepEdge extends DefaultEdge {

		private static final long serialVersionUID = 5926757404834780955L;

		public Entry getSourceObservation() {
			return (Entry)getSource();
		}

		public Entry getTargetObservation() {
			return (Entry)getTarget();
		}
	}
	
	public static class HasSourceEdge extends DepEdge {

		private static final long serialVersionUID = 6142612810715681661L;

		@Override
		public String toString() {
			return "hasSource";
		}
	}
	
	public static class HasNamespaceEdge extends DepEdge {

		private static final long serialVersionUID = -5609019002308371804L;

		@Override
		public String toString() {
			return "hasNamespace";
		}
	}

	public static class HasResourceEdge extends DepEdge {

		private static final long serialVersionUID = -4213027224950106375L;

		@Override
		public String toString() {
			return "hasResource";
		}
	}
	
	public static class HasNextEdge extends DepEdge {

		private static final long serialVersionUID = -1058307488813243279L;

		@Override
		public String toString() {
			return "hasNext";
		}
	}

	public static class DependsOnEdge extends DepEdge {

		private static final long serialVersionUID = -7164071102006383731L;

		@Override
		public String toString() {
			return "dependsOn";
		}
	}

	public static class SourceEdge extends DepEdge {

		private static final long serialVersionUID = 5074198053168651780L;

		@Override
		public String toString() {
			return "hasCode";
		}
	}
	
	private static void checkTopology() throws ThinklabCircularDependencyException {

		CycleDetector<Entry, DepEdge> cycleDetector = 
			new CycleDetector<Entry, DepEdge>(map);
		
		if (cycleDetector.detectCycles()) {

			Set<Entry> problemObs = cycleDetector.findCycles();
			throw new ThinklabCircularDependencyException(
					"observation has circular dependencies in " + problemObs);
		}
	}

	private static void checkName(String name) throws ThinklabDuplicateNameException {
		if (forms.get(name) != null)
			throw new ThinklabDuplicateNameException(
					"cannot use name " + name + " for more than one model object");
	}
	
	/*
	 * -------------------------------------------------------------------
	 * done with types, code starts here
	 * -------------------------------------------------------------------
	 */
	public static void sync() throws ThinklabException {
		
	}
	
	public static Entry addSource(String source, Entry resource, Entry previous) {

		Entry ret = new CodeFragmentEntry(source);
		map.addVertex(ret);
		map.addEdge(ret, resource, new HasResourceEdge());
		if (previous != null) {
			map.addEdge(previous, ret, new HasNextEdge());
		} else {
			map.addEdge(resource, ret, new SourceEdge());
		}
		return ret;
	}
	
	public static Entry addForm(
			IModelForm form, 
			Entry source,
			Entry nsEntry,
			Entry ... dependsOn) 
		throws ThinklabException {
		
		checkName(form.getName());
		
		Entry ret = new FormObjectEntry(form);
		map.addVertex(ret);
		map.addEdge(ret, source, new HasSourceEdge());
		map.addEdge(ret, nsEntry, new HasNamespaceEdge());
		
		if (dependsOn != null)	
			for (Entry dep : dependsOn) {
				if (dep != null) {
					map.addEdge(ret, dep, new DependsOnEdge());
				}
			}
		
		forms.put(form.getName(), ret);
		return ret;
	}
	
	/**
	 * Won't allow to add a namespace that exists already. We enforce the one-file, 
	 * one-namespace notion.
	 * 
	 * @param namespace
	 * @param source
	 * @return
	 * @throws ThinklabException
	 */
	public static Entry addNamespace(String namespace, Entry resource) throws ThinklabException {

		if (getNamespace(namespace) != null)
			throw new ThinklabInternalErrorException("namespace cannot be redefined: " + namespace);
	
		NamespaceEntry ret = new NamespaceEntry(namespace, resource.lastModification);

		map.addVertex(ret);
		map.addEdge(ret, resource, new HasResourceEdge());
		namespaces.put(namespace, ret);
		return ret;
	}
	
	public static Entry addResource(String resource, long lastModification) throws ThinklabException {

		if (getResource(resource) != null)
			throw new ThinklabInternalErrorException("redefined resource: " + resource);
		
		Entry ret = new ResourceEntry(resource, lastModification);
		map.addVertex(ret);
		resources.put(resource, ret);
		return ret;
	}
	
	public static Entry getNamespace(String ns) {
		return namespaces.get(ns);
	}
	
	public static Entry getFormObject(String ns) {
		return forms.get(ns);
	}
	
	public static Entry getResource(String ns) {
		return resources.get(ns);
	}
	
	public static void show() throws ThinklabException {

		GraphViz gviz = new GraphViz();
		gviz.loadGraph(map, new GraphViz.NodePropertiesProvider() {
			
			@Override
			public int getNodeWidth(Object o) {
				// TODO Auto-generated method stub
				return 68;
			}
			
			@Override
			public String getNodeId(Object o) {
				// TODO Auto-generated method stub
				return o.toString();
			}
			
			@Override
			public int getNodeHeight(Object o) {
				// TODO Auto-generated method stub
				return 32;
			}
		});
		
		gviz.show();
	}

	public static String getSource(String object) throws ThinklabException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		printSource(object, ps);
		return os.toString();
	}
	
	public static DefaultDirectedGraph<IModelForm, DepEdge> getDependencies(String object) 
		throws ThinklabException {
		
		FormObjectEntry src = (FormObjectEntry) getFormObject(object);
		if (src == null) {
			throw new ThinklabResourceNotFoundException(
					object + 
					" does not identify a model, context, scenario or agent");
		}
		return src.getDependencies();
	}
	
	public static void printSource(String object, PrintStream stream) throws ThinklabException {
		
		Entry src = getNamespace(object);
		if (src == null) 
			src = getFormObject(object);
		
		if (src == null) {
			throw new ThinklabResourceNotFoundException(
					object + 
					" does not identify a namespace, model, context, scenario or agent");
		}
		
		stream.println(src.getSource());
	}
	
	public static long getNamespaceLastModification(String ns) {
		NamespaceEntry nse = (NamespaceEntry) getNamespace(ns);
		return nse.lastModification;
	}
	
	public static IOntology getNamespaceOntology(String ns) {
		NamespaceEntry nse = (NamespaceEntry) getNamespace(ns);
		return nse.getOntology();
	}

	public static void releaseNamespace(String namespace) {
		NamespaceEntry ns = (NamespaceEntry) ModelMap.getNamespace(namespace);
		
		for (IModelForm m : ns.getAllModelObjects()) {	
			forms.remove(m.getName());
		}
		
		/*
		 * this also removes the resource and all source nodes
		 */
		ns.unlink();
		namespaces.remove(namespace);
	}

	public static Collection<Entry> getNamespaces() {
		return namespaces.values();
	}
	
	public static Collection<IModelForm> listNamespace(String ns) {
		NamespaceEntry nse = (NamespaceEntry) getNamespace(ns);
		return nse.getAllModelObjects();
	}

	public static IModelForm getModelForm(String model) throws ThinklabException {

		FormObjectEntry src = (FormObjectEntry) getFormObject(model);
		if (src == null) {
			throw new ThinklabResourceNotFoundException(
					model + 
					" does not identify a model, context, scenario or agent");
		}
		return src.form;
	}
}
