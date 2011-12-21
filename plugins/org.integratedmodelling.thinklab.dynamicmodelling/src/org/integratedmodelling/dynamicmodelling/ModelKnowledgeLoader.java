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
package org.integratedmodelling.dynamicmodelling;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoader;
import org.integratedmodelling.dynamicmodelling.loaders.ModelOWLLoader;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class ModelKnowledgeLoader implements KnowledgeLoader {

	@Override
	public Collection<IInstance> loadKnowledge(URL url, ISession session, IKBox kbox) throws ThinklabException {

		/* look for installed observation loader */
		IModelLoader l = DynamicModellingPlugin.get().retrieveModelLoader("observation");
			
		// default to OWL loader
		if (l == null) {
			l = new ModelOWLLoader();
		}
		
		Collection<Polylist> instances = l.loadModel(url.toString());
		ArrayList<IInstance> ret = new ArrayList<IInstance>();
		
		if (instances != null)
			for (Polylist list : instances) {
				if (list != null) {
					IInstance i = session.createObject(list);
					ret.add(i);
					if (kbox != null) {
						kbox.storeObject(i, null, null, session);
					}
				}
			}

		return ret;
	}


	@Override
	/* wouldn't it be nice, translate SIMILE to STELLA and back. */
	public void writeKnowledge(File outfile, String format, IInstance... instances)
			throws ThinklabException {
		throw new ThinklabInappropriateOperationException(
				"output to proprietary model formats is unsupported. " +
				"Please serialize models to XML.");
	}

}
