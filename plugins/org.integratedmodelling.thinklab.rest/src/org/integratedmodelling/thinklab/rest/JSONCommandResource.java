package org.integratedmodelling.thinklab.rest;

import joptsimple.util.KeyValuePair;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.rest.interfaces.JSONCommandHandler;
import org.integratedmodelling.utils.KeyValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

 
public class JSONCommandResource extends ServerResource {
  
  private static final String JSON_NAME_TABLE = "table";
  
  private static final String JSON_NAME_COLUMNS = "cols";
  private static final String JSON_NAME_COLUMNS_ID = "id";
  private static final String JSON_NAME_COLUMNS_LABEL = "label";
  private static final String JSON_NAME_COLUMNS_TYPE = "type";
  private static final String JSON_NAME_COLUMNS_PATTERN = "pattern";
  
  private static final String JSON_NAME_ROWS = "rows";
  private static final String JSON_NAME_ROWS_V = "v";
  private static final String JSON_NAME_ROWS_F = "f";

 
  @Get
  public Representation represent() throws ResourceException {
	  
    JSONObject json = null;
    Reference ref = this.getRequest().getOriginalRef();
    KeyValueMap query = new KeyValueMap(ref.getQuery(true), "&");
    
    ISession session = RESTPlugin.get().getSessionForCommand(query);
    
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
  
  private JSONObject createTable() throws JSONException{
    JSONArray columns = new JSONArray();
    JSONArray rows = new JSONArray();
    JSONObject r_c = new JSONObject();
    r_c.put(JSON_NAME_COLUMNS, columns);
    r_c.put(JSON_NAME_ROWS, rows);
    
    this.createColumns(columns);
    this.createRows(rows);
    
    return r_c;
  }
  
  private void createColumns(JSONArray columns) throws JSONException{
    
    columns.put(this.createColumn("A", "Date", "d", "M/d/yyyy"));
    columns.put(this.createColumn("B", "Budget", "n", "#0.###############"));
    columns.put(this.createColumn("C", "Revenue", "n", "#0.###############"));
    columns.put(this.createColumn("D", "Movie", "t", ""));
    
  }
  
  private void createRows(JSONArray rows) throws JSONException{
    
    JSONArray row = new JSONArray();
    
    row.put(this.createCell("new Date(1981,10,6)", "11/6/1981"));
    row.put(this.createCell("5000000.0", "5000000"));
    row.put(this.createCell("4.2365581E7", "42365581"));
    row.put(this.createCell("Time Bandits", null));
    
    rows.put(row);
    
  }
  
  private JSONObject createCell(String v, String f) throws JSONException{
    JSONObject jo = new JSONObject();
    jo.put(JSON_NAME_ROWS_V, v);
    if(f != null)
      jo.put(JSON_NAME_ROWS_F, f);
    return jo;
  }
  
  private JSONObject createColumn(String id, String label, String type, String pattern) throws JSONException{
    JSONObject jo = new JSONObject();
    jo.put(JSON_NAME_COLUMNS_ID, id);
    jo.put(JSON_NAME_COLUMNS_LABEL, label);
    jo.put(JSON_NAME_COLUMNS_TYPE, type);
    jo.put(JSON_NAME_COLUMNS_PATTERN, pattern);
    return jo;
  }
 
}