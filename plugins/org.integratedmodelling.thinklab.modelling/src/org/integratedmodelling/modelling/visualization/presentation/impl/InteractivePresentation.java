package org.integratedmodelling.modelling.visualization.presentation.impl;

import javax.swing.JPanel;

import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import javax.swing.JMenuBar;
import java.awt.GridLayout;
import javax.swing.JTabbedPane;

public class InteractivePresentation extends JPanel implements IPresentation {

	private static final long serialVersionUID = 5885297952918209745L;

	/**
	 * Create the panel.
	 */
	public InteractivePresentation() {
		
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		add(menuBar);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);

	}

	@Override
	public void render() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
