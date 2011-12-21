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
package org.integratedmodelling.thinklab.webapp.view.components;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.webapp.ZK;
import org.integratedmodelling.thinklab.webapp.ZK.ZKComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;


public class Ribbon extends ThinkcapComponent {

	private static final long serialVersionUID = -167206417898951849L;
	private int width;
	private int height;
	private int iWidth;
	private int spacing = 6;
	private ZKComponent[] components;
	private int cstart = 0;
	private int selected = -1;
	            
	public Ribbon(int width, int height, int itemWidth, ZKComponent[] components) {
		this.width = width;
		this.height = height;
		this.iWidth = itemWidth;
		this.components = components;
		display();
	}
	
	public void display() {

		clear();
		
		int totc = components.length;
		
		if (((iWidth + spacing)*totc) <= width) {
			setContent(ZK.hbox(components).spacing(spacing).lalign());
			return;
		}
		
		// max displayed
		int maxc = (width - 60)/(iWidth+spacing);
		
		ArrayList<ZKComponent> comps = new ArrayList<ZK.ZKComponent>();
		
		ZKComponent leftb = 
			ZK.vbox(
				ZK.spacer(24),
				ZK.label("\u25C4").
					style(cstart > 0 ? "color: #ffffff;" : "color: #666666").
					listener("onClick", cstart > 0 ? 
						new EventListener() {
						
						@Override
						public void onEvent(Event arg0) throws Exception {
							cstart--;
							display();
						}
					} : null).
					hand(cstart > 0)).width(28).calign();
		
		ZKComponent rightb =  
			ZK.vbox(
					ZK.spacer(24),
					ZK.label("\u25BA").
						style((cstart + maxc) < totc ?  "color: #ffffff;" : "color: #666666").
						listener("onClick", (cstart + maxc) < totc ? new EventListener() {
							
							@Override
							public void onEvent(Event arg0) throws Exception {
								cstart++;
								display();
							}
						} : null).
						hand((cstart + maxc) < totc)).width(28).calign();
		
		for (int i = 0; i < maxc; i++ ) {
			int cidx = i + cstart;
			if (cidx >= components.length)
				break;
			comps.add(components[cidx]);
		}
		
		ZKComponent box = ZK.hbox(comps.toArray(new ZKComponent[comps.size()])).
			spacing(spacing).calign().height(height).fillx();
		
		setContent(
			ZK.hbox(
				ZK.spacer(4),
				ZK.div(leftb).height(height).style("font-size: 24pt"),
				ZK.spacer(10),
				ZK.div(box.lalign()).height(height).lalign().fillx(),
				ZK.spacer(10),
				ZK.div(rightb).height(height).style("font-size: 24pt"),
				ZK.spacer(4)				
			).fillx()
		);
	}

	@Override
	public void initialize() {
	}
	
	public void select(int n) {
		
		if (n < 0)
			return;
		
		this.selected = n;
		// redefine start of show
		int totc = components.length;
		// max displayed
		int maxc = (width - 40)/(iWidth+spacing);

		// current range
		if (cstart > n || (cstart + maxc) <= n) {
			cstart = (n/maxc) * maxc;
			display();
		}
	}

	@Override
	public void postInitialize() {
	}

}
