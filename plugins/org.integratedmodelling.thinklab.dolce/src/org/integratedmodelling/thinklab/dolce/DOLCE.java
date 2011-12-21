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
package org.integratedmodelling.thinklab.dolce;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class DOLCE extends ThinklabPlugin {

	static final String PLUGIN_ID = "org.integratedmodelling.thinklab.dolce";
	
	// to be completed
	static public final String PART_OF = "DOLCE-Lite:part-of";
	static public final String PARTICIPANT_IN = "DOLCE-Lite:participant-in";
	static public final String INHERENT_IN = "DOLCE-Lite:inherent-in";
	static public final String IMMEDIATE_RELATION = "DOLCE-Lite:immediate-relation";
	static public final String IMMEDIATE_RELATION_I = "DOLCE-Lite:immediate-relation-i";
	
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
	}

}
