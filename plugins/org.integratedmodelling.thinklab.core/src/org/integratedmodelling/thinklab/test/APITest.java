/**
 * APITest.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeTree;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.impl.APIOnlyKnowledgeInterface;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.Quantifier;

public class APITest {

	
	public static void testKBox(IKnowledgeProvider km, ISession sess) throws ThinklabException {
		
			
    	IKBox kbox = sess.requireKBox("kbox:///Users/ionathan/Work/workspace/ThinkLab/scratchKB/tempdb.pg");
    	Collection<IInstance> objs = sess.loadObjects("/Users/ionathan/Work/workspace/ThinkLab/scratchKB/dataAnnotation.tcs");
    	
    	HashMap<String, String> refs = new HashMap<String, String>();
    	
    	ArrayList<String> ids = new ArrayList<String>();
    	
    	for (IInstance o : objs) {
    		ids.add(kbox.storeObject(o, sess, refs));
    	}
    	
    	
    	for (String s : ids) {
    		
    		System.out.println("Stored " + s);
    	
    		IInstance instt = sess.importObject("kbox:///Users/ionathan/Work/workspace/ThinkLab/scratchKB/tempdb.pg#" + s); 		
    		// IInstance inst = kbox.getObjectFromID(s, sess);
    		
    		System.out.println("retrieved:\n" + instt.toList(null));
    		
    	}
    	
    	// Polylist results = kbox.getObjectIDsFromQuery(query, null, -1, -1);
    	
    	// System.out.println("Result list is " + results);
    	
	}

	public static void testConstraintConstruction() throws ThinklabException {

		Constraint query = 
			new Constraint("observation:Observation").restrict(
				
				Restriction.OR(
						new Restriction("geospace:hasCentroid", "within", "POLYGON(10 20)"),
						new Restriction("geospace:hasCentroid", "within", "POLYGON(1 2)")),
						
				new Restriction("observation:dependsOn", 
						new Constraint("geospace:SpatialCoverageObservation")));
		
		Polylist l = query.asList();
		System.out.println("original: " + l);
		Constraint newc = new Constraint(l);
		System.out.println("reconstructed: " + newc.asList());

		Constraint rquery = 
			new Constraint("source:Randomizer");
		IKBox kbox = KnowledgeManager.get().requireGlobalKBox("test");

		System.out.println(Polylist.prettyPrint(rquery.asList()));		
//		System.out.println(Polylist.prettyPrint(kbox.query(rquery, null, 0, -1)));

		
		rquery = 
			new Constraint("observation:Observation").restrict(
					new Restriction(new Quantifier("all"), "observation:hasDataSource",
							new Constraint("source:Randomizer")));
		
		System.out.println(Polylist.prettyPrint(rquery.asList()));		
//		System.out.println(Polylist.prettyPrint(kbox.getObjectIDsFromQuery(rquery, null, 0, -1)));

	}
	
	public static void testClassTree() throws ThinklabException {
		KnowledgeTree ct = new KnowledgeTree();

		System.out.println(
				"geospace:Nation " +
				(ct.is("geospace:Nation", "geospace:GeographicalFeature") ? "is" : "is not") +
				" geospace:GeographicalFeature");
		
		System.out.println(
				"geospace:GeographicalFeature " +
				(ct.is("geospace:GeographicalFeature", "geospace:Nation") ? "is" : "is not") +
				" geospace:Nation ");
			
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {  
		
//	        try {      	

//	 	       KnowledgeManager km = 
//		    	   new KnowledgeManager(new FileKnowledgeRepository(), 
//		    			   		        new APIOnlyKnowledgeInterface(args));
//
//	 	    	km.initialize();				
//	 	    	ISession sess = km.requestNewSession();
	        	

	 	    	// testKBox(km, sess);
	 	    	
	 	    	// testClassTree();
	        	
	 	    	// testConstraintConstruction();
	        	
//	        } catch (ThinklabException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}

}
