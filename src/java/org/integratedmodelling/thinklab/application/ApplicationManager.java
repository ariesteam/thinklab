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
//package org.integratedmodelling.thinklab.application;
//
//import java.util.Hashtable;
//
//import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
//
//public class ApplicationManager {
//
//	static ApplicationManager _this = null;
//	
//	Hashtable<String, ApplicationDescriptor> apps = new Hashtable<String, ApplicationDescriptor>();
//	
//	private ApplicationManager() {
//		
//	}
//	
//	public static ApplicationManager get() {
//		
//		if (_this == null) {
//			_this = new ApplicationManager();
//		}
//		return _this;
//	}
//
//	public void registerApplication(ApplicationDescriptor desc) {
//		apps.put(desc.id, desc);
//	}
//	
//	public ApplicationDescriptor getApplicationDescriptor(String id) {
//		return apps.get(id);
//	}
//	
//	public ApplicationDescriptor requireApplicationDescriptor(String id) throws ThinklabResourceNotFoundException {
//
//		ApplicationDescriptor ret = apps.get(id);
//		if (ret == null)
//			throw new ThinklabResourceNotFoundException("application " + id + " undeclared");
//		
//		return ret;
//
//	}
//}
