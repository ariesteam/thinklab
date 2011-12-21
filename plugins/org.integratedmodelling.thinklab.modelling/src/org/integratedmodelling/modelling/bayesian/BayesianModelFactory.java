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
package org.integratedmodelling.modelling.bayesian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.riskwiz.bn.BNNode;
import org.integratedmodelling.riskwiz.bn.BeliefNetwork;
import org.integratedmodelling.riskwiz.io.genie.GenieReader;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.MiscUtilities;
import org.nfunk.jep.ParseException;

/**
 * 
 * @author ferdinando.villa
 *
 */
public class BayesianModelFactory {

	/**
	 * Analyze bayesian models coming from one of the formats recognized by
	 * RiskWiz, and turn them into model statements reusing the information, if
	 * any, already present in existing models using a given namespace to
	 * find them.
	 * 
	 * @param bayesianURL
	 * @param modelUrl
	 * @throws ThinklabException 
	 */
	public void syncModels(String bayesianUrl, String modelNamespace) throws ThinklabException {

		File bnfile = null;
		File propfile = null;
		long bndate = -1L;
		long propdate = new Date().getTime();
		
		if (ModelMap.getNamespace(modelNamespace) == null)
			// for now; we will eventually create it.
			throw new ThinklabUnimplementedFeatureException("namespace " + modelNamespace + " must exist before synchronization");
		
		long nsdate = ModelMap.getNamespaceLastModification(modelNamespace);
		
		GenieReader r = new GenieReader();
		Object oo = MiscUtilities.getSourceForResource(bayesianUrl);
		if (oo instanceof File) {
			bnfile = (File)oo;
			bndate = bnfile.lastModified();
		}
		
		if (bnfile == null) 
			throw new ThinklabResourceNotFoundException("bayesian network file " + bayesianUrl + " is not readable or is not a file");
			
		
		/*
		 * see if we have an associated property file
		 */
		propfile = 
			new File(MiscUtilities.changeExtension(bnfile.toString(), "properties"));
			
		Properties bnprops = new Properties();
		if (propfile.exists()) {
			try {
				bnprops.load(new FileInputStream(propfile));
				propdate = propfile.lastModified();
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		
		/*
		 * ontology associated with the namespace - can be set with (namespace-ontology)
		 * or is assigned by default to a temporary one.
		 */
		IOntology onto = ModelMap.getNamespaceOntology(modelNamespace);
		
		/*
		 * read the BN
		 */
		InputStream is = MiscUtilities.getInputStreamForResource(bayesianUrl);
		BeliefNetwork bn = null;
		
		try {
			bn = r.load(is);
		} catch (ParseException e) {
			throw new ThinklabValidationException(e);
		}
			
		/*
		 * groundwork is done: now we have
		 * 
		 * bn       the bayesian network
		 * bnprops  the properties associated, possibly empty, that will be stored
		 * 	        at the end alongside the BN file
		 * propdate the date of last synchronization
		 * nsdate   the date of last modification of the model namespace
		 * onto     the ontology for concepts and instances we didn't get from the
		 *          knowledge base.
		 */
		
		/*
		 * associate each node in the BN with a model if any exist or was
		 * specified, or leave it null if not so we know we have to create 
		 * it.
		 */
		for (String nn : bn.getNodeNames()) {	

			String mcname = onto.getConceptSpace() + ":" +  nn;
			IConcept mc = KnowledgeManager.get().retrieveConcept(mcname);
			
			if (mc == null) {
				
			}
				
			//			if (tm.getThird() == null) {
//				tm.setThird(createModel(bn, tm.getFirst()));
//			} else {
//				tm.setThird(syncModel(bn, tm.getFirst(), tm.getThird()));
//			}
		}
		
		/*
		 * perform synchronization
		 */
		
		/*
		 * save the namespace to its clojure representation if any change was
		 * done.
		 */
		ModelMap.sync();
		
		/*
		 * save the properties along with the file, whatever we did to them. This
		 * also gives us a date of last synchronization for next time.
		 */
		try {
			OutputStream pout = new FileOutputStream(propfile);
			bnprops.store(pout, "Change below at your own risk.");
			pout.close();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
	}

	private IModel syncModel(BeliefNetwork bn, String nodename, IModel third) {
		// TODO Auto-generated method stub
		return null;
	}

	private IModel createModel(BeliefNetwork bn, String nodename) {
		
		BNNode node = bn.getBeliefNode(nodename);
		
		String mname = CamelCase.toLowerCase(nodename, '-');
		ArrayList<IModel> context = new ArrayList<IModel>();
		
		/*
		 * concept for node must be there. If not, refuse to annotate.
		 */
		
		/*
		 * new node is:
		 * 
		 * ProbabilisticClassification by default;
		 * if concept is a BooleanRanking or OrdinalRanking, remain P.C. but rank the 
		 * 	concepts lexicographically.
		 * if concept is OrdinalRangeMapping, create a ProbabilisticMeasurement if it's also a 
		 * 	physical property, or a ProbabilisticRanking if not. Ensure coverage of 
		 *  a distribution and finite boundaries.
		 */
		
		for (BNNode o : bn.getChildren(node)) {
			
			/*
			 * 
			 */
			
		}
		
		// TODO Auto-generated method stub
		return null;
		
		
	}
	
}
