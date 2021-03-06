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
package org.integratedmodelling.geospace.coverage;

import java.net.URL;

import org.integratedmodelling.opal.utils.OPALListWriter;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;
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
