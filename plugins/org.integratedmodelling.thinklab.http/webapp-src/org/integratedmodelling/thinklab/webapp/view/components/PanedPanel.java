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

import org.integratedmodelling.thinklab.webapp.ZK;
import org.integratedmodelling.thinklab.webapp.ZK.ZKComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

public class PanedPanel extends ThinkcapComponent {

	private static final long serialVersionUID = 358945569924937100L;

	protected String headerBackgroundClass = "panel_header";
	protected String disabledTextClass = "panel_title_disabled";
	protected String enabledTextClass = "panel_title_enabled";

	// single main (top left) button if any of these are defined
	String buttonImage = "";
	String buttonTooltip = "";
	boolean buttonEnabled = false;
	
	private static final int INITIAL_PANE = -1000;
	
	class Pane {
		Window window;
		String name;
		String label;
		String button;
		String tooltip;
	}
	
	// <0 for button panes, >= 0 for main panes
	int activePane = INITIAL_PANE;
	// the main pane that is active, or should be visualized by clicking on its name when the active
	// one is a button pane
	int currentMainPane = 0;
	
	ArrayList<Pane> panes = new ArrayList<Pane>();
	ArrayList<Pane> bpanes = new ArrayList<Pane>();
	
	@Override
	public void initialize() {
		display();
	}
	
	/**
	 * To be called in constructor, before initialize().
	 * @param name
	 * @param label
	 * @return
	 */
	public ThinkcapComponent addPane(String name, String label, String tooltip) {
		
		ThinkcapWindow w = new ThinkcapWindow();
		w.setWidth("100%");
		w.setVisible(false);
		
		Pane pane = new Pane();
		pane.window = w;
		pane.label = label;
		pane.tooltip = tooltip;
		pane.name = name;
		
		panes.add(pane);
		
		return w;
	}

	/**
	 * The button on the title is disabled by default. Calling this
	 * will define its image, enabled/disabled status, and tooltip.
	 * The onButtonClicked() callback will be called whenever an 
	 * enabled button is pressed.
	 * 
	 * @param imageUrl
	 * @param enabled
	 * @param tooltip
	 */
	public void setButton(String imageUrl, boolean enabled, String tooltip) {
		
		this.buttonImage = imageUrl;
		this.buttonEnabled = enabled;
		this.buttonTooltip = tooltip;
			
		display();
	}
	
	public class PaneSwitcher implements EventListener {

		int pane = 0;
		PaneSwitcher(int n) { this.pane = n; }
		public PaneSwitcher(int activePane, int i) {
			int p = activePane + i;
			if ((p >= 0 && p < panes.size() - 1) ||
				 (p < 0 && -p < bpanes.size() - 1))
				pane = p;
			else
				pane = activePane;
		}
		@Override
		public void onEvent(Event arg0) throws Exception {
			activePane = pane;
			if (pane >= 0)
				currentMainPane = pane;
			display();
		}
	}
	
	public void setCurrentPane(String s) {
		for (int i = 0; i < panes.size(); i++)
			if (panes.get(i).name.equals(s)) {
				activePane = i;
				break;
			}
		if (activePane >= 0)
			currentMainPane = activePane;
		
		display();
	}
	
	public void removeAllPanes() {
		panes.clear();
		activePane = INITIAL_PANE;
		currentMainPane = 0;
		display();
	}
	
	public void setHeaderClass(String panelHeaderClass) {
		headerBackgroundClass = panelHeaderClass;
	}
	/**
	 * 
	 * @param name
	 * @param buttonImage
	 * @param tooltip
	 * @return
	 */
	public ThinkcapComponent addButtonPane(String name, String buttonImage, String tooltip) {
		
		ThinkcapWindow w = new ThinkcapWindow();
		w.setVisible(false);
		w.setWidth("100%");
		Pane pane = new Pane();
		pane.window = w;
		pane.button = buttonImage;
		pane.tooltip = tooltip;
		pane.name = name;
		bpanes.add(pane);
		return w;
	}
	
	public void display() {
		
		clear();
				
		if (activePane == INITIAL_PANE) {
			if (panes.size() > 0)
				activePane = 0;
			else if (bpanes.size() > 0) {
				activePane = -1;
			}
		} 
		
		/* main button if defined */
		ZKComponent mainbut = null;
		if (buttonImage != null) {
			mainbut = 
				ZK.imagebutton(buttonImage).
					tooltip(buttonTooltip).
					listener("onClick", new EventListener() {
						public void onEvent(Event event) throws Exception {
							onButtonClicked();
			            }
		            }).enable(buttonEnabled);
		}

		ZKComponent alabel = null, back = null, forward = null;
		
		if (panes.size() > 0) {
			
		int shownPane = activePane >= 0 ? activePane : currentMainPane;
			
		/* make label for first or active pane */
		 alabel = 
			ZK.label(panes.get(shownPane).label).
				sclass(shownPane >= 0 ? enabledTextClass : disabledTextClass).
				hand().
				listener("onClick", new PaneSwitcher(shownPane));
				
		/* label for back button */
		 back = 
			ZK.label(panes.size() > 1 ? "\u25C0" : "").
				sclass(shownPane > 0 ? enabledTextClass : disabledTextClass).
				width(12).
				hand(shownPane > 0).
				listener("onClick", shownPane > 0 ? new PaneSwitcher(shownPane -1) : null);
		
		/* label for forward button */
		 forward = 
			ZK.label(panes.size() > 1 ? "\u25BA" : "").
				sclass(shownPane < (panes.size() - 1) ? enabledTextClass : disabledTextClass).
				width(12).
				hand(shownPane < (panes.size() - 1)).
				listener("onClick", 
							(shownPane == panes.size() - 1) ?
								null : 
								new PaneSwitcher(shownPane + 1));
		}
		
		ZKComponent buttons = null;
		if (bpanes.size() > 0) {
			ZKComponent[] buts = new ZKComponent[bpanes.size()];
			
			for (int i = 0; i < bpanes.size(); i++) {
				buts[i] = 
					ZK.imagebutton(bpanes.get(i).button).
						tooltip(bpanes.get(i).tooltip).
						listener("onClick", new PaneSwitcher(-(i+1)));
				}
				buttons = ZK.div(ZK.hbox(buts)).align("right");
		}
		
		setContent(
				ZK.vbox(	
					ZK.hbox(
						mainbut,
						ZK.div(
							back,
							alabel,
							forward).align("left"),
						buttons
					).sclass(headerBackgroundClass).height(16).width("100%"),
				ZK.c(getPane(activePane)).show()
				).width("100%")
			);
	}

	private Window getPane(int pane) {
		
		Window ret = null;
		
		if (pane < 0) {
			ret = bpanes.get(-(pane+1)).window;
		} else {
			ret = panes.get(pane).window;
		}
		return ret;
	}
	
	protected void onButtonClicked() {
		
	}
}
