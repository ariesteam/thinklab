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

// import FishEyeMenu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.integratedmodelling.filter.OntFilter;
import org.integratedmodelling.growl.GMapBrowsePanel;
import org.integratedmodelling.growl.GrOWLDisplay;
import org.integratedmodelling.growl.GraphException;
import org.integratedmodelling.growl.GraphUtil;
import org.integratedmodelling.growl.GrowlGUI;
import org.integratedmodelling.growl.KRPolicyOWL;
import org.integratedmodelling.growl.LabelFactory;
import org.integratedmodelling.growl.LayoutIO;
import org.integratedmodelling.growl.MetadataPanel;
import org.integratedmodelling.growl.MouseHoverControl;
import org.integratedmodelling.growl.ObjectPropertyPanel;
import org.integratedmodelling.growl.RelationPropertyPanel;
import org.integratedmodelling.growl.editor.ClassViewer;
import org.integratedmodelling.growl.editor.EditToolsPanel;
import org.integratedmodelling.growl.editor.EditToolsRBox;
import org.integratedmodelling.growl.editor.GMapEditPanel;
import org.integratedmodelling.growl.editor.History;
import org.integratedmodelling.growl.editor.IncrMatchComboBox;
import org.integratedmodelling.growl.editor.reasoner.ReportPanel;
import org.integratedmodelling.ograph.OEdge;
import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.ograph.ONodeLib;
import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.policy.KRPolicyWrite;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinkscape.interfaces.IPopupMenuContainer;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.util.ui.JForcePanel;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * @author skrivov
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ThinkScapeGUI extends JPanel implements GrowlGUI {
	// status values for int owlDLCompliance(arg1, arg2),
	// suffix 11 means both arg1, arg2 !=null, suffix 10 means ard2==null
	
	private static final String graphs = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    
	public static final int OPTION_FAIL = -10;

	public static final int OPTION_EDGE = -20;

	public static final int OPTION_RNODE = -30;

	public static final int EDITING_OBJECT = 100;

	public static final int EDITING_RELATION = 200;

	public static final int EDITING_EDGE = 300;

	public static final int TOOL_BROWSE = 0;

	public static final int TOOL_DELETE = -1;

	public static final int TOOL_CLASS = 2;

	public static final int TOOL_DATATYPE = 3;

	public static final int TOOL_INDIVIDUAL = 4;

	public static final int TOOL_DATA_VALUE = 5;

	public static final int TOOL_EDGE_ISA = 6;

	public static final int TOOL_EDGE_EQVIVALENT = 7;

	public static final int TOOL_PROPERTY = 8;

	public static final int TOOL_PROPERTY_VALUE = 9;

	public static final int TOOL_DATA_PROPERTY = 10;

	public static final int TOOL_DATA_PROPERTY_VALUE = 11;

	public static final int TOOL_RESTRICTION_SOME = 12;

	public static final int TOOL_RESTRICTION_ALL = 13;

	public static final int TOOL_RESTRICTION_NUMBER = 14;

	public static final int TOOL_RESTRICTION_VALUE = 15;

	public static final int TOOL_DATA_RESTRICTION_SOME = 16;

	public static final int TOOL_DATA_RESTRICTION_ALL = 17;

	public static final int TOOL_DATA_RESTRICTION_NUMBER = 18;

	public static final int TOOL_DATA_RESTRICTION_VALUE = 19;

	public static final int TOOL_OR = 20;

	public static final int TOOL_AND = 21;

	public static final int TOOL_NOT = 22;

	public static final int TOOL_ONE_OF = 23;

	public static final int TOOL_EQVIVALENT = 24;

	public static final int TOOL_DISJOINT = 25;

	public static final int TOOL_INDIVIDUALS_EQUAL = 26;

	public static final int TOOL_INDIVIDUALS_DIFFERENT = 27;

	public static final int TOOL_INVERSE_OF = 28;

	public static int ALL = 0;

	public static int ABOX = 1;

	public static int TBOX = 2;

	public static int HIERARCH = 3;

	public static int HIERARCH_INST = 4;

	public static int RBOX = 5;

	public static int LIST_CLASSES = 0;

	public static int LIST_INSTANCES = 1;

	public static int LIST_RELATIONS = 2;

	protected OGraph graph = null;

	// protected boolean objPaneIsActive = true;
	public int activePropPanel = 0;

	public static int OBJ_PANEL = 0;

	public static int REL_PANEL = 1;
	
	public static int REP_PANEL = 2;

	public static int GMAP_B_PANEL = 3;

	public static int GMAP_E_PANEL = 4;

	public boolean suppressSelectedEvent = false;

	protected ObjectPropertyPanel objPanel = null;

	protected RelationPropertyPanel relPanel = null;
	
	protected ReportPanel repPanel=null;

	protected GMapBrowsePanel gmapBPanel = null;

	protected GMapEditPanel gmapEPanel = null;

	//public ApplicationFrame appFrame = null;

	protected GrOWLDisplay display = null;

	protected javax.swing.JScrollBar hScrollBar = null;

	protected javax.swing.JScrollBar vScrollBar = null;

	protected javax.swing.JToolBar jToolBar = null;

	protected javax.swing.JButton showMoreButton = null;

	protected javax.swing.JButton showLessButton = null;

	protected javax.swing.JButton showTBoxButton = null;

	protected javax.swing.JButton viewAllButton = null;

	protected javax.swing.JButton viewABoxButton = null;

	protected javax.swing.JButton viewHierarchyButton = null;

	protected javax.swing.JButton viewHierarchInstButton = null;

	protected javax.swing.JButton toggleRBoxButton = null;

	
	private JPopupMenu backPopup = null;

	ONode popupNode = null;

	boolean deleteOnLClick = false;

	private HashMap filterHash = null;

	public int editingItem = 100;

	public int editToolPressed = 0;

	public int createNodeType;

	public int geType;

	boolean doLayout = false;

	ImageIcon stopLayoutIco = null;

	ImageIcon doLayoutIco = null;

	ImageIcon editIco = null;

	ImageIcon browseIco = null;

	ImageIcon viewMetadataIco = null;

	ImageIcon viewGraphIco = null;

	public boolean editMode = false;

	private JPopupMenu nodeEditPopup = null;

	private JPopupMenu backEditPopup = null;

	private boolean mdataPanelIsActive = false;

	private boolean loading = false;

	private boolean inNotification;

	private MetadataPanel mdataPanel = null;

	private File currentFile = null;

	// @jve:visual-info decl-index=0 visual-constraint="664,233"
	private int mode = 0;

	private int lastSplit = -1;

	private boolean controlsVisible = true;

	// private OWLOntology ontology = null;

	// private Set includeOntologies = null;

	private JSplitPane splitPane = null;

	private ClassViewer selectPanel = null;

	//private PrefixSearchPanel searcher = null;

	// @jve:visual-info decl-index=0 visual-constraint="229,143"
	private javax.swing.JButton zoomInButton = null;

	private javax.swing.JButton zoomOutButton = null;

	private javax.swing.JMenuBar mainMenuBar = null;

	// @jve:visual-info decl-index=0 visual-constraint="362,346"
	private javax.swing.JMenu fileMenu = null;

	private javax.swing.JMenuItem loadMenuItem = null;

	private javax.swing.JFileChooser fileChooser = null;

	// @jve:visual-info decl-index=0 visual-constraint="668,4"

	private javax.swing.JMenuItem saveOWLRDFMenuItem = null;

	private javax.swing.JMenuItem saveAbstractMenuItem = null;

	private javax.swing.JMenuItem exitMenuItem = null;

	private javax.swing.JPopupMenu editPopup = null;

	private IncrMatchComboBox classSelectCombo = null;
	
	//from GrOWLGUI
//	protected int editingItem;
//	protected boolean deleteOnLClick;
//	protected int editToolPressed;
//	protected int createNodeType;
	public int getCreateNodeType() {
		return createNodeType;
	}

	public void setCreateNodeType(int createNodeType) {
		this.createNodeType = createNodeType;
	}

	public boolean isDeleteOnLClick() {
		return deleteOnLClick;
	}

	public void setDeleteOnLClick(boolean deleteOnLClick) {
		this.deleteOnLClick = deleteOnLClick;
	}

	public int getEditingItem() {
		return editingItem;
	}

	public void setEditingItem(int editingItem) {
		this.editingItem = editingItem;
	}

	public int getEditToolPressed() {
		return editToolPressed;
	}

	public void setEditToolPressed(int editToolPressed) {
		this.editToolPressed = editToolPressed;
	}

	// @jve:visual-info decl-index=0 visual-constraint="356,258"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	// @jve:visual-info decl-index=0 visual-constraint="495,122"
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#initialize()
	 */

	private boolean rBoxIsActive = false;

	private javax.swing.JButton toggleEditButton = null;

	private javax.swing.JLabel jLabel = null;

	private EditToolsPanel editToolboxl = null;

	private EditToolsRBox editToolsRBox = null;

	private javax.swing.JButton jButton = null;

	private javax.swing.JButton layoutButton = null;

	private javax.swing.JMenuItem jMenuItem = null;

	private javax.swing.JButton fileOpenButton = null;

	private javax.swing.JButton fileSaveButton = null;

	private javax.swing.JToolBar navToolBar = null;

	private History history = null;
	private Display overview=null;
	
	public static int clickToCreate = 2;
	public boolean  initialization=false;
	public EditController editControl=null;

	

	// private JPanel propPanel = null;

	public void initializeGUI() {
		//getVisualization();
		
		
		
		// /((RenderGrowl)ApplicationFrame.getApplicationFrame().renPolicy).registry=getRegistry();
		stopLayoutIco = new javax.swing.ImageIcon(getClass().getResource(
				"/org/integratedmodelling/growl/images/stopLayout.png"));
		doLayoutIco = new javax.swing.ImageIcon(getClass().getResource(
				"/org/integratedmodelling/growl/images/doLayout.png"));
		editIco = new javax.swing.ImageIcon(getClass().getResource(
				"/org/integratedmodelling/growl/images/edit.png"));
		browseIco = new javax.swing.ImageIcon(getClass().getResource(
				"/org/integratedmodelling/growl/images/book.png"));
		viewMetadataIco = new javax.swing.ImageIcon(getClass().getResource(
				"/org/integratedmodelling/growl/images/OWL.png"));
		viewGraphIco = new javax.swing.ImageIcon(getClass().getResource(
				"/org/integratedmodelling/growl/images/viewGraph.png"));
		this.setLayout(new java.awt.BorderLayout());
//		JPanel tbarPanel= new JPanel();
//		tbarPanel.setLayout(new  BoxLayout(tbarPanel, BoxLayout.Y_AXIS));
//		tbarPanel.add(getJToolBar());
//		tbarPanel.add(getNavToolBar());
		this.add(getJToolBar(), java.awt.BorderLayout.NORTH);
		this.add(getSplitPane(), java.awt.BorderLayout.CENTER);
		this.add(getEditToolboxl(), java.awt.BorderLayout.WEST);
		// this.add(searcher, java.awt.BorderLayout.SOUTH);
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		// this.getFileChooser();
		// propPanel= new JPanel();
		// propPanel.setLayout(new java.awt.BorderLayout());
		
		getDisplay().initPrefuse();
//		controller to display hints
		MouseHoverControl mouseHoverControl=new MouseHoverControl();
		getDisplay().addControlListener(mouseHoverControl);
		((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).resetGraph();
		graph = ((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).getCGraph();
		
		 getDisplay().setGraph(graph);
		editControl=new EditController();
		objPanel = new ObjectPropertyPanel();
		 
		objPanel.setEditable(false);
		relPanel = new RelationPropertyPanel();
		 
		relPanel.setEditable(false);
		repPanel= new ReportPanel();
		 gmapBPanel = new GMapBrowsePanel();
		 
		gmapEPanel = new GMapEditPanel();
		 
		// gmapBPanel.setVisible(false);
		// propPanel.add(objPanel, java.awt.BorderLayout.SOUTH);
		this.add(objPanel, java.awt.BorderLayout.SOUTH);
		activePropPanel = OBJ_PANEL;
		getEditToolboxl().setVisible(false);
		getEditToolsRBox();
		this.setPreferredSize(new java.awt.Dimension(862, 600));
		this.setBounds(0, 0, 862, 509);
		//getEditPopup(null);
		history = new History(this);
		filterHash=getDisplay().createFilters();
		publishFilters();
		
		  addGrounding(graph); 
		
		
		  
		 setAbsLocation(GraphUtil.getThingNode(),   250, 250);
	     setFocusNode(GraphUtil.getThingNode());
		 
		  
		  centerDisplay();
		//updateHistoryControls();
		//initReasoner();
		  initialization = false;

	}
	
	public void publishFilters(){
		getFilterComboBox().addItem(new String("Fish Eye"));
		getFilterComboBox().addItem(new String("Definitions"));
		getFilterComboBox().addItem(new String("Relations"));
		getFilterComboBox().addItem(new String("Superclasses"));
		getFilterComboBox().addItem(new String("Subclasses"));
		getFilterComboBox().addItem(new String("Instances"));
		getFilterComboBox().addItem(new String("InstancesDef"));
		
	}
	
	public void setEditController() {
		getDisplay().addControlListener(editControl);
	}

	public void removeEditController() {
		getDisplay().removeControlListener(editControl);
	}

	public void setEditController(boolean edMode) {
		if (edMode) {
			getDisplay().removeBrowseController();
			setEditController();
		} else {
			removeEditController();
			getDisplay().setBrowserController();
		}
	}
	
	
	 

	
	private void addGrounding(OGraph g) {
		try {
			((KRPolicyThinkLab)  ApplicationFrame.getApplicationFrame().krPolicy).addGrounding(g);
			if (mdataPanel != null) {
				mdataPanel.setMetadata(ApplicationFrame.getApplicationFrame().krPolicy);
			}
			updateObjectList(LIST_CLASSES);
			updateObjectPropertyList();
			updateDataPropertyList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#setRelationNode(org.integratedmodelling.ontobrowser.OBRelationNode)
	 */
	public void setRelationNode(ONode rn) {
		if (!updateNode())
			return;
		if (activePropPanel != REL_PANEL) {
			removeActivePanel();
			this.add(relPanel, java.awt.BorderLayout.SOUTH);
			activePropPanel = REL_PANEL;
			this.updateUI();
		}
		relPanel.setNode(rn);
	}

	public void setPropertyPanel(ONode n) {
		if (!gmapMode) {
			if (ONodeLib.isObjectNode(n.getType())) {
				setObjectNode(n);
			} else {
				setRelationNode(n);
			}
		} else {
			gmapBPanel.setNode(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#setObjectNode(org.integratedmodelling.ontobrowser.OBObjectNode)
	 */
	public void setObjectNode(ONode on) {
		if (!updateNode())
			return;
		if (activePropPanel != OBJ_PANEL) {
			removeActivePanel();
			this.add(objPanel, java.awt.BorderLayout.SOUTH);
			activePropPanel = OBJ_PANEL;
			this.updateUI();
		}
		objPanel.setNode(on);
		if(doClassTreeSelection)
			selectPanel.positionTo(on);
	}

	// /**
	// * This method initializes hScrollBar
	// *
	// * @return javax.swing.JScrollBar
	// */
	// protected javax.swing.JScrollBar getHScrollBar() {
	// if (hScrollBar == null) {
	// hScrollBar = new javax.swing.JScrollBar();
	// hScrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
	// }
	// return hScrollBar;
	// }
	//	
	// /**
	// * This method initializes vScrollBar
	// *
	// * @return javax.swing.JScrollBar
	// */
	// protected javax.swing.JScrollBar getVScrollBar() {
	// if (vScrollBar == null) {
	// vScrollBar = new javax.swing.JScrollBar();
	// }
	// return vScrollBar;
	// }

	/**
	 * This method initializes zoomInButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getZoomInButton() {
		if (zoomInButton == null) {
			zoomInButton = new javax.swing.JButton();
			zoomInButton.setText("");
			zoomInButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/zoom in-tran.png")));
			zoomInButton.setToolTipText("Zoom In");
			zoomInButton.setPreferredSize(new java.awt.Dimension(24, 24));
			zoomInButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getDisplay().zoomIn();
				}
			});
		}

		return zoomInButton;
	}

	/**
	 * This method initializes zoomOutButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getZoomOutButton() {
		if (zoomOutButton == null) {
			zoomOutButton = new javax.swing.JButton();
			zoomOutButton.setText("");
			zoomOutButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/zoom ou-tran.png")));
			zoomOutButton.setToolTipText("Zoom Out");
			zoomOutButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getDisplay().zoomOut();
						}
					});
		}
		return zoomOutButton;
	}
	
	javax.swing.JButton togleForcePanelButton;
	boolean fpactive=false;
	protected javax.swing.JButton getOpenForcePanelButton() {
		if (togleForcePanelButton == null) {
			togleForcePanelButton = new javax.swing.JButton();
			togleForcePanelButton.setText("FP");
//			openForcePanelButton
//					.setIcon(new javax.swing.ImageIcon(
//							getClass()
//									.getResource(
//											"/org/integratedmodelling/growl/images/zoom ou-tran.png")));
			togleForcePanelButton.setToolTipText("Open Force Panel");
			togleForcePanelButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if(!fpactive) {
							    getSplitPane().setLeftComponent(getForcePanel());
							} else {
								getSplitPane().setLeftComponent(getShellPanel());
							}
							fpactive= !fpactive;
							 
						}
					});
		}
		return togleForcePanelButton;
	}
	
	JForcePanel forcePanel= null;
	
	public JForcePanel getForcePanel() {
		if(forcePanel== null) {
			forcePanel = new JForcePanel(getDisplay().getForceSimulator());
			
		}
		return forcePanel;
	}

	/**
	 * This method initializes showMoreButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getShowMoreButton() {
		if (showMoreButton == null) {
			showMoreButton = new javax.swing.JButton();
			showMoreButton.setText("");
			showMoreButton.setToolTipText("Show More");
			showMoreButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/showmore.png")));
			showMoreButton.addActionListener(showMoreAction);
		}
		return showMoreButton;
	}

	/**
	 * This method initializes showLessButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getShowLessButton() {
		if (showLessButton == null) {
			showLessButton = new javax.swing.JButton();
			showLessButton.setText("");
			showLessButton.setToolTipText("Show Less");
			showLessButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/showless.png")));
			showLessButton.addActionListener(showLessAction);
		}
		return showLessButton;
	}

	public  ActionListener showMoreAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {

			getDisplay().generalFilter.setDistance(getDisplay().generalFilter.getDistance()+ 1);
			//registry.run("draw");
			 filterStatic();
			writeBrowseState();
		}
	};

	public  ActionListener showLessAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (getDisplay().generalFilter.getDistance() -1 > 0) {
				getDisplay().generalFilter.setDistance(getDisplay().generalFilter.getDistance() -1);
				 filterStatic();
				//registry.run("draw");
				writeBrowseState();
			}
		}
	};

	public synchronized void filterStatic() {
		getDisplay().filterStatic();
	}

	

	public boolean updateNode() {
		if (activePropPanel == OBJ_PANEL) {
			if (!objPanel.updateNode()) {
				showErrorMessage("Malformed URI");
				setFocusNode(objPanel.getNode());
				return false;
			}
		} else if (activePropPanel == REL_PANEL) {
			if (!relPanel.updateNode()) {
				showErrorMessage("Malformed URI");
				setFocusNode(relPanel.getNode());
				return false;
			}
		}
		return true;
	}

	/**
	 * This is the default constructor
	 */
	public ThinkScapeGUI() {
		super();
		// initialize();
	}

	/**
	 * This method initializes tgPanel
	 * 
	 * @return com.touchgraph.graphlayout.TGPanel
	 */
	public GrOWLDisplay getDisplay() {
		if (display == null) {
			display = new GrOWLDisplay( );
			display.initPrefuse();
			display.setLayout( null); //new java.awt.BorderLayout());
			 
			//searcher.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			 display.add(getNavToolBar(), null); //java.awt.BorderLayout.SOUTH);
			display.setPreferredSize(new java.awt.Dimension(500, 500));
			display.setSize(500, 500);
			display.setBorder(javax.swing.BorderFactory.createLineBorder(
					java.awt.Color.gray, 1));
			
//			 overview = new Display(getDisplay().getVisualization());
//			 overview.setBorder(
//			 BorderFactory.createLineBorder(Color.BLACK, 1));
//			 overview.setSize(70,60);
//			 overview.zoom(new Point2D.Float(0,0),0.1);
//					
//			// propPanel.add(overview, java.awt.BorderLayout.NORTH);
//			 //display.setLayout(null); 
//			display.add(overview);
			
			display.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					centerDisplay();
				} //
			});
		}
		return display;
	}
	
	ShellPanel shellPanel=null;
	
	public ShellPanel getShellPanel(){
		if(shellPanel==null){
			shellPanel = new ShellPanel();
			shellPanel.setBorder(javax.swing.BorderFactory.createLineBorder(
					java.awt.Color.gray, 1));
		}
		
		return shellPanel;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					getShellPanel(), getDisplay());
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(220);
			splitPane.setDividerSize(8);
			splitPane.setResizeWeight(0);
		}
		return splitPane;
	}

	private ClassViewer getSelectPanel() {
		if (selectPanel == null) {
			selectPanel = new ClassViewer(this);
		}
		return selectPanel;
	}

	public void setControlVisibility(boolean vis) {
		hScrollBar.setVisible(vis);
		vScrollBar.setVisible(vis);
	}

	 

	/*
	 * public JMenuBar getMainMenuBar() { JMenuBar obMenuBar = new JMenuBar();
	 * JMenu fileMenu; JMenuItem menuItem;
	 * 
	 * fileMenu = new JMenu("File"); obMenuBar.add(fileMenu);
	 * 
	 * final JFileChooser chooser = new JFileChooser();
	 * //chooser.setCurrentDirectory((new File(".")));
	 * //System.getProperty("user.dir")
	 * 
	 * menuItem = new JMenuItem("Load"); ActionListener loadAction = new
	 * ActionListener() { public void actionPerformed(ActionEvent e) { int
	 * returnVal = chooser.showOpenDialog(ThinkScapeGUI.this); File loadFile; if
	 * (returnVal == JFileChooser.APPROVE_OPTION) { loadFile =
	 * chooser.getSelectedFile(); } else { return; } /* if (xmlFileName!=null)
	 * graphsVisited.push(new UrlAndBase(xmlFileName,documentBase));
	 * 
	 * esdOntoBrowser.xmlFileName = loadFile.getName(); try {
	 * esdOntoBrowser.documentBase = new File(loadFile.getParent()).toURL(); }
	 * catch (Exception ex) { ex.printStackTrace(); }
	 * 
	 * display.clearAll(); try { esdOntoBrowser.krPolicy.read(
	 * loadFile.getAbsolutePath(), esdOntoBrowser.new RestoreExactGraph()); //
	 * display.updateDrawPositions(); //display.updateLocalityFromVisibility();
	 * display.fireResetEvent(); } catch (Exception ex) { ex.printStackTrace(); }
	 * 
	 * display.resetDamper(); } };
	 * 
	 * menuItem.addActionListener(loadAction); fileMenu.add(menuItem);
	 * 
	 * menuItem = new JMenuItem("Save"); ActionListener saveAction = new
	 * ActionListener() { public void actionPerformed(ActionEvent e) { int
	 * returnVal = chooser.showSaveDialog(ThinkScapeGUI.this); File saveFile; if
	 * (returnVal == JFileChooser.APPROVE_OPTION) { saveFile =
	 * chooser.getSelectedFile(); } else { return; } //
	 * xmlio.setParameterHash(createParameterHash());
	 * 
	 * try { FileOutputStream saveFileStream = new FileOutputStream(saveFile);
	 * esdOntoBrowser.krPolicy.write(saveFileStream); } catch (Exception ex) {
	 * ex.printStackTrace(); } } };
	 * 
	 * menuItem.addActionListener(saveAction); fileMenu.add(menuItem);
	 * 
	 * menuItem = new JMenuItem("New"); ActionListener newAction = new
	 * ActionListener() { public void actionPerformed(ActionEvent e) {
	 * display.clearAll(); display.clearSelect(); try { OBObjectNode firstNode =
	 * new OBObjectNode(); display.addNode(firstNode);
	 * display.setSelect(firstNode); // lbNodeDialog.setLBNode(firstNode); //
	 * lbNodeDialog.showDialog(); } catch (TGException tge) {
	 * System.err.println(tge.getMessage()); tge.printStackTrace(System.err); }
	 * display.fireResetEvent(); display.repaint(); } };
	 * menuItem.addActionListener(newAction); fileMenu.add(menuItem);
	 * 
	 * menuItem = new JMenuItem("Exit"); ActionListener exitAction = new
	 * ActionListener() { public void actionPerformed(ActionEvent e) {
	 * System.exit(0); } };
	 * 
	 * menuItem.addActionListener(exitAction); fileMenu.add(menuItem);
	 * 
	 * return obMenuBar; }
	 */

	/**
	 * This method initializes jToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	public javax.swing.JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new javax.swing.JToolBar();
			jToolBar.add(getLoadButton());
			jToolBar.add(getImportButton());
			jToolBar.add(getOpenForcePanelButton());
			jToolBar.add(getFileOpenButton());
			jToolBar.add(getFileSaveButton());
			
			//jToolBar.add(getToggleEditButton());
			//jToolBar.add(getToggleRBoxButton());
			jToolBar.add(getLayoutButton());
			jToolBar.add(getFilterComboBox());
			jToolBar.add(getZoomInButton());
			jToolBar.add(getZoomOutButton());
			jToolBar.add(getShowMoreButton());
			jToolBar.add(getShowLessButton());
			jToolBar.add(getViewAllButton());
			jToolBar.add(getShowTBoxButton());
			jToolBar.add(getViewABoxButton());
			jToolBar.add(getViewHierarchyButton());
			jToolBar.add(getViewHierarchInstButton());
			//jToolBar.add(getNavToolBar());
			//jToolBar.add(getJmapButton());
			//jToolBar.add(getEditGmapButton());
			//jToolBar.add(getJLabel());
			jToolBar.add(getJButton());
			// jToolBar.add(searcher);//getSelectCombo());
			// searcher.setBackground(jToolBar.getBackground());
			
			jToolBar.setPreferredSize(new java.awt.Dimension(10, 30));
			// jToolBar.setBorder(new
			// javax.swing.border.SoftBevelBorder(javax.swing.border.SoftBevelBorder.RAISED));
			jToolBar.setBorderPainted(false);
			jToolBar.setFloatable(false);
			
			
			
			// jToolBar.add(getBackButton());

			// jToolBar.add(getForwardButton());
		}
		return jToolBar;
	}

	/**
	 * This method initializes mainMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	public javax.swing.JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new javax.swing.JMenuBar();
			mainMenuBar.add(getFileMenu());
			mainMenuBar.setSize(198, 20);
			mainMenuBar.add(getLayoutMenu());
			mainMenuBar.add(getViewMenu());
			mainMenuBar.add(getReasonerMenu());
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes fileMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new javax.swing.JMenu();
			fileMenu.add(getJMenuItem());
			fileMenu.add(getLoadMenuItem());
			fileMenu.add(getSaveOWLRDFMenuItem());
			fileMenu.add(getImageMenuItem());
			fileMenu.add(getSaveAbstractMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getSaveGmapMenuItem());
			fileMenu.add(getLoadGMapMenuItem());
			fileMenu.add(getLoadABoxMenuItem());
			fileMenu.add(getCloseABoxMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getExitMenuItem());
			fileMenu.setText("File");

		}
		return fileMenu;
	}

	/**
	 * This method initializes loadMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getLoadMenuItem() {
		if (loadMenuItem == null) {
			loadMenuItem = new javax.swing.JMenuItem();
			loadMenuItem.setText("Load...");
			loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fileLoad();
				}
			});
		}
		return loadMenuItem;
	}

	/**
	 * This method initializes fileChooser
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private javax.swing.JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new javax.swing.JFileChooser();
			fileChooser.setSize(360, 240);
		}
		return fileChooser;
	}

	/**
	 * This method initializes saveOWLRDFMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getSaveOWLRDFMenuItem() {
		if (saveOWLRDFMenuItem == null) {
			saveOWLRDFMenuItem = new javax.swing.JMenuItem();
			saveOWLRDFMenuItem.setText("Save (OWL/RDF)...");
			saveOWLRDFMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							fileSave(KRPolicyOWL.OWLRDF);
						}
					});
		}
		return saveOWLRDFMenuItem;
	}

	/**
	 * This method initializes saveMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getSaveAbstractMenuItem() {
		if (saveAbstractMenuItem == null) {
			saveAbstractMenuItem = new javax.swing.JMenuItem();
			saveAbstractMenuItem.setText("Save (Abstract)...");
			saveAbstractMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							fileSave(KRPolicyOWL.Abstract);
						}
					});
		}
		return saveAbstractMenuItem;
	}

	/**
	 * This method initializes exitMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new javax.swing.JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	public  void fileNew() {
		fileReset();
		setEditMode(true);
		// history.clear();
		// updateHistoryControls();
		 

	}
	
	public  void fileReset() {

		setRBoxMode(false);
		setLayoutMode(false);
		// setEditMode(false);
		if (mdataPanelIsActive)
			toggleMetadataPanel();
		
		// history.clear();
		// updateHistoryControls();
		((KRPolicyThinkLab)  ApplicationFrame.getApplicationFrame().krPolicy).resetGraph();
		graph = ((KRPolicyThinkLab)  ApplicationFrame.getApplicationFrame().krPolicy).getCGraph();

		addGrounding(graph);
		getDisplay().setGraph(graph);
		//VisualItem it = getVisualization().getVisualItem(graphs, GraphUtil.getThingNode());
		setAbsLocation(GraphUtil.getThingNode(),   250, 250);
		setFocusNode(GraphUtil.getThingNode());
		// runUpdate();

	}

	public void fileLoad() {

		setRBoxMode(false);
		setLayoutMode(false);
		setEditMode(false);
		history.clear();
		updateHistoryControls();
		int returnVal = getFileChooser().showOpenDialog(ThinkScapeGUI.this);
		File loadFile;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadFile = getFileChooser().getSelectedFile();
		} else {
			return;
		}
		if (!loadFile.exists()) {
			showErrorMessage("File does not exist: " + loadFile);
			return;
		}
		LayoutIO.hasLayout = LayoutIO.readLayout(loadFile.getAbsolutePath());
		loadFromFile(loadFile);

		setLayoutMode(!LayoutIO.hasLayout);

	}

	private void loadFromFile(File loadFile) {
		/*
		 * if (xmlFileName!=null) graphsVisited.push(new
		 * UrlAndBase(xmlFileName,documentBase));
		 * 
		 * esdOntoBrowser.xmlFileName = loadFile.getName();
		 */
		suppressSelectedEvent = true;
		try {
			 ApplicationFrame.getApplicationFrame().documentBase = new File(loadFile.getParent()).toURL();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		loading = true;

		try {
			 ApplicationFrame.getApplicationFrame().krPolicy.read(loadFile.getAbsolutePath(), null);
			// if (esdOntoBrowser.ntPolicy != null)
			// esdOntoBrowser.ntPolicy.resetOntology(false);
			if (mdataPanel != null) {
				mdataPanel.setMetadata( ApplicationFrame.getApplicationFrame().krPolicy);
			}
			graph = ((KRPolicyThinkLab)  ApplicationFrame.getApplicationFrame().krPolicy).getCGraph();

			getDisplay().setGraph(graph);

			setCurrentFile(loadFile);

		} catch (Exception ex) {
			((ThinkScape)  ApplicationFrame.getApplicationFrame().rtPolicy).setOntoTitle(null);
			showErrorMessage("Error loading OWL file");
			ex.printStackTrace();
		}
		loading = false;
		mode = 0;

		updateObjectList(LIST_CLASSES);
		updateObjectPropertyList();
		updateDataPropertyList();
		suppressSelectedEvent = false;
		setFocusNode(GraphUtil.getThingNode());
		// registry.run("Force Layout");
		//registry.run("Force Layout");
		runUpdate();
		centerDisplay();

		// if (((KRPolicyOWL)
		// esdOntoBrowser.krPolicy).getCGraph().getNodeCount() > 50) {
		// GraphUtil.hideThingNode(false);
		// //esdOntoBrowser.localityScroll.setRadius(2);
		// //setFocusNode(GraphUtil.getThingNode());
		// } else {
		//
		// //GraphUtil.hideThingNode(true);
		// //esdOntoBrowser.localityScroll.setRadius(6);
		// //display.setSelect(null);
		//
		// }
		// try {
		// display.updateLocalityFromVisibility();
		// } catch (TGException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// display.getLocalityUtils().removeMarkedNodes();
		// display.fireResetEvent();
		// display.resetDamper();
	}

	private void fileSave(int format) {
		setRBoxMode(false);
		setLayoutMode(false);
		int returnVal = getFileChooser().showSaveDialog(ThinkScapeGUI.this);
		File saveFile;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			saveFile = getFileChooser().getSelectedFile();
		} else {
			return;
		}

		// xmlio.setParameterHash(createParameterHash());

		try {
			if (mdataPanel != null) {
				mdataPanel.getMetadata();
			}
			FileOutputStream saveFileStream = new FileOutputStream(saveFile);

			((KRPolicyWrite) ApplicationFrame.getApplicationFrame().krPolicy).write(saveFileStream, format);
			setCurrentFile(saveFile);

			// Layout.parameterHash = esdOntoBrowser.createParameterHash();
			LayoutIO.writeLayout(saveFile.getAbsolutePath(), graph);

		} catch (GraphException ex) {
			setFocusNode(ex.getNode());
			showErrorMessage("Graph structure cannot converteded to OWL model");
			ex.printStackTrace();

		} catch (Exception ex) {
			// showErrorMessage("Error saving to OWL file");
			ex.printStackTrace();
		}
	}

	private void fileSaveImage() {

		setLayoutMode(false);
		IMGFilter fltr = new IMGFilter();
		getFileChooser().addChoosableFileFilter(fltr);
		int returnVal = getFileChooser().showSaveDialog(ThinkScapeGUI.this);

		File saveFile;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			saveFile = getFileChooser().getSelectedFile();
		} else {
			return;
		}

		try {
			FileOutputStream saveFileStream = new FileOutputStream(saveFile);
			getDisplay().saveImage(saveFileStream, "pnj", 1); // getFileChooser().getTypeDescription(saveFile).substring(0,3),1);

			saveFileStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getFileChooser().removeChoosableFileFilter(fltr);
	}

	class IMGFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			String filename = file.getName();
			return filename.endsWith(".png");// ||filename.endsWith(".jpg")||filename.endsWith(".gif");

		}

		public String getDescription() {
			return "ImageFile (*.png)";// , *.jpg, *gif)";
		}
	}

	private void setCurrentFile(File f) {
		currentFile = f;
		((ThinkScape) ApplicationFrame.getApplicationFrame().rtPolicy).setOntoTitle(currentFile.getName());
	}

	public void fileSaveAndReload() {
		fileSave(KRPolicyOWL.OWLRDF);
		loadFromFile(currentFile);
	}

	/**
	 * This method initializes showTBoxButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getShowTBoxButton() {
		if (showTBoxButton == null) {
			showTBoxButton = new javax.swing.JButton();
			showTBoxButton.setText("");
			showTBoxButton.setToolTipText("View TBox-Terminology and Axioms");
			showTBoxButton.setIcon(new javax.swing.ImageIcon(getClass()
					.getResource(
							"/org/integratedmodelling/growl/images/Tbox.png")));
			showTBoxButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setRBoxMode(false);
							// setFilter("Fish Eye");
							filterComboBox.setSelectedItem("Fish Eye");

							getDisplay().feyeFilter.setTBoxView();
							getDisplay().getVisualization().run("Fish Eye");
							if (mode == ABOX || mode == RBOX)
								updateObjectList(LIST_CLASSES);
							mode = TBOX;
						}
					});
		}
		return showTBoxButton;
	}

	/**
	 * This method initializes viewAllButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getViewAllButton() {
		if (viewAllButton == null) {
			viewAllButton = new javax.swing.JButton();
			viewAllButton.setText("");
			viewAllButton.setIcon(new javax.swing.ImageIcon(getClass()
					.getResource(
							"/org/integratedmodelling/growl/images/All.png")));
			viewAllButton.setToolTipText("View All Ontology");
			viewAllButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (rBoxIsActive)
								setRBoxMode(false);
							else {
								// setFilter("Fish Eye");
								filterComboBox.setSelectedItem("Fish Eye");
								getDisplay().feyeFilter.setAllView();
								getDisplay().getVisualization().run("Fish Eye");
							}
							if (mode == ABOX || mode == RBOX)
								updateObjectList(LIST_CLASSES);
							mode = ALL;
						}
					});
		}
		return viewAllButton;
	}

	/**
	 * This method initializes viewABoxButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getViewABoxButton() {
		if (viewABoxButton == null) {
			viewABoxButton = new javax.swing.JButton();
			viewABoxButton.setText("");
			viewABoxButton
					.setToolTipText("View ABox- Assertions about instances");
			viewABoxButton.setIcon(new javax.swing.ImageIcon(getClass()
					.getResource(
							"/org/integratedmodelling/growl/images/Abox.png")));
			viewABoxButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setRBoxMode(false);
							// setFilter("Fish Eye");
							filterComboBox.setSelectedItem("Fish Eye");
							getDisplay().feyeFilter.setABoxView();
							getDisplay().getVisualization().run("Fish Eye");
							if (mode != ABOX)
								updateObjectList(LIST_INSTANCES);
							mode = ABOX;
						}
					});
		}
		return viewABoxButton;
	}

	/**
	 * This method initializes viewHierarchyButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getViewHierarchyButton() {
		if (viewHierarchyButton == null) {
			viewHierarchyButton = new javax.swing.JButton();
			viewHierarchyButton.setText("");
			viewHierarchyButton.setToolTipText("View Class Hierarchy");
			viewHierarchyButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/hierarchy.png")));
			viewHierarchyButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setRBoxMode(false);
							// setFilter("Fish Eye");
							filterComboBox.setSelectedItem("Fish Eye");
							getDisplay().feyeFilter.setHierarchView();
							getDisplay().getVisualization().run("Fish Eye");
							if (mode == ABOX || mode == RBOX)
								updateObjectList(LIST_CLASSES);
							mode = HIERARCH;
						}
					});
		}
		return viewHierarchyButton;
	}

	/**
	 * This method initializes viewHierarchInstButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getViewHierarchInstButton() {
		if (viewHierarchInstButton == null) {
			viewHierarchInstButton = new javax.swing.JButton();
			viewHierarchInstButton.setText("");
			viewHierarchInstButton
					.setToolTipText("View Class Hierarchy with Instances");
			viewHierarchInstButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/hierarchyinst.png")));
			viewHierarchInstButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setRBoxMode(false);
							// setFilter("Fish Eye");
							filterComboBox.setSelectedItem("Fish Eye");
							getDisplay().feyeFilter.setHierarchInstView();
							getDisplay().getVisualization().run("Fish Eye");
							if (mode == ABOX || mode == RBOX)
								updateObjectList(LIST_CLASSES);
							mode = HIERARCH_INST;
						}
					});
		}
		return viewHierarchInstButton;
	}

	// private ActionListener expandSubclassAction = new ActionListener() {
	// public void actionPerformed(java.awt.event.ActionEvent e) {
	// NodeFilter filter = new NodeFilter() {
	// public boolean match(Node node) {
	// if (node instanceof ORelationNode) {
	// return false;
	// //((OBRelationNode)node).relationSubCategory ==
	// // RenderGrowl.REL_SUBCLASS;
	// }
	// return false;
	// }
	// };
	// esdOntoBrowser.expand(popupNode, filter);
	// }
	// };

	private ActionListener expandPropertyAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			// NodeFilter filter = new NodeFilter() {
			// public boolean match(Node node) {
			// if (node instanceof ONode) {
			// switch (((ONode) node).relationSubCategory) {
			// case RenderGrowl.REL_OBJECT_PROPERTY:
			// case RenderGrowl.REL_DATA_PROPERTY:
			// case RenderGrowl.REL_OBJECT_PROPERTY_RESTRICT_SOME:
			// case RenderGrowl.REL_OBJECT_PROPERTY_RESTRICT_ALL:
			// case RenderGrowl.REL_OBJECT_PROPERTY_RESTRICT_VALUE:
			// case RenderGrowl.REL_OBJECT_PROPERTY_RESTRICT_CARD:
			// case RenderGrowl.REL_DATA_PROPERTY_RESTRICT_SOME:
			// case RenderGrowl.REL_DATA_PROPERTY_RESTRICT_ALL:
			// case RenderGrowl.REL_DATA_PROPERTY_RESTRICT_VALUE:
			// case RenderGrowl.REL_DATA_PROPERTY_RESTRICT_CARD:
			// return true;
			// }
			// }
			// return false;
			// }
			// };
			// esdOntoBrowser.expand(popupNode, filter);
		}
	};

	private JComboBox filterComboBox = null;

	private JMenu layoutMenu = null;

	private JMenuItem frishtMenuItem = null;

	private JMenuItem radialMenuItem = null;

	private JMenuItem circleMenuItem = null;

	private JMenuItem randomMenuItem = null;

	private JMenu viewMenu = null;

	private JCheckBoxMenuItem feyeMenuItem = null;

	private JButton backButton = null;

	private JButton forwardButton = null;

	private JMenuItem imageMenuItem = null;

	private JMenuItem saveGmapMenuItem = null;

	private JMenuItem loadGMapMenuItem = null;

	private JMenuItem loadABoxMenuItem = null;

	private JMenuItem closeABoxMenuItem = null;

	private JButton jmapButton = null;

	 
	
	IPopupMenuContainer browseMenuContainer; 

	public JPopupMenu getBrowsePopup(ONode node) {
		popupNode = node; 
		if(browseMenuContainer==null)
			browseMenuContainer= new BrowseMenuContainer();
		
		return browseMenuContainer.getPopupMenu(node);
	}

	private ActionListener addTopCMapAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			gmapEPanel.addTopNCmap(popupNode);
			// runUpdate();
		}
	};

	private ActionListener addARMapAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			// if (gmapEPanel.isRoleOfEditNode(popupNode))
			gmapEPanel.addARoleMap(popupNode);
			// runUpdate();
		}
	};

	private ActionListener addERMapAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			// if (gmapEPanel.isRoleOfEditNode(popupNode))
			gmapEPanel.addERoleMap(popupNode);
			// runUpdate();
		}
	};

	private ActionListener addNRMapAction = new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			// if (gmapEPanel.isRoleOfEditNode(popupNode))
			gmapEPanel.addNRoleMap(popupNode);
			// runUpdate();
		}
	};

	// public JPopupMenu getBackPopup() {
	// if (editMode == true) {
	// if (backEditPopup == null) {
	// backEditPopup = new JPopupMenu();
	// }
	// return backEditPopup;
	// }
	// if (backPopup == null) {
	// backPopup = new JPopupMenu();
	// }
	// return backPopup;
	// }

	/*
	 * private ActionListener selectAListener = new ActionListener() { public
	 * void actionPerformed(java.awt.event.ActionEvent e) { String label =
	 * ((JMenuItem) e.getSource()).getText(); esdOntoBrowser.setLocaleByName(
	 * label, esdOntoBrowser.localityScroll.getRadius()); } };
	 */

//	public JPopupMenu getEditPopup(ONode node) {
//		popupNode = node;
//
//		if (editPopup == null) {
//			editPopup = new JPopupMenu();
//			// JMenuItem menuItem;
//
//			JMenuItem expandItem = new JMenuItem("Expand node");
//			expandItem.addActionListener(expandNodeAction);
//			editPopup.add(expandItem);
//
//			JMenuItem hideItem = new JMenuItem("Hide node");
//			hideItem.addActionListener(hideNodeAction);
//			editPopup.add(hideItem);
//
//			JMenuItem localItem = new JMenuItem("Local view");
//			localItem.addActionListener(showAllAction);
//			editPopup.add(localItem);
//
//			editPopup.addPopupMenuListener(new PopupMenuListener() {
//				public void popupMenuCanceled(PopupMenuEvent e) {
//				}
//
//				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//					// here we had something
//				}
//
//				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//				}
//			});
//
//		}
//		return editPopup;
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#showOBPopup(java.awt.event.MouseEvent)
	 */
	// public void showOBPopup(MouseEvent e) {
	// getBackPopup().show(e.getComponent(), e.getX(), e.getY());
	//
	// }
	private SortedSet getObjects(int obj_type) {
		SortedSet objects = new TreeSet();
		Set owlObjects;
		if (obj_type == LIST_CLASSES)
			owlObjects = graph.getONodes(ONode.OBJ_CLASS);
		else if (obj_type == LIST_INSTANCES)
			owlObjects = graph.getONodes(ONode.OBJ_INDIVIDUAL);
		else {
			owlObjects = graph.getONodes(ONode.REL_OBJECT_PROPERTY);
			owlObjects.addAll(graph.getONodes(ONode.REL_DATA_PROPERTY));
		}

		for (Iterator it = owlObjects.iterator(); it.hasNext();) {
			ONode obj = (ONode) it.next();
			objects.add(obj.getLabel());
		}

		return objects;
	}

	/**
	 * This method initializes classSelectCombo
	 * 
	 * @return IncrMatchComboBox
	 */
	private IncrMatchComboBox getSelectCombo() {
		if (classSelectCombo == null) {
			classSelectCombo = new IncrMatchComboBox(null, false);
			classSelectCombo.setName("Select");
			classSelectCombo.setAlignmentX(20.0F);
			classSelectCombo.setAlignmentY(0.5F);
			classSelectCombo.setAutoscrolls(true);
			classSelectCombo.setMaximumSize(new java.awt.Dimension(200, 19));
			classSelectCombo.setMinimumSize(new java.awt.Dimension(100, 19));
			// classSelectCombo
			// .addActionListener(new java.awt.event.ActionListener() {
			// public void actionPerformed(java.awt.event.ActionEvent e) {
			// if (!suppressSelectedEvent && !inNotification) {
			// IncrMatchComboBox cb = (IncrMatchComboBox) e
			// .getSource();
			// if (cb.gotMatch()) {
			// String label = (String) cb
			// .getSelectedItem();
			// ONode node = esdOntoBrowser
			// .getNodeByName(label);
			// if (node != null) {
			// display.setLocale(node,
			// esdOntoBrowser.localityScroll
			// .getRadius());
			// esdOntoBrowser.setSelect(node);
			// if (!display.getVisibleLocality()
			// .contains(node)) {
			// display.layoutOnce();
			// }
			// //display.fireResetEvent();
			// esdOntoBrowser.hvScroll
			// .slowScrollToCenter(node);
			// }
			// }
			// }
			// }
			// });
		}
		return classSelectCombo;
	}

	private void updateObjectList(int obj_type) {
		boolean saveLayout = doLayout;
		doLayout = false;
		if (obj_type != LIST_RELATIONS) {
			if (lastSplit >= 0) {
				splitPane.setDividerLocation(lastSplit);
				lastSplit = -1;
			}
			updateClassTree();
		} else {
			getSelectPanel().setClasses(null);
			lastSplit = splitPane.getDividerLocation();
			splitPane.setDividerLocation(0);
		}
		IncrMatchComboBox cb = getSelectCombo();
		String sel = (String) cb.getSelectedItem();
		boolean gotSel = false;
		Vector strings = new Vector();
		SortedSet objs = getObjects(obj_type);
		for (Iterator iter = objs.iterator(); iter.hasNext();) {
			String s = (String) iter.next();
			strings.add(s);
			if (!gotSel && s.equals(sel)) {
				gotSel = true;
			}
		}
		cb.replaceAll(strings, true);
		cb.setSelectedItem(gotSel ? sel : "");
		doLayout = saveLayout;
	}

	private void updateClassTree() {
		getSelectPanel().setClasses(graph.getONodes(ONode.OBJ_CLASS));
	}

	private void updateDataPropertyList() {
		SortedSet objects = new TreeSet();
		Set owlObjects;
		owlObjects = graph.getONodes(ONode.REL_DATA_PROPERTY);
		for (Iterator it = owlObjects.iterator(); it.hasNext();) {
			ONode obj = (ONode) it.next();
			objects.add(obj.getName()); // wasLabel
		}
		Vector strings = new Vector();
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			strings.add(iter.next());
		}
		relPanel.updateDataPropertyList(strings);
	}

	private void updateObjectPropertyList() {
		SortedSet objects = new TreeSet();
		Set owlObjects;
		owlObjects = graph.getONodes(ONode.REL_OBJECT_PROPERTY);
		for (Iterator it = owlObjects.iterator(); it.hasNext();) {
			ONode obj = (ONode) it.next();
			objects.add(obj.getName()); // wasLabel
		}
		Vector strings = new Vector();
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			strings.add(iter.next());
		}
		relPanel.updateObjectPropertyList(strings);
	}

	/**
	 * This method initializes viewRBoxButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getToggleRBoxButton() {
		if (toggleRBoxButton == null) {
			toggleRBoxButton = new javax.swing.JButton();
			toggleRBoxButton.setText("");
			toggleRBoxButton.setIcon(new javax.swing.ImageIcon(getClass()
					.getResource(
							"/org/integratedmodelling/growl/images/Rbox.png")));
			toggleRBoxButton.setToolTipText("Toggle RBox View");
			toggleRBoxButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setRBoxMode(!rBoxIsActive);

						}
					});
		}
		return toggleRBoxButton;
	}

	private void setRBoxMode(boolean shouldBeActive) {
		suppressSelectedEvent = true;
		if (!rBoxIsActive && shouldBeActive) {
			graph = ((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).getRGraph();
			getDisplay().setGraph(graph);
			updateObjectList(LIST_RELATIONS);
			// viewAll();
			toggleRBoxButton.setSelected(true);
			this.remove(getEditToolboxl());
			this.add(getEditToolsRBox(), java.awt.BorderLayout.WEST);

			if (editMode)
				getEditToolsRBox().setVisible(true);
			else
				getEditToolsRBox().setVisible(false);

			this.updateUI();

		} else if (rBoxIsActive && !shouldBeActive) {
			// esdOntoBrowser.clearSelect();
			graph = ((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).getCGraph();
			getDisplay().setGraph(graph);
			updateObjectList(LIST_CLASSES);
			// viewAll();
			toggleRBoxButton.setSelected(false);
			this.remove(getEditToolsRBox());
			this.add(getEditToolboxl(), java.awt.BorderLayout.WEST);

			if (editMode)
				getEditToolboxl().setVisible(true);
			else
				getEditToolboxl().setVisible(false);

			this.updateUI();

		}
		suppressSelectedEvent = false;
		rBoxIsActive = shouldBeActive;

		if (rBoxIsActive)
			mode = RBOX;

	}

	/**
	 * This method initializes toggleEditButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getToggleEditButton() {
		if (toggleEditButton == null) {
			toggleEditButton = new javax.swing.JButton();
			toggleEditButton.setToolTipText("Switch to Edit Mode");
			toggleEditButton.setIcon(editIco);

			toggleEditButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (!suppressSelectedEvent)
								setEditMode(!editMode);
						}

					});
		}
		return toggleEditButton;
	}

	private void setEditMode(boolean edit) {
		if (editMode == edit)
			return;
		suppressSelectedEvent = true;
		editMode = edit;
		getEditToolboxl().setVisible(editMode);
		getEditToolsRBox().setVisible(editMode);
		objPanel.setEditable(editMode);
		relPanel.setEditable(editMode);
		setLayoutMode(!editMode);

		getFilterComboBox().setVisible(!editMode);
		setEditController(editMode);
		//searcher.clear();
		getNavToolBar().setVisible(!editMode);
		 overview.setVisible(!editMode);
		

		if (!editMode) {
			// esdOntoBrowser.tgUIManager.activate("Navigate");
			getToggleEditButton().setIcon(editIco);
			getToggleEditButton().setToolTipText("Switch to Edit Mode");
		} else {
			// esdOntoBrowser.tgUIManager.activate("Edit");
			getToggleEditButton().setIcon(browseIco);
			getToggleEditButton().setToolTipText("Switch to Browse Mode");
			// registry.run(staticLayout);

		}
		getDisplay().generalFilter.setEnabled(!editMode);
		if (!doLayout)
			getDisplay().getVisualization().run(getDisplay().staticLayout);
		suppressSelectedEvent = false;
	}

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setText("        ");
		}
		return jLabel;
	}

	/**
	 * This method initializes editToolboxl
	 * 
	 * @return org.integratedmodelling.growl.EditToolsPanel
	 */
	private org.integratedmodelling.growl.editor.EditToolsPanel getEditToolboxl() {
		if (editToolboxl == null) {
			editToolboxl = new org.integratedmodelling.growl.editor.EditToolsPanel(
					this);
			editToolboxl.setPreferredSize(new java.awt.Dimension(67, 531));
		}
		return editToolboxl;
	}

	public void setPopupNode(ONode n) {
		popupNode = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#processLBClick(int,
	 *      int)
	 */
	public ONode processLBClick(double x, double y) {
		if (deleteOnLClick || editToolPressed == 0 || createNodeType == 0) {
			deleteOnLClick = false;
			getEditToolsRBox().deselectALL();
			getEditToolboxl().deselectALL();
			return null;
		}

		if (editingItem == EDITING_OBJECT) {
			suppressSelectedEvent = true;
			ONode on = createNewObject(x, y, createNodeType);
			if (createNodeType == ONode.OBJ_CLASS) {
				addThingSuperClass(on);
			}
			suppressSelectedEvent = false;
			return on;

			// esdOntoBrowser.getTGPanel().setMouseOverN(on);

		} else if (editingItem == EDITING_RELATION)
		// if ( (rBoxIsActive
		// && editToolPressed ==TOOL_PROPERTY ))
		{
			ONode rn = createNewRelation(x, y, createNodeType);

			return rn;

		}
		return null;

	}

	private void addThingSuperClass(ONode on) {
		ONode thing = GraphUtil.getThingNode();
		OEdge e = createNewEdge(on, thing, OEdge.SUBCLASS_OF_EDGE);
		// if (thing.isHiden) {
		// display.hideEdge(e);
		// }
	}

	private int owlDLComplianceRBox(ONode obj1, ONode obj2) {
		ONode r1 = (ONode) obj1;
		ONode r2 = (ONode) obj2;
		if (r1.getType() == ONode.OP_ARE_EQUIVALENT) {

			if (r2.getType() == ONode.REL_OBJECT_PROPERTY) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		}
		if (editingItem == EDITING_EDGE) {
			if (!(r1.getType() == ONode.REL_OBJECT_PROPERTY)
					|| !(r2.getType() == ONode.REL_OBJECT_PROPERTY))
				return OPTION_FAIL;
			else if (editToolPressed == TOOL_EDGE_ISA) {
				geType = OEdge.SUBCLASS_OF_EDGE;
				return OPTION_EDGE;
			} else if (editToolPressed == TOOL_EDGE_EQVIVALENT) {
				geType = OEdge.EQUIVALENT_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else {
			if (editToolPressed == TOOL_INVERSE_OF) {
				createNodeType = ONode.OP_INVERSE_OF;
				return OPTION_RNODE;
			} else
				return OPTION_FAIL;

		}

	}

	private int owlDLCompliance(ONode obj1, ONode obj2) {

		if (editingItem == EDITING_EDGE) {
			if (editToolPressed == TOOL_EDGE_ISA) {
				if (ONodeLib.isClassBase(obj1) && ONodeLib.isClassBase(obj2)) {
					geType = OEdge.SUBCLASS_OF_EDGE;
					return OPTION_EDGE;
				} else if (ONodeLib.isObjectType(obj1, ONode.OBJ_INDIVIDUAL)
						&& ONodeLib.isClassBase(obj2)) {
					geType = OEdge.INSTANCE_OF_EDGE;
					return OPTION_EDGE;
				} else
					return OPTION_FAIL;
			} else if (editToolPressed == TOOL_EDGE_EQVIVALENT) {
				if (ONodeLib.isClassBase(obj1) && ONodeLib.isClassBase(obj2)) {
					geType = OEdge.EQUIVALENT_EDGE;
					return OPTION_EDGE;
				} else
					return OPTION_FAIL;
			}

		}
		// the following option almost do not depend on tool buttons pressed
		// Besides the edge buttons
		// those are used to draw link
		if (ONodeLib.isObjRelation(obj1.getType())) {
			if (obj1.hasOutput())
				return OPTION_FAIL;
			else if (ONodeLib.isSetOrClass(obj2)
					|| (ONodeLib.isObjectType(obj1,
							ONode.REL_OBJECT_PROPERTY_RESTRICT_VALUE) && ONodeLib
							.isObjectType(obj2, ONode.OBJ_INDIVIDUAL))) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;

			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isObjPropertyValueRestriction(obj1)) {
			if (obj1.hasOutput())
				return OPTION_FAIL;
			else if (ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL)) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isObjPropertyValue(obj1)) {
			if (ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL)) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isDataRelation(obj1.getType())) {
			if (obj1.hasOutput())
				return OPTION_FAIL;
			else if (ONodeLib.isObjectType(obj2, ONode.OBJ_DATATYPE)
					|| (ONodeLib.isObjectType(obj1,
							ONode.REL_DATA_PROPERTY_RESTRICT_VALUE) && ONodeLib
							.isObjectType(obj2, ONode.OBJ_DATAVALUE))) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isDataPropertyValueRestriction(obj1)) {
			if (obj1.hasOutput())
				return OPTION_FAIL;
			else if (ONodeLib.isObjectType(obj2, ONode.OBJ_DATAVALUE)) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isDataPropertyValue(obj1)) {
			if (ONodeLib.isObjectType(obj2, ONode.OBJ_DATAVALUE)) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isIndividualOperator(obj1.getType())) {
			if (ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL)) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isClassOperator(obj1.getType())) {
			if (ONodeLib.isSetOrClass(obj2)) {
				geType = OEdge.END_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isProperty(obj2.getType())) {
			if (!obj2.hasInput() && ONodeLib.isSetOrClass(obj1)) {
				geType = OEdge.START_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;

		} else if (ONodeLib.isDataPropertyValue(obj2)
				|| ONodeLib.isObjPropertyValue(obj2)) {
			if (!obj2.hasInput()
					&& ONodeLib.isObjectType(obj1, ONode.OBJ_INDIVIDUAL)) {
				geType = OEdge.START_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;
		} else
		// the following option do depend on tool buttons pressed
		if (editingItem == EDITING_OBJECT) {
			/*
			 * switch (editEntityType) { case ONode.OBJ_CLASS : case
			 * ONode.OBJ_INDIVIDUAL : case ONode.OBJ_DATATYPE : case
			 * ONode.OBJ_DATAVALUE : case ONode.OBJ_INTERSECTION : case
			 * ONode.OBJ_UNION : case ONode.OBJ_COMPLEMENT : {
			 */

			if (ONodeLib.isObjectType(obj1, ONode.OBJ_CLASS)
					&& ONodeLib.isSetOrClass(obj2)) {
				geType = OEdge.SUBCLASS_OF_EDGE;
				return OPTION_EDGE;
			} else if (ONodeLib.isObjectType(obj1, ONode.OBJ_INDIVIDUAL)
					&& ONodeLib.isObjectType(obj2, ONode.OBJ_CLASS)) {
				geType = OEdge.INSTANCE_OF_EDGE;
				return OPTION_EDGE;
			} else if (ONodeLib.isObjectType(obj1, ONode.OP_COMPLEMENT)) {
				if (!obj1.hasOutput() && ONodeLib.isClassBase(obj2)) {
					geType = OEdge.ARGUMENT_EDGE;
					return OPTION_EDGE;
				} else
					return OPTION_FAIL;
			} else if (ONodeLib.isSet(obj1.getType())) {
				if (ONodeLib.isClassBase(obj2)) {
					geType = OEdge.ARGUMENT_EDGE;
					return OPTION_EDGE;
				} else
					return OPTION_FAIL;
			} else if (ONodeLib.isEnumeration(obj1)
					&& ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL)) {
				geType = OEdge.ARGUMENT_EDGE;
				return OPTION_EDGE;
			} else if (ONodeLib.isDataEnumeration(obj1)
					&& ONodeLib.isObjectType(obj2, ONode.OBJ_DATAVALUE)) {
				geType = OEdge.ARGUMENT_EDGE;
				return OPTION_EDGE;
			} else
				return OPTION_FAIL;
			// }
			// }

		} else if (editingItem == EDITING_RELATION) {

			switch (createNodeType) {

			case ONode.REL_OBJECT_PROPERTY:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_SOME:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_ALL:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_CARD:
				if (ONodeLib.isSetOrClass(obj1) && ONodeLib.isSetOrClass(obj2))
					return OPTION_RNODE;

				else
					return OPTION_FAIL;
			case ONode.REL_DATA_PROPERTY_RESTRICT_SOME:
			case ONode.REL_DATA_PROPERTY_RESTRICT_ALL:
			case ONode.REL_DATA_PROPERTY_RESTRICT_CARD:
			case ONode.REL_DATA_PROPERTY:
				if (ONodeLib.isSetOrClass(obj1)
						&& (ONodeLib.isObjectType(obj2, ONode.OBJ_DATATYPE)
						// || ONodeLib.isDataEnumeration(obj2 )
						))
					return OPTION_RNODE;
				else
					return OPTION_FAIL;
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_VALUE:
				if (ONodeLib.isSetOrClass(obj1)
						&& ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL))
					return OPTION_RNODE;
				else
					return OPTION_FAIL;
			case ONode.REL_DATA_PROPERTY_RESTRICT_VALUE:
				if (ONodeLib.isSetOrClass(obj1)
						&& ONodeLib.isObjectType(obj2, ONode.OBJ_DATAVALUE))
					return OPTION_RNODE;
				else
					return OPTION_FAIL;
			case ONode.REL_OBJECT_PROPERTY_VALUE:
				if (ONodeLib.isObjectType(obj1, ONode.OBJ_INDIVIDUAL)
						&& ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL))
					return OPTION_RNODE;
				else
					return OPTION_FAIL;
			case ONode.REL_DATA_PROPERTY_VALUE:
				if (ONodeLib.isObjectType(obj1, ONode.OBJ_INDIVIDUAL)
						&& ONodeLib.isObjectType(obj2, ONode.OBJ_DATAVALUE))
					return OPTION_RNODE;
				else
					return OPTION_FAIL;
			case ONode.OP_ARE_SAME:
			case ONode.OP_ARE_DIFFERENT:
				if ((!ONodeLib.isObjectNode(obj1.getType()))
						&& ((ONode) obj1).getType() == editToolPressed
						&& ONodeLib.isObjectType(obj2, ONode.OBJ_INDIVIDUAL)) {
					geType = OEdge.END_EDGE;
					return OPTION_EDGE;
				} else
					return OPTION_FAIL;
			case ONode.OP_ARE_EQUIVALENT:
			case ONode.OP_ARE_DISJOINT:
				if ((!ONodeLib.isObjectNode(obj1.getType()))
						&& ((ONode) obj1).getType() == editToolPressed
						&& ONodeLib.isSetOrClass(obj2)) {
					geType = OEdge.END_EDGE;
					return OPTION_EDGE;
				} else
					return OPTION_FAIL;

			}

		}
		return 0;
	}

	private int owlDLCompliance(ONode obj1) {
		// if (editingItem == EDITING_OBJECT) {
		// switch (createNodeType) {
		// case ONode.OBJ_CLASS:
		// case ONode.OBJ_INDIVIDUAL:
		// case ONode.OBJ_DATATYPE:
		// case ONode.OBJ_DATAVALUE:
		// case ONode.OBJ_INTERSECTION:
		// case ONode.OBJ_UNION:
		// case ONode.OBJ_COMPLEMENT:
		// }
		// } else {
		// switch (editToolPressed) {
		// case ONode.REL_OBJECT_PROPERTY_VALUE:
		// case ONode.REL_DATA_PROPERTY_VALUE:
		// case ONode.REL_OBJECT_PROPERTY:
		// case ONode.REL_DATA_PROPERTY:
		// case ONode.REL_OBJECT_PROPERTY_RESTRICT_SOME:
		// case ONode.REL_OBJECT_PROPERTY_RESTRICT_ALL:
		// case ONode.REL_OBJECT_PROPERTY_RESTRICT_VALUE:
		// case ONode.REL_OBJECT_PROPERTY_RESTRICT_CARD:
		//
		// case ONode.REL_DATA_PROPERTY_RESTRICT_SOME:
		// case ONode.REL_DATA_PROPERTY_RESTRICT_ALL:
		// case ONode.REL_DATA_PROPERTY_RESTRICT_VALUE:
		// case ONode.REL_DATA_PROPERTY_RESTRICT_CARD:
		//
		// //case ONode.REL_SUBCLASS:
		// //case ONode.REL_INSTANCE:
		//
		// case ONode.REL_ARE_EQUIVALENT:
		// case ONode.REL_ARE_SAME:
		// case ONode.REL_ARE_DIFFERENT:
		// case ONode.REL_ARE_DISJOINT:
		//
		// }
		//
		// }
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#addLink(org.integratedmodelling.ontobrowser.OBNode,
	 *      org.integratedmodelling.ontobrowser.OBNode)
	 */
	public void addLink(ONode fromObj, ONode toObj, double x, double y) {

		if (toObj == null) { // drag into open space
			addLink(fromObj, x, y);
			return;
		}
		VisualItem fromItem = getDisplay().getVisualization().getVisualItem(graphs, fromObj);
		VisualItem toItem = getDisplay().getVisualization().getVisualItem(graphs,toObj);

		int linkType;
		if (rBoxIsActive)
			linkType = owlDLComplianceRBox(fromObj, toObj);
		else
			linkType = owlDLCompliance(fromObj, toObj);

		switch (linkType) {
		case OPTION_FAIL:
			display.repaint();
			return;
		case OPTION_RNODE:
			int drawx = (int) (fromItem.getX() + toItem.getX()) / 2;
			int drawy = (int) (fromItem.getY() + toItem.getY()) / 2;
			ONode rn = createNewRelation(drawx, drawy, createNodeType);
			// KRPolicyOWL.addLabelPrefix(rn, rn.getLabel());
			createNewEdge(rn, toObj, OEdge.END_EDGE);
			if (ONodeLib.isPropertyRestriction(rn.getType()))
				createNewEdge(fromObj, rn, OEdge.SUBCLASS_OF_EDGE);
			else
				createNewEdge(fromObj, rn, OEdge.START_EDGE);

			setFocusNode(rn);
			// display.repaint();
			// esdOntoBrowser.getTGPanel().setMouseOverN(rn);

			break;
		case OPTION_EDGE:
			if (geType == OEdge.SUBCLASS_OF_EDGE) {
				ONode thing = GraphUtil.getThingNode();
				for (Iterator it = fromObj.edges(); it.hasNext();) {
					OEdge e = (OEdge) it.next();
					if (e.getTargetNode().equals(thing)) {
						deleteEdge(e);
						break;
					}
				}
			}
			// suppressSelectedEvent = true;
			createNewEdge(fromObj, toObj, geType);
			suppressSelectedEvent = false;
			// needs change
			display.repaint();
			break;
		default:
			rn = createNewRelation(fromObj, toObj, linkType);
			// addLabelSuffix(rn, rn.getLabel());
			// display.repaint();
			setFocusNode(rn);
			display.repaint();
		}

	}

	/**
	 * Method addLink Add link(s) and node(s) after dragging from a node into
	 * empty space
	 * 
	 * @param fromObj
	 */
	public void addLink(ONode fromObj, double x, double y) {

		switch (fromObj.getType()) {
		case ONode.OBJ_CLASS: // drag from class node
			dragFromClassNode(fromObj, x, y);
			break;
		case ONode.OBJ_INDIVIDUAL:
			dragFromIndividualNode(fromObj, x, y);
			break;
		case ONode.REL_DATA_PROPERTY:
			dragFromDataPropertyNode(fromObj, x, y);
			break;
		case ONode.REL_OBJECT_PROPERTY:
			dragFromObjectPropertyNode(fromObj, x, y);
			break;
		case ONode.REL_DATA_PROPERTY_VALUE:
			dragFromDataPropertyValueNode(fromObj, x, y);
			break;
		case ONode.REL_OBJECT_PROPERTY_VALUE:
			dragFromObjectPropertyValueNode(fromObj, x, y);
			break;
		}

		// display.repaint();
	}

	private void dragFromClassNode(ONode fromObj, double x, double y) {

		ONode newNode;
		if (editingItem == EDITING_OBJECT) {
			switch (createNodeType) {
			case ONode.OBJ_CLASS:
				/* case ONode.REL_SUBCLASS: */
				newNode = createSubclass(fromObj, x, y);
				if (newNode != null) {
					updateClassTree();
					setFocusNode(newNode);
				}
				break;
			case ONode.OBJ_INDIVIDUAL:
				// case ONode.REL_INSTANCE:
				newNode = createIndividual((ONode) fromObj, x, y);
				if (newNode != null) {
					setFocusNode(newNode);
				}
				break;
			}
		} else {
			switch (createNodeType) {
			case ONode.REL_OBJECT_PROPERTY:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_ALL:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_CARD:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_SOME:
			case ONode.REL_OBJECT_PROPERTY_RESTRICT_VALUE:
			case ONode.REL_DATA_PROPERTY:
			case ONode.REL_DATA_PROPERTY_RESTRICT_ALL:
			case ONode.REL_DATA_PROPERTY_RESTRICT_CARD:
			case ONode.REL_DATA_PROPERTY_RESTRICT_SOME:
			case ONode.REL_DATA_PROPERTY_RESTRICT_VALUE:
				// TODO require user input to say which property to create a
				// value or restriction of
				processLBClick(x, y); // creates new node
				ONode node = getFocusNode();
				if (node != null) {
					addLink(fromObj, node, x, y);
				}
				break;
			}
		}
	}

	private void dragFromIndividualNode(ONode fromObj, double x, double y) {
		if (editingItem == EDITING_OBJECT) {
		} else {
			switch (editToolPressed) {
			case ONode.REL_DATA_PROPERTY_VALUE:
			case ONode.REL_OBJECT_PROPERTY_VALUE:
				processLBClick(x, y); // creates new node
				ONode node = getFocusNode();
				if (node != null) {
					addLink(fromObj, node, x, y);
				}
				break;
			}
		}
	}

	private void dragFromDataPropertyNode(ONode fromObj, double x, double y) {
		if (editingItem == EDITING_OBJECT) {
			switch (createNodeType) {
			case ONode.OBJ_DATATYPE:
				processLBClick(x, y); // creates new node
				ONode node = getFocusNode();
				if (node != null) {
					addLink(fromObj, node, x, y);
				}
				break;
			}
		} else {
		}
	}

	private void dragFromDataPropertyValueNode(ONode fromObj, double x, double y) {
		if (editingItem == EDITING_OBJECT) {
			switch (createNodeType) {
			case ONode.OBJ_DATAVALUE:
				processLBClick(x, y); // creates new node
				ONode node = getFocusNode();
				if (node != null) {
					addLink(fromObj, node, x, y);
				}
				break;
			}
		} else {
		}
	}

	private void dragFromObjectPropertyValueNode(ONode fromObj, double x,
			double y) {
		if (editingItem == EDITING_OBJECT) {
			switch (createNodeType) {
			case ONode.OBJ_INDIVIDUAL:
				processLBClick(x, y); // creates new node
				ONode node = getFocusNode();
				if (node != null) {
					addLink(fromObj, node, x, y);
				}
				break;
			}
		} else {
		}
	}

	private void dragFromObjectPropertyNode(ONode fromRel, double x, double y) {
		if (editingItem == EDITING_OBJECT) {
			switch (createNodeType) {
			case ONode.OBJ_CLASS:
				getAndAddRange(fromRel, x, y);
				break;
			}
		} else {
			switch (createNodeType) {
			case ONode.REL_OBJECT_PROPERTY:
				getAndAddRange(fromRel, x, y);
				break;
			}
		}
	}

	private void getAndAddRange(ONode fromRel, double x, double y) {
		// Set classes = esdOntoBrowser.getObjectNodes(ONode.OBJ_CLASS);
		// Point loc = display.getLocationOnScreen();
		// SelectClassDialog d = new SelectClassDialog(classes, (JFrame) this
		// .getTopLevelAncestor(), loc.x + x, loc.y + y, "Property range");
		// d.setVisible(true);
		// if ((classes = d.getSelectedClasses()) != null) {
		// ONode from = fromRel;
		//			 
		// int save = editToolPressed;
		// int saveObj = createNodeType;
		// if (classes.size() > 1) {
		// ONode union;
		// union = esdOntoBrowser.createNewObject(x, y,
		// ONode.OP_UNION);
		// addLink(fromRel, union, x, y);
		// from = union;
		// editToolPressed = ONode.OP_UNION;
		// editingItem = EDITING_OBJECT;
		// }
		// for (Iterator i = classes.iterator(); i.hasNext();) {
		// addLink(from, (ONode) i.next(), x, y);
		// }
		// createNodeType = save;
		// editingItem = saveObj;
		//			 
		// }
	}

	private ONode createSubclass(ONode parent, double x, double y) {

		ONode subClass = createNewObject(x, y, ONode.OBJ_CLASS);
		createNewEdge(subClass, parent, OEdge.SUBCLASS_OF_EDGE);
		return subClass;

	}

	private ONode createIndividual(ONode parent, double x, double y) {

		ONode indiv = createNewObject(x, y, ONode.OBJ_INDIVIDUAL);
		createNewEdge(indiv, parent, OEdge.INSTANCE_OF_EDGE);
		return indiv;

	}

	private javax.swing.JButton getJButton() {
		if (jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setIcon(viewMetadataIco);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					toggleMetadataPanel();
				}
			});
		}
		return jButton;
	}

	/**
	 * @return
	 */
	public MetadataPanel getMDataPanel() {
		if (mdataPanel == null) {
			mdataPanel = new MetadataPanel(ApplicationFrame.getApplicationFrame().krPolicy);
			mdataPanel.setPreferredSize(new java.awt.Dimension(500, 500));
			mdataPanel.setBorder(javax.swing.BorderFactory.createLineBorder(
					java.awt.Color.gray, 1));
		} else {
			mdataPanel.setMetadata(ApplicationFrame.getApplicationFrame().krPolicy);
		}
		return mdataPanel;
	}

	public void toggleMetadataPanel() {
		if (mdataPanelIsActive) {
			this.remove(getMDataPanel());
			this.add(getSplitPane(), java.awt.BorderLayout.CENTER);
			relPanel.setVisible(true);
			objPanel.setVisible(true);
			jButton.setToolTipText("View Metadata");
			jButton.setIcon(viewMetadataIco);
			mdataPanelIsActive = false;
			ONode node = null;// display.getSelect();
			if (node != null) {
				if (!ONodeLib.isObjectNode(node.getType()))
					setRelationNode(node);
				else
					setObjectNode(node);
			}
		} else {
			this.remove(getSplitPane());
			this.add(getMDataPanel(), java.awt.BorderLayout.CENTER);
			relPanel.setVisible(false);
			objPanel.setVisible(false);
			jButton.setToolTipText("View Graph");
			jButton.setIcon(viewGraphIco);
			mdataPanelIsActive = true;
		}
		this.updateUI();
	}

	/**
	 * This method initializes layoutButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getLayoutButton() {
		if (layoutButton == null) {
			layoutButton = new javax.swing.JButton();
			layoutButton.setText("");
			layoutButton.setIcon(doLayoutIco);
			layoutButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setLayoutMode(!doLayout);
				}
			});
		}
		return layoutButton;
	}

	private void setLayoutMode(boolean doLt) {
		if (doLayout == doLt)
			return;

		doLayout = doLt;
		if (doLayout) {
			// display.addControlListener(focusControl);
			// display.removeControlListener(clickControl);
			getDisplay().getVisualization().run("layout");
			layoutButton.setIcon(stopLayoutIco);
			layoutButton.setToolTipText("Stop Layout Engine");
			// generalFilter.setEnabled(true);
			if (getDisplay().fisheyedistortion) {
				getFeyeMenuItem().doClick();
			}
		} else {
			// display.addControlListener(clickControl);
			// display.removeControlListener(focusControl);
			getDisplay().getVisualization().cancel("layout");
			layoutButton.setIcon(doLayoutIco);
			layoutButton.setToolTipText("Start Layout Engine");
			// generalFilter.setEnabled(false);
		}
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new javax.swing.JMenuItem();
			jMenuItem.setText("New");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fileNew();
				}
			});
		}
		return jMenuItem;
	}

	/**
	 * This method initializes fileOpenButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getFileOpenButton() {
		if (fileOpenButton == null) {
			fileOpenButton = new javax.swing.JButton();
			fileOpenButton.setIcon(new javax.swing.ImageIcon(getClass()
					.getResource(
							"/org/integratedmodelling/growl/images/open.png")));
			fileOpenButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							fileLoad();
						}
					});
		}
		return fileOpenButton;
	}

	/**
	 * This method initializes fileSaveButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getFileSaveButton() {
		if (fileSaveButton == null) {
			fileSaveButton = new javax.swing.JButton();
			fileSaveButton
					.setIcon(new javax.swing.ImageIcon(
							getClass()
									.getResource(
											"/org/integratedmodelling/growl/images/fileSave.png")));
			fileSaveButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							fileSave(KRPolicyOWL.OWLRDF);
						}
					});
		}
		return fileSaveButton;
	}

	/**
	 * @return
	 */
	public EditToolsRBox getEditToolsRBox() {
		if (editToolsRBox == null) {
			editToolsRBox = new EditToolsRBox(this);
			editToolsRBox.setPreferredSize(new java.awt.Dimension(34, 531));
			// editToolsRBox.setVisible(true);
		}
		return editToolsRBox;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#addNode(org.integratedmodelling.ontobrowser.OBNode)
	 */
	public void addNotification(ONode node) {
		updateEntityList(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#changeNode(org.integratedmodelling.ontobrowser.OBNode)
	 */
	public void changeNameNotification(ONode node, String old) {
		updateEntityList(node);
		if (node instanceof ONode) {
			String newName = node.getName(); // wasLabel
			Set nodes;
			int category = ((ONode) node).getType();
			switch (category) {
			case ONode.REL_OBJECT_PROPERTY:
				nodes = graph.getONodes(ONode.REL_OBJECT_PROPERTY_VALUE);
				updateRestrictionNames(getObjectPropertyRestrictions(), old,
						newName);
				updateNodeNames(nodes, old, newName);
				break;
			case ONode.REL_DATA_PROPERTY:
				nodes = graph.getONodes(ONode.REL_DATA_PROPERTY_VALUE);
				updateRestrictionNames(getDataPropertyRestrictions(), old,
						newName);
				updateNodeNames(nodes, old, newName);
				break;
			}
		}
	}

	public void changeLabelNotification(ONode node, String newLabel) {
		updateEntityList(node);
		if (node instanceof ONode) {
			String nodeName = node.getName(); // wasLabel
			Set nodes;
			int category = ((ONode) node).getType();
			switch (category) {
			case ONode.REL_OBJECT_PROPERTY:
				nodes = graph.getONodes(ONode.REL_OBJECT_PROPERTY_VALUE);

				updateNodeLabels(nodes, nodeName, newLabel);
				updateRestrictionLabels(getObjectPropertyRestrictions(),
						nodeName, newLabel);
				break;
			case ONode.REL_DATA_PROPERTY:
				nodes = graph.getONodes(ONode.REL_DATA_PROPERTY_VALUE);
				updateNodeLabels(nodes, nodeName, newLabel);
				updateRestrictionLabels(getDataPropertyRestrictions(),
						nodeName, newLabel);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ontobrowser.GUIPolicy#deleteNode(org.integratedmodelling.ontobrowser.OBNode)
	 */
	public void deleteNotification(ONode node) {
		updateEntityList(node);
	}

	public void addEdgeNotification(OEdge e) {
		if (!loading) {
			if (e.getType() == OEdge.SUBCLASS_OF_EDGE
					&& GraphUtil.isClass((ONode) e.getTargetNode())
					&& GraphUtil.isClass((ONode) e.getSourceNode())) {
				updateObjectList(LIST_CLASSES);
			}
		}
	}

	public void deleteEdgeNotification(OEdge e) {
		if (!loading) {
			if (e.getType() == OEdge.SUBCLASS_OF_EDGE) {
				if (GraphUtil.isClass((ONode) e.getSourceNode())
						&& ONodeLib.getSuperClasses((ONode) e.getSourceNode())
								.size() == 0) { // add Thing superclass
					// grounding if class has no
					// other superclasses
					addThingSuperClass((ONode) e.getSourceNode()); // notification
					// generated by
					// this does the
					// updateObjectList()
				} else {
					updateObjectList(LIST_CLASSES);
				}
			}
		}
	}

	/**
	 * Method updateEntityList Update the combo boxes with lists of class and
	 * property names
	 * 
	 * @param node
	 */
	private void updateEntityList(ONode node) {
		inNotification = true;
		if (!loading) {
			if (ONodeLib.isObjectNode(node.getType())) {
				if (((ONode) node).getType() == ONode.OBJ_CLASS) {
					updateObjectList(LIST_CLASSES);
				}
			} else {
				int category = ((ONode) node).getType();
				switch (category) {
				case ONode.REL_OBJECT_PROPERTY:
					updateObjectPropertyList();
					break;
				case ONode.REL_DATA_PROPERTY:
					updateDataPropertyList();
					break;
				/*
				 * case ONode.REL_SUBCLASS: updateClassTree(); break;
				 */
				}
			}
		}
		inNotification = false;
	}

	private Set getDataPropertyRestrictions() {
		Set restrictions = graph
				.getONodes(ONode.REL_DATA_PROPERTY_RESTRICT_ALL);
		restrictions.addAll(graph
				.getONodes(ONode.REL_DATA_PROPERTY_RESTRICT_SOME));
		restrictions.addAll(graph
				.getONodes(ONode.REL_DATA_PROPERTY_RESTRICT_CARD));
		restrictions.addAll(graph
				.getONodes(ONode.REL_DATA_PROPERTY_RESTRICT_VALUE));
		return restrictions;
	}

	private Set getObjectPropertyRestrictions() {
		Set restrictions;
		restrictions = graph.getONodes(ONode.REL_OBJECT_PROPERTY_RESTRICT_ALL);
		restrictions.addAll(graph
				.getONodes(ONode.REL_OBJECT_PROPERTY_RESTRICT_SOME));
		restrictions.addAll(graph
				.getONodes(ONode.REL_OBJECT_PROPERTY_RESTRICT_CARD));
		restrictions.addAll(graph
				.getONodes(ONode.REL_OBJECT_PROPERTY_RESTRICT_VALUE));
		return restrictions;
	}

	private void updateRestrictionNames(Set restrictions, String oldName,
			String newName) {
		for (Iterator i = restrictions.iterator(); i.hasNext();) {
			ONode n = (ONode) i.next();

			String s = n.getName();
			/* KRPolicyOWL.stripNamespace(n.getLabel()) wasLabel */
			if (s.equals(oldName)) {

				// if(!n.hasLabel)
				String oldStrippedLabl = LabelFactory.stripLabel(n.getLabel());
				n.setName(newName);
				if (s.equals(oldStrippedLabl)) // test for presense of rdfs:
					// label
					LabelFactory.addLabelPrefix(n, newName); // was newName , not
				// n.getLabel()
			}
		}
	}

	private void updateRestrictionLabels(Set restrictions, String Name,
			String newLabel) {
		for (Iterator i = restrictions.iterator(); i.hasNext();) {
			ONode n = (ONode) i.next();
			String s = n.getName(); // KRPolicyOWL.stripNamespace(n.getLabel())
			// wasLabel
			if (s.equals(Name)) {
				n.setRDFLabel(newLabel);
				if (newLabel.equals(""))

					LabelFactory.addLabelPrefix(n, s);
				else
					LabelFactory.addLabelPrefix(n, newLabel);
			}
		}
	}

	private void updateNodeNames(Set nodes, String oldName, String newName) {
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			ONode n = (ONode) i.next();
			if (n.getName().equals(oldName)) { // wasLabel()
				n.setName(new String(newName)); // wasLabel
			}
		}
	}

	private void updateNodeLabels(Set nodes, String Name, String newLabel) {
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			ONode n = (ONode) i.next();
			if (n.getName().equals(Name)) { // wasLabel()
				if (newLabel.equals(""))
					n.setRDFLabel(null);
				else
					n.setRDFLabel(new String(newLabel)); // wasLabel
			}
		}
	}

	public void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "GrOWL Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void restoreAll() {
		// boolean hidden = GraphUtil.getThingNode().isHiden;
		// display.clearSelect();
		// display.restoreAll();
		// GraphUtil.hideThingNode(hidden);
	}

	// Change view functions

	/**
	 * This method initializes filterComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getFilterComboBox() {
		if (filterComboBox == null) {
			filterComboBox = new JComboBox();
			filterComboBox.setPreferredSize(new java.awt.Dimension(119, 19));
			filterComboBox.setMaximumSize(new java.awt.Dimension(119, 19));
			filterComboBox.setMinimumSize(new java.awt.Dimension(80, 19));
			filterComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseExited(java.awt.event.MouseEvent e) {
					// if (doLayout)
					// getDisplay().setLayoutMode(true);
				}

				public void mouseEntered(java.awt.event.MouseEvent e) {
					// if (doLayout)
					// getDisplay().setLayoutMode(false);
				}
			});

			filterComboBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JComboBox cb = (JComboBox) e.getSource();
							if (!suppressSelectedEvent)
								setFilter((String) cb.getSelectedItem());

							 
						}

					});

		}
		return filterComboBox;
	}

	/**
	 * @param string
	 */
	protected void setFilter(String fname) {
		if(initialization)
			return;
		OntFilter newFilter = (OntFilter) filterHash.get(fname);

		if (newFilter != null) {
			newFilter.setEnabled(getDisplay().generalFilter.isEnabled());
			getDisplay().generalFilter = newFilter;
			getDisplay().currentFilter = fname;
			getDisplay().getVisualization().run(getDisplay().currentFilter);
			
		} else {
			getDisplay().currentFilter = "Fish Eye";
			getDisplay().generalFilter = getDisplay().feyeFilter;
			getDisplay().getVisualization().run(getDisplay().currentFilter);

		}
		 
		writeBrowseState();

	}

	private void doLayout(String layout) {
		getDisplay().curLayout = layout;
		getDisplay().getVisualization().cancel("layout");
		 
		getDisplay().getVisualization().run(layout);
	} //

	class MenuController extends ControlAdapter implements MouseListener {
		public void itemPressed(VisualItem item, MouseEvent e) {
			if (!(item instanceof NodeItem))
				return;
			if (SwingUtilities.isRightMouseButton(e)) {
				getBrowsePopup((ONode) item.getSourceTuple() ).show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}

	class EditController extends ControlAdapter implements MouseListener,
			KeyListener {
		private int xDown, yDown, xCur, yCur;

		private boolean drag = false;

		private boolean drawLine = false;

		private boolean editing = false;

		// private VisualItem activeItem;

		private VisualItem edgeItem;

		private VisualItem mouseOverItem;

		private VisualItem dragItem;

		private boolean edited = false;

		private File saveFile = null;

		// from drag
		private VisualItem activeItem;

		protected Activity update;

		protected Point2D down = new Point2D.Double();

		protected Point2D tmp = new Point2D.Double();

		protected boolean dragged;

		private boolean fixOnMouseOver;

		public void itemEntered(VisualItem item, MouseEvent e) {
			if (!(item instanceof NodeItem))
				return;

			// Display d = (Display)e.getSource();
			display.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			activeItem = item;
			if (fixOnMouseOver)
				item.setFixed(true);

		} //

		public void itemExited(VisualItem item, MouseEvent e) {
			if (!(item instanceof NodeItem))
				return;
			if (activeItem == item) {
				activeItem = null;
				//item.setFixed(item.wasFixed());
			}
			// Display d = (Display)e.getSource();
			display.setCursor(Cursor.getDefaultCursor());
		} //

		// public void itemClicked(VisualItem item, MouseEvent e) {
		// if (!(item instanceof NodeItem))
		// return;
		// ((NodeItem)item).setFixed(!((NodeItem)item).isFixed());
		// }

		public void itemPressed(VisualItem item, MouseEvent e) {
			if (!(item instanceof NodeItem))
				return;
			if (SwingUtilities.isLeftMouseButton(e)) {

				if (!fixOnMouseOver)
					item.setFixed(true);
				dragged = false;
				// Display d = (Display)e.getComponent();
				down = display.getAbsoluteCoordinate(e.getPoint(), down);
				if (item == getDisplay().getVisualization().getVisualItem(graphs,getFocusNode())) {
					drawLine = true;
					display.setCursor(Cursor.getDefaultCursor());
					// dragItem = item;
				}
			} else if (SwingUtilities.isRightMouseButton(e)) {
				//getEditPopup((ONode) item ).show(e.getComponent(),
				//		e.getX(), e.getY());
			}
			// if (!(item instanceof NodeItem)) {
			// return;
			// }
			// if (!e.isControlDown()) {
			// xDown = e.getX();
			// yDown = e.getY();
			//
			// if (item == getDisplay().getVisualization().getNodeItem(getFocusNode())) {
			// drawLine = true;
			// dragItem = item;
			// }
			//				
			// }
		} //

		public void itemReleased(VisualItem item, MouseEvent e) {
			if (!(item instanceof NodeItem))
				return;

			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			if (dragged) {
				activeItem = null;
				//item.setFixed(item.wasFixed());
				dragged = false;
			}

			display.line = null;
			drawLine = false;
			VisualItem vi = display.findItem(e.getPoint());
			if (vi != item) {
				ONode dragNode = (ONode) item ;
				ONode mouseOverNode = null;
				if (vi != null && (vi instanceof NodeItem)) {
					mouseOverNode = (ONode) vi ;
				}
				tmp = display.getAbsoluteCoordinate(e.getPoint(), tmp);
				addLink(dragNode, mouseOverNode, tmp.getX(), tmp.getY());

			}
			// registry.run("filter");
			runUpdate();

		} //

		public void itemDragged(VisualItem item, MouseEvent e) {
			if (!(item instanceof NodeItem))
				return;
			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			dragged = true;
			// Display d = (Display) e.getComponent();
			tmp = display.getAbsoluteCoordinate(e.getPoint(), tmp);
			double dx = tmp.getX() - down.getX();
			double dy = tmp.getY() - down.getY();
			Point2D p = new Point2D.Double(item.getX(), item.getY());
			if (drawLine) {
				display.line = new Line2D.Double((double) p.getX(), (double) p
						.getY(), down.getX(), down.getY());
			} else {
				//item.updateLocation(p.getX() + dx, p.getY() + dy);
				item.setX(p.getX() + dx);
				item.setY( p.getY() + dy);
			}

			down.setLocation(tmp);
			runUpdate();
			// if (!(item instanceof NodeItem))
			// return;
			//
			// drag = true;
			// Point2D p = item.getLocation();
			// double x = p.getX() + e.getX() - xDown;
			// double y = p.getY() + e.getY() - yDown;
			//
			// if (drawLine) {
			// //if(display.line==null)
			// display.line = new Line2D.Double((double) p.getX(), (double) p
			// .getY(), xDown, yDown);
			// //else
			// //display.line.setLine((double) p.getX(), (double) p.getY(),
			// // p.getX() , p.getY() );
			// } else {
			// item.updateLocation(x, y);
			// item.setLocation(x, y);
			// }
			// xDown = e.getX();
			// yDown = e.getY();
			// registry.run("update");
			// setEdited(true);

		} //

		public void itemClicked(VisualItem item, MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {

				if (deleteOnLClick) { // double click would conflicts with
					// focus event
					deleteItem(item);
					runUpdate();
				} else if (item != null && item instanceof ONode) {
					setFocusNode((ONode) item );
				}

			}
		}

		public void itemKeyTyped(VisualItem item, KeyEvent e) {
			// if (e.getKeyChar() == '\b') {
			// if (item == activeItem)
			// activeItem = null;
			// removeNode(item);
			// // registry.run("filter");
			// registry.run("update");
			// setEdited(true);
			// }
		} //

		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				xDown = e.getX();
				yDown = e.getY();
			}
		} //

		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {

				int x = e.getX(), y = e.getY();
				int dx = x - xDown, dy = y - yDown;
				display.pan(dx, dy);
				xDown = x;
				yDown = y;
				display.repaint();
			}
		} //

		/**
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			// System.out.print("...\n");
			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			ONode n = null;
			display.line = null;
			// mouseOverItem = display.findItem(e.getPoint());
			// synchronized (registry) {
			if (e.getClickCount() == clickToCreate) {
				// if (mouseOverItem == null) {

				tmp = display.getAbsoluteCoordinate(e.getPoint(), tmp);
				n = processLBClick(tmp.getX(), tmp.getY());
				if (n != null) {
					setFocusNode(n);

				}

				// }

				// RepaintThread rc= new RepaintThread();
				// rc.start();
				// registry.cancel(currentFilter);
				runUpdate();
				// display.drawItem(registry.getNodeItem(n,true));

			}
			// }

		} //

		public void mouseMoved(MouseEvent e) {
			// if (!editing)
			// display.requestFocus();
			xCur = e.getX();
			yCur = e.getY();
		} //

		public void keyPressed(KeyEvent e) {
			// Object src = e.getSource();
			// char c = e.getKeyChar();
			// int modifiers = e.getModifiers();
			// boolean modded = (modifiers & (KeyEvent.ALT_MASK |
			// KeyEvent.CTRL_MASK)) > 0;
			// if (Character.isLetterOrDigit(c) && !modded && src == display) {
			// VisualItem item = addNode(xCur, yCur);
			// if (activeItem != null) {
			// addEdge(activeItem, item);
			// deactivateItem();
			// }
			// item.setAttribute(nameField, String.valueOf(c));
			// editing = true;
			// Rectangle r = item.getBounds().getBounds();
			// r.width = 52;
			// r.height += 2;
			// r.x -= 1 + r.width / 2;
			// r.y -= 1;
			// activeItem = item;
			// item.setFixed(true);
			// display.editText(item, nameField, r);
			// setEdited(true);
			// registry.run("filter");
			// registry.run("update");
			// }
		} //

		public void keyReleased(KeyEvent e) {
			Object src = e.getSource();
			if (src == display.getTextEditor()
					&& e.getKeyCode() == KeyEvent.VK_ENTER) {
				stopEditing();
				runUpdate();
			}
		} //

		private void deactivateItem() {
			activeItem.setStrokeColor( Color.BLACK.getRGB());
			activeItem.setFillColor(Color.WHITE.getRGB());
			activeItem = null;
		} //

		private void stopEditing() {
			// display.stopEditing();
			// if (activeItem != null) {
			// activeItem.setColor(Color.BLACK);
			// activeItem.setFillColor(Color.WHITE);
			// activeItem.setFixed(false);
			// activeItem = null;
			// }
			// editing = false;
		} //

		private void setEdited(boolean s) {
			// if (edited == s)
			// return;
			// edited = s;
			// saveItem.setEnabled(s);
			// String titleString;
			// if (saveFile == null) {
			// titleString = TITLE;
			// } else {
			// titleString = TITLE + " - " + saveFile.getName()
			// + (s ? "*" : "");
			// }
			// if (!titleString.equals(getTitle()))
			// setTitle(titleString);
		} //

	}

	public void runUpdate() {
		  
		getDisplay().runUpdate();
		 
	}

	// public class RepaintThread extends Thread {
	// public void run() {
	// try {
	// sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// runUpdate();
	// }
	// }

	public ONode createNewObject(double x, double y, int type) {

		ONode n = graph.addONode();
		n.init(GrOWLDisplay.DEFAULT_LABEL, GrOWLDisplay.DEFAULT_LABEL, type);
		n.set(GrOWLDisplay.nameField, GrOWLDisplay.DEFAULT_LABEL);
		
		n.setInt("X", (int)x);
		n.setInt("Y", (int)y);

		VisualItem item = getDisplay().getVisualization().getVisualItem(graphs,n);

		// item.setFixed(false);
		// item.setColor(Color.BLACK);
		// item.setFillColor(Color.WHITE);
		// item.updateLocation(x, y);
		// item.setLocation(x, y);
		setAbsLocation(n,  (int) x, (int)y);
		addNotification(n);
		// display.drawItem(item);

		return n;

	}

	public ONode createNewRelation(double x, double y, int type) {
		ONode n = graph.addONode();
		n.init(GrOWLDisplay.DEFAULT_LABEL, GrOWLDisplay.DEFAULT_LABEL, type);
		n.set(GrOWLDisplay.nameField, GrOWLDisplay.DEFAULT_LABEL);
		
		VisualItem item = getDisplay().getVisualization().getVisualItem(graphs, n);
		// item.setColor(Color.BLACK);
		// item.setFillColor(Color.WHITE);
		// item.updateLocation(x, y);
		// item.setLocation(x, y);
		item.setFixed(false);
		setAbsLocation(n,  (int) x, (int)y);
		LabelFactory.addLabelPrefix(n, n.getLabel());
		addNotification(n);
		return n;
	}

	private void setAbsLocation(ONode n,   int x, int y) {
		// Point2D p = display.getAbsoluteCoordinate(new Point2D.Double(x, y),
		// null);
		//item.updateLocation(x, y);// p.getX(), p.getY());
		//item.setLocation(x, y); // p.getX(), p.getY());
		n.setInt("X",  x);
		n.setInt("Y",  y);
		//item.setAttribute("X", String.valueOf(x));
		//item.setAttribute("Y", String.valueOf(y));
	}

	public ONode createNewRelation(ONode fromObj, ONode toObj, int type) {
		VisualItem fromItem = getDisplay().getVisualization().getVisualItem(graphs, fromObj);
		VisualItem toItem = getDisplay().getVisualization().getVisualItem(graphs, toObj );
		int x = (int) (fromItem.getX() + toItem.getX()) / 2;
		int y = (int) (fromItem.getY() + toItem.getY()) / 2;
		ONode n = createNewRelation(x, y, type);

		return n;
	}

	public OEdge createNewEdge(ONode from, ONode to, int type) {
		OEdge e = graph.createEdge(from, to, type);
		
		showEdge(e);
		addEdgeNotification(e);
		return e;
	}

	private void addEdge(VisualItem item1, VisualItem item2) {
		Node n1 = (Node) item1;
		Node n2 = (Node) item2;
		//if (n1.getIndex(n2) < 0) {
			Edge e = graph.addEdge( n1, n2);
			 
		//}
	} //

	public void deleteEdge(OEdge e) {

	}

	public void deleteItem(VisualItem item) {
		if (item instanceof NodeItem) {
			deleteNodeItem((NodeItem) item);
		} else if (item instanceof EdgeItem) {
			deleteEdgeItem((EdgeItem) item);
		}

	}

	public synchronized void deleteNodeItem(NodeItem item) {
		ONode n = (ONode) item;
		for (Iterator it = n.edges(); it.hasNext();) {
			OEdge e = (OEdge) it.next();
			VisualItem eit = (EdgeItem) getDisplay().getVisualization().getVisualItem(graphs, e);
			if (eit != null) {
				eit.setVisible(false);
				//eit.touch();
			}
		}

		// item.setFixed(false);
		item.setVisible(false);
		//item.touch();

		graph.removeNode(n);
		deleteNotification(n);
	}

	public synchronized void deleteEdgeItem(EdgeItem item) {
		OEdge e = (OEdge) item ;
		item.setVisible(false);
		//item.touch();
		graph.removeEdge(e);
		deleteEdgeNotification(e);
	}

	// private void removeNode(VisualItem item) {
	// Node n = (Node) item.getEntity();
	// graph.removeNode(n);
	// } //

	public void showAll() {
		for (Iterator i = graph.nodes(); i.hasNext();) {
			showNode((ONode) i.next(), true, false);
		}
		for (Iterator i = graph.edges(); i.hasNext();) {
			showEdge((OEdge) i.next());
		}

		refreshGraph();
	}

	public void showNode(ONode n, boolean shownode, boolean show_edges) {
		if (shownode) {
			n.setHiden(false);
			VisualItem item = getDisplay().getVisualization().getVisualItem(graphs, n );
		}

		if (show_edges)
			for (Iterator it = n.edges(); it.hasNext();) {
				showEdge((OEdge) it.next());
			}

	}

	public void expandNode(ONode n) {
		for (Iterator it = n.edges(); it.hasNext();) {
			OEdge e = (OEdge) it.next();
			ONode an = (ONode) e.getAdjacentNode(n);
			if (an != null)
				showNode(an, true, false);
		}

		for (Iterator it = n.edges(); it.hasNext();) {
			OEdge e = (OEdge) it.next();
			ONode an = (ONode) e.getAdjacentNode(n);
			if (an != null)
				showNode(an, false, true);
		}
	}

	public void showEdge(OEdge e) {
		if (((ONode) e.getSourceNode()).isHiden()
				|| ((ONode) e.getTargetNode()).isHiden()) {
			// e.setHiden(false);
			return;
		}

		VisualItem from = getDisplay().getVisualization().getVisualItem(graphs, e.getSourceNode());
		VisualItem to = getDisplay().getVisualization().getVisualItem(graphs,e.getTargetNode());
		if (from != null && to != null) {
			if (from.isVisible() && to.isVisible()) {
				e.setHiden(false);
				getDisplay().getVisualization().getVisualItem(graphs,e );// ??
			}
		}

	}

	public void hideNode(ONode n) {
		for (Iterator it = n.edges(); it.hasNext();) {
			OEdge e = (OEdge) it.next();
			e.setHiden(true);
			VisualItem eit = (EdgeItem) getDisplay().getVisualization().getVisualItem(graphs,e);
			if (eit != null) {
				eit.setVisible(false);
				//eit.touch();
			}
		}
		n.setHiden(true);
		VisualItem item = getDisplay().getVisualization().getVisualItem(graphs, n );
		if (item != null) {
			item.setFixed(false);
			item.setVisible(false);
			//item.touch();
			// getDisplay().getVisualization().removeItem(item);
			// getDisplay().getVisualization().removeMappings(item);
		}
	}

	public boolean useClassTreeMenu() {
		if (getDisplay().currentFilter.equalsIgnoreCase("Fish Eye"))// && !editMode)
			return true;
		else
			return false;

	}

	/**
	 * This method initializes layoutMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getLayoutMenu() {
		if (layoutMenu == null) {
			layoutMenu = new JMenu();
			layoutMenu.setText("Layout");
			layoutMenu.add(getFrishtMenuItem());
			// layoutMenu.add(getRadialMenuItem());
			layoutMenu.add(getCircleMenuItem());
			layoutMenu.add(getRandomMenuItem());
		}
		return layoutMenu;
	}

	/**
	 * This method initializes frishtMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFrishtMenuItem() {
		if (frishtMenuItem == null) {
			frishtMenuItem = new JMenuItem();
			frishtMenuItem.setText("Fruchterman");
			frishtMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							doStaticLayout("frLayout");
						}
					});
		}
		return frishtMenuItem;
	}

	/**
	 * This method initializes radialMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	// private JMenuItem getRadialMenuItem() {
	// if (radialMenuItem == null) {
	// radialMenuItem = new JMenuItem();
	// radialMenuItem.setText("Radial");
	// radialMenuItem
	// .addActionListener(new java.awt.event.ActionListener() {
	// public void actionPerformed(java.awt.event.ActionEvent e) {
	//
	// doStaticLayout("radialLayout");
	// }
	// });
	// }
	// return radialMenuItem;
	// }
	/**
	 * This method initializes circleMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCircleMenuItem() {
		if (circleMenuItem == null) {
			circleMenuItem = new JMenuItem();
			circleMenuItem.setText("Circle");
			circleMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							doStaticLayout("circleLayout");
						}
					});
		}
		return circleMenuItem;
	}

	/**
	 * This method initializes randomMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRandomMenuItem() {
		if (randomMenuItem == null) {
			randomMenuItem = new JMenuItem();
			randomMenuItem.setText("Random");
			randomMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							doStaticLayout("randomLayout");
						}
					});
		}
		return randomMenuItem;
	}

	public void doStaticLayout(String l) {

		// generalFilter.setEnabled(false);
		// getDisplay().getVisualization().getNodeItem( getFocusNode()).setFixed(true);

		// Iterator nodeIter = registry.getFilteredGraph().nodes();
		// while (nodeIter.hasNext()) {
		// NodeItem item = (NodeItem) nodeIter.next();
		// item.setFixed(false);
		// item.setWasFixed(false);
		//			
		// }

		if (editMode) {
		      getDisplay().getVisualization().getGroup(Visualization.FOCUS_ITEMS).clear();
			// registry.getFocusManager().getFocusSet(CLICK_KEY).clear();
			// registry.getFocusManager().getFocusSet(MOUSE_KEY).clear();
		} else if (getFocusNode() != null) {
			filterStatic();
		}
		setLayoutMode(false);

		getDisplay().getVisualization().run(l);
		// registry.run(staticLayout);
		// generalFilter.setEnabled(!editMode);

	}

	/**
	 * This method initializes viewMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu();
			viewMenu.setText("View");
			viewMenu.add(getFeyeMenuItem());
		}
		return viewMenu;
	}

	/**
	 * This method initializes feyeMenuItem
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getFeyeMenuItem() {
		if (feyeMenuItem == null) {
			feyeMenuItem = new JCheckBoxMenuItem();
			feyeMenuItem.setText("FishEye");
			feyeMenuItem.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getDisplay().fisheyedistortion = !getDisplay().fisheyedistortion;
					if (getDisplay().fisheyedistortion) {
						setLayoutMode(false);
						display.addMouseListener(getDisplay().auc);
						display.addMouseMotionListener(getDisplay().auc);
					} else {
						display.removeMouseListener(getDisplay().auc);
						display.removeMouseMotionListener(getDisplay().auc);
					}
				}
			});
		}
		return feyeMenuItem;
	}

	//	
	// public void setFEyeDistortion(boolean dist){
	//		
	// }

	public javax.swing.JToolBar getNavToolBar() {
		if (navToolBar == null) {
			navToolBar = new javax.swing.JToolBar();

			 navToolBar.setBackground(Color.WHITE);
			 //navToolBar.setPreferredSize()
			 navToolBar.setBounds(80,1, 500,30);

			navToolBar.setBorderPainted(false);
			navToolBar.setFloatable(false);
			navToolBar.add(getBackButton());
			navToolBar.add(getForwardButton());
			//searcher.setBackground(navToolBar.getBackground());
			//navToolBar.add(searcher);

		}
		return navToolBar;
	}

	/**
	 * This method initializes backButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBackButton() {
		if (backButton == null) {
			backButton = new JButton();
			backButton.setText("<<back");
			 backButton.setBackground(Color.WHITE);
			backButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					history.back();
					updateHistoryControls();
				}
			});
		}
		return backButton;
	}

	/**
	 * This method initializes forwardButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getForwardButton() {
		if (forwardButton == null) {
			forwardButton = new JButton();
			forwardButton.setText("forward>>");
			 forwardButton.setBackground(Color.WHITE);
			forwardButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							history.forward();
							updateHistoryControls();
						}
					});
		}
		return forwardButton;
	}

	public void updateHistoryControls() {
		getForwardButton().setEnabled(history.hasForward());
		getBackButton().setEnabled(history.hasBack());
	}

	public void setBrowseState(ONode n, String fname, int loc) {
		if (n != getFocusNode()) {
			setFocusNode(n);
		}

		if (getDisplay().generalFilter.getDistance() != loc) {
			getDisplay().generalFilter.setDistance(loc);
		}

		if (!getDisplay().currentFilter.equals(fname)) {
			getFilterComboBox().setSelectedItem(fname);
		}
		filterStatic();

	}

	public void writeBrowseState() {
		if (editMode)
			return;

//		if (!history.isActive) {
//			history.writeState(getFocusNode(), currentFilter, generalFilter
//					.getDistance());
//			updateHistoryControls();
//		}
//		if (!initialization)
//			history.isActive = false;
	}

	/**
	 * This method initializes jMenuItem1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getImageMenuItem() {
		if (imageMenuItem == null) {
			imageMenuItem = new JMenuItem();
			imageMenuItem.setText("Save Image");
			imageMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							fileSaveImage();
						}
					});
		}
		return imageMenuItem;
	}

	/**
	 * This method initializes saveGmapMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveGmapMenuItem() {
		if (saveGmapMenuItem == null) {
			saveGmapMenuItem = new JMenuItem();
			saveGmapMenuItem.setText("Save GMap");
			saveGmapMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							//saveGMap();
						}
					});
		}
		return saveGmapMenuItem;
	}

	/**
	 * This method initializes loadGMapMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLoadGMapMenuItem() {
		if (loadGMapMenuItem == null) {
			loadGMapMenuItem = new JMenuItem();
			loadGMapMenuItem.setText("Load GMap");
			loadGMapMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							//loadGMap();
						}
					});
		}
		return loadGMapMenuItem;
	}

	/**
	 * This method initializes loadABoxMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLoadABoxMenuItem() {
		if (loadABoxMenuItem == null) {
			loadABoxMenuItem = new JMenuItem();
			loadABoxMenuItem.setText("Load ABox");
			loadABoxMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							loadABox();
						}
					});
		}
		return loadABoxMenuItem;
	}

	/**
	 * This method initializes closeABoxMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCloseABoxMenuItem() {
		if (closeABoxMenuItem == null) {
			closeABoxMenuItem = new JMenuItem();
			closeABoxMenuItem.setText("Close ABox");
			closeABoxMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							closeABox();
						}
					});
		}
		return closeABoxMenuItem;
	}

//	public void loadGMap() {
//		history.clear();
//		updateHistoryControls();
//		int returnVal = getFileChooser().showOpenDialog(ThinkScapeGUI.this);
//		File loadFile;
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//			loadFile = getFileChooser().getSelectedFile();
//		} else {
//			return;
//		}
//		if (!loadFile.exists()) {
//			showErrorMessage("File does not exist: " + loadFile);
//			return;
//		}
//		((KRPolicyOWL) appFrame.krPolicy).gmap = new GraphMap();
//		((KRPolicyOWL) appFrame.krPolicy).gmap.load(loadFile);
//
//		setEditGMap(true);
//	}

//	public void saveGMap() {
//		setLayoutMode(false);
//		int returnVal = getFileChooser().showSaveDialog(ThinkScapeGUI.this);
//		File saveFile;
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//			saveFile = getFileChooser().getSelectedFile();
//		} else {
//			return;
//		}
//
//		try {
//			FileOutputStream saveFileStream = new FileOutputStream(saveFile);
//			((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).gmap = gmapEPanel.gmap;
//			((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).gmap.write(saveFileStream);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public void loadABox() {
		fileLoad();
		//setGMapMode(true);
	}

	public void closeABox() {
		//setGMapMode(false);
		//setEditGMap(false);

	}

	/**
	 * This method initializes jmapButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJmapButton() {
		if (jmapButton == null) {
			jmapButton = new JButton();
			jmapButton.setText("GMap");
			jmapButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//setGMapMode(!gmapMode);
				}
			});
		}
		return jmapButton;
	}

	public boolean gmapMode = false;

	boolean editGmap = false;

	private JButton editGmapButton = null;

	private JMenu reasonerMenu = null;

	private JMenuItem reasonerURLMenuItem = null;

	private JMenuItem validateMenuItem = null;

	private JCheckBoxMenuItem showDerivedCheckBoxMenuItem = null;

	private JCheckBoxMenuItem markDerivedCheckBoxMenuItem = null;

//	public void setGMapMode(boolean mode) {
//		setEditMode(false);
//		removeActivePanel();
//		if (mode) {
//			this.add(gmapBPanel, java.awt.BorderLayout.SOUTH);
//			activePropPanel = GMAP_B_PANEL;
//			 setGMapView();
//		} else if (editGmap) {
//			this.add(gmapEPanel, java.awt.BorderLayout.SOUTH);
//			activePropPanel = GMAP_E_PANEL;
//			 gmapEPanel.setGraphMap(((KRPolicyOWL) appFrame.krPolicy).gmap);
//			setNormalView();
//		} else {
//			this.add(objPanel, java.awt.BorderLayout.SOUTH);
//			activePropPanel = OBJ_PANEL;
//			setNormalView();
//		}
//		gmapMode = mode;
//		this.updateUI();
//	}
	
	public void removeActivePanel(){
		if (activePropPanel == OBJ_PANEL) {
			this.remove(objPanel);
		} else if (activePropPanel == REL_PANEL) {
			this.remove(relPanel);
		} else if (activePropPanel == GMAP_E_PANEL) {
			this.remove(gmapEPanel);
		} else if (activePropPanel == GMAP_B_PANEL) {
			this.remove(gmapBPanel);
		}else if (activePropPanel == REP_PANEL) {
			this.remove(repPanel);
		}
	}

//	public void setEditGMap(boolean mode) {
//		removeActivePanel();
//		setEditMode(false);
//		
//		if (mode) {
//			this.add(gmapEPanel, java.awt.BorderLayout.SOUTH);
//			activePropPanel = GMAP_E_PANEL;
//			gmapEPanel.setGraphMap(((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).gmap);
//			setNormalView();
//		} else if (gmapMode) {
//			this.add(gmapBPanel, java.awt.BorderLayout.SOUTH);
//			activePropPanel = GMAP_B_PANEL;
//			//((KRPolicyOWL) appFrame.krPolicy).gmap = gmapEPanel.gmap;
//			setGMapView();
//		} else {
//			this.add(objPanel, java.awt.BorderLayout.SOUTH);
//			activePropPanel = OBJ_PANEL;
//			//((KRPolicyOWL) appFrame.krPolicy).gmap = gmapEPanel.gmap;
//			setNormalView();
//		}
//		editGmap = mode;
//		this.updateUI();
//	}
//
//	public void setGMapView() {
//		showAll();
//		setRBoxMode(false);
//		setLayoutMode(false);
//		setEditMode(false);
//		getSelectPanel().setVisible(false);
//		getFilterComboBox().setVisible(false);
//		splitPane.setDividerLocation(0);
//		splitPane.setDividerSize(0);
//		splitPane.setResizeWeight(0);
//
//		GraphMap g = ((KRPolicyOWL) appFrame.krPolicy).gmap;
//		OGraph mgraph = new OGraph();
//		g.apply(graph, mgraph);
//		graph = mgraph;
//		getDisplay().setGraph(mgraph);
//		//
//		// updateObjectList(LIST_CLASSES);
//		// updateObjectPropertyList();
//		// updateDataPropertyList();
//		if (graph.nodes().hasNext())
//			setFocusNode((ONode) graph.nodes().next());
//		// registry.run("Force Layout");
//		setLayoutMode(true);
//		runUpdate();
//		centerDisplay();
//
//	}

	public void setNormalView() {
		setRBoxMode(false);
		setLayoutMode(false);
		setEditMode(false);
		getSelectPanel().setVisible(true);
		getFilterComboBox().setVisible(true);
		splitPane.setDividerLocation(150);
		splitPane.setDividerSize(8);
		splitPane.setResizeWeight(0);
		graph = ((KRPolicyThinkLab) ApplicationFrame.getApplicationFrame().krPolicy).getCGraph();

		getDisplay().setGraph(graph);
		suppressSelectedEvent = true;
		updateObjectList(LIST_CLASSES);
		updateObjectPropertyList();
		updateDataPropertyList();
		suppressSelectedEvent = false;
		setFocusNode(GraphUtil.getThingNode());
		// registry.run("Force Layout");
		getDisplay().getVisualization().run("Force Layout");
		setLayoutMode(true);
		runUpdate();
		centerDisplay();

	}

	/**
	 * This method initializes editGmapButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getEditGmapButton() {
		if (editGmapButton == null) {
			editGmapButton = new JButton();
			editGmapButton.setText("Edit GMap");
			editGmapButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							//setEditGMap(!editGmap);
						}
					});
		}
		return editGmapButton;
	}

	public ONode setFocusNodeName(String name) {
		ONode n = graph.findNode("label", name);
		setFocusNode(n);
		return n;
	}

	 
	 

	/**
	 * This method initializes reasonerMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getReasonerMenu() {
		if (reasonerMenu == null) {
			reasonerMenu = new JMenu();
			reasonerMenu.setText("Reasoner");
			reasonerMenu.add(getReasonerURLMenuItem());
			reasonerMenu.add(getValidateMenuItem());
			reasonerMenu.add(getShowDerivedCheckBoxMenuItem());
			reasonerMenu.add(getMarkDerivedCheckBoxMenuItem());
		}
		return reasonerMenu;
	}

	/**
	 * This method initializes reasonerURLMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getReasonerURLMenuItem() {
		if (reasonerURLMenuItem == null) {
			reasonerURLMenuItem = new JMenuItem();
			reasonerURLMenuItem.setText("Set Reasoner URL");
			reasonerURLMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//greasoner.setRaesonerURL();
				}
			});
		}
		return reasonerURLMenuItem;
	}

	/**
	 * This method initializes validateMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getValidateMenuItem() {
		if (validateMenuItem == null) {
			validateMenuItem = new JMenuItem();
			validateMenuItem.setText("Validate Ontology");
			validateMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//greasoner.validate();
				}
			});
		}
		return validateMenuItem;
	}

	/**
	 * This method initializes showDerivedCheckBoxMenuItem	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	private JCheckBoxMenuItem getShowDerivedCheckBoxMenuItem() {
		if (showDerivedCheckBoxMenuItem == null) {
			showDerivedCheckBoxMenuItem = new JCheckBoxMenuItem();
			showDerivedCheckBoxMenuItem.setText("Show Derived");
		}
		return showDerivedCheckBoxMenuItem;
	}

	/**
	 * This method initializes markDerivedCheckBoxMenuItem	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	private JCheckBoxMenuItem getMarkDerivedCheckBoxMenuItem() {
		if (markDerivedCheckBoxMenuItem == null) {
			markDerivedCheckBoxMenuItem = new JCheckBoxMenuItem();
			markDerivedCheckBoxMenuItem.setText("Mark Derived");
		}
		return markDerivedCheckBoxMenuItem;
	}
	
	public void displayValidityReport(String result){
		removeActivePanel();
		this.add(repPanel, java.awt.BorderLayout.SOUTH);
		activePropPanel = REP_PANEL;
		this.updateUI();
		repPanel.setText(result);
		
	}
	
    private boolean doClassTreeSelection=true;
	private JButton loadButton = null;
	private JButton importButton = null;
	public void doClassTreeSelection(boolean selection) {
		doClassTreeSelection=selection;
		
	}
	
	 

	public void centerDisplay() {
		getDisplay().centerDisplay();
		
	}

	public void clearSelect() {
		getDisplay().clearSelect();
		
	}

	public ONode getFocusNode() {
		 
		return getDisplay().getFocusNode();
	}

	public Visualization getVisualization() {
		 
		return getDisplay().getVisualization();
	}

	public void refreshGraph() {
		getDisplay().refreshGraph();
		
	}

	public void setFocusNode(ONode n) {
		getDisplay().setFocusNode( n);
		
	}

	public OGraph getGraph() {
		return graph;
	}

	/**
	 * This method initializes loadButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton();
			loadButton.setText("Load");
			loadButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Load();
				}
			});
		}
		return loadButton;
	}
 
	

	/**
	 * This method initializes importButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getImportButton() {
		if (importButton == null) {
			importButton = new JButton();
			importButton.setText("Import");
			importButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Import();
				}
			});
		}
		return importButton;
	}
	
    public void Load(){
    	
		String filename =openFile();
		Collection<IInstance> objs = null;
		
		if(filename != null){
					
			ISession session = null;
//			try {
//				session = KnowledgeManager.get().getSessionManager().getCurrentSession();
//				//objs = session.loadObjects(filename);
//			} catch (ThinklabException e) {
//				ThinkScape.logger().error(e.stackTrace());
//			}
			
			ThinkScape.logger().info(
					(objs == null ? 0 : objs.size()) + 
					" main objects loaded from " + 
					filename);
		}
	}
    
    
    /**
     * FIXME this should be able to use a URL directly input from the user, OR a file input.
     */
	public void Import(){
		String filename =openFile();
		if(filename!=null){
			shellPanel.submitCommand("import "+filename);
		}
	}
	
	public String openFile() {
		int returnVal = getFileChooser().showOpenDialog(ThinkScapeGUI.this);
		File loadFile;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadFile = getFileChooser().getSelectedFile();
		} else {
			return null;
		}
		if (!loadFile.exists()) {
			showErrorMessage("File does not exist: " + loadFile);
			return null;
		}
		return loadFile.getAbsolutePath();
	}

	public IPopupMenuContainer getBrowseMenuContainer() {
		return browseMenuContainer;
	}

	public void setBrowseMenuContainer(IPopupMenuContainer browseMenuContainer) {
		this.browseMenuContainer = browseMenuContainer;
	}

} // @jve:visual-info decl-index=0 visual-constraint="7,10"
// @jve:visual-info decl-index=0 visual-constraint="7,10"
