/**
 * AllTestsSetup.java
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.MiscUtilities;

public class AllTestsSetup extends TestCase {

	protected File repositoryOntDir;
	protected File alternativeOntDir;
	
	protected String pizzaFilename;
	protected String pizzaURI;
	protected File pizzafile;
	protected File altpizza;

	protected File example;
	protected String exampleURI;
	
	protected String workflowURI;
	protected String workflowConceptSpace;

	public AllTestsSetup() {
		try {
			repositoryOntDir = LocalConfiguration.getDataDirectory("testontologies/set1");
			alternativeOntDir = LocalConfiguration.getDataDirectory("testontologies/set2");
		} catch (ThinklabIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pizzaFilename = "pizza.owl";
		pizzaURI = "http://www.integratedmodelling.org/ontologies/test/2006/11/03/pizza.owl";
		pizzafile = new File(alternativeOntDir,pizzaFilename);
		altpizza = new File(new File(repositoryOntDir,"temp"),"pizza.owl");
		example = new File(alternativeOntDir,"example.owl");
		exampleURI = "http://www.co-ode.org/ontologies/PizzaImportTestA.owl";
		
		workflowURI = "http://seamless.idsia.ch/ontologies/workflow#";
		workflowConceptSpace = "wf";
	
		File originalpizza = new File(repositoryOntDir,pizzaFilename);
		File originalexample= new File(repositoryOntDir,"example.owl");
		try {
			MiscUtilities.writeToFile(pizzafile.toString(), ((InputStream) new FileInputStream(originalpizza)), true);
			MiscUtilities.writeToFile(example.toString(), ((InputStream) new FileInputStream(originalexample)), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	protected void setUp() throws Exception {
		super.setUp();
	
	}

}
