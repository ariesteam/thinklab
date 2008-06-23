package org.integratedmodelling.opal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.integratedmodelling.opal.profile.OPALProfileFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.java.plugin.registry.Extension;

public class OPAL extends ThinklabPlugin {

	ArrayList<URL> profiles = new ArrayList<URL>();
	
	@Override
	protected void loadExtensions() throws ThinklabException {
		// TODO Auto-generated method stub
		super.loadExtensions();
		
		for (Iterator<Extension> exts = this.getExtensions("opal-profile"); exts.hasNext(); ) {
			Extension ext = exts.next();
			addProfile(this.exportResourceCached(ext.getParameter("url").valueAsString()));
		}
	}

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
	protected void unload() {
		// TODO Auto-generated method stub
		
	}

}
