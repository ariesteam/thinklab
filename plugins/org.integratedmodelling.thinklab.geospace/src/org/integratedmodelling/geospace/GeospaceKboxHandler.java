package org.integratedmodelling.geospace;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.geospace.feature.ShapefileKBox;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.extensions.KBoxHandler;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.utils.MiscUtilities;

public class GeospaceKboxHandler implements KBoxHandler {

	@Override
	public IKBox createKBox(String protocol, String dataUri, Properties properties) throws ThinklabException {

		IKBox ret = null;
		
		if (protocol.equals("shapefile")) {
			try {
				ret = new ShapefileKBox(new URL(dataUri), properties);
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		return ret;
	}

	@Override
	public IKBox createKBoxFromURL(URI url) throws ThinklabStorageException {
		
		if (url.toString().startsWith("shapefile:")) {
			try {
				return new ShapefileKBox(MiscUtilities.getURLForResource(url.toString()), null);
			} catch (ThinklabException e) {
				throw new ThinklabStorageException(e);
			}
		}
		
		return null;
	}

}
