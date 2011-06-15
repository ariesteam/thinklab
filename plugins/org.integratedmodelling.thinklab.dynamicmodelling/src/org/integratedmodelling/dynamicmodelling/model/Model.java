/**
 * Model.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
 * @author    Gary W. Johnson, Jr.
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.dynamicmodelling.model;

import java.util.Hashtable;
import java.util.Vector;

import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

public class Model extends DirectedSparseGraph {
	
	private String name;
	private String program;
	private String version;
	private String edition;
	private String date;
	private Vector<String> annotations = new Vector<String>();
	private Vector<Object> dependencies = new Vector<Object>();
	private Vector<Hashtable<String, Object>> ghostNotes = new Vector<Hashtable<String, Object>>();

	public Stock addStock(Stock s) {
		return (Stock) this.addVertex(s);
	}

	public Flow addFlow(Flow f) {
		return (Flow) this.addVertex(f);
	}

	public Variable addVariable(Variable v) {
		return (Variable) this.addVertex(v);
	}

	public Submodel addSubmodel(Submodel s) {
		return (Submodel) this.addVertex(s);
	}

	public FlowEdge addFlowEdge(FlowEdge fe) {
		return (FlowEdge) this.addEdge(fe);
	}

	public InfluenceEdge addInfluenceEdge(InfluenceEdge ve) {
		return (InfluenceEdge) this.addEdge(ve);
	}

	public SubmodelEdge addSubmodelEdge(SubmodelEdge se) {
		return (SubmodelEdge) this.addEdge(se);
	}

	public void removeStock(Stock s) {
		this.removeVertex(s);
	}

	public void removeFlow(Flow f) {
		this.removeVertex(f);
	}

	public void removeVariable(Variable v) {
		this.removeVertex(v);
	}

	public void removeSubmodel(Submodel s) {
		this.removeVertex(s);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void addAnnotation(String text) {
		this.annotations.add(text);
	}

	public void addDependency(Object requiredElement) {
		this.dependencies.add(requiredElement);
	}

	public void addGhostNote(Hashtable<String, Object> ghostNote) {
		this.ghostNotes.add(ghostNote);
	}

	public String getName() {
		return this.name;
	}

	public String getProgram() {
		return this.program;
	}

	public String getVersion() {
		return this.version;
	}

	public String getEdition() {
		return this.edition;
	}

	public String getDate() {
		return this.date;
	}

	public Vector<String> getAnnotations() {
		return this.annotations;
	}

	public Vector<Object> getDependencies() {
		return this.dependencies;
	}

	public Vector<Hashtable<String, Object>> getGhostNotes() {
		return this.ghostNotes;
	}
	
	/**
	 * TODO retrieve from metadata or construct from other medatata fields.
	 * @return
	 */
	public String getDescription() {
		return null;
	}
}
