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
 * An Event source that notifies listeners when files have changed on
 * disk.  In a perfect world, file systems would provide some kind of
 * signaling mechanism so that you could find out when your precious
 * files have been touched.  Unfortunately, no such thing exists, so
 * you have to just go around and look at all the files.<BR><BR>
 *
 * Users subscribe to particular files, and then those files'
 * modification dates are scanned.  When a file is touched, the
 * subscriber receives a FileChanged message.<BR><BR>
 *
 * When the file to be watched is a directory, the subscriber will be
 * notified whenever files are added, removed, or renamed in the
 * directory.  However, no specific information about what has changed
 * will be given.  To receive a specific "file added" or "file
 * removed" message whenever a directory's roster is modified, you
 * should use a DirWatcher instead.
 *
 * @see DirWatcher, DirChangedListener, FileChangedListener
 *
 **/
public class FileWatcher
{
		private Hashtable files    = new Hashtable();  // A map of (File -> WatchedFile)
		
		
		
		/**
		 * There's one of these for each file being watched.  It contains
		 * the last known mod time, and who wants to know about this file.
		 **/
		private class WatchedFile
		{
				public WatchedFile (long time) {
						m      = time;
				}
				
				
				public long   m   = 0L;            // the last known mod time
				public Vector who = new Vector();  // the list of FileChangedListeners subscribed
		}
		
		
		
		
		
		
		
		
		private WatcherThread thread = null;  // the thread; null means no thread is alive now
		private long      lastScan   = 0L;    // the time since the last scan
		private boolean   stayAlive  = true;  // true if the thread should continue examining
		private long      interval   = 5000L; // how long to wait between scans
		
		
		
		/**
		 * The thread that actually watches the files
		 **/
		private class WatcherThread extends Thread
		{
				public WatcherThread () {
						super();
						start();
				}
				
				
				
				public void run () {
						while (stayAlive) {
								// mark the time
								lastScan = System.currentTimeMillis();
								
								
								// examine the files
								Enumeration e = files.keys();
								while (e.hasMoreElements()) {
										examineAndNotify ((File)e.nextElement());
								}
								
								
								// sleep until it's time to examine again
								long now = System.currentTimeMillis();
								
								if (now-lastScan < interval) {
										try {
												sleep (interval - (now-lastScan));
										} catch (InterruptedException ie) { /* eh... */ }
								}
						}
						
						
						// stop executing and indicate that I'm dead
						thread = null;
				}
		}
		
		
		
		/**
		 * Constructs a new FileWatcher event source that will examine all
		 * of the files that it is watching every 5 seconds.
		 **/
		public FileWatcher () {}
		
		
		
		/**
		 * Constructs a new FileWatcher event source that will examine all
		 * of the files that it is watching at a set interval.
		 *
		 * @param ms The time, in milliseconds, to wait between
		 * examinations of a file.  Note that hitting the disk can be
		 * expensive and slow; your server may experience excessive cache
		 * missing or disk cache waste if you set this to be too frequent.
		 * As a guideline, I'd say you don't want to be scanning more than
		 * every second or so (1000ms).
		 **/
		public FileWatcher (long ms)
		{
				interval = ms;
		}
		
		
		
		/**
		 * Causes this file to be watched.  This call will not return
		 * until the given File's modification stamp has been recorded.
		 * Once a listener has watched a file, that listener will receive
		 * handleFileChanged calls every time the file's date stamp has
		 * been touched.  If the listener wishes to stop receiving
		 * notifications, it should call unwatchFile.  Duplicate requests
		 * to watch a file will be ignored.<BR><BR>
		 *
		 * <b>Note:</b> the handleFileChanged calls will be made by a
		 * different thread, so implementors of FileChangedListener which
		 * also call watchFile and unwatchFile are inherently concurrent.
		 * If your handleFileChanged method is very slow, this may impact
		 * the delay before other listeners can be notified of changes to
		 * the files that they are watching.
		 *
		 * @param l The listener that should receive the notification when
		 * the file changes.
		 *
		 * @param f The file that should be watched.
		 *
		 * @author rus
		 **/
		public void watchFile (FileChangedListener l, File f)
		{
				watchFile (l, f, false);
		}
		
		
		
		/**
		 * Causes this file to be watched, and possibly give an initial
		 * artificial fileChanged message to the newly subscribed
		 * listener.  This call will not return until the given File's
		 * modification stamp has been recorded and (if
		 * initialNotify=true) the newl subscribed listener has received
		 * its first fileChanged message.  Once a listener has watched a
		 * file, that listener will receive handleFileChanged calls every
		 * time the file's date stamp has been touched.  If the listener
		 * wishes to stop receiving notifications, it should call
		 * unwatchFile.  Duplicate requests to watch a file will be
		 * ignored.<BR><BR>
		 *
		 * <b>Note:</b> Except for the initial notification, the
		 * handleFileChanged calls will be made by a different thread, so
		 * implementors of FileChangedListener which also call watchFile
		 * and unwatchFile are inherently concurrent.  If your
		 * handleFileChanged method is very slow, this may impact the
		 * delay before other listeners can be notified of changes to the
		 * files that they are watching.
		 *
		 * @param l The listener that should receive the notification when
		 * the file changes.
		 *
		 * @param f The file that should be watched.
		 *
		 * @param initialNotify When true, the newly subscribed listener
		 * will receive a handleFileChanged message immediately, before
		 * this method returns.  This may be useful for some clients
		 * because it can allow them to roll their startup and notify code
		 * together into the fileChanged handler.  However, it does muddy
		 * the semantics a bit because the first handleFileChanged message
		 * will be synchronous, and it will not -actually- indicate that
		 * the file has changed.  Use with care.
		 *
		 * @author rus
		 **/
		public void watchFile (FileChangedListener l, File f, boolean initialNotify)
		{
				// preconditions
				if (l == null)
						throw new NullPointerException ("Can't notify a null listener.");
				if (f == null)
						throw new NullPointerException ("Can't watch a null file.");
				
				
				// Ensure that the file stamp is up to date; otherwise it
				// is possible that the newly added listener could be
				// notified of a change to the file that happened before
				// it ever subscribed, which seems wrong.
				examineAndNotify (f);
				
				
				WatchedFile node = (WatchedFile)files.get (f);
				if (node != null) {
						
						// Already watching this file
						if (node.who.indexOf (l) != -1) {
								// A duplicate request; ignore it
								return;
						}
						
				} else {
						// A new file to watch!  Yay!
						node = new WatchedFile (f.lastModified());
						files.put (f, node);
				}
				
				
				// Give the artificial notification if they asked for it
				if (initialNotify) {
						l.handleFileChanged (this, f);
				}
				
				// Add the listener.  Do this last to ensure that the
				// artificial first notification really is the first one they
				// get.
				node.who.addElement (l);
				
				// Possibly start or stop the thread, if there are listeners
				// now.
				controlThread();
		}
		
		
		
		/**
		 * Unsubscribes the given listener from the given file.  If the
		 * given listener is not subscribed to the file, this method does
		 * nothing.
		 **/
		public void unwatchFile (FileChangedListener l, File f)
		{
				// preconditions
				if (l == null)
						throw new NullPointerException ("Can't unnotify a null listener.");
				if (f == null)
						throw new NullPointerException ("Can't unwatch a null file.");
				
				removeWatcherFromTable (l, f);
				
				
				// Possibly start or stop the thread, if there are listeners
				// now.
				controlThread();
		}
		
		
		/**
		 * Unsubscribes the given listener from all of the files that it
		 * is currently listening to.  If the given listener is not
		 * watching any files, this method does nothing.
		 **/
		public void unwatchAllFiles (FileChangedListener l)
		{
				// preconditions
				if (l == null)
						throw new NullPointerException ("Can't unnotify a null listener.");
				
				
				Enumeration e = files.keys();
				
				while (e.hasMoreElements()) {
						removeWatcherFromTable (l, (File)e.nextElement());
				}
				
				
				// Possibly start or stop the thread, if there are listeners
				// now.
				controlThread();
		}
		
		
		
		/**
		 * Updates the data structures as necessary to remove the given
		 * listener from the give file.  Does not do thread control.
		 **/
		private void removeWatcherFromTable (FileChangedListener l, File f)
		{
				WatchedFile node = (WatchedFile)files.get (f);
				if (node != null) {
						// Already watching this file
						node.who.removeElement (l);
						
						if (node.who.isEmpty()) {
								// No one cares about this file any more.  Ditch it
								files.remove(f);
						}
				}
		}
		
		
		
		/**
		 * Checks the listener data structures to see if there are any
		 * listeners.  If so, and the thread is not running, starts up the
		 * examiner thread.  If not, and the thread is running, stops the
		 * examiner thread.
		 **/
		private void controlThread ()
		{
				if (files.isEmpty()) {
						// should die
						stayAlive = false;
						
				} else {
						// should run
						stayAlive = true;
						if (thread == null)
								thread = new WatcherThread ();
				}
		}
		
		
		
		/**
		 * Inspects the file stamp of the given file, updates the stamp in
		 * the WatchedFile if it has changed, and notifies any listeners
		 * if it has changed.
		 **/
		public void examineAndNotify (File f)
		{
				WatchedFile node = (WatchedFile)files.get (f);
				if (node == null) return; // precondition
				
				
				long modTime = f.lastModified();
				
				
				if (node.m != modTime) {
						// gotta update
						node.m = modTime;
						
						
						// and notify everyone
						Enumeration e = node.who.elements();
						while (e.hasMoreElements()) {
								((FileChangedListener)e.nextElement()).handleFileChanged (this, f);
						}
						
						
				} else {
				}
		}
		
		
		
		
		
		
		
		
		/**
		 * A test of the FileWatcher.  Creates two listeners, and lets
		 * them each listen to one file that only they care about, one
		 * file that they both care about, and the directory that contains
		 * the files.  This requires, as arguments, 4 files that I can
		 * create and touch.  The files must be in a directory that I have
		 * write permissions on, because I need to delete and create
		 * files.
		 * 
		 * @author rus
		 **/
		public static void main (String args[])
		{
				try {
						// preconditions
						if (args.length != 4) {
								System.out.println ("Usage: [writable-file] [writable-file] [writeable-file] [writable-file]");
								System.exit(1);
						}
						
						
						// create the listeners and the watcher
						TestListener l1 = new TestListener ();
						TestListener l2 = new TestListener ();
						FileWatcher  w  = new FileWatcher (2000L);
						
						
						// Prep the files; delete the one I need to create and create
						// the other three.
						File f1 = new File (args[0]);
						File f2 = new File (args[1]);
						File cm = new File (args[2]);
						File nw = new File (args[3]);
						File dr = new File (nw.getParent());
						
						FileOutputStream fo;
						
						fo = new FileOutputStream (f1);
						fo.write ('1');
						fo.close();
						
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						fo = new FileOutputStream (f2);
						fo.write ('2');
						fo.close();
						
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						fo = new FileOutputStream (cm);
						fo.write ('3');
						fo.close();
						
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						nw.delete ();
						if (nw.exists()) {
								System.out.println ("Can't delete file: " + args[3]);
								System.exit(1);
						}
						
						
						// start watching
						w.watchFile (l1, f1);
						w.watchFile (l1, cm);
						w.watchFile (l1, dr);
						
						w.watchFile (l2, f2);
						w.watchFile (l2, cm);
						w.watchFile (l2, dr);
						
						// duplicate watches to prove that it has no effect
						// w.watchFile (l1, f1);
						// w.watchFile (l1, cm);
						// w.watchFile (l1, dr);
						// 
						// w.watchFile (l2, f2);
						// w.watchFile (l2, cm);
						// w.watchFile (l2, dr);
						
						
						// touch the files
						fo = new FileOutputStream (f1);
						fo.write ('a');
						fo.close();
						
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						fo = new FileOutputStream (f2);
						fo.write ('b');
						fo.close();
						
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						fo = new FileOutputStream (cm);
						fo.write ('c');
						fo.close();
						
						
						try {
								Thread.sleep (1000L);
						} catch (InterruptedException ie) {}
						
						
						fo = new FileOutputStream (nw);
						fo.write ('d');
						fo.close();
						
						try {
								Thread.sleep (5000L);
						} catch (InterruptedException ie) {}
						
						
						// stop watching
						w.unwatchAllFiles (l1);
						w.unwatchAllFiles (l2);
						
						
						// Let's see what we heard...
						Vector l1f = l1.whatDidYouHear();
						Vector l2f = l2.whatDidYouHear();
						
						if (l1f.indexOf (f1) == -1) {
								System.out.println ("Didn't hear " + f1.getName());
								System.exit(1);
						}
						
						if (l1f.indexOf (f1) != l1f.lastIndexOf (f1)) {
								System.out.println ("Heard " + f1.getName() + " more than once.");
								System.exit(1);
						}
						
						if (l1f.indexOf (cm) == -1) {
								System.out.println ("Didn't hear " + cm.getName());
								System.exit(1);
						}
						
						if (l1f.indexOf (cm) != l1f.lastIndexOf (cm)) {
								System.out.println ("Heard " + cm.getName() + " more than once.");
								System.exit(1);
						}
						
						if (l1f.indexOf (dr) == -1) {
								System.out.println ("Didn't hear " + dr.getName());
								System.exit(1);
						}
						
						if (l1f.indexOf (dr) != l1f.lastIndexOf (dr)) {
								System.out.println ("Heard " + dr.getName() + " more than once.");
								System.exit(1);
						}
						
						if (l2f.indexOf (f2) == -1) {
								System.out.println ("Didn't hear " + f2.getName());
								System.exit(1);
						}
						
						if (l2f.indexOf (f2) != l2f.lastIndexOf (f2)) {
								System.out.println ("Heard " + f2.getName() + " more than once.");
								System.exit(1);
						}
						
						if (l2f.indexOf (cm) == -1) {
								System.out.println ("Didn't hear " + cm.getName());
								System.exit(1);
						}
						
						if (l2f.indexOf (cm) != l2f.lastIndexOf (cm)) {
								System.out.println ("Heard " + cm.getName() + " more than once.");
								System.exit(1);
						}
						
						if (l2f.indexOf (dr) == -1) {
								System.out.println ("Didn't hear " + dr.getName());
								System.exit(1);
						}
						
						if (l2f.indexOf (dr) != l2f.lastIndexOf (dr)) {
								System.out.println ("Heard " + dr.getName() + " more than once.");
								System.exit(1);
						}
						
						System.out.println ("FileWatcher: PASSED");
						System.exit (0);
						
				} catch (IOException ioe) {
						System.out.println ("File IO problem: " + ioe);
						System.exit(1);
				}
		}
		
		
		
		private static class TestListener implements FileChangedListener
		{
				private Vector filesIHeard = new Vector();
				
				public void handleFileChanged (FileWatcher w, File f) {
						filesIHeard.addElement (f);
				}
				
				public Vector whatDidYouHear() {
						return filesIHeard;
				}
		}
}




















