package org.integratedmodelling.modelling.visualization;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ObsVisualizer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JMenuItem jMenuItem0;
	private JMenu jMenu0;
	private JMenuBar jMenuBar0;
	private JLabel jLabel0;
	private JToolBar jToolBar0;
	private JPanel visPanel;
	private JTree obsTree;
	private JScrollPane jScrollPane0;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public ObsVisualizer() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabel0(), new Constraints(new Bilateral(0, 0, 41), new Leading(437, 10, 10)));
		add(getJScrollPane0(), new Constraints(new Leading(0, 150, 10, 10), new Leading(0, 431, 12, 12)));
		add(getVisPanel(), new Constraints(new Bilateral(151, 10, 586), new Leading(3, 428, 10, 10)));
		setJMenuBar(getJMenuBar0());
		setSize(747, 485);
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getObsTree());
		}
		return jScrollPane0;
	}

	private JTree getObsTree() {
		if (obsTree == null) {
			obsTree = new JTree();
			DefaultTreeModel treeModel = null;
			{
				DefaultMutableTreeNode node0 = new DefaultMutableTreeNode("JTree");
				{
					DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("colors");
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("blue");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("violet");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("red");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("yellow");
						node1.add(node2);
					}
					node0.add(node1);
				}
				{
					DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("sports");
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("basketball");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("soccer");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("football");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("hockey");
						node1.add(node2);
					}
					node0.add(node1);
				}
				{
					DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("food");
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("hot dogs");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("pizza");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("ravioli");
						node1.add(node2);
					}
					{
						DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("bananas");
						node1.add(node2);
					}
					node0.add(node1);
				}
				treeModel = new DefaultTreeModel(node0);
			}
			obsTree.setModel(treeModel);
		}
		return obsTree;
	}

	private JPanel getVisPanel() {
		if (visPanel == null) {
			visPanel = new JPanel();
			visPanel.setDoubleBuffered(false);
			visPanel.setInheritsPopupMenu(true);
			visPanel.setLayout(new BoxLayout(visPanel, BoxLayout.LINE_AXIS));
			visPanel.add(getJToolBar0());
		}
		return visPanel;
	}

	private JToolBar getJToolBar0() {
		if (jToolBar0 == null) {
			jToolBar0 = new JToolBar();
		}
		return jToolBar0;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Idle.");
			jLabel0.setVerticalAlignment(SwingConstants.BOTTOM);
		}
		return jLabel0;
	}

	private JMenuBar getJMenuBar0() {
		if (jMenuBar0 == null) {
			jMenuBar0 = new JMenuBar();
			jMenuBar0.add(getJMenu0());
		}
		return jMenuBar0;
	}

	private JMenu getJMenu0() {
		if (jMenu0 == null) {
			jMenu0 = new JMenu();
			jMenu0.setText("jMenu0");
			jMenu0.add(getJMenuItem0());
		}
		return jMenu0;
	}

	private JMenuItem getJMenuItem0() {
		if (jMenuItem0 == null) {
			jMenuItem0 = new JMenuItem();
			jMenuItem0.setText("jMenuItem0");
		}
		return jMenuItem0;
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	
	
	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ObsVisualizer frame = new ObsVisualizer();
				frame.setDefaultCloseOperation(ObsVisualizer.EXIT_ON_CLOSE);
				frame.setTitle("ObsVisualizer");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
