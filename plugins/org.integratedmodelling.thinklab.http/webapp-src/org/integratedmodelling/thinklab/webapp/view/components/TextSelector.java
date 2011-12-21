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

import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponentConstructor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

public class TextSelector extends LiteralSelector implements IRestrictionComponentConstructor {

	private static final long serialVersionUID = 866669186594668554L;

	Listbox opts, cs, mode;
	Textbox field;

	private int indentLevel;
	
	public TextSelector() {
		super();
		/* 
		 * no action needed: we just use this as a 
		 * constructor of other
		 * selectors, never to create a component.
		 */
	}
	
	public TextSelector(IProperty p, int indentLevel) {
		
		super(p);
		this.indentLevel = indentLevel;
		
		//1 
		addPropertyField();
		//2
		addSearchmodeListbox();
		//3
		addSearchField();
		//4, 5
		addOptions();
		
	}
	
	private void addSearchField() {
		
		field = new Textbox();		
		appendChild(field);		
	}

	private void addSearchmodeListbox() {
		
		mode = new Listbox();
		mode.setMold("select");
		mode.setRows(1);
		
		mode.appendItem("Contains", "contains");
		mode.appendItem("Equals", "equals");
		mode.appendItem("Starts with", "starts");
		mode.appendItem("Ends with", "ends");
		
		appendChild(mode);
	}

	private void addOptions() {
		
		Hbox hb = new Hbox();
		
		cs = new Listbox();		
		cs.setMold("select");
		cs.setRows(1);		
		cs.appendItem("Do not match case", "nocase");
		cs.appendItem("Match case", "case");
	
		
		opts = new Listbox();
		opts.setMold("select");
		opts.setRows(1);	
		opts.appendItem("Any", "any");
		opts.appendItem("All", "all");
		opts.appendItem("As a phrase", "string");
		
		hb.appendChild(opts);
		hb.appendChild(cs);
		
		appendChild(hb);
	}
	
	@Override
	public String getSpans() {
		return ",,,2";
	}

	@Override
	protected Restriction defineRestriction() throws ThinklabException {
		
		Restriction ret = null;
		
		String sval = field.getValue();
		
		if (sval == null || sval.trim().equals(""))
			return null;
			
		String match = 
			opts.getSelectedItem() == null ? "any" : opts.getSelectedItem().getValue().toString();

		boolean mcase = 
			cs.getSelectedItem() == null ? 
					false :
					cs.getSelectedItem().getValue().equals("case");
		
		String[] pieces = null;
		
		if (match.equals("string")) {
			pieces = new String[1];
			pieces[0] = sval; 
		} else {
			pieces = sval.split(" ");
		}
		
		String smode = "contains";
		
		if (mode.getSelectedItem() != null)
			smode = mode.getSelectedItem().getId();
		
		ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
		
		for (String pc : pieces) {

			if (smode.equals("contains")) {
				
				restrictions.add(					
						new Restriction(
								this.getProperty().getProperty(),
								(mcase ? "like" : "ilike"),
								"%" + pc + "%"));
				
			} else if (smode.equals("equals")) {
				
				restrictions.add(					
						new Restriction(
								this.getProperty().getProperty(),
								(mcase ? "eq" : "ieq"),
								pc));
				
			}  else if (smode.equals("starts")) {
				
				restrictions.add(					
						new Restriction(
								this.getProperty().getProperty(),
								(mcase ? "like" : "ilike"),
								pc + "%"));		
				
			}  else if (smode.equals("ends")) {
				
				restrictions.add(					
						new Restriction(
								this.getProperty().getProperty(),
								(mcase ? "like" : "ilike"),
								"%" + pc));				
			} 
			
		}
		
		if (restrictions.size() == 1) {
			
			ret = restrictions.get(0);
			
		} else if (restrictions.size() > 1) {
			
			if (match.equals("any")) {				
				ret = Restriction.OR(restrictions.toArray(new Restriction[restrictions.size()]));
			} else if (match.equals("all")) {
				ret = Restriction.AND(restrictions.toArray(new Restriction[restrictions.size()]));
				
			}
		}
		
		return ret;
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component createRestrictionComponent(IProperty property, String label, int indentLevel)  throws ThinklabException {
		TextSelector ret = new TextSelector(property, indentLevel);
		ret.setWidth("100%");
		return ret;
	}

}
