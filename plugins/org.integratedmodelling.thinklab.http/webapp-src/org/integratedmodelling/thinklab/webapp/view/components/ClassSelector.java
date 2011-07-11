/**
 * ClassSelector.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinkcap.
 * 
 * Thinkcap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinkcap is distributed in the hope that it will be useful,
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
 * @author    Ferdinando
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.webapp.view.components;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.integratedmodelling.thinklab.webapp.view.VisualProperty;

public class ClassSelector extends LiteralSelector {

	IConcept concept = null;
	
	public class KSelector extends KnowledgeSelector {
		
		public KSelector(String conceptID) throws ThinklabException {
			super(conceptID);
			concept = KnowledgeManager.get().requireConcept(conceptID);
		}

		private static final long serialVersionUID = 8305556589770529869L;

		@Override
		public void notifyKnowledgeSelected(String selected)
				throws ThinklabException {
			concept = KnowledgeManager.get().requireConcept(selected);
		}
	}
	
	public ClassSelector(IProperty p) {
		
		super(p);
		
		VisualProperty pp = TypeManager.get().getVisualProperty(p);
		IConcept c = pp.getVisualizedRange();
		concept = c;
		
	}

	private static final long serialVersionUID = -7613788803788653069L;

	@Override
	protected Restriction defineRestriction() {
		// TODO Auto-generated method stub
		return new Restriction(property.getProperty(), concept);
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
	
	}


}