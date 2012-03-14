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
package org.integratedmodelling.thinklab.rest.resources;

import java.io.File;

import org.integratedmodelling.exceptions.ThinklabAuthenticationException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.utils.MiscUtilities;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Send a file to the server using a valid handle returned previously. Only
 * files created in the workspace of the same session will be sent.
 * 
 * @author ferdinando.villa
 *
 */
public class FileSendService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {
		
		File file = retrieveFile(getArgument("handle"), getSession()); 
		String extension = MiscUtilities.getFileExtension(file.toString());
		MediaType mt = Thinklab.get().getMetadataService().getMediaType(extension);
		FileRepresentation rep = new FileRepresentation(file, mt);
		Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
		disp.setFilename(file.getName());
		disp.setSize(file.length());
		rep.setDisposition(disp);
		return rep;
	}

	private File retrieveFile(String string, ISession session) throws ThinklabException {
		
		if (!string.startsWith(session.getWorkspace()))
			throw new ThinklabAuthenticationException(
					"send: trying to access another session's workspace");
		
		File sdir = new File(
				Thinklab.get().getScratchArea() + File.separator + "rest/tmp" + 
				File.separator + string);

		if (!sdir.exists())
			throw new ThinklabResourceNotFoundException(
					"send: trying to access nonexistent file " + string);

		return sdir;
	}
	
}
