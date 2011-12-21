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

import org.integratedmodelling.thinklab.webapp.view.components.Sidebar.ModuleHeader;

public abstract class SidebarModule extends ThinkcapComponent {
	
	private static final long serialVersionUID = -8258093232425877564L;
	boolean disabled = false;

	String headerLabel;
	String headerLabelClass;
	Sidebar sidebar = null;
	int minHeight = 0;
	boolean isOpen = false;
	protected boolean isEnabled = true;

	public ModuleHeader header;
	private int currentWidth;
	private int currentHeight;
	
	/**
	 * Set the label for the header. If this module has no label, we cannot close it or minimize it
	 * and it will be fixed on the sidebar.
	 * 
	 * @param s
	 */
	public void setHeaderLabel(String s) {
		this.headerLabel = s;
	}

	public void setHeaderLabelClass(String s) {
		this.headerLabelClass = s;
	}
	
	public int getCurrentWidth() {
		return currentWidth;
	}

	public int getCurrentHeight() {
		return currentHeight;
	}
	
	/**
	 * The height we will try to give this module when it is open.
	 * 
	 * @param n
	 */
	public void setMinimumHeight(int n) {
		this.minHeight = n;
	}
	
	public void setDisabled(boolean b) {
		this.disabled = b;
	}

	public void setOpen(boolean b) {
		this.isOpen = b;
		sidebar.openModule(getId());
	}

	public void toggleOpen() {
		isOpen = !isOpen;
	}
	
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * This is called when the module is displayed. Height will be > 0 only when the module
	 * is open, and not necessarily.
	 * 
	 * @param w
	 * @param h
	 */
	void notifySize(int w, int h) {
		this.currentWidth = w;
		this.currentHeight = h;
	}
	
	/**
	 * get the header component.
	 */
	public ModuleHeader getHeader() {
		return header;
	}

	/**
	 * Override this one if you want to control display every time the module is 
	 * opened.
	 */
	public void display() {
	}

}
