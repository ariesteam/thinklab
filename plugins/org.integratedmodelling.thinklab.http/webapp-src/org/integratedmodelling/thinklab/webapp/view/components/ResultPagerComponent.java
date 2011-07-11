/**
 * ResultPagerComponent.java
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

import java.util.ArrayList;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabHttpdPlugin;
import org.integratedmodelling.thinklab.http.ThinklabWebSession;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.utils.Pager;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * A component that shows a label with an arrow next to it. Clicking the label 
 * toggles visibility of the other components besides the label.
 * @author Ferdinando Villa
 *
 */
public class ResultPagerComponent extends Vbox {

	private static final long serialVersionUID = -8780021920901962604L;

	private static final String linkStyle = 
		"a {color: black; text-decoration: none;} a:link {text-decoration: none;} a:visited { text-decoration: none; color=black;} a:hover {color: #3333FF;}";
	
	// TODO some sensible default here
	private String labelStyle = "font-size: 10pt; font-weight: bold;";

	private IQueryResult result;
	private int itemsPerPage = -1;
	private Pager pager;

	private boolean havePager;
	ThinklabWebApplication application = null;
	ThinklabWebSession session = null;

	public class SwitchPage implements EventListener {
		
		int newPage;
		
		public SwitchPage(int page) {
			newPage = page;
		}

		@Override
		public void onEvent(Event event) throws Exception {
			pager.setCurrentPage(newPage, 0);
			setup();
		}
		
	}
	
	public ResultPagerComponent() {
		super();
		session = ThinkWeb.getThinkcapSession(Sessions.getCurrent());
		application = session.getApplication();
	}

	/**
	 * Constructor for API usage. setup() must be called manually after setting all other parameters.
	 * @param results
	 * @param itemsPerPage
	 * @throws ThinklabException
	 */
	public ResultPagerComponent(IQueryResult results, int itemsPerPage) throws ThinklabException {
		super();
		this.result = results;
		this.itemsPerPage = itemsPerPage;
		session = ThinkWeb.getThinkcapSession(Sessions.getCurrent());
		application = session.getApplication();
	}
	
	protected void reset() {
		
		ArrayList<Component> cp = new ArrayList<Component>();
		
		/* erase the whole form */
		for (Object c : getChildren()) {
			cp.add((Component)c);
		}
		
		for (Component c : cp)
			c.detach();
	}

	
	public void setup() throws ThinklabException {
		
		if (result == null || itemsPerPage == -1)
			return;

		reset();
		
		if (result.getTotalResultCount() <= 0) {
			this.appendChild(new Label("No results."));
			return;
		}
	
		if (pager == null) {
			try {
				this.pager = new Pager(result, itemsPerPage, -1);
			} catch (ThinklabException e) {
				// TODO Auto-generated catch block
			}
		}
		
		/* make main label with pager and info links */

		/* label 1: Showing X to Y of N */
		int f = pager.getFirstItemIndexInCurrentPage(1);
		int l = pager.getLastItemIndexInCurrentPage(1);
		int t = pager.getTotalItemsCount();
		
		String text = 
			"<b>" +
			f + 
			"</b>-<b>" +
			l + 
			"</b> of <b>" +
			t +
			"</b>";
		
		/* First | <Prev | Next> | Last */
		Hbox pgr = null;
		
		if (pager.getTotalPagesCount() > 1) {
		
			pgr = new Hbox();
			
			/* First: label if we're there, toolbar button otherwise */
			if (pager.getCurrentPage(1) == 1) {
				Image ll = new Image("/images/icons/first16d.png");
				pgr.appendChild(ll);	
			} else {
				Toolbarbutton tbr = new Toolbarbutton(); 
				tbr.setImage("/images/icons/first16.png");
				tbr.addEventListener("onClick", new SwitchPage(0));
				pgr.appendChild(tbr);
			}
			
			/* separator */
//			Separator sep = new Separator("vertical");
//			sep.setBar(true);
//			pgr.appendChild(sep);
			
			/* Prev: toolbar button if we're not 1 */
			if (pager.getCurrentPage(1) > 1) {
				Toolbarbutton tbr = new Toolbarbutton(); 
				tbr.setImage("/images/icons/previous16.png");
				tbr.addEventListener("onClick", new SwitchPage(pager.getCurrentPage(0) - 1));
				pgr.appendChild(tbr);				
			} else {
				Image ll = new Image("/images/icons/previous16d.png");
				pgr.appendChild(ll);	
			}
				
			/* separator */
//			sep = new Separator("vertical");
//			sep.setBar(true);
//			pgr.appendChild(sep);
			
			/* Next: button unless we're at end */
			if (pager.getCurrentPage(1) < pager.getLastPage(1)) {
				Toolbarbutton tbr = new Toolbarbutton(); 
				tbr.setImage("/images/icons/next16.png");
				tbr.addEventListener("onClick", new SwitchPage(pager.getCurrentPage(0)+1));
				pgr.appendChild(tbr);								
			} else {
				Image ll = new Image("/images/icons/next16d.png");
				pgr.appendChild(ll);	
			}
			
			/* separator */
//			sep = new Separator("vertical");
//			sep.setBar(true);
//			pgr.appendChild(sep);

			/* Last: button unless we're at last page */
			if (pager.getCurrentPage(1) < pager.getLastPage(1)) {
				Toolbarbutton tbr = new Toolbarbutton(); 
				tbr.setImage("/images/icons/last16.png");
				tbr.addEventListener("onClick", new SwitchPage(pager.getLastPage(0)));
				pgr.appendChild(tbr);								
			} else {
				Image ll = new Image("/images/icons/last16d.png");
				ll.setStyle("color: #cccccc");
				pgr.appendChild(ll);	
			}
		}
		
		/* make top element in vbox */
		Hbox top = new Hbox(); 
		top.setWidth("100%");

		Div topRight = new Div();
		topRight.setAlign("right");
		if (pgr != null)
			topRight.appendChild(pgr);
		
		Text txt = new Text(text);
		top.appendChild(txt);
		top.appendChild(topRight);

		
		/* make main groupbox and windows in it */
		Window middle = new Window();
		middle.setWidth("100%");
		middle.setBorder("none");
		
		/* make all the appropriate visual elements */
		for (int i = pager.getFirstItemIndexInCurrentPage(0); i <= pager.getLastItemIndexInCurrentPage(0); i++) {
			
			HtmlBasedComponent rit = createItemComponent(result, i);
			
			if (rit == null) {
				ThinklabHttpdPlugin.get().logger().warn("internal error - null object kbox retrieval");
				continue;
			}
			rit.setWidth("100%");
			middle.appendChild(rit);
			Separator sep = new Separator("horizontal");
			sep.setHeight("3px");
			middle.appendChild(sep);
		}
		
		/* (optional) show pager with ellipsis at end */
		Div bottom = null;
		
		/* compose the vbox */
		this.appendChild(top);
		this.appendChild(middle);
		if (bottom != null)
			this.appendChild(bottom);
		
		//invalidate();
	}
	
	/**
	 * Override this one to change the component that gets created to display
	 * the passed result.
	 * 
	 * @param result2
	 * @param i
	 * @return
	 * @throws ThinklabException 
	 */
	protected HtmlBasedComponent createItemComponent(IQueryResult result, int i) throws ThinklabException {
		return new ResultItem(result, i);
	}

	public void setLabelStyle(String style) throws ThinklabException {
		labelStyle = style;
		setup();
	}
	
	public void setPager(boolean pager) {
		this.havePager = pager;
	}
	
	public void setQueryResult(IQueryResult result) throws ThinklabException {
		this.result = result;
		setup();
	}
	
	public void setItemsPerPage(int n) throws ThinklabException {
		this.itemsPerPage = n;
		setup();
	}
	
	
	
}
