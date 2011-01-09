package org.integratedmodelling.modelling;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.thinklab.exception.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.exception.ThinklabDuplicateNameException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.graph.GraphViz;
import org.integratedmodelling.utils.MiscUtilities;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The holder of the treemap to the whole modeling landscape. The map links together 
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
		
		public void setDirty() {
			modified = true;
			ModelMap.dirty = true;
		}
		public boolean isDirty() {
			return modified;
		}
	}
	
	public static class FormObjectEntry extends Entry {

		public IModelForm form;

		public FormObjectEntry(IModelForm form) {
			this.form = form;
		}
	
		@Override
		public String toString() {
			return form.getName();
		}
		
	}
	public static class CodeFragmentEntry extends Entry {

		public String source;

		public CodeFragmentEntry(String source) {
			this.source = source;
		}

		@Override
		public String toString() {
			return StringUtils.abbreviate(source, 24);
		}
	}
	public static class ResourceEntry extends Entry {

		public String resource;

		public ResourceEntry(String resource) {
			this.resource = resource;
		}

		@Override
		public String toString() {
			return MiscUtilities.getNameFromURL(resource);
		}
	}
	public static class NamespaceEntry extends Entry {

		public String namespace;

		public NamespaceEntry(String namespace) {
			this.namespace = namespace;
		}

		@Override
		public String toString() {
			return "NS: " + namespace;
		}
	}

	static HashMap<String,Entry> namespaces = 
		new HashMap<String, ModelMap.Entry>();
	static HashMap<IModelForm,Entry> forms = 
		new HashMap<IModelForm, ModelMap.Entry>();
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
			Entry ... dependsOn) 
		throws ThinklabException {
		
		checkName(form.getName());
		
		Entry ret = new FormObjectEntry(form);
		map.addVertex(ret);
		map.addEdge(ret, source, new HasSourceEdge());
		
		if (dependsOn != null)	
			for (Entry dep : dependsOn) {
				map.addEdge(ret, dep, new DependsOnEdge());
			}
		
		forms.put(form, ret);
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
	
		Entry ret = new NamespaceEntry(namespace);
		map.addVertex(ret);
		map.addEdge(ret, resource, new HasNamespaceEdge());
		namespaces.put(namespace, ret);
		return ret;
	}
	
	public static Entry addResource(String resource) throws ThinklabException {

		if (getResource(resource) != null)
			throw new ThinklabInternalErrorException("redefined resource: " + resource);
		
		Entry ret = new ResourceEntry(resource);
		map.addVertex(ret);
		resources.put(resource, ret);
		return ret;
	}
	
	public static Entry getNamespace(String ns) {
		return namespaces.get(ns);
	}
	
	public static Entry getFormObject(IModelForm ns) {
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
		
		System.out.println(gviz.getDotSource());
		
		// gets enormous, takes hours -- gviz.show();
	}
}
