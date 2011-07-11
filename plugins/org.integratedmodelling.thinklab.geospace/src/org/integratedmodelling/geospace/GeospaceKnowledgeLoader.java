package org.integratedmodelling.geospace;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.geospace.coverage.InstanceCoverageLoader;
import org.integratedmodelling.geospace.feature.InstanceShapefileLoader;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.utils.MiscUtilities;

public class GeospaceKnowledgeLoader implements KnowledgeLoader {

	@Override
	public Collection<IInstance> loadKnowledge(URL url, ISession session,
			IKBox kbox) throws ThinklabException {

		Collection<IInstance> ret = null;

		if (MiscUtilities.getFileExtension(url.toString()).equals("shp")) {

			ret = new InstanceShapefileLoader(url).loadObservations(session);
			if (kbox != null) {
				for (IInstance inst : ret)
					kbox.storeObject(inst, null, null, session);
			}
		} else if (MiscUtilities.getFileExtension(url.toString()).equals("tif")) {

			ret = new InstanceCoverageLoader(url, null)
					.loadObservations(session);
			if (kbox != null) {
				for (IInstance inst : ret)
					kbox.storeObject(inst, null, null, session);
			}
		}

		return ret;
	}

	@Override
	public void writeKnowledge(File outfile, String format,
			IInstance... instances) throws ThinklabException {

		throw new ThinklabUnimplementedFeatureException("geospace: writing to "
				+ format + " unsupported");

	}

}
