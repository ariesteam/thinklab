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
package org.integratedmodelling.modelling.ploaders;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.annotations.ProjectLoader;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;
import org.integratedmodelling.utils.MiscUtilities;

@ProjectLoader(folder="ontologies")
public class OntologyLoader implements IProjectLoader {

	ArrayList<String> ontologies = new ArrayList<String>();
	
	@Override
	public void load(File directory) throws ThinklabException {
		
		for (File owl : directory.listFiles()) {
			if (owl.isFile() && owl.toString().endsWith(".owl")) {

				URL url = null;
				try {
					url = owl.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new ThinklabIOException(e);
				}
				String csp = MiscUtilities.getFileBaseName(owl.toString());
				
				ontologies.add(KnowledgeManager.get().getKnowledgeRepository().
					refreshOntology(url, csp, false));
				
			} else if (owl.isDirectory()) {
				load(owl);
			}
		}
	}

	@Override
	public void unload(File directory) throws ThinklabException {

		for (String ont : ontologies) {
			KnowledgeManager.get().getKnowledgeRepository().releaseOntology(ont);
		}
	}

}
