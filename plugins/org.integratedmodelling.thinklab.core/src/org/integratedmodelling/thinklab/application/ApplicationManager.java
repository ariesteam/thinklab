package org.integratedmodelling.thinklab.application;

import java.util.Hashtable;

import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;

public class ApplicationManager {

	static ApplicationManager _this = null;
	
	Hashtable<String, ApplicationDescriptor> apps = new Hashtable<String, ApplicationDescriptor>();
	
	private ApplicationManager() {
		
	}
	
	public static ApplicationManager get() {
		
		if (_this == null) {
			_this = new ApplicationManager();
		}
		return _this;
	}

	public void registerApplication(ApplicationDescriptor desc) {
		apps.put(desc.id, desc);
	}
	
	public ApplicationDescriptor getApplicationDescriptor(String id) {
		return apps.get(id);
	}
	
	public ApplicationDescriptor requireApplicationDescriptor(String id) throws ThinklabResourceNotFoundException {

		ApplicationDescriptor ret = apps.get(id);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("application " + id + " undeclared");
		
		return ret;

	}
}
