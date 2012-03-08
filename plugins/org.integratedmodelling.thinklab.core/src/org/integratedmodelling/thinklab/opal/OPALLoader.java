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
//import java.io.File;
//import java.net.URL;
//import java.util.Collection;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.thinklab.api.knowledge.IInstance;
//import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
//import org.integratedmodelling.thinklab.api.runtime.ISession;
//import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
//
//public class OPALLoader implements KnowledgeLoader {
//
//	public Collection<IInstance> loadKnowledge(URL url, ISession session, IKBox kbox) throws ThinklabException {
//		
//		OPALValidator val = new OPALValidator();
//		return val.validate(url, session, kbox);
//	}
//
//
//	public void writeKnowledge(File outfile, String format, IInstance... instances)
//			throws ThinklabException {
//		
//		OPALWriter.writeInstances(outfile, format, instances);
//		
//	}
//
//
//}
