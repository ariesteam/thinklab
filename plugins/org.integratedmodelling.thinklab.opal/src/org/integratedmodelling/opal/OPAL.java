package org.integratedmodelling.opal;

import java.net.URL;
import java.util.ArrayList;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.opal.profile.OPALProfileFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class OPAL extends ThinklabPlugin {

	public final static String PLUGIN_ID = "org.integratedmodelling.thinklab.opal";
	
	ArrayList<URL> profiles = new ArrayList<URL>();
	
//	@Override
//	protected void loadExtensions() throws Exception {
//
//		super.loadExtensions();
//		
//		for (Extension ext : this.getOwnExtensions(PLUGIN_ID, "opal-profile")) {
//			addProfile(this.getResourceURL(ext.getParameter("url").valueAsString()));
//		}
//	}

	private void addProfile(URL profile) {
		profiles.add(profile);
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		for (URL url : profiles) {
			OPALProfileFactory.get().readProfile(url);
		}
	}

	@Override
	protected void unload()  throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
