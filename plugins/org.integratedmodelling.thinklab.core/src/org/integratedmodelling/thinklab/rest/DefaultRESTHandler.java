package org.integratedmodelling.thinklab.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.restlet.resource.ServerResource;

/**
 * REST resource handlers must be derived from this one, so that Thinklab knows how to work with
 * the resource. The execute methods (tagged with Get, Post, Delete or Put) should use the
 * get... functions to access the context and the expected MIME type of the result, and behave
 * accordingly. Only MIME types that are declared in the RESTResourceHandler annotation will
 * be admitted, anything else will generate an error upstream.
 * 
 * @author Ferdinando
 *
 */
public abstract class DefaultRESTHandler extends ServerResource {

	ArrayList<String> _context = new ArrayList<String>();
	HashMap<String, String> _query = new HashMap<String, String>();
	String _MIME = null;
	
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
	
}
