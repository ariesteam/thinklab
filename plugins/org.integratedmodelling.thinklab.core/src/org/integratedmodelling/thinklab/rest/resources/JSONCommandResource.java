package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.thinklab.rest.interfaces.JSONCommandHandler;
import org.integratedmodelling.utils.KeyValueMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

 
public class JSONCommandResource extends ServerResource {
  
 
  @Get
  public Representation represent() throws ResourceException {
	  
    JSONObject json = null;
    Reference ref = this.getRequest().getOriginalRef();
    KeyValueMap query = new KeyValueMap(ref.getQuery(true), "&");
    
    ISession session = RESTManager.get().getSessionForCommand(query);
    
    try {
		
    	Command command = CommandParser.parse(query);
		
    	if (command instanceof JSONCommandHandler)
    		json = ((JSONCommandHandler)command).executeJSON(command, session);
    	else {
    		
    		IValue value = CommandManager.get().submitCommand(command, session);
    		
    	    try {    	                		
        		json = new JSONObject();
    	        json.put("result", value == null ? "nil" : value.toString());
    	    } catch (JSONException e) {
    	      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    	    }
    	}
    	
	} catch (ThinklabException e1) {
		throw new ResourceException(e1);
	}
    
    
    JsonRepresentation jr = new JsonRepresentation(json);
    
    jr.setCharacterSet(CharacterSet.UTF_8);
    
    return jr;
  }
 
}