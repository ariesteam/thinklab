package org.integratedmodelling.thinklab.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.rest.interfaces.IRESTHandler;
import org.integratedmodelling.utils.NameGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.data.Method;
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
	
	// codes for getStatus()
	static public final int DONE = 0;
	static public final int FAIL = 0;
	static public final int WAIT = 0;

	ArrayList<String> _context = new ArrayList<String>();
	HashMap<String, String> _query = new HashMap<String, String>();
	String _MIME = null;
	Date start = null;
	int resultStatus = DONE;
	private ResultHolder rh = new ResultHolder();

	boolean _processed = false;
	
	/**
	 * The thread used to enqueue any non-instantaneous work.
	 * 
	 * @author Ferdinando
	 *
	 */
	protected abstract class TaskThread extends Thread  {

		private volatile boolean isException = false;
		private String error = null;
		
		protected abstract void execute() throws Exception;
		protected abstract void cleanup();
		
		
		@Override
		public void run() {

			try {
				execute();
			} catch (Exception e) {
				error = e.getMessage();
				isException = true;
			} finally {
				cleanup();
			}
		}
		
		public boolean error() {
			return isException;
		}
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
		
		// TODO Auto-generated method stub
		if (getRequest().getMethod().equals(Method.GET)) {
			
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
	
	/**
	 * TODO pass a thread of a subclass that can be checked for finish, so we 
	 * can rely on this uniformly.
	 * 
	 * Postpone result to next call. Sets task and returns taskId. If possible pass
	 * a number of milliseconds before which it's not worth trying again. If not, pass
	 * -1L to tell client that it's on its own figuring it out.
	 * 
	 * @return
	 */
	protected String postpone(long howLong) {

		String taskId = NameGenerator.newName("task");
		
		resultStatus = WAIT;
		
		return taskId;
	}
	
	/**
	 * Return this if you have used any of the put() or setResult() functions. Will create and 
	 * wrap a suitable JSON object automatically.
	 * @return
	 */
	protected JsonRepresentation wrap() {
		
		JSONObject jsonObject = new JSONObject();
		rh.toJSON(jsonObject);
	    JsonRepresentation jr = new JsonRepresentation(jsonObject);   
	    jr.setCharacterSet(CharacterSet.UTF_8);
	    return jr;
	}
}
