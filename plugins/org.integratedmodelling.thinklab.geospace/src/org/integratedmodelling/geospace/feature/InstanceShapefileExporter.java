/**
 * InstanceShapefileExporter.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.feature;

import java.net.URL;

import org.integratedmodelling.opal.utils.OPALListWriter;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.XMLDocument;

public class InstanceShapefileExporter extends InstanceShapefileHandler {

	private XMLDocument document;
	private String profile;

	public InstanceShapefileExporter(URL resource, XMLDocument document, String profile) throws ThinklabException {
		super(resource);
		this.document = document;
		this.profile = profile;
	}

	@Override
	public void notifyInstance(Polylist list)  throws ThinklabException {
		OPALListWriter.appendOPAL(list, document, profile);
	}


}
