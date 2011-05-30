package org.integratedmodelling.thinklab.rest.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.utils.Pair;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public class Receive extends DefaultRESTHandler {

	 @Post
	 public Representation accept(Representation entity) throws Exception {

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
