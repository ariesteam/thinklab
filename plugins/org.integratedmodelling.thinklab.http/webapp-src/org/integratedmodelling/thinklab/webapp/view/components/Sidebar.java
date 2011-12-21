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

import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabWebSession;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.integratedmodelling.thinklab.webapp.ZK.ZKComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;

public class Sidebar extends ThinkcapComponent {

	private static final long serialVersionUID = -1069963740614184101L;
	private ArrayList<SidebarModule> modules = new ArrayList<SidebarModule>();
	private boolean initialized = false;
	private int headerSize = 32;
	private int sHeight = -1;
	private int sWidth = -1;
	
	// auto-wire
	private Vbox contents = null;
	private String headerStyle ="";
	private String labelStyle = "";
	private String disabledLabelStyle = "";
	
	ThinklabWebSession session = null;
	ThinklabWebApplication application = null;

	public Sidebar() {
		super();
		// get these while we are in the main thread.
		session = ThinkWeb.getThinkcapSession(Sessions.getCurrent());
		application = session.getApplication();
	}
	
	public class ModuleHeader {
		
		private static final long serialVersionUID = -3523264992585282208L;
		SidebarModule module;

		String rightImage = null;
		String labelClass = null;
		Component _current;
		ZKComponent right = null;
		
		ModuleHeader(SidebarModule m) {
			this.module = m;
			m.header = this;
		}
		
		/**
		 * Set the right div (normally empty and invisible) to the passed content. Pass a null
		 * to simply empty it.
		 * 
		 * @param zk
		 */
		public void setRightImage(String image) {
			this.rightImage = image;
		}
		
		public void setRightComponent(ZKComponent zk) {
			this.right = zk;
			redisplay();
		}
		
		public void redisplay() {

			if (_current != null) {
				Label lb = (Label) ZK.getComponentById(_current, module.getId() + "_label");
				lb.setSclass(labelClass == null ? 
						(module.isEnabled ? labelStyle : disabledLabelStyle) :
								labelClass);

				Div   rt = (Div) ZK.getComponentById(_current, module.getId() + "_rightdiv");
				ZK.resetComponent(rt);
				if (right != null)
					rt.appendChild(right.get());
			}
		}
		
		public ZK.ZKComponent getContent() {
			
			ArrayList<ZK.ZKComponent> components = new ArrayList<ZK.ZKComponent>();
			
			components.add(
					ZK.label((module.isOpen ? "\u25BC " : "\u25BA ") + module.headerLabel)
						.sclass(
							labelClass == null ? 
								(module.isEnabled ? labelStyle : disabledLabelStyle) :
								labelClass)
						.id(module.getId() + "_label").height(headerSize).hand()
						.listener("onClick", new EventListener() {
							@Override
							public void onEvent(Event arg0) throws Exception {
								module.toggleOpen();
								Sidebar.this.redisplay();
							}
						})
						.listener("onDoubleClick", new EventListener() {
							@Override
							public void onEvent(Event arg0) throws Exception {
								closeAllBut(module);
								Sidebar.this.redisplay();							}
						}));
			
			components.add(ZK.div(rightImage == null ? null : ZK.image(rightImage).tmargin(2).rmargin(2)).
								  id(module.getId() + "_rightdiv").
								  width("100%").align("right"));
			
			ZK.ZKComponent ret = ZK.hbox(
					components.toArray(new ZK.ZKComponent[components.size()])
			   ).width(sWidth).height(headerSize).sclass(headerStyle);
			
			_current = ret.get();
			
			ZK.setupEventListeners(_current, this);
			
			return ret;

		}

		public void setLabelClass(String string) {
			this.labelClass = string;
		}
	}

	public void setMaxHeight(int height) {
		this.sHeight = height;
	}

	public void closeAllBut(SidebarModule module) {
		for (SidebarModule m : modules) {
			if (m.getId().equals(module.getId())) {
				m.isOpen = true;
			} else if (m.headerLabel != null) {
				m.isOpen = false;
			}
		}
	}

	@Override
	public void setWidth(String width) {
		if (!width.endsWith("px")) {
			throw new ThinklabRuntimeException("sidebar height must be given in explicit pixels");
		}
		this.sWidth = Integer.parseInt(width.substring(0, width.length()-2));
	}

	public void openModule(String moduleId) {
		redisplay();
	}

	public int getModuleIndex(String moduleId) {
		int ret = -1; int i = 0;
		for (SidebarModule m : modules) {
			if (m.getId().equals(moduleId)) {
				ret = i;
				break;
			}
			i++;
		}
		return ret;
	}

	@Override
	public void initialize() {
		setMinheight(sHeight);
		setMinwidth(sWidth);
		setContent(ZK.vbox().valign("top").spacing(0).id("contents").width(sWidth));
	}

	public void addModule(SidebarModule module) {
		
		module.sidebar = this;
		modules.add(module);
		module.header = new ModuleHeader(module);
		
		if (module.headerLabel == null) {
			// force open, or we'll never see it
			module.isOpen = true;
		}
		if (initialized) {
			redisplay();
		}
	}
	
	public void resetModules(ArrayList<SidebarModule> modules) {
		modules.clear();
		for (SidebarModule m : modules) {
			addModule(m);
		}
		redisplay();
	}

	public void resetModules() {
		modules.clear();
		redisplay();
	}

	public void redisplay() {
		
		ZK.resetComponent(contents);
		
		int[] sizes = computeSizes();
		int h = 0;
		
		for (int i = 0; i < modules.size(); i++) {
			
			SidebarModule module = modules.get(i);
			
			if (module.headerLabel != null) {
				if ((h + headerSize) > sHeight)
					break;
				contents.appendChild(module.header.getContent().get());
				h += headerSize;
			}
			
			if (module.isOpen) {
				if (sizes[i] != 0) {
					if ((h + sizes[i]) > sHeight)
						break;
					
					module.notifySize(sWidth, sizes[i]);
					module.display();
					module.setHeight(sizes[i]+"px");
					contents.appendChild(module);
					h += sizes[i];
				} else {
					module .notifySize(sWidth,0);
				}
			} else {
				module .notifySize(sWidth,0);
			}
		}
		
		if (sizes.length > modules.size()) {
			// pad empty space
			contents.appendChild(ZK.div().width(sWidth-4).height(sizes[sizes.length - 1]).get());
		}
	}

	private int[] computeSizes() {
		
		
		if (sHeight < 0 || sWidth < 0) {
			throw new ThinklabRuntimeException("sidebar dimensions must be explicit");			
		}
		
		int available = sHeight;
		int toh = 0; // total desired height of all open panels
		int nOpen = 0;
		/*
		 * subtract all headers, sum up open heights
		 */
		for (SidebarModule m : modules) {
			if (m.headerLabel != null)
				available -= headerSize;
			if (m.isOpen) {
				toh += m.minHeight;
				nOpen++;
			}
		}

		// add the size of a padding div if all are closed
		int[] ret = new int[modules.size() + (nOpen > 0 ? 0 : 1)];

		/*
		 * return all closed, the display func will only show as many as 
		 * possible. TODO show an arrow or sth to show the remainder.
		 */
		if (available < 0)
			return ret;
		
		/*
		 * compute allowed size for each component; if only one is open, it takes all the available
		 * space even if it's more than it wants; if > 1 is open,each gets as much of their wish as 
		 * possible, proportionally to the desired heights.
		 */
		int i = 0; 
		for (SidebarModule m : modules) {
			if (m.isOpen) {
				ret[i] = 
					nOpen == 1 ? 
						available : 
						(int)Math.floor((double)available * ((double)m.minHeight/(double)toh));
			}
			i++;
		}
		
		if (nOpen == 0)
			ret[i] = available;

		return ret;
	}

	public void setHeaderSclass(String style) {
		this.headerStyle = style;
	}
	
	public void setHeaderSize(int n) {
		headerSize = n;
	}
	
	public void setLabelSclass(String styleEnabled, String styleDisabled) {
		this.labelStyle = styleEnabled;
		this.disabledLabelStyle = styleDisabled;
	}
}