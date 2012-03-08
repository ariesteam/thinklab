///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.opal;
//
//import java.net.URL;
//import java.util.ArrayList;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.thinklab.KnowledgeManager;
//import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
//
//public class OPAL extends ThinklabPlugin {
//
//	public final static String PLUGIN_ID = "org.integratedmodelling.thinklab.opal";
//	
//	ArrayList<URL> profiles = new ArrayList<URL>();
//	
////	@Override
////	protected void loadExtensions() throws Exception {
////
////		super.loadExtensions();
////		
////		for (Extension ext : this.getOwnExtensions(PLUGIN_ID, "opal-profile")) {
////			addProfile(this.getResourceURL(ext.getParameter("url").valueAsString()));
////		}
////	}
//
//	private void addProfile(URL profile) {
//		profiles.add(profile);
//	}
//
//	@Override
//	protected void load(KnowledgeManager km) throws ThinklabException {
//		for (URL url : profiles) {
//			OPALProfileFactory.get().readProfile(url);
//		}
//	}
//
//	@Override
//	protected void unload()  throws ThinklabException {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
