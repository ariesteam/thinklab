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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

/**
 * FolderZiper provide a static method to zip a folder.
 * Added: the filtering of filetypes 
 * 
 * @author pitchoun
 * @author ionathan
 *  
 * @see http://www.theserverside.com/discussions/thread.tss?thread_id=34906
 * @since Jan 2007 
 */

public class FolderZiper {

	/**
	 * Zip the srcFolder into the destFileZipFile. All the folder subtree of the
	 * src folder is added to the destZipFile archive.
	 * 
	 * TODO handle the usecase of srcFolder being en file.
	 * 
	 * @param srcFolder
	 *            String, the path of the srcFolder
	 * @param destZipFile
	 *            String, the path of the destination zipFile. This file will be
	 *            created or erased.
	 */
	static public void zipFolder(String srcFolder, String destZipFile) {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		try {
			fileWriter = new FileOutputStream(destZipFile);
			zip = new ZipOutputStream(fileWriter);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}

		addFolderToZip("", srcFolder, zip);
		try {
			zip.flush();
			zip.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	static public void zipSubFolders(String srcFolder, String destZipFile) {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		try {
			fileWriter = new FileOutputStream(destZipFile);
			zip = new ZipOutputStream(fileWriter);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		
		File folder = new File(srcFolder);
		String fileLists[] = folder.list();
		for(String f: fileLists){
			File dir = new File(folder+"/"+f);
			if (dir.isDirectory())
				addFolderToZip("", f, zip);
			else
				addToZip("", dir.toString(), zip);
		}
		try {
			zip.flush();
			zip.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Write the content of srcFile in a new ZipEntry, named path+srcFile, of
	 * the zip stream. The result is that the srcFile will be in the path folder
	 * in the generated archive.
	 * 
	 * @param path
	 *            String, the relatif path with the root archive.
	 * @param srcFile
	 *            String, the absolute path of the file to add
	 * @param zip
	 *            ZipOutputStram, the stream to use to write the given file.
	 */
	static private void addToZip(String path, String srcFile, ZipOutputStream zip) {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			try {
				FileInputStream in = new FileInputStream(srcFile);
//				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				zip.putNextEntry(new ZipEntry(MiscUtilities.getFileName(srcFile)));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * add the srcFolder to the zip stream.
	 * 
	 * @param path
	 *            String, the relatif path with the root archive.
	 * @param srcFile
	 *            String, the absolute path of the file to add
	 * @param zip
	 *            ZipOutputStram, the stream to use to write the given file.
	 */
	static private void addFolderToZip(String path, String srcFolder,
			ZipOutputStream zip) {
		File folder = new File(srcFolder);
		String fileListe[] = folder.list();
		try {
			int i = 0;
			while (true) {
				addToZip(path + "/" + folder.getName(), srcFolder + "/" + fileListe[i], zip);
				i++;
			}
		} catch (Exception ex) {
		}
	}


	static private void addFolderToZip(String path, String srcFolder,
				ZipOutputStream zip, FilenameFilter filter) {
			File folder = new File(srcFolder);
			String fileListe[] = folder.list(filter);
			try {
				int i = 0;
				while (true) {
					addToZip(path + "/" + folder.getName(), srcFolder + "/"
							+ fileListe[i], zip);
					i++;
				}
			} catch (Exception ex) {
			}
		}
	
	public static void unzip(File sourceZipFile, File unzipDestinationDirectory)
			throws ThinklabIOException {
		
		int BUFFER = 2048;
		List<String> zipFiles = new ArrayList<String>();
		
		unzipDestinationDirectory.mkdir();

		ZipFile zipFile;
		try {
			zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
		} catch (IOException e1) {
			throw new ThinklabIOException(e1);
		}

		// Create an enumeration of the entries in the zip file
		Enumeration<?> zipFileEntries = zipFile.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

			String currentEntry = entry.getName();			
			File destFile = new File(unzipDestinationDirectory, currentEntry);

			if (currentEntry.endsWith(".zip")) {
				zipFiles.add(destFile.getAbsolutePath());
			}

			// grab file's parent directory structure
			File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();

			try {
				// extract file if not a directory
				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(
							zipFile.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER];

					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos,
							BUFFER);

					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();
				}
			} catch (IOException ioe) {
				throw new ThinklabIOException(ioe);
			}
		}
		try {
			zipFile.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}

		for (Iterator<String> iter = zipFiles.iterator(); iter.hasNext();) {
			String zipName = iter.next();
			unzip(new File(zipName), new File(unzipDestinationDirectory + File.separator
					+ zipName.substring(0, zipName.lastIndexOf(".zip"))));
		}

	}
	
}

