/**
 * ConceptTest.java
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
package org.integratedmodelling.thinklab.test.core.impl.protege;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.impl.APIOnlyKnowledgeInterface;
import org.integratedmodelling.thinklab.impl.protege.Concept;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.ISession;

public class ConceptTest extends AllTestsSetup {

	private Concept napoletana;
	private Concept interesting;
	private Concept namedpizza;
	private Concept pizza;
	private Concept country;
	private Concept domainconcept;
	private FileKnowledgeRepository kr;

	protected void setUp() throws Exception {
		super.setUp();
		kr = new FileKnowledgeRepository();
		KnowledgeManager km = new KnowledgeManager(kr, new APIOnlyKnowledgeInterface());
		km.initialize();
		
		ISession ses = km.requestNewSession();
		ses.loadObjects(pizzafile.toURL());
		String localname = ses.getSessionID();
//		kr.importOntology(pizzafile.toURL(),"pizza");
		
		pizza = (Concept) km.requireConcept(new SemanticType(localname,"Pizza"));
		napoletana = (Concept) km.requireConcept(new SemanticType(localname,"Napoletana"));
		interesting = (Concept) km.requireConcept(new SemanticType(localname,"InterestingPizza"));
		namedpizza = (Concept) km.requireConcept(new SemanticType(localname,"NamedPizza"));
		country = (Concept) km.requireConcept(new SemanticType(localname,"Country"));
		domainconcept = (Concept) km.requireConcept(new SemanticType(localname,"DomainConcept"));
		kr.printDetails();
	}

	public void testGetParents() {
		ArrayList<IConcept> napoletanaparents = new ArrayList<IConcept>();
		napoletanaparents.add(namedpizza);
		assertEquals(true, napoletana.getParents().containsAll(napoletanaparents));
		assertEquals(true, napoletanaparents.containsAll(napoletana.getParents()));
	}

	public void testGetAllParents() {
		fail("Not yet implemented");
	}
	public void testGetChildren() {
		assertEquals(23,namedpizza.getChildren().size());
		assertEquals(11,pizza.getChildren().size());
	}

	public void testGetProperties() {
		// 26 properties come from owl and think-lab ontologies!
		
		assertEquals(0,country.getProperties().size());
		assertEquals(2,namedpizza.getProperties().size());
		assertEquals(2,pizza.getProperties().size());
		assertEquals(2,interesting.getProperties().size());
	}
	public void testGetDirectProperties() {
		assertEquals(0,country.getDirectProperties().size());
		assertEquals(0,namedpizza.getDirectProperties().size());
		assertEquals(2,pizza.getDirectProperties().size());
		assertEquals(0,interesting.getDirectProperties().size());
	}


	public void testGetParent() throws ThinklabException {
		assertEquals(namedpizza, napoletana.getParent());
	}

	public void testIsIKnowledge() {
		if(kr.reasonerConnected())
			assertEquals(true,napoletana.is(interesting));
		else
			assertEquals(false,napoletana.is(interesting));
		assertEquals(true, napoletana.is(domainconcept));
		assertEquals(false, napoletana.is(country));
	}

	public void testIsString() {
		if(kr.reasonerConnected())
			assertEquals(true,napoletana.is("pizza:InterestingPizza"));
		else
			assertEquals(false,napoletana.is("pizza:InterestingPizza"));
		assertEquals(false, napoletana.is("some:thing"));
	}
	
	public void testGetLeastGeneralCommonConcept() {
		IConcept c1 = napoletana.getLeastGeneralCommonConcept(namedpizza);
		assertEquals(namedpizza, c1);
		IConcept c2 = namedpizza.getLeastGeneralCommonConcept(napoletana);
		assertEquals(namedpizza, c2);
		IConcept c3 = napoletana.getLeastGeneralCommonConcept(interesting);
		if(kr.reasonerConnected())
			assertEquals(interesting, c3);
		else
			assertEquals(pizza, c3);
		IConcept c4 = interesting.getLeastGeneralCommonConcept(napoletana);
		if(kr.reasonerConnected())
			assertEquals(interesting, c4);
		else
			assertEquals(pizza, c4);
	}

	public void testGetRestrictionsAsConstraint() {
		fail("Not yet implemented");
	}

	public void testIsAbstract() {
		assertEquals(true,domainconcept.isAbstract());
		
	}

	public void testGetRelationships() {
		fail("Not yet implemented");
	}

	public void testGetRelationshipsString() {
		fail("Not yet implemented");
	}

	public void testGetNumberOfRelationships() {
		fail("Not yet implemented");
	}

	public void testGet() {
		fail("Not yet implemented");
	}

	public void testGetRelationshipsTransitive() {
		fail("Not yet implemented");
	}

	public void testGetLocalName() {
		fail("Not yet implemented");
	}

	public void testGetSemanticType() {
		fail("Not yet implemented");
	}


	public void testAddDescriptionString() {
		fail("Not yet implemented");
	}

	public void testAddDescriptionStringString() {
		fail("Not yet implemented");
	}

	public void testAddLabelString() {
		fail("Not yet implemented");
	}

	public void testAddLabelStringString() {
		fail("Not yet implemented");
	}

	public void testEqualsIConcept() {
		fail("Not yet implemented");
	}

	public void testEqualsString() {
		fail("Not yet implemented");
	}

	public void testEqualsSemanticType() {
		fail("Not yet implemented");
	}

	public void testEqualsIResource() {
		fail("Not yet implemented");
	}

	public void testGetConceptSpace() {
		fail("Not yet implemented");
	}

	public void testGetLabel() {
		fail("Not yet implemented");
	}

	public void testGetDescription() {
		fail("Not yet implemented");
	}

	public void testGetLabelString() {
		fail("Not yet implemented");
	}

	public void testGetDescriptionString() {
		fail("Not yet implemented");
	}

	public void testGetURI() {
		fail("Not yet implemented");
	}



	public void testGetAllInstances() {
		fail("Not yet implemented");
	}

	public void testGetInstances() {
		fail("Not yet implemented");
	}

	public void testGetType() {
		fail("Not yet implemented");
	}



}
