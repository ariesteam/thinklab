/**
 * AdminPanel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Dec 10, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of seamless-ontology-browser.
 * 
 * seamless-ontology-browser is free software; you can redistribute it and/or modify
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
 * @date      Dec 10, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.webapp.view.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;

public class AdminPanel extends Window {

	private static final long serialVersionUID = 5892062283703504246L;

	public class OntologyChooser extends Window {

		private static final long serialVersionUID = 9086494634810544081L;

		public OntologyChooser() {
			setup();
		}
		
		private void setup() {
			
			/*
			 * all ontologies in KM excluding sessions
			 */
			ArrayList<IOntology> ontologies = new ArrayList<IOntology>();
			for (IOntology o : KnowledgeManager.get().getKnowledgeRepository().retrieveAllOntologies()) {
				if (!o.isAnonymous())
					ontologies.add(o);
			}
			Collections.sort(ontologies, new Comparator<IOntology>() {

				@Override
				public int compare(IOntology o1, IOntology o2) {
					return o1.getConceptSpace().compareTo(o2.getConceptSpace());
				}});
			
			HtmlBasedComponent lb =
				ZK.listbox(
					ZK.listhead(
						ZK.listheader("Ontology"),	
						ZK.listheader("URI")
						))
					.checkmark(true)
					.multiple(true)
					.fixedLayout(true)
					.nrows(12)
					.id("listbox")
					.get();
			
			for (IOntology o : ontologies) {
				lb.appendChild(
					ZK.listitem(
							ZK.listcell(o.getConceptSpace()),
							ZK.listcell(o.getURI()))
						.id(o.getConceptSpace())
						.get());
			}
			
			appendChild(lb);
		}
		
		
	}

	public AdminPanel() {

		// TODO check that model has administrator privileges
		
		this.appendChild(
				
				ZK.tabbox(
						
					"Ontologies",
					
					ZK.vbox(
						ZK.groupbox("Loaded ontologies",
							ZK.vbox(
								ZK.c(new OntologyChooser()),
								ZK.hbox(
									ZK.linkbutton("Reset form").id("reset"),
									ZK.label("::"),
									ZK.linkbutton("Apply changes").id("redefine")
								)
							).fill()
						).fillx().height(320),
						
						ZK.groupbox("Import and upload"
						).fillx().height(240)
						
					).width("99%").height(600).id("ontologies").fill(),
					
					"Kboxes", // creation, deletion, monitoring, statistics
					ZK.window(
					).id("kboxes").fill(),
					
					"Plugins", // activation, monitoring, configure (new tabs) and maintenance
					ZK.window(
							
						
							
					).width("99%").height(600).id("plugins").fill(),
					
					"Applications", // list, publish, unpublish, admin (new tabs)
					ZK.window(
					).id("applications").fill(),
					
					"System", // statistics, sessions, control
					ZK.window(
					).id("system").fill()
					
				).id("admin").width("99%").height(610).get()
				
//				ZK.vbox(
//						ZK.groupbox("Search Engine",
//							ZK.vbox(
//								ZK.c(new OntologyChooser()),
//								ZK.hbox(
//									ZK.linkbutton("Reset form").id("reset"),
//									ZK.label("::"),
//									ZK.linkbutton("Reset and reindex").id("reindex"),
//									ZK.label("::"),
//									ZK.linkbutton("Apply changes").id("redefine")
//								)
//							).fillx()
//						).width(700).height(140),
//						
//						ZK.groupbox("User Management"
//							
//						).width(700).height(330),
//						
//						ZK.groupbox("Statistics"
//								
//						).width(700).height(100)
//					).spacing(6)
		);
		
		ZK.setupEventListeners(this, this);

	}
}
