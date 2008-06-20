/**
 * KnowledgeTree.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IProperty;

/**
 * A simple to use class tree that allows fast subsumption checking and handles
 * multiple inheritance appropriately. Can be used for a fast is() check that
 * misses many of the cases a reasoner won't miss, but completes long before Christmas.
 *  
 * TODO check thread safety of tree structure in read-only usage.
 * @author Ferdinando Villa
 * @since 26.11.2007 refactoring into more general KnowledgeTree that will
 * 		  follow given relationships in both Instances and Concepts
 */
public class KnowledgeTree {

	public class ClassNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = -7682568395628788545L;
		public String ID;
		
		public ClassNode(IConcept c) {
			ID = c.getSemanticType().toString();
			setUserObject(c);
		}
	}
	
	ClassNode root;

	private HashMap<String, ArrayList<ClassNode>> map = 
		new HashMap<String, ArrayList<ClassNode>>();
	
		
	/**
	 * Create a tree containing the asserted subsumption hierarchy across the whole
	 * knowledge repository starting at the given concept, and return the root node.
	 * 
	 * Can be used to display or recurse the class structure without the inefficiency
	 * of using the reasoner whenever this is acceptable. All the overhead is at
	 * creation. Multiple inheritance determines replication of tree branches, as in 
	 * the protege' tree display.
	 * 
	 * @param thing
	 * @return The root node of the subsumption tree.
	 */
	private ClassNode getClassStructure(IConcept root, HashSet<String> catalog) {
		
		String cid = root.getSemanticType().toString();
		
		ClassNode ret = new ClassNode(root);

		if (!catalog.contains(cid)) {

			catalog.add(cid);

			for (IConcept c : root.getChildren()) {
				ClassNode cn = getClassStructure(c, catalog);
				if (cn != null)
					ret.add(cn);
			}

		}
		/* put class node into hash */
		ArrayList<ClassNode> r = map.get(cid);
		if (r == null) {
			r = new ArrayList<ClassNode>();
		}
		
		r.add(ret);
		map.put(cid, r);
		
		return ret;
	}
	
	private boolean compareIDs(String cc1, ClassNode cn) {
		
		if (cn != null && (cn.ID.equals(cc1) || compareIDs(cc1, (ClassNode)cn.getParent())))
			return true;
		
		return false;
	}
	
	public KnowledgeTree(IConcept c) {
		root = getClassStructure(c, new HashSet<String>());
	}

	public KnowledgeTree() {
		root = getClassStructure(KnowledgeManager.Thing(), new HashSet<String>());
	}
	
	public KnowledgeTree(String concept, String property, boolean followTransitive, boolean forceLinear) {
		
	}
	
	public ClassNode getRootNode() {
		return root;
	}

	public boolean is(String cc1, String cc2) {

		/* get all occurrences of concept node from hash */
		ArrayList<ClassNode> r = map.get(cc1);
		
		if (r != null) {
			for (ClassNode cn : r) {
				if (compareIDs(cc2, cn))
					return true;
			}
		}
		
		return false;

	}
	
	public boolean is(IConcept c1, IConcept c2) throws ThinklabException {
		
		String cc2 = c2.getSemanticType().toString();
		String cc1 = c1.getSemanticType().toString();
		
		return is(cc1, cc2);
	}

	
	private void dumpNode(ClassNode node, String prefix, ICommandOutputReceptor outputWriter) {
		
		outputWriter.displayOutput(prefix + node.ID);
		
		for (int i = 0; i < node.getChildCount(); i++)
			dumpNode((ClassNode)node.getChildAt(i), prefix + "  ", outputWriter);
		
	}
	
	public void dump(ICommandOutputReceptor outputWriter) {
		dumpNode(root, "", outputWriter);
	}
	
	
}
