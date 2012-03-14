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
package org.integratedmodelling.thinklab.rest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.list.Escape;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.rest.interfaces.IRESTHandler;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Default resource handler always responds JSON, with fields pointing to results or 
 * further resource URNs.
 * 
 * The handler methods should return one of the wrap() functions.
 * 
 * @author Ferdinando
 *
 */
public abstract class DefaultRESTHandler extends ServerResource implements IRESTHandler {

	// result types
	static public final int VOID = 0;
	static public final int INT = 1;
	static public final int DOUBLE = 2;
	static public final int TEXT = 3;
	static public final int URN = 4;
	static public final int INTS = 5;
	static public final int DOUBLES = 6;
	static public final int TEXTS = 7;
	static public final int URNS = 8;
	 // this one means "you're on your own figuring it out" and is only used for internal
	// hand-shaking commands where the result structure is known to the client.
	static public final int OBJECT = 9;
	static public final int LIST = 10;
	
	// codes for getStatus()
	static public final int DONE = 0;
	static public final int FAIL = 1;
	static public final int WAIT = 2;

	ArrayList<String> _context = new ArrayList<String>();
	HashMap<String, String> _query = new HashMap<String, String>();
	String _MIME = null;
	Date start = null;
	int resultStatus = DONE;
	private ResultHolder rh = new ResultHolder();
	
	private ArrayList<Pair<String,String>> _downloads = 
		new ArrayList<Pair<String,String>>();

	String error = null, info = null, warn = null;
	
	boolean _processed = false;

	static RESTTaskScheduler _scheduler = null;

	
	/**
	 * Call this one to ensure that a restricted command is allowed for the
	 * current user. The actual session user is checked against the requested
	 * privilege. If RESTManager returns true to allowPrivilegedLocalConnection
	 * (currently always false) any connection coming from localhost is allowed
	 * privileged access.
	 * 
	 * @param concept the user role required for the command. Must resolve to a 
	 *        valid concept.
	 * @throws ThinklabException if the user is not allowed to run the command or 
	 * 		   is undefined
	 */
	protected boolean checkPrivileges(String concept) throws ThinklabException {
	
		String domain = getRequest().getResourceRef().getHostDomain();
		boolean isLocal = 
			(domain != null && (domain.equals("127.0.0.1") || domain.equals("localhost")));
		
		if (isLocal && RESTManager.get().allowPrivilegedLocalConnections())
			return true;
		
		if (getSession() == null) {
			fail("no user privileges for command");
			return false;
		}
		
		ISemanticObject user = getSession().getUserModel().getUser();
		if (user == null || !user.is(Thinklab.c(concept))) {
			fail("not enough user privileges for command");
			return false;
		}
		return true;
	}
	
	protected static RESTTaskScheduler getScheduler() {
		
		if (_scheduler == null) {
			int ntasks = 
				Integer.parseInt(
						Thinklab.get().getProperties().getProperty(
								RESTTaskScheduler.N_TASKS_PROPERTY, "8"));
			_scheduler = new RESTTaskScheduler(ntasks);
			_scheduler.start();
		}
		return _scheduler;
	}
	
	/*
	 * TODO pass command for info, record time, track listeners for task-specific logging
	 */
	public Representation enqueue(final Thread thread) {
		
		getScheduler().enqueue(thread);
		
		JSONObject ret = new JSONObject();
		try {
			ret.put("taskid", thread.getId()+"");
			ret.put("status", WAIT);
		} catch (JSONException e) {
			// come on
		}
		return new JsonRepresentation(ret);
	}
	
	protected void addDownload(String handle, String filename) {
		_downloads.add(new Pair<String, String>(filename, handle));
	}

	/**
	 * Takes the session from the session parameter, which must be in all
	 * commands that request a session.
	 * 
	 * @throws ThinklabInternalErrorException
	 */
	public ISession getSession() throws ThinklabException {

		String id = getArgument("session");
		if (id == null)
			throw new ThinklabInternalErrorException("REST command did not specify required session ID");
		
		return RESTManager.get().getSession(id);
	}
	
	/**
	 * Return the file correspondent to the handle previously returned by 
	 * getFileName() and supposedly uploaded by a client.
	 * 
	 * @param argument
	 * @return
	 * @throws ThinklabException 
	 */
	protected File getFileForHandle(String handle, boolean mustExist) throws ThinklabException {

		File ret = new File(Thinklab.get().getScratchArea() + File.separator + "rest/tmp" + 
				File.separator + handle);

		if (mustExist && !ret.exists())
			throw new ThinklabResourceNotFoundException(handle);
			
		return ret;	
	}
	
	/**
	 * Return a file path and "handle" for a file that will be created and returned to the 
	 * client to retrieve through receive(handle).

	 * @param fileName the file the user wants us to create
	 * @param session current session
	 * @return pair<file, handle> - create file in File, return handle to client using 
	 * 		   addDownload(handle, fileName)
	 * @throws ThinklabException
	 */
	protected Pair<File,String> getFileName(String fileName, ISession session) throws ThinklabException {

		Pair<File,String> ret = null;
		String workspace = session.getWorkspace();		
		File sdir = new File(Thinklab.get().getScratchArea() + File.separator + "rest/tmp" + 
					File.separator + workspace);
		sdir.mkdirs();
		
		String ext = MiscUtilities.getFileExtension(fileName);
		ext = (ext == null || ext.isEmpty()) ? ".tmp" : ("." + ext); 
		try {
			File out = File.createTempFile("upl", ext, sdir);
			String handle = workspace + File.separator + MiscUtilities.getFileName(out.toString());
			ret = new Pair<File, String>(out, handle);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		return ret;
	}
	
	/**
	 * Publish given file in default publish directory
	 * 
	 * @param first
	 * @param _publish2
	 * @throws ThinklabIOException 
	 */
	protected void publish(File file, String publish) throws ThinklabIOException {
		
		String pdir = System.getenv("THINKLAB_PUBLISH_DIRECTORY");
		if (pdir == null) 
			pdir = "./publish";
		File out = new File(pdir + File.separator + publish);
		File dir = MiscUtilities.getPath(out.toString());
		dir.mkdirs();
		CopyURL.copy(file, out);
	}

	
	/**
	 * Return the elements of the request path after the service identifier, in the same
	 * order they have in the URL.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public List<String> getRequestPath() throws ThinklabException {
		
		if (!_processed)
			processRequest();
		return _context;
	}

	/**
	 * Get a map of all query arguments, no matter what method was used in the request.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public HashMap<String, String> getArguments() throws ThinklabException {

		if (!_processed)
			processRequest();
		return _query;
	}
	
	public String getArgument(String id) throws ThinklabException {
		return getArguments().get(id);
	}
	
	public String requireArgument(String id) throws ThinklabException {
		String ret = getArguments().get(id);
		if (ret == null)
			throw new ThinklabResourceNotFoundException(
					"required argument " + id + 
					" not passed in REST request");
		return ret;
	}

	// only used in CheckWaiting for now - set the result object directly
	protected void setResult(ResultHolder result) {
		rh = result;
	}
	
	protected void keepWaiting(String taskId) {
		put("taskid", taskId);
		resultStatus = WAIT;
	}
	
	/**
	 * Return the string correspondent to the MIME type that was selected by the URL
	 * extension. Will return null if no extension was used.
	 * 
	 * @return
	 */
	protected String getMIMEType() {
		if (!_processed)
			processRequest();
		return _MIME;
	}
	
	private void processRequest() {
		
			Form form = getRequest().getResourceRef().getQueryAsForm();
			for (Parameter parameter : form) {
				_query.put(parameter.getName(), Escape.fromURL(parameter.getValue()));
			}
		
		_processed = true;
	}
	
	@Override
	protected void doInit() throws ResourceException {
		// TODO Auto-generated method stub
		super.doInit();
		start = new Date();
	}

	@Override
	protected void doRelease() throws ResourceException {
		
		Date date = new Date();
		
		Representation r = getResponseEntity();

		if (r instanceof JsonRepresentation) {
			try {
				
				((JsonRepresentation)r).getJsonObject().put(
						"elapsed", 
						((float)(date.getTime() - start.getTime()))/1000.0f);
				
				((JsonRepresentation)r).getJsonObject().put(
						"endTime", date.getTime());
				
			} catch (JSONException e) {
				throw new ResourceException(e);
			}
		}
		
		super.doRelease();
	}
	
	/**
	 * Return this when you have a JSON object of your own
	 * 
	 * @param jsonObject
	 * @return
	 */
	protected JsonRepresentation wrap(JSONObject jsonObject) {
	    JsonRepresentation jr = new JsonRepresentation(jsonObject);   
	    jr.setCharacterSet(CharacterSet.UTF_8);
	    return jr;
	}
	
	/**
	 * If this is used, "return wrap()" should be the last call in your handler function. Any 
	 * data set through this one or setResult will be automatically returned in a JSON object.
	 * 
	 * @param key
	 * @param o
	 */
	protected void put(String key, Object... o) {
		rh.put(key, o);
	}
	
	public void setResult(Object o) {
		if (o instanceof IList)
			rh.setList((IList)o);
		rh.setResult(o);
	}
	
	protected void setResult(int... iResult) {
		rh.setResult(iResult);
	}

	protected void setResult(double... dResult) {
		rh.setResult(dResult);
	}

	protected void setResult(String... tResult) {
		rh.setResult(tResult);
	}
	
	protected void addResult(String rURN, String rMIME) {
		rh.addResult(rURN, rMIME);
	}
	
	protected void fail() {
		resultStatus = FAIL;
	}

	protected void fail(String message) {
		resultStatus = FAIL;
		error = message;
	}

	protected void fail(Throwable e) {
		resultStatus = FAIL;
		error = e.getMessage();
		rh.put("exception-class", e.getClass().getCanonicalName());
		rh.put("stack-trace", MiscUtilities.getStackTrace(e));
	}
	
	protected void warn(String s) {
		warn = s;
	}

	protected void info(String s) {
		info = s;
	}

	/**
	 * Return this if you have used any of the put() or setResult() functions. Will create and 
	 * wrap a suitable JSON object automatically.
	 * @return
	 */
	protected JsonRepresentation wrap() {
		
		JSONObject jsonObject = new JSONObject();
		rh.toJSON(jsonObject);

		try {
			
			jsonObject.put("status", resultStatus);
	
			if (warn != null) {
				jsonObject.put("warn", warn);
			}
			if (info != null) {
				jsonObject.put("info", info);
			}
			if (error != null) {
				jsonObject.put("error", error);
			}
			
			if (_downloads.size() > 0) {
				Object[] oj = new Object[_downloads.size()];
				int i = 0;
				for (Pair<String, String> dl : _downloads) {
					oj[i++] = new String[]{dl.getFirst(), dl.getSecond()};
				}
				jsonObject.put("downloads", oj);
			}
			
		} catch (JSONException e) {
			throw new ThinklabRuntimeException(e);
		}
		
		JsonRepresentation jr = new JsonRepresentation(jsonObject);   
	    jr.setCharacterSet(CharacterSet.UTF_8);
	    return jr;
	}
}
