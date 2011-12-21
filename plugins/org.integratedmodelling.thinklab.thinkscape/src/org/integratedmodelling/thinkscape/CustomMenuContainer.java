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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.policy.ApplicationFrame;

public class CustomMenuContainer extends BrowseMenuContainer {
	
	
	protected JMenuItem rapuInstanceItem;
	protected JMenuItem rapuRelationItem;
	protected JMenuItem retriverClassInstItem ;
	protected JMenuItem baahhhItem;
	protected JMenuItem additionalItem;
	
	//this is just to show how to pass arguments to the action listener
	protected String name=null;
	 
	
	public CustomMenuContainer() {
		rapuInstanceItem = new JMenuItem("Rapu Instance");
		rapuInstanceItem.addActionListener(rapuInstanceAction);
		
		rapuRelationItem = new JMenuItem("Rapu Relations");
		rapuRelationItem.addActionListener(rapuRelationAction);
		
		retriverClassInstItem = new JMenuItem("Retrive Class Instances");
		retriverClassInstItem.addActionListener(retriveClassInstAction);
		
		baahhhItem = new JMenuItem("Baahhh");
		baahhhItem.addActionListener(baahhAction);
		
	}
	
	private ActionListener rapuInstanceAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			JOptionPane.showMessageDialog(gui, "Instance " +name+" has been rapued");


		}
	};
	
	private ActionListener rapuRelationAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			JOptionPane.showMessageDialog(gui, "Relation " +name+"has been rapued");


		}
	};
	
	private ActionListener retriveClassInstAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			JOptionPane.showMessageDialog(gui, "Class Instances of " +name+" have been rapued");


		}
	};
	
	private ActionListener baahhAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ThinkScapeGUI gui = (ThinkScapeGUI)ApplicationFrame.getApplicationFrame().guiPolicy;
			JOptionPane.showMessageDialog(gui, "baahh!!!");


		}
	};
	
	public JPopupMenu getPopupMenu(ONode node){
		int type =node.getType();
		switch(type) {
		case ONode.OBJ_CLASS:
			return getClassPopupMenu(node);
		case ONode.OBJ_INDIVIDUAL:
			return getInstancePopupMenu(node);
		case ONode.REL_OBJECT_PROPERTY_VALUE:
			return getRelationPopupMenu(node);
		
		}
		return null;
	}
	
	//here you can create menu you want for class. the items of BrowseMenuContainer are avalible
	//too. they can be obtained in bulk by calling browsePopup=null; super.getPopupMenu(node); after which browsePopup
	//has all of them. they are also avalible itemwise and can be inserted in custom menu in any order.
	
	public JPopupMenu getClassPopupMenu(ONode node){
		browsePopup=null;
		super.getPopupMenu(node);
		name= node.getName();
		retriverClassInstItem.setName("Retrive instances of"+node.getName());		
		browsePopup.insert( retriverClassInstItem,0);
		return browsePopup;
	}
	
	public JPopupMenu getRelationPopupMenu(ONode node){
		browsePopup=null;
		super.getPopupMenu(node);
		name= node.getName();
		rapuRelationItem.setName("Rapu Relation"+node.getName());
		browsePopup.addSeparator();
		browsePopup.add(rapuRelationItem);
		return browsePopup;
	}
	
	public JPopupMenu getInstancePopupMenu(ONode node){
		browsePopup=null;
		super.getPopupMenu(node);
		name= node.getName();
		rapuInstanceItem.setName("Rapu Instance"+node.getName());
		JMenu submenu= new JMenu("Instance Submenu");
		submenu.add(rapuInstanceItem);
		submenu.add(baahhhItem);		
		browsePopup.insert( submenu,0);
		return browsePopup;
	}

}
