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
package org.integratedmodelling.thinklab.webapp.view.components;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;

public class KnowledgeSelector extends Bandbox {

	private static final long serialVersionUID = -1425784337219232620L;
	private String conceptID;
	Bandbox bandbox;
	private String selected;
	
	public class SelClassListener implements EventListener {

		KnowledgeTreeSelector ktree;
		
		public SelClassListener(KnowledgeTreeSelector knowledgeTreeSelector) {
			ktree = knowledgeTreeSelector;
		}

		@Override
		public void onEvent(Event event) throws Exception {
			bandbox.setValue( (selected = ktree.getSelectedItem().getLabel()));
			notifyKnowledgeSelected(selected);
		}
	}
	
	public class KnowledgeTreeSelector extends KnowledgeTreeComponent {

		private static final long serialVersionUID = -1734267329230604353L;
		
		public KnowledgeTreeSelector() {
			addEventListener("onSelect", new SelClassListener(this));
		}
	}
	
	public void notifyKnowledgeSelected(String selected) throws ThinklabException {
	}
	
	public KnowledgeSelector(String conceptID) {
		
		this.conceptID = selected = conceptID;
		setup();
		bandbox = this;

	}

	public String getSelected() {
		return selected;
	}
	
	protected void reset() {
		
		ArrayList<Component> cp = new ArrayList<Component>();
		
		/* erase the whole form */
		for (Object c : getChildren()) {
			cp.add((Component)c);
		}
		
		for (Component c : cp)
			c.detach();
	}
	
	protected void setup() {

		reset();
		setValue(conceptID);
		Bandpopup popup = new Bandpopup();
		KnowledgeTreeComponent ktree = new KnowledgeTreeSelector();
		ktree.setConcept(conceptID);
		popup.appendChild(ktree);
		this.appendChild(popup);
	}

}
