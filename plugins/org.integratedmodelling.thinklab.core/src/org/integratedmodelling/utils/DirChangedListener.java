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
package org.integratedmodelling.utils;

/*
 * Copyright (C) 1999-2003 Russell Heywood @ devtools.org
 * All Rights Reserved.
 *
 * It is illegal to distribute this file under conditions other than
 * those expressly permitted in the GNU General Public License.  You
 * should have received a copy of the GPL with the distribution
 * containing this source file.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version. 
 */

import java.io.File;



/** 
 * Implement this interface if you'd like to be notified of changes to
 * the contents of a directory.  Classes that implement this interface
 * may add themselves to a FileWatcher, which will examine directories
 * at intervals and notify them of added or removed files.
 **/
public interface DirChangedListener
{
		/**
		 * This method will be called by FileWatcher when a file has been
		 * added to a directory that this class has subscribed to.
		 * FileWatcher will call this method once for each added file.
		 *
		 * @param w The FileWatcher that has detected the change.  This
		 * class is therefore listening to this FileWatcher.
		 *
		 * @param changedDir The directory that has changed.  This class
		 * is therefore listening to this directory via the watchDirectory
		 * method.
		 *
		 * @param newFile The File that has been added to the watched
		 * directory.  <b>Note:</b> Although this file has been added,
		 * this class is not automatically subscribed to it.
		 *
		 * @author rus
		 **/
		public void handleFileCreated (DirWatcher w, File changedDir, File newFile);


		/**
		 * This method will be called by FileWatcher when a file has been
		 * removed from a directory that this class has subscribed to.
		 * FileWatcher will call this method once for each removed file.
		 *
		 * @param w The FileWatcher that has detected the change.  This
		 * class is therefore listening to this FileWatcher.
		 *
		 * @param changedDir The directory that has changed.  This class
		 * is therefore listening to this directory via the watchDirectory
		 * method.
		 *
		 * @param deletedFile The File that has been deleted from the
		 * watched directory.  This file is likely not to exist anymore,
		 * so it would behoove you not to attempt to open it or access it.
		 *
		 * @author rus
		 **/
		public void handleFileDeleted (DirWatcher w, File changedDir, File deletedFile);
}


