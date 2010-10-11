package org.integratedmodelling.thinklab.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.rest.interfaces.IRESTHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Default resource handler always responds JSON, with fields pointing to results or 
 * further resource URNs.
 * 
 * @author Ferdinando
 *
 */
public abstract class DefaultRESTHandler extends ServerResource implements IRESTHandler {

	ArrayList<String> _context = new ArrayList<String>();
	HashMap<String, String> _query = new HashMap<String, String>();
	String _MIME = null;
	Date start = null;

//	private JSONObject _result = null;
//	
//	protected JSONObject getResult() {
//		if (_result == null)
//			_result = new JSONObject();
//		return _result;
//	}
//	
	boolean _processed = false;
	
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
				
			} catch (JSONException e) {
				throw new ResourceException(e);
			}
		}
		
		super.doRelease();
	}
	
	protected JsonRepresentation wrap(JSONObject jsonObject) {
	    JsonRepresentation jr = new JsonRepresentation(jsonObject);   
	    jr.setCharacterSet(CharacterSet.UTF_8);
	    return jr;
	}
	
}
