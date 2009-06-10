package org.integratedmodelling.geospace;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.geospace.coverage.InstanceCoverageLoader;
import org.integratedmodelling.geospace.feature.InstanceShapefileLoader;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
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
					kbox.storeObject(inst, null, session);
			}
		} else if (MiscUtilities.getFileExtension(url.toString()).equals("tif")) {

			ret = new InstanceCoverageLoader(url, null)
					.loadObservations(session);
			if (kbox != null) {
				for (IInstance inst : ret)
					kbox.storeObject(inst, null, session);
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
