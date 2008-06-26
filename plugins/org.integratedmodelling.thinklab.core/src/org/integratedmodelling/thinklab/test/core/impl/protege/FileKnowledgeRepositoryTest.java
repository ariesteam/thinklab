/**
 * FileKnowledgeRepositoryTest.java
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

import java.io.File;
import java.net.MalformedURLException;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.impl.protege.Ontology;
import org.integratedmodelling.thinklab.interfaces.IOntology;

public class FileKnowledgeRepositoryTest extends AllTestsSetup {

	private FileKnowledgeRepository kr = null;
	
	
	/* 
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
				
		kr = new FileKnowledgeRepository(null);
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.FileKnowledgeRepository()'
	 */
	public void testFileKnowledgeRepository() {
		assertEquals(true,kr.getURI().contains(kr.DEFAULT_BASE));
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.initialize()'
	 */
	public void testInitialize() throws ThinklabException {
		kr.initialize();
		assertEquals(3,kr.retrieveAllOntologies().size());
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.importOntology(URL, String)'
	 */
	public void testImportOntology() throws MalformedURLException, ThinklabException {
		String ns = null;
		ns = kr.importOntology(pizzafile.toURL(),"pz", true);
		assertEquals("pz",ns);
		assertEquals(true,(new File(repositoryOntDir,pizzaFilename).delete()));
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.exportOntologyByName(URI, String)'
	 */
	public void testExportOntologyByNameURIString() throws MalformedURLException, ThinklabException {
		String ns;
		ns = kr.importOntology(pizzafile.toURL(),"pz", true);
		kr.exportOntologyByName(altpizza.toURI(),ns);
		assertEquals(true,altpizza.delete());
		assertEquals(true,(new File(repositoryOntDir,pizzaFilename).delete()));
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.retrieveOntology(String)'
	 */
	public void testRetrieveOntology() throws ThinklabException {
		kr.initialize();
		IOntology onto = kr.retrieveOntology(workflowConceptSpace);
		assertEquals(workflowURI ,onto.getURI().toString());
		onto = kr.retrieveOntology("sh");
		assertEquals(null,onto);
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.requireOntology(String)'
	 */
	public void testRequireOntology(){
		try {
			kr.initialize();
		} catch (ThinklabException e) {
			e.printStackTrace();
		}
		IOntology onto;
		try {
			onto = kr.requireOntology(workflowConceptSpace);
			assertEquals(workflowURI ,onto.getURI().toString());
		} catch (ThinklabResourceNotFoundException e) {
			e.printStackTrace();
		}
		
		boolean flag = false;
		try {
			onto = kr.requireOntology("sh");
		} catch (ThinklabResourceNotFoundException e) {
			e.printStackTrace();
			flag = true;
		}
		assertEquals(true,flag);
		
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.retrieveAllOntologies()'
	 */
	public void testRetrieveAllOntologies() throws MalformedURLException, ThinklabException {
		kr.initialize();
		String cs =  kr.importOntology(pizzafile.toURL(),workflowConceptSpace, true);
		assertEquals(true,!cs.equals(workflowConceptSpace));
		IOntology onto = kr.requireOntology(cs);
		assertEquals(true, kr.retrieveAllOntologies().contains(onto));
		assertEquals(true,(new File(repositoryOntDir,pizzaFilename).delete()));
	}


	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.refreshOntology(URL, String)'
	 */
	public void testRefreshOntology() throws MalformedURLException, ThinklabException {
		String cs = kr.refreshOntology(pizzafile.toURL(),"pz", true);
		assertEquals("pz",cs);
		assertEquals(true,(new File(repositoryOntDir,pizzaFilename).delete()));
	}


	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.createTemporaryOntology(String)'
	 */
	public void testCreateTemporaryOntology() {
		IOntology ont = null;
		boolean flag = true;
		try {
			ont = kr.createTemporaryOntology("test");
		} catch (ThinklabException e) {
			flag = false;
			e.printStackTrace();
		}
		assertEquals(true,flag);
		assertEquals("test",ont.getConceptSpace());
		assertEquals(Ontology.DEFAULT_TEMP_URI+"test#",ont.getURI());
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.releaseOntology(String)'
	 */
	public void testReleaseOntology() {
		// Fail loudly until the method is implemented
		assertEquals(true,false);

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.releaseAllOntologies()'
	 */
	public void testReleaseAllOntologies() {
		// Fail loudly until the method is implemented
		assertEquals(true,false);	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.saveAll()'
	 */
	public void testSaveAll() {
		// Fail loudly until the method is implemented
		assertEquals(true,false);
	}





	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.FileKnowledgeRepository.exportOntologyByName(OutputStream, String)'
	 */
	public void testExportOntologyByNameOutputStreamString() {
		// Fail loudly until the method is implemented
		assertEquals(true,false);
	}

}
