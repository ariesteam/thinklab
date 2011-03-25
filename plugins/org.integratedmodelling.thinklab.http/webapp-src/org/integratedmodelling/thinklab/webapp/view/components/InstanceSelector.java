/**
 * InstanceSelector.java
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

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.webapp.view.QueryFormStructure;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.integratedmodelling.thinklab.webapp.view.VisualConcept;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

/**
 * Generates a vbox of fields from the definition of a concept that will 
 * create a constraint on the concept's properties when submitted.
 * 
 * @author Ferdinando Villa
 *
 */
public class InstanceSelector extends Vbox {

	VisualConcept concept = null;
	QueryFormStructure schema = null;
	Collection<Component> components = null;
	Component mainC = this;
	int offset = 0;
	int maxResults = -1;
	private IQueriable queriable;
	String[] resultSchema = null;

	private int indentLevel;
	
	private static final long serialVersionUID = 8072452505965901991L;

	/*
	 * the event fired when the user clicks the query button
	 */
	public class QueryEvent extends Event {

		Constraint constraint = null;
		IQueryResult result = null;
		
		public Constraint getConstraint() {
			return constraint;
		}
		
		public IQueryResult getResult() {
			return result;
		}
		
		public QueryEvent(String name, Component target, Constraint data, IQueryResult result) {
			super(name, target);
			this.constraint = data;
			this.result = result;
		}
		
	}
	
	public class NotifyConstraint implements EventListener {
		
		@Override
		public void onEvent(Event arg0) throws Exception {

			Constraint c = schema.getConstraint(components);	
			IQueryResult r = null;
			
			if (queriable != null) {
				r = queriable.query(c, resultSchema, offset, maxResults);
			}
			
			QueryEvent qevent = new QueryEvent("onQuery", mainC, c, r);
			Events.sendEvent(qevent);
		}
	}
	
	private class QueryButton extends Toolbarbutton {

		private static final long serialVersionUID = -6626671639141988555L;

		public QueryButton() {
			super();
			this.setLabel("Query");
			addEventListener(Events.ON_CLICK, new NotifyConstraint());
		}
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
	
	/* create the form */
	protected void setup() throws ThinklabException {
		
		reset();
	
		if (schema == null) {

			/* TODO retrieve schema from type manager */
			schema = TypeManager.get().getQueryFormStructure(concept.getConcept());
			components = schema.getFormComponents(indentLevel+1);
		}
		
		Component appendTo = this;
		
		if (indentLevel > 0) {

			Hbox hbox = new Hbox();
			hbox.setWidth("100%");
			Separator sep = new Separator("vertical");
			sep.setWidth((30*indentLevel) + "px");
			hbox.appendChild(sep);
			Vbox vbox = new Vbox();
			vbox.setWidth("100%");
			hbox.appendChild(vbox);
			appendChild(hbox);
			appendTo = vbox;
		}
		
		/* TODO see if a YUI grid is better. This will require YUI rows, too. */
		Grid cGrid = null; Rows rows = null;
		
		for (Component c : components) {
			
			if (c instanceof Row) {

				if (cGrid == null) {
					cGrid = new Grid();
					rows = new Rows();
					cGrid.setWidth("100%");
					cGrid.appendChild(rows);
				}
				
				rows.appendChild(c);
				
			} else {
				
				if (cGrid != null) {
					appendTo.appendChild(cGrid);
					cGrid = null;
					rows = null;
				}
				appendTo.appendChild(c);
			}
			
		}

		if (cGrid != null) {
			appendTo.appendChild(cGrid);
		}
		
		/*
		 * last but not least, submit button as requested, linked to 
		 * constraint publisher, unless this is being used as a sub-form
		 */
		if (indentLevel == 0) {
			Div buttons = new Div();
			buttons.setWidth("100%");
			buttons.setAlign("right");
			buttons.appendChild(new QueryButton());
			appendChild(buttons);
		}
		
		invalidate();
	}
	
	public Constraint getConstraint() throws ThinklabException {
		return schema.getConstraint(components);
	}
	
	public InstanceSelector() {
		this.indentLevel = 0;
	}
	
	public InstanceSelector(int indentLevel) {
		this.indentLevel = indentLevel;
	}

	public void setConcept(String c) throws ThinklabException {
		
		concept = TypeManager.get().getVisualConcept(c);
		schema = null;
		setup();
	}
	
	public void setQueriable(IQueriable queriable) {
		this.queriable = queriable;
	}
	
	public void setKbox(String queriable) throws ThinklabException {
		IKBox kbox = KBoxManager.get().retrieveGlobalKBox(queriable);
		this.queriable = kbox;
	}
	
	public void setResultSchema(String[] list) {
		this.resultSchema = list;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setMaxResults(int r) {
		this.maxResults = r;
	}

}


