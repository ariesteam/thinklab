/**
 * PropertiesTemplateAdapter.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 25, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Feb 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
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
	
	public ArrayList<Pair<String, Polylist>> computeListsFromTemplates(
			String templatePrefix, Hashtable<String, Object> context)
			throws ThinklabValidationException {

		ArrayList<Pair<String, Polylist>> ret = new ArrayList<Pair<String, Polylist>>();

		computeVariables(context);

		Enumeration<?> pnames = properties.propertyNames();
		while (pnames.hasMoreElements()) {

			String pn = (String) pnames.nextElement();
			if (pn.startsWith(templatePrefix)) {

				String tname = pn.substring(pn.lastIndexOf(".")+1);

				Polylist l;
				try {
					l = Polylist.parseWithTemplate(properties.getProperty(pn),
							context);
					ret.add(new Pair<String, Polylist>(tname, l));

				} catch (MalformedListException e) {

					throw new ThinklabValidationException(
							"geospace: invalid list template specification: "
									+ e.getMessage());
				}
			}
		}
		
		return ret;

	}
	
}
