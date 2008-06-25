package org.integratedmodelling.geospace.coverage;

import java.net.URL;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.opal.utils.OPALListWriter;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.XMLDocument;

public class InstanceCoverageExporter extends CoverageHandler {

	private XMLDocument document;
	private String profile;

	public InstanceCoverageExporter(URL resource, XMLDocument document, String profile) throws ThinklabException {
		
		super(resource, null);
		this.document = document;
		this.profile = profile;
	}

	@Override
	public int process() throws ThinklabException {
		
		int ret = super.process();
		
		for (Polylist l : getInstanceLists()) {
			OPALListWriter.appendOPAL(l, document, profile);
		}
		
		return ret;
	}
	

}
