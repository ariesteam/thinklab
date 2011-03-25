/**
 * KnowledgeTreeComponent.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinkcap.
 * 
 * Thinkcap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinkcap is distributed in the hope that it will be useful,
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
 * @author    Ferdinando
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.webapp.view.components;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.KnowledgeTree;
import org.integratedmodelling.thinklab.KnowledgeTree.ClassNode;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabWebSession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Tree;

public class KnowledgeTreeComponent extends Tree {

	private static final long serialVersionUID = 1L;

	public static class KBTreeModel extends AbstractTreeModel {

		private static final long serialVersionUID = -3801118072921104810L;

		private String[] relationships;
		
    	public KBTreeModel(String kbox , String[] relationships)  {
    		super(/*oot*/ null);
    		this.relationships = relationships;
    	}
		
		@Override
		public Object getChild(Object arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getChildCount(Object arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isLeaf(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	public static class CTreeModel extends AbstractTreeModel {

		private static final long serialVersionUID = 1L;
		private String[] ignore = null;
		private String[] chosen = null;
 
    	public CTreeModel(ClassNode root, String[] ignoredPrefixes, String[] chosenPrefixes)  {
    		super(root);
    		ignore = ignoredPrefixes;
    		chosen = chosenPrefixes;
    	}
    	
    	private boolean isSelected(ClassNode cn) {

    		boolean ret = (chosen == null);
    		
    		if (chosen != null) {
    			for (String prefix : chosen) {
    				if ((ret = cn.ID.startsWith(prefix)))
    					break;
    			}
    		}
    		
    		if (ret && ignore != null) {
    			for (String prefix : ignore) {
    				if ((cn.ID.startsWith(prefix))) {
    					ret = false;
    					break;
    				}
    			}
    			
    		}
    		
    		return ret;
    	}
    	
		public Object getChild(Object arg0, int arg1) {
			if (ignore == null && chosen == null)
				return ((ClassNode)arg0).getChildAt(arg1);
			else {
				for (int i = 0, cnt = 0; i < ((ClassNode)arg0).getChildCount(); i++) {
					ClassNode cn = (ClassNode) ((ClassNode)arg0).getChildAt(i);
					if (isSelected(cn)) {
						if (cnt++ == arg1)
							return cn;
					}
				}
			}
			// shouldn't happen
			return null;
		}

		public int getChildCount(Object arg0) {
			
			int ret = 0;
			
			if (ignore == null && chosen == null)
				ret = ((ClassNode)arg0).getChildCount();
			else {
				
				for (int i = 0; i < ((ClassNode)arg0).getChildCount(); i++) {
					if (isSelected((ClassNode) ((ClassNode)arg0).getChildAt(i))) {
						ret++;
					}
				}
			}
		return ret;
			
		}

		
		public boolean isLeaf(Object arg0) {
			return getChildCount(arg0) == 0;
		}

    	
    }
    
    IConcept concept = null;
    String ontology = null;
    KnowledgeTree ctree = null;
    
    ThinklabWebSession tSession = null;
	private String[] ignoredPrefixes = null;
	private String[] chosenPrefixes = null;
    
    public KnowledgeTreeComponent() {

    	tSession = ThinkWeb.getThinkcapSession(Sessions.getCurrent());   
    	ctree = null;
    }
     
    public void setConcept(String concept) {

    	if (!concept.equals("owl:Thing")) {
    		this.concept = KnowledgeManager.get().retrieveConcept(concept);
    	} else {
    		this.concept = KnowledgeManager.Thing();
    	}

        ctree = new KnowledgeTree(this.concept);

        try {
			this.setModel(new CTreeModel(ctree.getRootNode(), ignoredPrefixes, chosenPrefixes));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        
    }
    
	public void setIgnore(String ignoredPrefixes) {
		this.ignoredPrefixes = ignoredPrefixes.split(",");
		if (ctree != null) {
			try {
				this.setModel(new CTreeModel(ctree.getRootNode(), this.ignoredPrefixes, chosenPrefixes));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setOnly(String chosenPrefixes) {
		this.chosenPrefixes = chosenPrefixes.split(",");
		if (ctree != null) {
			try {
				this.setModel(new CTreeModel(ctree.getRootNode(), ignoredPrefixes, this.chosenPrefixes));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Show instances from a kbox, following one or more stated relationships.
	 * @param kbox
	 */
	public void setKbox(String kbox) {
		
	}
	
	/**
	 * Show icons for concepts or direct type of each node.
	 * @param what
	 */
	public void setIcons(boolean what) {
		
	}
}
