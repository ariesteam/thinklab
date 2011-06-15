package org.integratedmodelling.thinklab;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.project.ThinklabProject;
import org.integratedmodelling.thinklab.project.ThinklabProjectInstaller;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.template.MVELTemplate;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.PluginDescriptor;
import org.restlet.service.MetadataService;

/**
 * Activating this plugin means loading the knowledge manager, effectively initializing the
 * Thinklab system.
 * 
 * @author Ferdinando Villa
 *
 */
public class Thinklab extends ThinklabPlugin {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.core";
	
	public static Thinklab get() {
		return _this;
	}
	
	KnowledgeManager _km = null;
	private HashMap<String, Class<?>> _projectLoaders = 
		new HashMap<String, Class<?>>();

	private MetadataService _metadataService;

	// only for this plugin, very ugly, but we need to access logging etc. before doStart() has
	// finished and the plugin has been published.
	static Thinklab _this = null;
	
	public Thinklab() {
		_this = this;
	}
	
	@Override
	protected void preStart() throws ThinklabException {
		
		/*
		 * initialize global config from plugin properties before setConfiguration() is called
		 */
		URL config = getResourceURL("core.properties");
		
		if (config != null)
			LocalConfiguration.loadProperties(config);

		/*
		 * KM is created with the knowledge rep and session manager configured in the properties,
		 * defaulting to protege (for now) and single session.
		 */
		if (_km == null) {
			_km = new KnowledgeManager();
			_km.setPluginManager(getManager());
			_km.initialize();
		}
		
		/*
		 * install listener to handle non-code Thinklab projects
		 */
		getManager().registerListener(new ThinklabProjectInstaller());
		
		/*
		 * scan all plugins and initialize thinklab projects from them if they
		 * have the thinklab admin directories
		 */
		for (PluginDescriptor pd : getManager().getRegistry().getPluginDescriptors()) {
			
			String lf = 
				Escape.fromURL(new File(pd.getLocation().getFile()).getAbsolutePath());
			File ploc = MiscUtilities.getPath(lf);
			
			File loc = 
				new File(
						ploc +
						File.separator +
						"THINKLAB-INF" +
						File.separator +
						"thinklab.properties");
		
			if (loc.exists()) {
			
				/*
				 * install project wrapper in global register
				 */
				try {
					ThinklabProject.addProject(ploc);
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
			
				Thinklab.get().logger().info("thinklab project " + pd.getId() + " registered");
			}
		}

	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return _km.getKnowledgeRepository();
	}

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unload() throws ThinklabException {

		if (_km != null) {
			_km.shutdown();
			_km = null;
		}
	}
	
	/**
	 * Return a fully qualified plugin name given a partial or full name. If complain is true, throw an exception
	 * if not found or ambiguous, otherwise return null.
	 * @param name
	 * @return
	 * @throws ThinklabException 
	 */
	public static String resolvePluginName(String name, boolean complain) throws ThinklabException {
		
		String ret = null;
		
		// look for exact match first
		for (PluginDescriptor pd : get().getManager().getRegistry().getPluginDescriptors()) {
			if (pd.getId().equals(name)) {
				ret = pd.getId();
			}
		}
		
		if (ret == null) {
			
			/*
			 * automatically disambiguate partial word matches, which should not be found
			 */
			if (!name.startsWith("."))
				name = "." + name;
			
			for (PluginDescriptor pd : get().getManager().getRegistry().getPluginDescriptors()) {
				if (pd.getId().endsWith(name)) {
					if (ret != null) {
						ret = null;
						break;
					}
					ret = pd.getId();
				}
			}
		}
		
		if (ret == null && complain)
			throw new ThinklabPluginException("plugin name " + name + " unresolved or ambiguous");
		
		return ret;
	}
	
	public static ThinklabPlugin resolvePlugin(String name, boolean complain) throws ThinklabException {
	
		ThinklabPlugin ret = null;
		
		String pid = resolvePluginName(name, complain);

		if (pid != null)
			try {
				ret = (ThinklabPlugin)get().getManager().getPlugin(pid);
			} catch (PluginLifecycleException e) {
				throw new ThinklabPluginException(e);
			}
			
		return ret;
	}

	public void requirePlugin(String pluginId, boolean complain) throws ThinklabException {

		String pid = resolvePluginName(pluginId, complain);
		super.requirePlugin(pid);
	}
	
	public static boolean verbose(ISession session) {
		return session.getVariable(ISession.INFO) != null;
	}

	public static boolean debug(ISession session) {
		return session.getVariable(ISession.DEBUG) != null;
	}

	public MetadataService getMetadataService() throws ThinklabException {
		
		if (this._metadataService == null) {
			this._metadataService = new MetadataService();
			try {
				this._metadataService.start();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		return _metadataService;
	}

	public void shutdown(String hook, final int seconds, Map<String, String> params) throws ThinklabException {

		
		String inst = System.getenv("THINKLAB_INST");
		String home = System.getenv("THINKLAB_HOME");
		
		if (hook != null) {
						
			if (inst == null || home == null) {
				throw new ThinklabRuntimeException(
						"can't use the hook system: thinklab home and/or installation directories not defined");
			}
			
			File hdest = 
				new File(inst + File.separator + "tmp" + File.separator + "hooks");
			File hsour = new File(home + File.separator + "hooks" + File.separator + hook + ".hook");
			
			if (!hsour.exists()) {
				throw new ThinklabRuntimeException(
					"shutdown hook " + hook + " not installed");				
			}
			
			hdest.mkdirs();
			hdest = new File(hdest + File.separator + hook);
			
			MVELTemplate tmpl = new MVELTemplate(hsour);
			tmpl.write(hdest, params);
		}
		
		/*
		 * schedule shutdown
		 */
		new Thread() {
			
			@Override
			public void run() {
			
				int status = 0;
				try {
					sleep(seconds * 1000);
				} catch (InterruptedException e) {
					status = 255;
				}
				getManager().shutdown();
				System.exit(status);
				
			}
		}.start();
	}

	public static File getPluginLoadDirectory(Plugin plugin) {

		String lf = new File(plugin.getDescriptor().getLocation().getFile()).getAbsolutePath();
		return new File(Escape.fromURL(MiscUtilities.getPath(lf).toString()));
	}

	public void registerProjectLoader(String folder, Class<?> cls) {
		_projectLoaders.put(folder, cls);
	}
	
	public Class<?> getProjectLoader(String folder) {
		return _projectLoaders.get(folder);
	}
}
