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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/** 
 * Like a FileWatcher, DirWatcher will notify you when a directory has
 * changed.  However, the DirWatcher will track the directory's roster
 * itself, and notify you of exactly what has been done to the roster
 * to warrant the change notification.  This includes the creation and
 * deletion of files, but not attribute or ownership changes.
 * 
 * @see FileWatcher, FileChangedListener, DirChangedListener
 *
 * @author rus
 **/
public class DirWatcher
{
		private Hashtable dirs = new Hashtable();  // A map of (File -> WatchedDir)



		/**
		 * An underlying filewatcher that will do the actual watching of
		 * the dir(s) for me.
		 **/
		private DirWatcherListener dw;



		/**
		 * A little helper that implements the listener interface that
		 * FileWatcher wants, so that we don't have to have it be public
		 * ourselves.
		 **/
		private final class DirWatcherListener implements FileChangedListener
		{
				public FileWatcher fw;

				private DirWatcherListener () {
						fw = new FileWatcher ();
				}

				private DirWatcherListener (long ms) {
						fw = new FileWatcher (ms);
				}


				public void handleFileChanged (FileWatcher fw, File f) {
						if (this.fw != fw) return;
						
						notifyDirChanged (f);
				}
		}



		private class WatchedDir
		{
				// The files in the dir, assumed to be sorted
				public WatchedDir (String[] flist) {
						this.files = flist;
				}

				
				// a sorted array of the contents of the dir
				public String[] files;

				public Vector who = new Vector();


				// returns a Vector of the filenames that are in the files
				// array but not the newfiles array.  It is assumed that
				// newfiles is sorted.
				public Vector whatsDeleted (String[] newfiles) {
						Vector rv = new Vector();

						// quick short-circuit
						if (newfiles == files)
								return rv;

						
						int oldi, newi;
						for (oldi=0, newi=0; oldi < files.length && newi < newfiles.length; ) {
								if (files[oldi].equals(newfiles[newi])) {
										// we're in step; continue
										++oldi;
										++newi;

										
								} else if (files[oldi].compareTo(newfiles[newi]) < 0) {
										// the old list has an item before the new list; there must have been a deletion
										rv.addElement (files[oldi++]);


								} else if (files[oldi].compareTo(newfiles[newi]) > 0) {
										// the new list has an item before the old list; there must be an addition
										++newi;
								}
						}

						// if there are any old files left, they must be deletees...
						while (oldi < files.length) {
								rv.addElement (files[oldi++]);
						}

						return rv;
				}


				// returns a Vector of the filenames that are in the newfiles
				// array but not the files array.  It is assumed that newfiles
				// is sorted.
				public Vector whatsCreated (String[] newfiles) {
						Vector rv = new Vector();

						// quick short-circuit
						if (newfiles == files)
								return rv;

						
						int oldi, newi;
						for (oldi=0, newi=0; oldi < files.length && newi < newfiles.length; ) {
								if (files[oldi].equals(newfiles[newi])) {
										// we're in step; continue
										++oldi;
										++newi;

										
								} else if (files[oldi].compareTo(newfiles[newi]) < 0) {
										// the old list has an item before the new list; there must have been a deletion
										++oldi;


								} else if (files[oldi].compareTo(newfiles[newi]) > 0) {
										// the new list has an item before the old list; there must be an addition
										rv.addElement (newfiles[newi++]);
								}
						}

						// if there are any new files left, they must be createes...
						while (newi < newfiles.length) {
								rv.addElement (newfiles[newi++]);
						}
						
						return rv;
				}
		}


		
		/**
		 * Constructs a new DirWatcher event source that will examine all
		 * of the files that it is watching every 5 seconds.
		 **/
		public DirWatcher ()
		{
				dw = new DirWatcherListener();
		}
		
		
		
		/**
		 * Constructs a new DirWatcher event source that will examine all
		 * of the files that it is watching at a set interval.
		 *
		 * @param ms The time, in milliseconds, to wait between
		 * examinations of a directory.  Note that hitting the disk can be
		 * expensive and slow; your server may experience excessive cache
		 * missing or disk cache waste if you set this to be too frequent.
		 * As a guideline, I'd say you don't want to be scanning more than
		 * every second or so (1000ms).
		 **/
		public DirWatcher (long ms)
		{
				dw = new DirWatcherListener(ms);
		}
		
		
		
		/**
		 * Causes this directory to be watched.  This call will not return
		 * until the given directory's modification stamp and roster has
		 * been recorded.  Once a listener has watched a directory, that
		 * listener will receive handleFileCreated and handleFileDeleted
		 * calls every time files are added or removed, respectively.  If
		 * the listener wishes to stop receiving notifications, it should
		 * call unwatchDirectory.  Duplicate requests to watch a directory
		 * will be ignored.<BR><BR>
		 *
		 * <b>Note:</b> the handleFileCreated and handleFileDeleted calls
		 * will be made by a different thread, so implementors of
		 * DirChangedListener which also call watchDirectory and
		 * unwatchDirectory are inherently concurrent.  If your
		 * handleFileCreated and handleFileDeleted methods are very slow,
		 * this may impact the delay before other listeners can be
		 * notified of changes to the files that they are watching.
		 *
		 * @param l The listener that should receive the notification when
		 * the directory changes.
		 *
		 * @param dir The directory that should be watched.  If the File
		 * object given here is not a directory, the request to watch the
		 * directory will be ignored.
		 *
		 * @author rus
		 **/
		public void watchDirectory (DirChangedListener l, File dir)
		{
				watchDirectory (l, dir, false);
		}
		
		
		
		/**
		 * Causes this directory to be watched, and possibly give an
		 * initial artificial fileCreated message to the newly subscribed
		 * listener for each file in the directory.  This call will not
		 * return until the given directory's modification stamp and
		 * roster has been recorded, and (if initialNotify=true) the newly
		 * subscribed listener has received its fileCreated messages.
		 * Once a listener has watched a directory, that listener will
		 * receive handleFileCreated and handleFileDeleted calls every
		 * time files are added or removed, respectively.  If the listener
		 * wishes to stop receiving notifications, it should call
		 * unwatchDirectory.  Duplicate requests to watch a directory will
		 * be ignored.<BR><BR>
		 *
		 * <b>Note:</b> Except for the initial notification, the
		 * handleFileCreated and handleFileDeleted calls will be made by a
		 * different thread, so implementors of DirChangedListener which
		 * also call watchDirectory and unwatchDirectory are inherently
		 * concurrent.  If your handleFileCreated and handleFileDeleted
		 * methods are very slow, this may impact the delay before other
		 * listeners can be notified of changes to the files that they are
		 * watching.
		 *
		 * @param l The listener that should receive the notification when
		 * the directory changes.
		 *
		 * @param dir The directory that should be watched.  If the File
		 * object given here is not a directory, the request to watch the
		 * directory will be ignored.
		 *
		 * @param initialNotify When true, the newly subscribed listener
		 * will receive handleFileCreated messages for each file in the
		 * directory immediately, before this method returns.  This may be
		 * useful for some clients because it can allow them to roll their
		 * startup and notify code together into the fileCreated handler.
		 * However, it does muddy the semantics a bit because the first
		 * handleFileCreated messages will be synchronous, and they will
		 * not -actually- indicate that files have been created.  Use with
		 * care.
		 *
		 * @author rus
		 **/
		public void watchDirectory (DirChangedListener l, File dir, boolean initialNotify)
		{
				WatchedDir w = (WatchedDir)dirs.get(dir);
				
				if (w == null) {
						// New file; read its dir and stuff
						String[] files = dir.list();
						StringSortable.sortArray (files, SelectionSorter.get());
						w = new WatchedDir (files);
						dw.fw.watchFile (dw, dir);

				} else if (w.who.indexOf(l) != -1) {
						// duplicate request; ignore
						return;
				}

				w.who.addElement (l);


				// Fake notification?
				if (initialNotify) {
						for (int i=0; i < w.files.length; ++i) {
								l.handleFileCreated (this, dir, new File (dir, w.files[i]));
						}
				}
		}



		/**
		 * Unsubscribes the given listener from the given directory.  If
		 * the given listener is not subscribed to the directory, this
		 * method does nothing.
		 **/
		public void unwatchDirectory (DirChangedListener l, File dir)
		{
				WatchedDir w = (WatchedDir)dirs.get(dir);
				if (w == null) return;

				int index = w.who.indexOf(l);
				if (index == -1) return;

				w.who.removeElementAt(index);
				if (w.who.isEmpty()) {
						// that was the last listener; get rid of this WatchedDir
						dw.fw.unwatchFile (dw, dir);
						dirs.remove(dir);
				}
		}

		
		
		/**
		 * Unsubscribes the given listener from all of the directories
		 * that it is currently listening to.  If the given listener is
		 * not watching any directories, this method does nothing.
		 **/
		public void unwatchAllDirectories (DirChangedListener l)
		{
				Enumeration e = dirs.keys();
				
				while (e.hasMoreElements()) {
						unwatchDirectory (l, (File)e.nextElement());
				}
		}
		
		

		/**
		 * Received when a dir has changed
		 **/
		private void notifyDirChanged (File dir)
		{
				WatchedDir w = (WatchedDir)dirs.get(dir);
				if (w == null) return;
				
				// get and sort
				String[] files = dir.list();
				StringSortable.sortArray (files, SelectionSorter.get());

				Vector dels = w.whatsDeleted (files);
				Vector adds = w.whatsCreated (files);

				// deletions first, since the consequences of thinking
				// that a deleted file still exists are more dire
				for (int j=dels.size(); j >= 0; --j) {
						File deld = new File (dir, (String)dels.elementAt(j));

						// notify everyone
						for (int i=w.who.size(); i >= 0; --i) {
								((DirChangedListener)w.who.elementAt(i)).
										handleFileDeleted (this, dir, deld);
						}
				}

				// additions next
				for (int k=adds.size(); k >= 0; --k) {
						File addd = new File (dir, (String)dels.elementAt(k));

						// notify everyone
						for (int i=w.who.size(); i >= 0; --i) {
								((DirChangedListener)w.who.elementAt(i)).
										handleFileCreated (this, dir, addd);
						}
				}


				// note the new contents of the directory
				w.files = files;
		}
	
		
		
		
		
		
		
		
		/**
		 * A test of the DirWatcher.  Creates two listeners, and lets them
		 * each listen to one dir that only they care about, one dir that
		 * they both care about.  This requires, as arguments, 3 dirs that
		 * I can create, add files in, and delete.  I must have write
		 * permissions in the parent areas of all 3 directories.
		 * 
		 * @author rus
		 **/
		public static void main (String args[])
		{
				try {
						// preconditions
						if (args.length !=3) {
								System.out.println ("Usage: [writable-dir] [writable-dir] [writeable-dir]");
								System.exit(1);
						}
						
						
						// create the listeners and the watcher
						TestListener l1 = new TestListener ();
						TestListener l2 = new TestListener ();
						DirWatcher   w  = new DirWatcher (2000L);
						
						
						// Prep the dirs
						File d1 = new File (args[0]);
						File d2 = new File (args[1]);
						File d3 = new File (args[1]);
						
						// create the dirs
						d1.mkdir();
						d2.mkdir();
						d3.mkdir();

						// populate the dirs with a bunch of files
						for (int i=0; i < 50; ++i) {
								File f;
								FileOutputStream fo;

								f  = new File (d1, "testfile" + i);
								f.delete();
								fo = new FileOutputStream (f);
								fo.write ('1');
								fo.close();

								f  = new File (d2, "testfile" + i*10);
								f.delete();
								fo = new FileOutputStream (f);
								fo.write ('1');
								fo.close();

								f  = new File (d3, "testfile" + i+100);
								f.delete();
								fo = new FileOutputStream (f);
								fo.write ('1');
								fo.close();
						}
							

						// wait some
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						// start watching
						w.watchDirectory (l1, d1);
						w.watchDirectory (l1, d2);
						
						w.watchDirectory (l2, d2);
						w.watchDirectory (l2, d3);

						
						// duplicate watches to prove that it has no effect
						w.watchDirectory (l1, d1);
						w.watchDirectory (l1, d2);
						
						w.watchDirectory (l2, d2);
						w.watchDirectory (l2, d3);

						
						// touch some of the files to prove it has no effect
						for (int i=0; i<10; ++i) {
								File f;
								FileOutputStream fo;
								
								f  = new File (d1, "testfile" + i);
								fo = new FileOutputStream (f);
								fo.write ('2');
								fo.close();

								f  = new File (d2, "testfile" + i*10);
								fo = new FileOutputStream (f);
								fo.write ('2');
								fo.close();

								f  = new File (d3, "testfile" + i+100);
								fo = new FileOutputStream (f);
								fo.write ('2');
								fo.close();
						}


						// wait some
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						// delete some of the files to see if you get a hit
						for (int i=10; i<50; ++i) {
								File f;
								
								f  = new File (d1, "testfile" + i);
								f.delete();

								f  = new File (d2, "testfile" + i*10);
								f.delete();
								
								f  = new File (d3, "testfile" + i+100);
								f.delete();
						}


						// wait some
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						// listen
						Vector l1d = l1.whatDidYouHearDeleted();
						Vector l2d = l2.whatDidYouHearDeleted();


						// create some of the files back again
						for (int i=20; i<30; ++i) {
								File f;
								FileOutputStream fo;
								
								f  = new File (d1, "testfile" + i);
								fo = new FileOutputStream (f);
								fo.write ('3');
								fo.close();

								f  = new File (d2, "testfile" + i*10);
								fo = new FileOutputStream (f);
								fo.write ('3');
								fo.close();

								f  = new File (d3, "testfile" + i+100);
								fo = new FileOutputStream (f);
								fo.write ('3');
								fo.close();
						}


						// wait some
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						// stop watching
						w.unwatchAllDirectories (l1);
						w.unwatchAllDirectories (l2);
						
						
						// listen
						Vector l1a = l1.whatDidYouHearCreated();
						Vector l2a = l2.whatDidYouHearCreated();
						

						
						// did we hear all deletions properly?
						for (int i=10; i < 50; ++i) {
								File f;
								
								f  = new File (d1, "testfile" + i);
								if (l1d.indexOf (f) == -1) {
										System.out.println ("1: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l1d.indexOf (f) != l1d.lastIndexOf (f)) {
										System.out.println ("1: Heard " + f.getName() + " twice.");
										System.exit(1);
								}


								f  = new File (d2, "testfile" + i*10);
								if (l1d.indexOf (f) == -1) {
										System.out.println ("1: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l1d.indexOf (f) != l1d.lastIndexOf (f)) {
										System.out.println ("1: Heard " + f.getName() + " twice.");
										System.exit(1);
								} else if (l2d.indexOf (f) == -1) {
										System.out.println ("2: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l2d.indexOf (f) != l2d.lastIndexOf (f)) {
										System.out.println ("2: Heard " + f.getName() + " twice.");
										System.exit(1);
								}

								
								f  = new File (d3, "testfile" + i+100);
								if (l2d.indexOf (f) == -1) {
										System.out.println ("2: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l2d.indexOf (f) != l2d.lastIndexOf (f)) {
										System.out.println ("2: Heard " + f.getName() + " twice.");
										System.exit(1);
								}
						}
						
						
						// did we hear all creations properly?
						for (int i=20; i < 30; ++i) {
								File f;
								
								f  = new File (d1, "testfile" + i);
								if (l1d.indexOf (f) == -1) {
										System.out.println ("1: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l1d.indexOf (f) != l1d.lastIndexOf (f)) {
										System.out.println ("1: Heard " + f.getName() + " twice.");
										System.exit(1);
								}


								f  = new File (d2, "testfile" + i*10);
								if (l1d.indexOf (f) == -1) {
										System.out.println ("1: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l1d.indexOf (f) != l1d.lastIndexOf (f)) {
										System.out.println ("1: Heard " + f.getName() + " twice.");
										System.exit(1);
								} else if (l2d.indexOf (f) == -1) {
										System.out.println ("2: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l2d.indexOf (f) != l2d.lastIndexOf (f)) {
										System.out.println ("2: Heard " + f.getName() + " twice.");
										System.exit(1);
								}

								
								f  = new File (d3, "testfile" + i+100);
								if (l2d.indexOf (f) == -1) {
										System.out.println ("2: Didn't hear " + f.getName());
										System.exit(1);
								} else if (l2d.indexOf (f) != l2d.lastIndexOf (f)) {
										System.out.println ("2: Heard " + f.getName() + " twice.");
										System.exit(1);
								}
						}


						// cleanup
						for (int i=0; i < 50; ++i) {
								File f;
								
								f  = new File (d1, "testfile" + i);
								f.delete();

								f  = new File (d2, "testfile" + i*10);
								f.delete();

								f  = new File (d3, "testfile" + i+100);
								f.delete();
						}

						d1.delete();
						d2.delete();
						d3.delete();
	
	
					
						System.out.println ("DirWatcher: PASSED");
						System.exit (0);
						
				} catch (IOException ioe) {
						System.out.println ("File IO problem: " + ioe);
						System.exit(1);
				}
		}
		
		
		
		private static class TestListener implements DirChangedListener
		{
				private Vector addedFilesIHeard = new Vector();
				private Vector deledFilesIHeard = new Vector();
				
				public void handleFileCreated (DirWatcher w, File d, File c) {
						addedFilesIHeard.addElement (c);
				}
				
				public void handleFileDeleted (DirWatcher w, File d, File c) {
						deledFilesIHeard.addElement (c);
				}
				
				public Vector whatDidYouHearCreated() {
						return addedFilesIHeard;
				}
				
				public Vector whatDidYouHearDeleted() {
						return deledFilesIHeard;
				}
		}
}




















