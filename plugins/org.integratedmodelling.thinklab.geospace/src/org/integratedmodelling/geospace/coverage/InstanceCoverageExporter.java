package org.integratedmodelling.geospace.coverage;

import java.net.URL;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.opal.utils.OPALListWriter;
import org.integratedmodelling.utils.xml.XMLDocument;

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
