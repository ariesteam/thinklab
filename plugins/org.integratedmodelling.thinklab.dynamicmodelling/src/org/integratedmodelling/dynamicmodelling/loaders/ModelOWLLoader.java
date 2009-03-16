/**
 * ModelOWLLoader.java
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.dynamicmodelling.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.dynamicmodelling.DynamicModellingPlugin;
import org.integratedmodelling.dynamicmodelling.annotation.ModelAnnotation;
import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoader;
import org.integratedmodelling.dynamicmodelling.model.Flow;
import org.integratedmodelling.dynamicmodelling.model.Model;
import org.integratedmodelling.dynamicmodelling.model.Stock;
import org.integratedmodelling.dynamicmodelling.model.Variable;
import org.integratedmodelling.dynamicmodelling.simile.SimilePrologReader;
import org.integratedmodelling.dynamicmodelling.utils.SimileModelExtractor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.KList;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Loads a model from intermediate graph representation into Thinklab knowledge. 
 * 
 * We must have a strategy to prepare an empty annotation document with all the remaining semantic info that
 * are not in the model annotations. If the annotation is there, we should read it in; otherwise, we should
 * prepare one, to be put in the same folder as the model source, and give it to the user to fill in.
 * 
 * @author Ferdinando Villa
 * @date June 10, 2007
 *
 */
public class ModelOWLLoader implements IModelLoader {
	
	public Collection<Polylist> loadModel(String msource) throws ThinklabException {
		
		URL murl = MiscUtilities.getURLForResource(msource);
		
		ArrayList<Polylist> ret = new ArrayList<Polylist>();
		
		if (msource.endsWith(".simile") || msource.endsWith(".sml")) {
			
			InputStream in;
			try {
				in = 
					msource.endsWith(".sml") ? 
							SimileModelExtractor.extractPrologModel(murl, null) :
							murl.openStream();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
			
			Model model = null;
			
			/* get graph from Simile parser */
			try {
				model = new SimilePrologReader(in).generateModel();
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}
			
			/* load or create annotation */
			ModelAnnotation annotation = loadAnnotation(msource);
			
			ret.add(graphToList(model, annotation));
			
			if (annotation.isNew()) {
				annotation.serialize();
			}
			
		/* TODO add stella when we have it */
			
		} else {
			throw new ThinklabIOException("can't read model from " + msource + "; format not supported");
		}
		
		return ret;
		
	}
	
	/* 
	 * load annotation if any exists, or create it where the file was. If file was a 
	 * remote URL, just make annotation in plugin scratch area for now. 
	 */
	public ModelAnnotation loadAnnotation(String modelSource) {

		ModelAnnotation annotation = new ModelAnnotation();
		
		
		return annotation;
	}
	
	public Polylist graphToList(Model model, ModelAnnotation annotation) {
		
		/* just record the names, so we only define it the first time. */
		HashSet<String> mdesc = new HashSet<String>();
		
		/* create main Identification */
		KList mdl = new KList(CoreScience.IDENTIFICATION, model.getName() + "Model");

		/* observable */
		mdl.addObjectProperty(CoreScience.HAS_OBSERVABLE,
				annotation.getObservableForModel(model, CoreScience.GENERIC_OBSERVABLE));

		if (model.getDescription() != null && !model.getDescription().trim().equals("")) {
			mdl.addDescription(model.getDescription());
		}

		
		/* context(s) from annotations, if any */
		Polylist ctx = annotation.getDefaultContextStatementForModel(model);
		if (ctx != null)
			mdl.addElement(ctx);
		
		for (Iterator<?> iter = model.getVertices().iterator(); iter.hasNext(); ) {
	           
			 	Vertex v = (Vertex) iter.next();
	            
			 	/*
			 	 * flows can't exist alone, so we only translate stocks and vars in the
			 	 * main loop.
			 	 */
	            if (v instanceof Stock) {
	            	
	            	String id = ((Stock)v).getName();
	            	
	            	/* TODO this check should be unnecessary, but who knows, at my old age. */
	            	if (!mdesc.contains(id)) {
		            	KList l = translateStock((Stock)v, annotation, mdesc);
	            		mdl.addObjectProperty(CoreScience.HAS_CONTINGENCY, l.list());
	            		mdesc.add(id);
	            	}
	            } else if (v instanceof Variable) {
	            	
	            	String id = ((Variable)v).getName();
	            	
	            	if (!mdesc.contains(id)) {
		            	KList l = translateVariable((Variable)v, annotation, mdesc);		            	
	            		mdl.addObjectProperty(CoreScience.HAS_CONTINGENCY, l.list());
	            		mdesc.add(id);
	            	}
	            }
	        }
		
	    return mdl.list();

	}

	protected KList translateVariable(Variable variable, ModelAnnotation annotation, HashSet<String> mdesc) {

		/* use annotations for type */
		KList l = new KList(
				annotation.getTypeForVariable(variable, DynamicModellingPlugin.VARIABLE_TYPE), 
				variable.getName() + "Observation");
		
		/* observable */
		l.addObjectProperty(CoreScience.HAS_OBSERVABLE,
				annotation.getObservableForVariable(variable, CoreScience.GENERIC_QUANTIFIABLE));

		
		/* annotations */
		if (variable.getComment() != null && !variable.getComment().trim().equals("")) {
			l.addDescription(variable.getComment());
		}

		/* conceptual model */
		Polylist ccl = annotation.getConceptualModelPropertyListForVariable(variable);
		if (ccl != null)
			l.addElement(ccl);

		/* datasource w/equation */
		KList dl = new KList(DynamicModellingPlugin.VARIABLE_DATASOURCE);
		/* TODO we should have turned this one into the language we want to support, and
		 * make it available as an algorithm object */
		l.addLiteralProperty(CoreScience.DATASOURCE_FUNCTION_LITERAL, variable.getValue());
		l.addObjectProperty(CoreScience.HAS_DATASOURCE, dl.list());
		
		
		/* only add stuff we depend on */			
		for (Object edge : variable.getInEdges()) {
			
			Object src = ((DirectedSparseEdge)edge).getSource();
			if (src instanceof Variable) {

				Variable variable2 = (Variable)src;
				if (mdesc.contains(variable2.getName()))					
					l.addReference(CoreScience.DEPENDS_ON, variable.getName() + "Observation");
				else {
					KList vl = translateVariable(variable2, annotation, mdesc);
					l.addObjectProperty(CoreScience.DEPENDS_ON, vl.list());
					mdesc.add(variable.getName());
				}
			} else if (src instanceof Stock) {
				/**
				 * TODO check if this is ok: the rationale is, if a variable depends on a stock,
				 * it is dependent on its PREVIOUS value and it's part of the workflow that
				 * calculates it. So it does not mean that the stock value must be calculated before
				 * calculating the variable, and we don't add a dependency to the workflow, because stocks
				 * are always initialized.
				 * 
				 * FIXME The point is that by doing this, we miss the logics of its dependency on its
				 * previous value. So we should use another relationships which is not a 
				 * contextual dependency, but captures the link to its previous value.
				 * 
				 * l.addReference(CoreSciencePlugin.DEPENDS_ON, ((Stock)src).getName() + "StockObservation");				
				 */
			} else if (src instanceof Flow) {
				l.addReference(CoreScience.DEPENDS_ON, ((Flow)src).getName() + "FlowObservation");				
			}
		}	
		
		return l;
	}

	protected KList translateFlow(Flow flow, ModelAnnotation annotation, HashSet<String> mdesc) {
		
		/* use annotations for type */
		KList l = new KList(
				annotation.getTypeForFlow(flow, DynamicModellingPlugin.FLOW_TYPE),
				flow.getName() + "FlowObservation");
		
		/* observable */
		l.addObjectProperty(CoreScience.HAS_OBSERVABLE,
				annotation.getObservableForFlow(flow, CoreScience.GENERIC_QUANTIFIABLE));

		/* annotations */
		if (flow.getComment() != null && !flow.getComment().trim().equals("")) {
			l.addDescription(flow.getComment());
		}
		
		/* conceptual model */
		Polylist ccl = annotation.getConceptualModelPropertyListForFlow(flow);
		if (ccl != null)
			l.addElement(ccl);

		
		/* datasource w/rate equation */
		KList dl = new KList(DynamicModellingPlugin.FLOW_DATASOURCE);
		/* TODO we should have turned this one into the language we want to support, and
		 * make it available as an algorithm object */
		l.addLiteralProperty(CoreScience.DATASOURCE_FUNCTION_LITERAL, flow.getRate());
		l.addObjectProperty(CoreScience.HAS_DATASOURCE, dl.list());

		
		/* only add stuff we depend on */			
		for (Object edge : flow.getInEdges()) {
			
			Object src = ((DirectedSparseEdge)edge).getSource();
			if (src instanceof Variable) {

				Variable variable = (Variable)src;
				if (mdesc.contains(variable.getName()))					
					l.addReference(CoreScience.DEPENDS_ON, variable.getName() + "Observation");
				else {
					KList vl = translateVariable(variable, annotation, mdesc);
					l.addObjectProperty(CoreScience.DEPENDS_ON, vl.list());
					mdesc.add(variable.getName());
				}
			} else if (src instanceof Stock) {
				l.addReference(CoreScience.DEPENDS_ON, ((Stock)src).getName() + "StockObservation");				
			}
		}	
		
		return l;
	}
	
	protected KList translateStock(Stock stock, ModelAnnotation annotation, HashSet<String> mdesc) {

		/* use annotation to find proper class, defaulting to ranking */
		
		KList l = new KList(
				annotation.getTypeForStock(stock, DynamicModellingPlugin.STOCK_TYPE),
				stock.getName() + "StockObservation");
		
		/*
		 * label and description annotations from node. 
		 */
		if (stock.getComment() != null && !stock.getComment().trim().equals("")) {
			l.addDescription(stock.getComment());
		}


		/* conceptual model */
		Polylist ccl = annotation.getConceptualModelPropertyListForStock(stock);
		if (ccl != null)
			l.addElement(ccl);
		
		/* observable */
		l.addObjectProperty(CoreScience.HAS_OBSERVABLE,
				annotation.getObservableForStock(stock, CoreScience.GENERIC_QUANTIFIABLE));

		/*
		 * add stock datasource with proper initialization value
		 */
		KList dl = new KList(DynamicModellingPlugin.STOCK_DATASOURCE);
		l.addLiteralProperty(DynamicModellingPlugin.STOCK_INITVALUE_LITERAL, stock.getState());
		l.addObjectProperty(CoreScience.HAS_DATASOURCE, dl.list());
		
		/*
		 * add dependencies from inflows and outflows
		 */
		for (Flow inflow : stock.getInflows()) {
			
			if (mdesc.contains(inflow.getName()))
				l.addReference(DynamicModellingPlugin.HAS_INFLOW, inflow.getName() + "FlowObservation");
			else {
				l.addObjectProperty(DynamicModellingPlugin.HAS_OUTFLOW,
						translateFlow(inflow, annotation, mdesc).list());
				mdesc.add(inflow.getName());
			}
		}

		for (Flow outflow : stock.getOutflows()) {

			if (mdesc.contains(outflow.getName()))
				l.addReference(DynamicModellingPlugin.HAS_INFLOW, outflow.getName() + "FlowObservation");
			else {
				l.addObjectProperty(DynamicModellingPlugin.HAS_INFLOW,
						translateFlow(outflow, annotation, mdesc).list());
				mdesc.add(outflow.getName());
			}
		}
		
		return l;
	}
	
	

}