package org.integratedmodelling.clojure;

import clojure.lang.DynamicClassLoader;

public class MultiClassLoader extends DynamicClassLoader {

	ClassLoader[] cls = null;
		
	MultiClassLoader(ClassLoader ...classLoaders) {
		cls = classLoaders;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> ret = null;
		for (ClassLoader cl : cls)
			if ((ret = cl.loadClass(name)) != null)
				break;
		return ret;
	}

}
