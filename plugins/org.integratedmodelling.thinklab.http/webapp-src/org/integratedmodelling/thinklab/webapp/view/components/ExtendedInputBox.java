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
import java.util.Collection;

import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

/**
 * Input box with an add button; when button is clicked, another box is added below it
 * with a clickable "AND" label that becomes an "OR" when clicked.
 * @author Ferdinando Villa
 *
 */
public class ExtendedInputBox extends Vbox {

	private static final long serialVersionUID = -7494108915607610221L;
	private int width = 240;
	private String connector = "AND";
	
	private String fieldStyle = "";
	private String labelStyle = "";
	
	ArrayList<Textbox> fields = new ArrayList<Textbox>();
	ArrayList<Label>   labels = new ArrayList<Label>();
	
	public class AddButton extends Toolbarbutton {

		private static final long serialVersionUID = -3564471403562531784L;

		public AddButton(String image) {
			super();
			setImage(image);
		}
		
		public void onClick() {
			addField();
		}
	}
	
	public class ConnectorLabel extends Label {
		
		private static final long serialVersionUID = -5866824827758747356L;
		
		public ConnectorLabel() {
			super();
			setStyle(getLabelStyle());
			setValue(getConnector());
			setWidth("48px");
		}
		
		public void onClick() {
			setConnector(getValue().equals("AND") ? "OR" : "AND");
		}
	}
	
	public ExtendedInputBox() {
		super();
	}
	
	/**
	 * For API use (only possible so far)
	 * @param width
	 * @param fieldStyle
	 * @param labelStyle
	 */
	public ExtendedInputBox(int width, String fieldStyle, String labelStyle) {
		super();
		this.width = width;
		this.fieldStyle = fieldStyle;
		this.labelStyle = labelStyle;
		setup();
	}
	
	public void setFieldStyle(String s) {
		fieldStyle = s;
	}
	
	public void setLabelStyle(String s) {
		labelStyle = s;
	}
	
	public String getFieldStyle() {
		return fieldStyle;
	}
	
	public String getLabelStyle() {
		return labelStyle;
	}
	public void setConnector(String s) {
		connector = s;
		for (Label l : labels) {
			l.setValue(s);
		}
	}

	public void setup() {

		/*
		 * start with one input box and the plus button
		 */
		Hbox hb = new Hbox();
		
		Textbox tb = new Textbox();
		tb.setStyle(fieldStyle);
		tb.setWidth((width - 32) + "px");
		hb.appendChild(tb);
		hb.appendChild(new AddButton("/images/icons/Add16.png"));
		
		fields.add(tb);
		
		this.appendChild(hb);
	}
	
	public void addField() {
		
		Hbox hb = new Hbox();
		
		Textbox tb = new Textbox();
		tb.setStyle(fieldStyle);
		tb.setWidth((width - 64) + "px");
		Label l = new ConnectorLabel();
		hb.appendChild(l);
		hb.appendChild(tb);
		hb.appendChild(new AddButton("/images/icons/Add16.png"));

		fields.add(tb);
		labels.add(l);
		
		this.appendChild(hb);
	}
	
	public Collection<String> getValues() {
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for (Textbox t : fields) {
			String v = t.getValue().trim();
			if (!v.equals(""))
				ret.add(v);
		}
		
		return ret;
	}
	
	public String getConnector() {
		return connector;
	}
}
