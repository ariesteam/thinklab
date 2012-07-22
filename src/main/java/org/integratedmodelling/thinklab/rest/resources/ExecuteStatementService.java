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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.modelling.datasets.FileDataset;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTUserModel;
import org.integratedmodelling.utils.FolderZiper;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Execute a language statement and optionally produce a visualization and/or dataset
 * for the results.
 * 
 * @author ferdinando.villa
 *
 */
public class ExecuteStatementService extends DefaultRESTHandler {
	
	/*
	 * TODO localize to user ID if authenticated
	 */
	private static final String USER_NAMESPACE_PREFIX = "rest.user.";
	
	@Get
	public Representation service() {
		
		try {
			
			/*
			 * if visualize = true is passed, we are asked to produce a NetCDF output
			 * and make it available for download. If vdir is also passed, we produce
			 * the file in the given directory instead, with the name "data.nc". 
			 */
			boolean visualize = Boolean.parseBoolean(getArgument("visualize", "false"));
			boolean localserv = Boolean.parseBoolean(getArgument("islocal",   "false"));

			int thW = Integer.parseInt(getArgument("thumbnailWidth", ""+FileDataset.DEFAULT_THUMBNAIL_WIDTH));
			int thH = Integer.parseInt(getArgument("thumbnailHeight", ""+FileDataset.DEFAULT_THUMBNAIL_HEIGHT));
			int imW = Integer.parseInt(getArgument("imageWidth", ""+FileDataset.DEFAULT_IMAGE_WIDTH));
			int imH = Integer.parseInt(getArgument("imageHeight", ""+FileDataset.DEFAULT_IMAGE_HEIGHT));
			
			String ns = USER_NAMESPACE_PREFIX + getSession().getID();
			RESTUserModel um = (RESTUserModel)(getSession().getUserModel());
			
			String statement = getArgument("statement");
			InputStream is = new ByteArrayInputStream(statement.getBytes());
			um.getModelGenerator().parseInNamespace(is, ns, um.getResolver());
			is.close();
			
			Context ctx = (Context)((ModelManager.Resolver)um.getResolver()).getCurrentContext();
			
			if (visualize && ctx != null && !ctx.isEmpty()) {

				/*
				 * create a filedataset from the context
				 */
				FileDataset fds = new FileDataset(ctx);
			
				fds.setThumbnailBoxSize(thW, thH);
				fds.setImageBoxSize(imW, imH);
				
				/*
				 * persist in temp dir
				 */
				File fdir = Thinklab.get().getTempArea(getSession().getWorkspace());
				fds.persist(fdir.toString());
				
				/*
				 * if local, send the location directly; otherwise zip things up and 
				 * make available for download.
				 */
				if (localserv) {
					put("location", fdir.toString());
				} else {
					Pair<File, String> fname = this.getFileName("context.zip", getSession());
					FolderZiper.zipFolder(fdir.toString(), fname.getFirst().toString());
					addDownload(fname.getSecond(), fname.getFirst().toString());
				}
				
			}
			
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
