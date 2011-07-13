/**
 * KnowledgeGraph.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 25, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Apr 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * A directed graph whose nodes are knowledge objects and whose edges may be
 * tagged by properties. Implement followRelationship to define what gets in and
 * what not.
 * 
 * @author Ferdinando Villa
 * 
 */
public abstract class KnowledgeGraph extends
		DefaultDirectedGraph<Object, Object> {
	
	IKnowledge root = null;

	private static final long serialVersionUID = 9151878779989532686L;

	/*
	 * if true, do not create further links to objects that are already part of
	 * the graph.
	 */
	boolean forceTreeGeometry = false;

	/*
	 * if false, do not follow concepts or instances that are part of a
	 * different concept space than what we start at.
	 */
	boolean crossConceptSpace = false;

	/*
	 * if true and starting at a class, existing instances are followed and
	 * processed. Not recommended in a multi-user environment at least with the
	 * protege implementation (instances are all visibles).
	 */
	boolean followInstances = false;

	/*
	 * if true, edges between vertexes will only be created once, and their
	 * content set to what given in edgeContent
	 */
	boolean collapseEdges = false;
	Object edgeContent = null;

	/*
	 * when starting at an instance, 0 means that the class is not added, 1
	 * means that it's added but not further followed, and >1 means that the
	 * whole class structure is generated.
	 */
	int followClassLevel = 0;

	/*
	 * remember if we have started the current graph from a concept or an
	 * instance
	 */
	boolean rootIsConcept = false;

	public KnowledgeGraph() {
		super(PropertyEdge.class);
	}

	/**
	 * The graph may or may not have a root. It's up to the implementation to
	 * define it. It's obviously a good thing to have if the graph is a tree.
	 * 
	 * @param root
	 */
	public void setRoot(IKnowledge root) {
		this.root = root;
	}
	
	public IKnowledge getRoot() {
		return this.root;
	}
	
	protected void buildGraph(IKnowledge root) throws ThinklabException {
		buildGraph(root, null);
	}

	/*
	 * can be called as many times as you want unless forceTreeGeometry is true.
	 */
	private void buildGraph(IKnowledge rootConcept, Set<String> refs)
			throws ThinklabException {

		if (refs == null && forceTreeGeometry && this.vertexSet().size() > 0)
			throw new ThinklabValidationException(
					"knowledge graph: tree geometry requested, cannot add a new root concept");

		String uri = rootConcept.getURI();

		if (refs == null) {
			refs = new HashSet<String>();
			if (rootConcept instanceof IConcept) {
				rootIsConcept = true;
			}
		} else if (refs.contains(uri)) {
			return;
		}

		refs.add(uri);
		this.addVertex(rootConcept);

		/*
		 * do not follow further if we're graphing an instance and we're not
		 * supposed to graph more than the direct type.
		 */
		if (rootConcept instanceof IConcept && !rootIsConcept
				&& followClassLevel == 0) {
			return;
		}

		if (rootConcept instanceof IConcept) {

			/*
			 * see what edge we want for the children
			 */
			for (IConcept c : ((IConcept) rootConcept).getChildren()) {

				if (followProperty((IConcept) rootConcept, c, null)) {

					buildGraph(c, refs);
					this.addEdge(c, rootConcept);
				}

			}
		} else {

			/*
			 * if we're graphing instances and we want concepts, add the concept
			 */
			if (!rootIsConcept && followClassLevel > 0) {

				if (followRelationship(rootConcept, null,
						((IInstance) rootConcept).getDirectType())) {

					buildGraph(((IInstance) rootConcept).getDirectType(), refs);
					this.addEdge(rootConcept, ((IInstance) rootConcept)
							.getDirectType());
				}

			}
		}

		/*
		 * if it's a concept, we want to follow the range of each direct
		 * property
		 */
		if (rootConcept instanceof IConcept) {

			IConcept source = (IConcept) rootConcept;

			for (IProperty property : source.getProperties()) {

				for (IConcept target : source.getPropertyRange(property)) {

					if (!followProperty(source, target, property))
						continue;

					boolean isThere = refs.contains(target.getURI());

					/*
					 * follow the object if it's not there
					 */
					if (!isThere) {
						buildGraph(target, refs);
					}

					/*
					 * add edge unless we want a tree and it's there already
					 */
					if (!(isThere && forceTreeGeometry)) {
						PropertyEdge edge = (PropertyEdge) this.addEdge(rootConcept, target);

						if (edge != null)
							edge.setProperty(property);
					}

					isThere = refs.contains(target.getURI());
				}

			}
		}

		/**
		 * Follow relationships
		 * FIXME
		 * TODO
		 * ehm - only instances should have relationships. Ensure this is for instances only
		 */
//		for (IRelationship r : ((IConcept)rootConcept).getRelationships()) {
//
//			boolean doit = followRelationship(rootConcept, r, null);
//
//			if (doit) {
//
//				if (r.isObject()) {
//
//					IInstance inst = r.getValue().asObject();
//					boolean isThere = refs.contains(inst.getURI());
//
//					/*
//					 * follow the object if it's not there
//					 */
//					if (!isThere && (!rootIsConcept || followInstances)) {
//						buildGraph(inst, refs);
//					}
//
//					/*
//					 * add edge unless we want a tree and it's there already
//					 */
//					if (!(isThere && forceTreeGeometry)) {
//						((PropertyEdge) this.addEdge(rootConcept, inst)).setProperty(
//								r.getProperty());
//					}
//
//					isThere = refs.contains(inst.getURI());
//
//				} else if (r.isClassification()) {
//
//					IConcept conc = r.getValue().getConcept();
//
//					/*
//					 * follow concept if we are allowed
//					 */
//					if (rootIsConcept || followClassLevel > 0) {
//
//						buildGraph(conc, refs);
//						this.addEdge(rootConcept, conc);
//
//					}
//				}
//			}
//		}
		
	}

	public void forceTreeGeometry(boolean what) {
		forceTreeGeometry = what;
	}

	public void followInstances(boolean what) {
		followInstances = what;
	}

	public void collapseEdges(boolean collapse, Object content) {
		collapseEdges = collapse;
		edgeContent = content;
	}

	/**
	 * Quick and dirty method to show the graph in a window. Depends on graphviz
	 * installed.
	 * 
	 * @throws ThinklabException
	 */
	public void show() throws ThinklabException {
		
		class NPP implements GraphViz.NodePropertiesProvider {

			public int getNodeHeight(Object o) {
				return 0;
			}

			public String getNodeId(Object o) {
				return o.toString().replace(':', '_').replace(' ', '_');
			}

			public int getNodeWidth(Object o) {
				return 0;
			}
			
		}
		
		GraphViz gv = new GraphViz();
		gv.loadGraph(this, new NPP());
		gv.show();
	}

	public void dump(OutputStream o) {

		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(o));

		try {
			for (Object c : this.vertexSet()) {

				w.write(c.toString());

				int edgecount = 0;
				for (Object p : this.outgoingEdgesOf(c)) {

					if (edgecount++ == 0)
						w.write(": --- " + ((PropertyEdge) p).property
								+ " --> (");
					else
						w.write(", ");

					w.write(this.getEdgeTarget(p).toString());

				}
				if (edgecount > 0)
					w.write(")\n");
				else
					w.write("\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dump() {
		dump(System.out);
	}

	/**
	 * Read a graph from a simple text format. Each non-empty line in the file can be
	 * a comment (starting with #) or a set of edges. 
	 * 
	 * If null property edges are desired, the first token (whitespace-separated) should
	 * be the semantic type of the source concept, and the remaining are concepts that
	 * should be linked to it.
	 * 
	 * If the property for the edge is specified, the first token is the semantic type
	 * of the property with a colon appended. The remaining token are concepts, structured
	 * as said above.
	 * 
	 * @param infile
	 * @throws ThinklabException
	 */
	public void read(File infile) throws ThinklabException {

		try {
			FileInputStream fstream = new FileInputStream(infile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			while ((strLine = br.readLine()) != null) {

				strLine = strLine.trim();
				
				if (strLine.startsWith("#") || strLine.equals(""))
					continue;
				
				/* tokenize */
				String[] tokens = strLine.split("\\s+");
				
				if (tokens.length < 1)
					continue;
				
				IProperty property = null;
				
				/* see if we have a property as first token, which must have a : at the end */
				int start = 0;
				if (tokens[0].endsWith(":")) {
					start = 1;
					property = 
						KnowledgeManager.get().requireProperty(
								tokens[0].substring(0, tokens[0].length()-2));
				}
				
				/* first remaining token is concept, others (if any) are its dependents */
				IConcept source = KnowledgeManager.get().requireConcept(tokens[start]);
				
				addVertex(source);
				
				/* add any remaining edges and vertices */
				for (int i = start + 1; i < tokens.length; i++) {

					IConcept target = KnowledgeManager.get().requireConcept(tokens[i]);
					addVertex(target);
					PropertyEdge e = (PropertyEdge) addEdge(source, target);
					
					if (e != null)
						e.setProperty(property);
				
				}
			}

			in.close();

		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	/**
	 * Called at each relationship found. If it returns an edge, the associated
	 * knowledge is linked and (if unknown) processed. Otherwise the cycle
	 * stops. It's called on literals and classifications as well; of course it
	 * should return null if called on a literal.
	 * 
	 * @param source
	 * @param relationship
	 *            the relationship. If null, type is not null and the
	 *            relationship is direct type
	 * @param type
	 *            the concept that we're linking to. If not null, we're
	 *            processing a direct type and relationship is null.
	 * @return
	 */
	protected abstract boolean followRelationship(IKnowledge source,
			IRelationship relationship, IConcept type);

	/**
	 * Called at each explicit relationship between concepts, caused by a range
	 * statement or specialized through a restriction. Also called for isa,
	 * passing a null property.
	 * 
	 * @param source
	 * @param target
	 * @param property
	 *            if null, we're processing children or direct types
	 * @return
	 */
	protected abstract boolean followProperty(IConcept source, IConcept target,
			IProperty property);

}
