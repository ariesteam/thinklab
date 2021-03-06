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
package org.integratedmodelling.thinkscape;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.thinkscape.interfaces.IPopupMenuContainer;

public class BrowseMenuContainer implements IPopupMenuContainer {
	protected JPopupMenu browsePopup=null;
	protected ONode popupNode = null;
	
	protected JMenuItem expandItem;
	protected JMenuItem hideItem;
	protected JMenuItem localItem ;
	protected JMenuItem showMoreMenuItem;
	protected JMenuItem  showLessMenuItem;
	
	public BrowseMenuContainer() {

		expandItem = new JMenuItem("Expand node");
		expandItem.addActionListener(expandNodeAction);
		hideItem = new JMenuItem("Hide node");
		hideItem.addActionListener(hideNodeAction);
		localItem = new JMenuItem("Show All");
		localItem.addActionListener(showAllAction);
		ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
		showMoreMenuItem = new JMenuItem("Show More");
		showMoreMenuItem.addActionListener(gui.showMoreAction);
		showLessMenuItem = new JMenuItem("Show Less");
		showLessMenuItem.addActionListener(gui.showLessAction);
		
	}
	
	public void setPopupMenu( JPopupMenu menu) {
		browsePopup=menu;
	}
	
	
	public JPopupMenu getPopupMenu(ONode node) {
		
		popupNode = node;
		if (browsePopup == null) {			
			
			browsePopup = new JPopupMenu();
			browsePopup.add(expandItem);			
			browsePopup.add(hideItem);			
			browsePopup.add(localItem);
			browsePopup.addSeparator();
			
			
			browsePopup.add(showMoreMenuItem);			
			browsePopup.add(showLessMenuItem);

			// menuItem = new JMenuItem("Toggle Controls");
			// //menuItem.addActionListener(esdOntoBrowser.toggleControlsAction);
			// nodePopup.add(menuItem);

			browsePopup.addPopupMenuListener(new PopupMenuListener() {
				public void popupMenuCanceled(PopupMenuEvent e) {
				}

				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					// display.setMaintainMouseOver(false);
					// display.setMouseOverN(null);
					// display.repaintAfterMove();
					// obNodeHintUI.activate();
				}

				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				}
			});
		}
		return browsePopup;
	}
	
	private ActionListener expandNodeAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			gui.expandNode(popupNode);
			// refreshGraph();
			gui.runUpdate();
			if (!gui.doLayout)
				gui.getDisplay().getVisualization().run(gui.getDisplay().staticLayout);
		}
	};

	private ActionListener hideNodeAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			gui.hideNode(popupNode);
			// refreshGraph();
			gui.runUpdate();
		}
	};

	private ActionListener showAllAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			gui.showAll();
			gui.runUpdate();
		}
	};
	
	

}
