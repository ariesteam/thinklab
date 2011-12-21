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
package org.integratedmodelling.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.mvel2.MVEL;

/**
 * Encapsulates expression and template functionalities in a properties document, used in several places in
 * Thinklab plugins.
 * 
 * When a PropertiesTemplateAdapter is created on a Properties object, it will look for variable definitions
 * and template definitions in the properties. Both are expected to start with a common prefix, passed to 
 * the constructor. 
 * 
 * Variable definitions are of the form prefix.variable.varname = expression. Expressions are MVEL expressions
 * that can use the Java runtime environment and may reference variables that are passed in a hash when 
 * computeVariables() is called. NOTE: variables should not refer to each other.
 * 
 * Templates can use these variables and any other that are in the passed hash when substituteTemplates() is
 * called.
 * 
 * Better documentation later.
 * 
 * @author Ferdinando
 *
 */
public class PropertiesTemplateAdapter {

	Hashtable<String, Serializable> variables = new Hashtable<String, Serializable>();
	private Properties properties = null;
	
	public PropertiesTemplateAdapter(Properties properties, String variablePrefix) {
		
		this.properties = properties;
		
		/* compile the definition of any variables we may have in the properties */
		Enumeration<?> pnames = properties.propertyNames();
		while (pnames.hasMoreElements()) {
			
			String pn = (String)pnames.nextElement();
			if (pn.startsWith(variablePrefix)) {
				
				String varname = pn.substring(pn.lastIndexOf(".")+1);
				String expr = properties.getProperty(pn);
				variables.put(varname, MVEL.compileExpression(expr));
			}
		}
	}
	
	/**
	 * Compute variables and add them to the context. 
	 * @param context
	 * @return
	 */
	Hashtable<String, Object> computeVariables(Hashtable<String, Object> context) {
		
		/* build map to create list from template */
		Hashtable<String, Object> vmap = new Hashtable<String,Object>();
					
		/* evaluate all variables and add their values to the symbol table */
		for (String kvar : variables.keySet()) {
			vmap.put(kvar, MVEL.executeExpression(variables.get(kvar), context));
		}
		
		context.putAll(vmap);
		
		return context;
	}
	
	public ArrayList< Pair<String, String>> 
		computeTemplates(String templatePrefix, Hashtable<String, Object> context) {
		
		return null;
	}
	
	public ArrayList<Pair<String, IList>> computeListsFromTemplates(
			String templatePrefix, Hashtable<String, Object> context)
			throws ThinklabException {

		ArrayList<Pair<String, IList>> ret = new ArrayList<Pair<String, IList>>();

		computeVariables(context);

		Enumeration<?> pnames = properties.propertyNames();
		while (pnames.hasMoreElements()) {

			String pn = (String) pnames.nextElement();
			if (pn.startsWith(templatePrefix)) {

				String tname = pn.substring(pn.lastIndexOf(".")+1);

				IList l;
					l = PolyList.parseWithTemplate(properties.getProperty(pn),
							context);
					ret.add(new Pair<String, IList>(tname, l));
			}
		}
		
		return ret;

	}
	
}
