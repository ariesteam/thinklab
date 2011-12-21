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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * A layout for a result item with nine boxes arranged this way:
 * 
 * 	nw n ne
 *  w  c e
 *  sw s se 
 *  
 *  By default (as specified in typedecorations.xml), the boxes will contain:
 *  
 *  nw. An icon for the object's type;
 *  n. The icon's official label or short description;
 *  ne. either nothing, or icons that illustrate state or context of the associated obj;
 *  w. nothing;
 *  c. A longer description, or excerpt of an even longer one;
 *  e. nothing;
 *  sw. nothing;
 *  s. a right-aligned menu of things to do with the object, e.g. "Details", "Export" etc.
 *  se. nothing
 *  
 *  Such fields can be controlled by associating expressions to the respective content on a 
 *  type by type basis, using the type decorations statements. A VisualResult is constructed
 *  from a QueryResult object and an index, and set up appropriately according to the
 *  closest specification in the type decorations.
 *  
 * @author Ferdinando Villa
 *
 */
public class ResultItem extends Vbox {

	private static final long serialVersionUID = 3959452758657367447L;
	IQueryResult qres = null;
	int qind = 0;
	TypeManager.TypeResultVisualizer tvr = null;
	
	Window[] boxes = new Window[9];
	
	public ResultItem(IQueryResult result, int index) throws ThinklabException {
		
		setSpacing("1px");	
		qres = result;
		qind = index;
		
		/*
		 * recover type of result and specifications of result visualizer for
		 * type.
		 */
		String type = result.getResultField(index, "type").toString();
		if (type == null || type.equals("")) 
			type = KnowledgeManager.Thing().toString();
		
		this.tvr = 
			TypeManager.get().getResultVisualizer(
					KnowledgeManager.get().retrieveConcept(type));		
		setup();	
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
	
	private void setup() throws ThinklabException {
		
		reset();
		
		for (int i = 0; i < 9; i++) {
			boxes[i] = new Window();
			tvr.setupWindow(boxes[i], i, qres, qind);
		}
		
		Hbox hb1 = new Hbox();
		hb1.appendChild(boxes[0]);
		hb1.appendChild(boxes[1]);
		hb1.appendChild(boxes[2]);
		
		Hbox hb2 = new Hbox();
		hb2.appendChild(boxes[3]);
		hb2.appendChild(boxes[4]);
		hb2.appendChild(boxes[5]);

		Hbox hb3 = new Hbox();
		hb3.appendChild(boxes[6]);
		hb3.appendChild(boxes[7]);
		hb3.appendChild(boxes[8]);
		
		appendChild(hb1);
		appendChild(hb2);
		appendChild(hb3);

		invalidate();
	}
	
}