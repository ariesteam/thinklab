/**
 * RulePlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabRulePlugin.
 * 
 * ThinklabRulePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabRulePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.rules;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.integratedmodelling.rules.exceptions.ThinklabRuleEngineException;
import org.integratedmodelling.rules.interfaces.IThinklabRuleEngine;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;


public class RulePlugin extends ThinklabPlugin {

//	private OWLModel owlModel;
	
	static final public String PLUGIN_ID = "org.integratedmodelling.thinklab.rules";

	// property to select the type of engine to be created. Should be jess or drools. 
	// defaults: no rule engine
	private static final String CREATE_JESS_ENGINE_PROPERTY = "rule.engine";

	// the ID for the rule engine in session user data catalog
	public static final String ENGINE_USERDATA_ID = "rule.engine.id";
	
	private boolean usingJess = false;
	private boolean usingDrools = false;
	
	public static RulePlugin get() {
		return (RulePlugin) getPlugin(PLUGIN_ID);
	}

	/*
	 * the Drools rule base (thread safe).
	 */
	public static RuleBase ruleBase = null;
	
	/**
	 * Obtain a rule engine through this one. At this point I 'm crossing fingers and 
	 * assuming that rule engines can be
	 * created concurrently, and that each has its own rule space while accessing the same
	 * owl model. This will be necessary to attach a rule engine to a session.
	 * 
	 * @return
	 * @throws ThinklabRuleEngineException
	 */
	public IThinklabRuleEngine createRuleEngine() throws ThinklabRuleEngineException {
		return /* new JessRuleEngine(owlModel)*/ null;
	}

	@Override
	public void load(KnowledgeManager km)
			throws ThinklabPluginException {
	
		String engine = getProperties().getProperty(CREATE_JESS_ENGINE_PROPERTY, "").trim();
		
		if (engine.equals("")) {
		
			// do nothing, obviously
			
		} else if (engine.equals("jess")) {
			
//				usingJess = true;
//			
//				/* 
//				 * make sure we're using the Protege repository, or we don't know what to do *
//				 */
//				if (!(km.getKnowledgeRepository() instanceof FileKnowledgeRepository))
//					throw new ThinklabPluginException("the Rule plugin can only work with a knowledge repository based on protege");		

				/* create the SWRL factory based on common OWLModel */
				//owlModel = ((FileKnowledgeRepository)km.getKnowledgeRepository()).getOWLModel();
			
		} else if (engine.equals("drools")) {

			usingDrools = true;
			ruleBase = RuleBaseFactory.newRuleBase();
			
		} else {
			throw new ThinklabPluginException("rule: unknown rule engine configured: " + engine);
		}
				
	}

	public boolean isUsingJess() {
		return usingJess;
	}
	
	public boolean isUsingDrools() {
		return usingDrools;
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	

}
