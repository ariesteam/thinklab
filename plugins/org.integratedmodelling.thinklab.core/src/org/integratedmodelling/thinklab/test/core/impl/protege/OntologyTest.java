/**
 * OntologyTest.java
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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.impl.APIOnlyKnowledgeInterface;
import org.integratedmodelling.thinklab.impl.protege.Concept;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.impl.protege.Ontology;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;

public class OntologyTest extends AllTestsSetup {
	FileKnowledgeRepository kr = null;	
	KnowledgeProvider km = null;
	Ontology onto = null;
	protected void setUp() throws Exception {
		super.setUp();
		kr = new FileKnowledgeRepository();
		km = new KnowledgeManager(kr, new APIOnlyKnowledgeInterface());
		kr.initialize();
		onto = (Ontology) kr.requireOntology("pizza");		
	}

	
	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.createConcept(String)'
	 */
	public void testCreateConceptStringIConcept() {
		IConcept parent = onto.getConcept("TomatoTopping");
		IConcept child =null;
		try{
			child = onto.createConcept("Test", parent);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		System.out.println(child.toString());
		assertEquals(child.getSemanticType().toString(), "pizza:Test");
	}


	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.createInstance(String, IConcept)'
	 */
	public void testCreateInstanceStringIConcept() throws ThinklabException, Exception {
		Concept concept = (Concept) onto.getConcept("TomatoTopping");
		IInstance inst = onto.createInstance("kati", concept);
		assertEquals(inst.getSemanticType(), new SemanticType("pizza","kati"));

	}

	
	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.Ontology(JenaOWLModel, URL, String, boolean)'
	 */
	public void testOntologyJenaOWLModelURLStringBoolean() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.read(URL, String, boolean)'
	 */
	public void testReadURLStringBoolean() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.Ontology(JenaOWLModel, TripleStore)'
	 */
	public void testOntologyJenaOWLModelTripleStore() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.Ontology(JenaOWLModel, String, URL)'
	 */
	public void testOntologyJenaOWLModelStringURL() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.build_relationships(RDFResource, boolean)'
	 */
	public void testBuild_relationships() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getUniqueObjectName(String)'
	 */
	public void testGetUniqueObjectName() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getConcepts()'
	 */
	public void testGetConcepts() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getProperties()'
	 */
	public void testGetProperties() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getInstances()'
	 */
	public void testGetInstances() {
		// TODO Auto-generated method stub

	}


	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.createInstance(IInstance)'
	 */
	public void testCreateInstanceIInstance() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.createInstance(String, Polylist)'
	 */
	public void testCreateInstanceStringPolylist() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.createInstance(Polylist)'
	 */
	public void testCreateInstancePolylist() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.createProperty(String)'
	 */
	public void testCreateProperty() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.requireConcept(String)'
	 */
	public void testRequireConcept() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.requireInstance(String)'
	 */
	public void testRequireInstance() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.requireProperty(String)'
	 */
	public void testRequireProperty() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getConcept(String)'
	 */
	public void testGetConcept() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getInstance(String)'
	 */
	public void testGetInstance() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getProperty(String)'
	 */
	public void testGetProperty() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getOriginalURI()'
	 */
	public void testGetOriginalURI() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.removeIndividual(String)'
	 */
	public void testRemoveIndividual() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.write(OutputStream)'
	 */
	public void testWrite() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.read(URL)'
	 */
	public void testReadURL() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getID()'
	 */
	public void testGetID() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getConceptSpace()'
	 */
	public void testGetConceptSpace() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getURI()'
	 */
	public void testGetURI() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getSemanticType()'
	 */
	public void testGetSemanticType() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getLabel()'
	 */
	public void testGetLabel() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getDescription()'
	 */
	public void testGetDescription() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getLabel(String)'
	 */
	public void testGetLabelString() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.getDescription(String)'
	 */
	public void testGetDescriptionString() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.equals(String)'
	 */
	public void testEqualsString() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.equals(SemanticType)'
	 */
	public void testEqualsSemanticType() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.equals(IResource)'
	 */
	public void testEqualsIResource() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.Ontology.toString()'
	 */
	public void testToString() {
		// TODO Auto-generated method stub

	}

}
