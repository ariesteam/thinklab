package org.integratedmodelling.thinklab.webapp.view;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.http.ThinklabWebPlugin;
import org.integratedmodelling.thinklab.http.utils.JPFUtils;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.java.plugin.Plugin;
import org.java.plugin.registry.Extension;

/**
 * Read into global catalog by ThinkcapPlugin - used in initialization of ThinkcapPortletContainer
 * to define or load the layout.
 * @author Ferdinando Villa
 *
 */
public class LayoutDescriptor {

	public String id;
	public boolean persistent;
	public Plugin registeringPlugin;
	public ArrayList<Column> columns = new ArrayList<Column>();

	public class PortletD {
		public String id;
		public String title = null;
		public String state = null;
		public boolean collapsible = true;
		public boolean open = true;
		public boolean moveable = true;
		public boolean closable;
	}
	
	public class Column {
		public String width = "100%";
		public String height = "400px";
		public ArrayList<PortletD> portlets = new ArrayList<PortletD>();
	}
	
	public LayoutDescriptor(ThinklabWebPlugin plugin, Extension ext) {
		
		this.registeringPlugin = plugin;
		
		this.id = ext.getParameter("id").valueAsString();
		this.persistent = ext.getParameter("id").valueAsBoolean();
		
		for (Extension.Parameter aext : ext.getParameters("column")) {
			
			Column cl = new Column();

			cl.width = aext.getSubParameter("width").valueAsString();
			cl.height = aext.getSubParameter("height").valueAsString();
			
			for (Extension.Parameter pext : aext.getSubParameters("portlet")) {

				PortletD pl = new PortletD();
				
				pl.title = JPFUtils.getParameter(pext, "title");
				pl.state = JPFUtils.getParameter(pext, "state");
				pl.id = JPFUtils.getParameter(pext, "id");
				pl.collapsible = BooleanValue.parseBoolean(JPFUtils.getParameter(pext, "collapsible", "true"));
				pl.closable = BooleanValue.parseBoolean(JPFUtils.getParameter(pext, "closable", "true"));
				pl.open = BooleanValue.parseBoolean(JPFUtils.getParameter(pext, "open", "true"));
				pl.moveable = BooleanValue.parseBoolean(JPFUtils.getParameter(pext, "moveable", "true"));
				
				cl.portlets.add(pl);
			}
				
			columns.add(cl);
		}
	}
	
	public String getId() {
		return id;
	}

}
