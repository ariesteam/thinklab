package org.integratedmodelling.thinklab.http.geospace.view;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.olmaps.OLmaps;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

/**
 * 
 * @author Ferdinando
 * 
 * oh come vorrei poter lavorare senza 'sto immaturo di merda che gioca al 
 * piccolo Einstein sulla mia lavagna
 * 
 */
public class LayerSwitcher extends Window {

	public String groupLabelSclass;
	public String layerLabelSclass;
	public String layerDescriptionSclass;
	
	private class LayerDesc extends DefaultMutableTreeNode {
		
		private static final long serialVersionUID = -2341542871094156937L;
		
		String service = null;
		String layerId = null;
		String description = null;
		String label = null;
		boolean computable = false;
		boolean computing = false;
		boolean isGroup = false;
		Component controller = null;
		
		LayerDesc(String s) {
			layerId = s;
		}
		
		@Override
		public boolean equals(Object obj) {
			return layerId.equals(obj);
		}
		@Override
		public int hashCode() {
			return layerId.hashCode();
		}
		
		@Override
		public String toString() {
			return "[layer: " + layerId + (isGroup ? " (group)]" : "]");
		}
	}

	private class LayerTree extends AbstractTreeModel {

		LayerDesc _root = null;

		private static final long serialVersionUID = -6408411432556055413L;
		
		private LayerDesc findLayer(LayerDesc root, String id) {		
			LayerDesc ret = null;
			if (root.layerId.equals(id)) 
				return root;
			for (int i = 0; i < root.getChildCount(); i++) {
				if ( (ret = findLayer((LayerDesc) root.getChildAt(i), id)) != null)
					break;
			}
			return ret;
		}
		
		public LayerTree(LayerDesc root) {
			super(root);
			_root = root;
		}

		@Override
		public Object getChild(Object arg0, int arg1) {
			return ((LayerDesc)arg0).getChildAt(arg1);
		}

		@Override
		public int getChildCount(Object arg0) {
			return ((LayerDesc)arg0).getChildCount();
		}

		@Override
		public boolean isLeaf(Object arg0) {
			return ((LayerDesc)arg0).isLeaf();
		}
		
		public LayerDesc lookup(String id) {
			return findLayer(_root, id);
		}
	}
	
	private class LayerRenderer implements TreeitemRenderer {

		@Override
		public void render(Treeitem item, Object data) throws Exception {
			
			LayerDesc layer = (LayerDesc) data;
			item.setId(layer.layerId);
			
			item.setOpen(true);
			Treerow row = new Treerow();
			Treecell rc = new Treecell();
			
			/*
			 * if not a layer, just render label; may have compute widget
			 */
			if (layer.isGroup) {
				rc.appendChild(
					ZK.label(layer.label).sclass(groupLabelSclass).get());
			} else {
				/*
				 * icon if any, label (large), description below;
				 * if computable, add little compute widget
				 */
				rc.appendChild(
//					ZK.vbox(
						ZK.label(layer.label).sclass(layerLabelSclass)/*,
						ZK.text(layer.description).sclass(layerDescriptionSclass))*/.get());
			}
			
			row.appendChild(rc);
			item.appendChild(row);
		}
		
	}
	
	private static final long serialVersionUID = -3985821609077491723L;
	private ArrayList<Component> nodes  = new ArrayList<Component>();
	private HashSet<LayerDesc> displayed = new HashSet<LayerDesc>();
	private OLmaps map = null;

	LayerTree _tree = null;
	Tree      layertree = null;
	private String treeSclass; 
	private String treeZclass = "dottree"; 
	
	public LayerSwitcher(OLmaps map) {
		this.map  = map;
	}
	
	public void initialize() {
		
		layertree = 
			(Tree) ZK.tree().checkbox().vflex()
				.model(_tree = new LayerTree(new LayerDesc("_root_")))
				.renderer(new LayerRenderer())
				.sclass(treeSclass)/*.zclass(treeZclass)*/.width("100%").id("layertree").get();
		
		appendChild(layertree);
		ZK.setupEventListeners(this, this);
	}

	public void addLayerGroup(String root, String label) {
		
		LayerDesc node = root == null ? _tree._root : _tree.lookup(root);
		LayerDesc desc = new LayerDesc(label);
		desc.label = label;
		desc.isGroup = true;
		node.add(desc);
		layertree.setModel(_tree);
	}
	
	public void onSelect$layertree(Event e) {
		
		SelectEvent ev = (SelectEvent) e;
		HashSet<LayerDesc> layerset = new HashSet<LayerDesc>();
		for (Object c : ev.getSelectedItems()) {
			getSelectedLayers(_tree.lookup(((Component)c).getId()), layerset);			
		}
		
		displayLayers(layerset);
	}

	private void displayLayers(HashSet<LayerDesc> layerset) {
		
		HashSet<LayerDesc> removed = new HashSet<LayerDesc>();
		
		for (LayerDesc layer : displayed) {
			if (!layerset.contains(layer)) {
				map.removeLayer(layer.layerId);
				removed.add(layer);
			}
		}
		
		for (LayerDesc layer : removed) {
			displayed.remove(layer);
		}

		for (LayerDesc layer : layerset) {	
			if (!displayed.contains(layer)) {
				displayed.add(layer);
				if (layer.service != null) {
					map.addLayer(layer.service, layer.layerId);
				}
			}
		}
	}

	private void getSelectedLayers(LayerDesc d, HashSet<LayerDesc> layerset) {
		
		if (d == null)
			return;
		
		if (d.isGroup) {
			for (int i = 0; i < d.getChildCount(); i++)
				getSelectedLayers((LayerDesc)d.getChildAt(i), layerset);
		} else {
			layerset.add(d);
		}
	}

	public void setLayerLabelClass(String s) {
		layerLabelSclass = s;
	}
	
	public void setLayerDescriptionClass(String s) {
		layerDescriptionSclass = s;
	}

	public void setGroupLabelClass(String s) {
		groupLabelSclass = s;
	}
	
	public void setTreeClass(String s) {
		treeSclass = s;
	}

	public void setTreeZclass(String s) {
		treeZclass = s;
	}

	public void addLayer(String group, String label, String id, String server,
			String description, Component controller) {

		LayerDesc node = group == null ? _tree._root : _tree.lookup(group);
		
		if (node == null)
			throw new ThinklabRuntimeException("internal: trying to add a layer to a non-existing group");
		
		LayerDesc desc = new LayerDesc(id);
		desc.label = label;
		desc.service = server;
		desc.controller = controller;
		desc.description = description;
		node.add(desc);
		layertree.setModel(_tree);
	}
	
}
