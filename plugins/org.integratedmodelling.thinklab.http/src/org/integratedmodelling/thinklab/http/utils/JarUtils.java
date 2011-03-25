package org.integratedmodelling.thinklab.http.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarUtils {

	private static FileOutputStream fos = null;
	private static JarOutputStream jos = null;
	private static int iBaseFolderLength = 0;

	public static void checkDirectory(String directoryName) {
		File dirobject = new File(directoryName);
		if (dirobject.exists() == true) {
			if (dirobject.isDirectory() == true) {
				File[] fileList = dirobject.listFiles();
				// Loop through the files
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].isDirectory()) {
						checkDirectory(fileList[i].getPath());
					} else if (fileList[i].isFile()) {
						// Call the zipFunc function
						jarFile(fileList[i].getPath());
					}
				}
			} else {
				System.out.println(directoryName + " is not a directory.");
			}
		} else {
			System.out.println("Directory " + directoryName
					+ " does not exist.");
		}
	}

	// a Jar method.
	private static void jarFile(String filePath) {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			JarEntry fileEntry = new JarEntry(filePath
					.substring(iBaseFolderLength));
			jos.putNextEntry(fileEntry);
			byte[] data = new byte[1024];
			int byteCount;
			while ((byteCount = bis.read(data, 0, 1024)) > -1) {
				jos.write(data, 0, byteCount);
			}
		} catch (IOException e) {
		}
		System.out.println(filePath.substring(iBaseFolderLength));
	}

	private static void test(String checkDir, String outputPath,
			String manifestVerion, String mainClass, String classPath) {
		try {
			System.out.println(checkDir);
			// Create the file output streams for both the file and the zip.
			String strBaseFolder = checkDir + "/";
			iBaseFolderLength = strBaseFolder.length();
			fos = new FileOutputStream(outputPath);
			Manifest manifest = new Manifest();
			Attributes manifestAttr = manifest.getMainAttributes();
			//note:Must set Manifest-Version,or the manifest file will be empty!
			if (manifestVerion != null) {
				manifestAttr.putValue("Manifest-Version", manifestVerion);
				if (mainClass != null) {
					manifestAttr.putValue("Main-Class", mainClass);
				}
				if (classPath != null) {
					manifestAttr.putValue("Class-Path", classPath);
				}
			}
			java.util.Set entries = manifestAttr.entrySet();
			for (java.util.Iterator i = entries.iterator(); i.hasNext();) {
				System.out.println("Manifest attribute:>> "
						+ i.next().toString());
			}

			jos = new JarOutputStream(fos, manifest);

			System.out.println(strBaseFolder);

			checkDirectory(checkDir);
			// Close the file output streams
			jos.flush();
			jos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String[] paramters = {};
		test("E:\\j2sdk1.4.2_03\\Jdevelope\\dbBackup\\bin",
				"E:\\j2sdk1.4.2_03\\Jdevelope\\dbBackup\\test.jar", "1.0",
				"dbbackup.Main", "lib/jdom.jar");
	}

}
