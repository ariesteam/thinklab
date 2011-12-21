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
package org.integratedmodelling.persistence.kbox;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class JarFileLoader extends URLClassLoader
{
    public JarFileLoader (URL[] urls)
    {
        super (urls);
    }

    public JarFileLoader(URL url) {
		this(new URL[]{url});
	}

	public void addFile (String path) throws MalformedURLException
    {
        String urlPath = "jar:file://" + path + "!/";
        addURL (new URL (urlPath));
    }
	
	

    public static void main (String args []) throws Exception
    {
//    	PluginClassLoader pcl = new PluginClassLoader("/Users/ionathan/Work/workspace/SeamlessV02/SeamFrameV0.2/knowledgemanager/ThinklabInstallation/KnowledgeBase/data/plugins/scratch/Persistence");
//    	//pcl.loadClass("org.seamless_ip.ontologies.farm.FarmIntensity")
//    	Class c = ClassLoader.getSystemClassLoader().loadClass("org.seamless_ip.ontologies.farm.FarmIntensity");
//    	
//    }
//        try
//        {
//        
//            System.out.println ("First attempt...");
//            Class c = Class.forName ("org.seamless_ip.ontologies.farm.FarmType");
//           System.out.println(c.getAnnotations()[0]);
//        }
//        catch (Exception ex)
//        {
//            System.out.println ("Failed.");
//        }
//
//        try
//        {
//            
    	URL loc = new URL("jar:file:///Users/ionathan/Work/workspace/SeamlessV02/SeamFrameV0.2/knowledgemanager/ThinklabInstallation/KnowledgeBase/data/plugins/scratch/Persistence/generated.jar!/");
            URLClassLoader cl = new URLClassLoader(new URL[]{loc},Thread.currentThread().getContextClassLoader());
////            cl.addFile ("/Users/ionathan/Work/workspace/SeamlessV02/SeamFrameV0.2/knowledgemanager/ThinklabInstallation/KnowledgeBase/data/plugins/scratch/Persistence/generated.jar");
////            
//            System.out.println ("Second attempt...");
////            
//            ZipFile jarFile = new ZipFile("/Users/ionathan/Work/workspace/SeamlessV02/SeamFrameV0.2/knowledgemanager/ThinklabInstallation/KnowledgeBase/data/plugins/scratch/Persistence/generated.jar");
//			Enumeration entries = jarFile.entries();
//			while (entries.hasMoreElements()) {
//				ZipEntry entry = (ZipEntry) entries.nextElement();
//        		if (entry.getName().endsWith(".class")) {
////////        			cl.loadClass(entry.toString());
//        			String e = entry.toString();
//        			System.out.println(e.substring(0, e.length()-6).replaceAll("/", "."));
//        			Class c = cl.loadClass(e.substring(0, e.length()-6).replaceAll("/", "."));
//        			
////        			cl.resolveClass(c);
//        		}
//			}
//////        
//////			entries = jarFile.entries();
//////			while (entries.hasMoreElements()) {
//////				ZipEntry entry = (ZipEntry) entries.nextElement();
//////        		if (entry.getName().endsWith(".xml")) {
//////        			System.out.println(entry);
//////        			cl.getResource(entry.toString());
//////        		}
//////			}
////			
////			
////		
           Thread.currentThread().setContextClassLoader(cl);
            System.out.println(Thread.currentThread().getContextClassLoader() == cl);
           // cl.getRe
            Class c = cl.loadClass("org.seamless_ip.ontologies.farm.FarmIntensity");
////            URL url = ClassLoader.getSystemClassLoader().getResource("META-INF/persistence.xml");
////            Enumeration<URL> u = ClassLoader.getSystemClassLoader().getResources("file:///Users/ionathan/Work/workspace/SeamlessV02/SeamFrameV0.2/knowledgemanager/ThinklabInstallation/KnowledgeBase/data/plugins/scratch/Persistence/generated.jar!/META-INF/persistence.xml");
////            while(u.hasMoreElements()){
////            	URL u1 = u.nextElement();
////            	System.out.println("****" + u1);
////            }
////            System.out.println ("Success!");
//            System.out.println(c.getAnnotations()[0]);
////            System.out.println(url.getFile());
////            
          
            Object in = c.newInstance();
            System.out.println(in);
////            
            
//            Class ch =  cl.loadClass("org.integratedmodelling.persistence.kbox.HibernateUtil");
//            SessionFactory sf = (SessionFactory) ch.getMethod("getSessionFactory", new Class[0]).invoke(null, new Object[0]);
//            org.hibernate.util.ConfigHelper.locateConfig("WEB-INF/hibernate.cfg.xml");
////////            
//            SessionFactory sf = HibernateUtil.getSessionFactory();
//////            Session s = sf.openSession();
//////            org.hibernate.Transaction tx = s.beginTransaction();
//////            Class c = cl.loadClass ("org.seamless_ip.ontologies.farm.FarmType");
//////            System.out.println(c.getAnnotations()[0]);
//////            Constructor con = c.getConstructor();
//////            Object in = con.newInstance();
//////            System.out.println(in);
//////            s.save(in);
//////            tx.commit();
//////            s.flush();
//////            s.close();
////            
//            
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("seamfaces");
////        	EntityManager em = emf.createEntityManager();
//            
//        	
//        	
//        }
//        catch (Exception ex)
//        {
//            System.out.println ("Failed.");
//            ex.printStackTrace ();
//        }
//    }
    }
}
