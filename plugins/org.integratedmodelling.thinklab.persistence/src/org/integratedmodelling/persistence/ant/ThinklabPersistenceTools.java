/**
 * ThinklabPersistenceTools.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabPersistencePlugin.
 * 
 * ThinklabPersistencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabPersistencePlugin is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.persistence.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.integratedmodelling.persistence.factory.PersistentStorageFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IOntology;

/**
 * An ant task for generating java sources and hibernate mappings using the 
 * Thinklab perirsistence plugin.
 * ThinklabPersistenceTools task is configured through  three parameters:
 * - The {@code outputpath} for selecting where the generate codes should be stored
 * - The {@code concept} for selecting from which concept the generation should  start
 * - The {@code ontology} for selecting from which ontology the generation should start
 * In each case you should use either {@code ontology} or {@code concept}.  
 * 
 * Example task: In an ant build file you need to declare the {@code ThinklabPersistenceTools} task, as:
 * {@code <taskdef name="thinklabpersistencetools" 
 *                 classname="org.integratedmodelling.persistence.ant.ThinklabPersistenceTools"
 *		           classpathref="classpath"/>}
 *		
 * And then you can use it for defining an ant target as:
 * {@code 	  <target name="generate" depends="init">
 *	                  <thinklabpersistencetools ontology="thinklab-core"/>
 *	          </target>}
 * 
 * @author Ioannis N. Athanasiadis
 * @since Mar 15 2007
 * @version 0.2
 *
 */
public class ThinklabPersistenceTools extends Task {

	private String concept = "";

	private String ontology = "";

	private String outputpath = "";

	// The method executing the task
	public void execute() throws BuildException {
		KnowledgeManager km;
		try {
			km = new KnowledgeManager(new FileKnowledgeRepository(null));
		} catch (ThinklabException e) {
			throw new BuildException(e);
		}
		try {
			km.initialize();
		} catch (ThinklabException e) {
			throw new BuildException(e);
		}


		if (outputpath != "")
			LocalConfiguration.getProperties().setProperty(
					"persistence.jarpath", outputpath);

		if (concept != "") {
			try {
				IConcept c = KnowledgeManager.KM.requireConcept(concept);
				PersistentStorageFactory.createPersistentStorage(c);
			} catch (ThinklabException e) {
				throw new BuildException(e);
			}
		}

		if (ontology != "") {
			try {
				IOntology ont = KnowledgeManager.KM.getKnowledgeRepository()
						.requireOntology(ontology);
				PersistentStorageFactory.createPersistentStorage(ont);
			} catch (ThinklabException e) {
				throw new BuildException(e);
			}
		}

	}

	public void setConcept(String c) {
		this.concept = c;
	}

	public void setOntology(String o) {
		this.ontology = o;
	}

	public void setOutputRoot(String s) {
		this.outputpath = s;
	}

}
