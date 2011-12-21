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

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.thinkscape.interfaces.ICommandReceptor;

public class ShellPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField commandTextField = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JTextArea sessionTextArea = null;
	private JButton doButton = null;
	
	/**
	 * This is the default constructor
	 */
	public ShellPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabel1 = new JLabel();
		jLabel1.setBounds(new Rectangle(17, 52, 192, 18));
		jLabel1.setText("Session");
		jLabel = new JLabel();
		jLabel.setBounds(new Rectangle(15, 4, 194, 17));
		jLabel.setText("Type Command");
		this.setSize(218, 418);
		this. setPreferredSize(new java.awt.Dimension(218, 418));
		 
		this.setLayout(null);
		this.setToolTipText("Submit Command");
		this.add(getCommandTextField(), null);
		this.add(jLabel, null);
		this.add(jLabel1, null);
		this.add(getSessionTextArea(), null);
		this.add(getDoButton(), null);
		 
		 
	}
	
	public ICommandReceptor getICommandReceptor(){
		return (ICommandReceptor)ApplicationFrame.getApplicationFrame().krPolicy;
	}
	

		/**
	 * This method initializes commandTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCommandTextField() {
		if (commandTextField == null) {
			commandTextField = new JTextField();
			commandTextField.setBounds(new Rectangle(11, 25, 167, 24));
			commandTextField.addKeyListener(new java.awt.event.KeyAdapter() {   
				public void keyPressed(java.awt.event.KeyEvent e) {    
					if (e.getKeyCode()==KeyEvent.VK_ENTER) {
						 
							 submitCommand();		
						 
						
					}
				}
 
			});
		}
		return commandTextField;
	}

	/**
	 * This method initializes sessionTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getSessionTextArea() {
		if (sessionTextArea == null) {
			sessionTextArea = new JTextArea();
			sessionTextArea.setBounds(new Rectangle(13, 74, 191, 327));
			sessionTextArea.setLineWrap(true);
		}
		return sessionTextArea;
	}

	/**
	 * This method initializes doButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDoButton() {
		if (doButton == null) {
			doButton = new JButton();
			doButton.setBounds(new Rectangle(183, 26, 24, 21));
			doButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			doButton.setActionCommand("");
			doButton.setToolTipText("Execute Commnd");
			doButton.setText("");
			doButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					submitCommand();
				}
			});
		}
		return doButton;
	}
	
	private void submitCommand(){
		submitCommand(getCommandTextField().getText());		 
	}

	public void submitCommand(String cmd){
		getICommandReceptor().submitCommand(cmd);
		getSessionTextArea().append("\n >"+ cmd);
		getCommandTextField().setText("");
	}
	
	public void displayOutput(String result) {
		getSessionTextArea().append("\n >>"+result);

	}

	public void appendOutput(String result) {
		getSessionTextArea().append(result);
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
