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
//package org.integratedmodelling.thinklab.project;
//
//import java.util.Properties;
//
//import org.integratedmodelling.exceptions.ThinklabRuntimeException;
//import org.java.plugin.Plugin;
//import org.java.plugin.PluginManager;
//import org.java.plugin.registry.PluginDescriptor;
//
///**
// * One of these is installed by thinklab to allow loading Thinklab projects.
// * These are JPF plugins that have no java methods, therefore do not contain a 
// * ThinklabPlugin class.
// * 
// * Such plugins have a META-INF/thinklab.properties file that specifies
// * source folders to load. Each plugin is wrapped in a ThinklabProject and
// * registered with the ProjectFactory. The source folders are read to install
// * models and ontologies: this is done in the ThinklabProject.load() method.
// * 
// * @author ferdinando.villa
// *
// */
//public class ThinklabProjectInstaller implements PluginManager.EventListener {
//
//	@Override
//	public void pluginActivated(Plugin plugin) {
//
//		try {
//			Properties prop = ThinklabProject.getThinklabPluginProperties(plugin);
//			if (prop != null) {
//				ProjectFactory.get().registerProject(plugin);
//			}
//		} catch (Exception e) {
//			throw new ThinklabRuntimeException(e);
//		}
//	}
//
//	@Override
//	public void pluginDeactivated(Plugin plugin) {
//	}
//
//	@Override
//	public void pluginDisabled(PluginDescriptor arg0) {
//		ProjectFactory.get().removeProject(arg0.getId());
//	}
//
//	@Override
//	public void pluginEnabled(PluginDescriptor arg0) {
//	}
//}
