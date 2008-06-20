package org.integratedmodelling.thinkscape.interfaces;

import javax.swing.JPopupMenu;

import org.integratedmodelling.ograph.ONode;

public interface IPopupMenuContainer {
	public JPopupMenu getPopupMenu(ONode node);
	public void setPopupMenu( JPopupMenu menu);

}
