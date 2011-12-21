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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

/**
 * Receive a file from the client and return an handle that can be used later
 * to communicate the file location for services that will use it.
 * 
 * @author ferdinando.villa
 *
 */
public class FileReceiveService extends DefaultRESTHandler {

	 @Post
	 public Representation service(Representation entity) throws Exception {

		 ISession session = getSession();
		 
		 if (entity != null) {
			 if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					 true)) {
				 	             
	             // 1/ Create a factory for disk-based file items
	             DiskFileItemFactory factory = new DiskFileItemFactory();
	             factory.setSizeThreshold(1000240);

	             // 2/ Create a new file upload handler based on the Restlet
	             // FileUpload extension that will parse Restlet requests and
	             // generates FileItems.
	             RestletFileUpload upload = new RestletFileUpload(factory);
	             List<FileItem> items;

	             // 3/ Request is parsed by the handler which generates a
	             // list of FileItems
	             items = upload.parseRequest(getRequest());

	             // save each file
	             ArrayList<String> done = new ArrayList<String>();
	             for (final Iterator<FileItem> it = items.iterator(); it.hasNext(); ) {
	            	 FileItem fi = it.next();
	            	 Pair<File,String> filename = getFileName(fi.getName(), session);
	            	 fi.write(filename.getFirst());
	            	 done.add(filename.getSecond());
	             }

	             if (done.size() > 0) {
	            	 setResult(done.toArray(new String[done.size()]));
	             } else {
	            	 fail("file upload failed: no file received");
	             }
			 }
		 } else {
			fail("file upload: not a multipart request");
		 }

		 return wrap();
	  }

}
