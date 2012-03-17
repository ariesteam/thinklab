package org.integratedmodelling.utils;

//Revised from hibernate search util
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;


/**
* Utility class for synchronizing files/directories.
*
* @author Emmanuel Bernard
* @author Sanne Grinovero
* @author Hardy Ferentschik
*/
public abstract class FileSync {

private static final int FAT_PRECISION = 2000;
public static final long DEFAULT_COPY_BUFFER_SIZE = 16 * 1024 * 1024; // 16 MB


public static boolean areInSync(File source, File destination) throws IOException {
  if ( source.isDirectory() ) {
    if ( !destination.exists() ) {
      return false;
    }
    else if ( !destination.isDirectory() ) {
      throw new IOException(
          "Source and Destination not of the same type:"
              + source.getCanonicalPath() + " , " + destination.getCanonicalPath()
      );
    }
    String[] sources = source.list();
    Set<String> srcNames = new HashSet<String>( Arrays.asList( sources ) );
    String[] dests = destination.list();

    // check for files in destination and not in source
    for ( String fileName : dests ) {
      if ( !srcNames.contains( fileName ) ) {
        return false;
      }
    }

    boolean inSync = true;
    for ( String fileName : sources ) {
      File srcFile = new File( source, fileName );
      File destFile = new File( destination, fileName );
      if ( !areInSync( srcFile, destFile ) ) {
        inSync = false;
        break;
      }
    }
    return inSync;
  }
  else {
    if ( destination.exists() && destination.isFile() ) {
      long sts = source.lastModified() / FAT_PRECISION;
      long dts = destination.lastModified() / FAT_PRECISION;
      return sts == dts;
    }
    else {
      return false;
    }
  }
}

public static void synchronize(File source, File destination) throws ThinklabException {
  synchronize( source, destination, DEFAULT_COPY_BUFFER_SIZE );
}

public static void synchronize(File source, File destination, long chunkSize) throws ThinklabException {
  if ( chunkSize <= 0 ) {
    System.out.println("Chunk size must be positive: using default value." );
    chunkSize = DEFAULT_COPY_BUFFER_SIZE;
  }
  if ( source.isDirectory() ) {
    if ( !destination.exists() ) {
      if ( !destination.mkdirs() ) {
        throw new ThinklabIOException( "Could not create path " + destination );
      }
    }
    else if ( !destination.isDirectory() ) {
      throw new ThinklabIOException(
          "Source and Destination not of the same type:"
              + source + " , " + destination
      );
    }
    String[] sources = source.list();
    Set<String> srcNames = new HashSet<String>( Arrays.asList( sources ) );
    String[] dests = destination.list();

    //delete files not present in source
    for ( String fileName : dests ) {
      if ( !srcNames.contains( fileName ) ) {
        delete( new File( destination, fileName ) );
      }
    }
    //copy each file from source
    for ( String fileName : sources ) {
      File srcFile = new File( source, fileName );
      File destFile = new File( destination, fileName );
      synchronize( srcFile, destFile, chunkSize );
    }
  }
  else {
    if ( destination.exists() && destination.isDirectory() ) {
      delete( destination );
    }
    if ( destination.exists() ) {
      long sts = source.lastModified();
      long dts = destination.lastModified();
      //do not copy if smart and same timestamp and same length
      if (dts < sts) {
        copyFile( source, destination, chunkSize );
      }
    }
    else {
      copyFile( source, destination, chunkSize );
    }
  }
}

	private static void copyFile(File srcFile, File destFile, long chunkSize)
			throws ThinklabIOException {
		FileInputStream is = null;
		FileOutputStream os = null;
		try {
			try {
				is = new FileInputStream(srcFile);
				FileChannel iChannel = is.getChannel();
				os = new FileOutputStream(destFile, false);
				FileChannel oChannel = os.getChannel();
				long doneBytes = 0L;
				long todoBytes = srcFile.length();
				while (todoBytes != 0L) {
					long iterationBytes = Math.min(todoBytes, chunkSize);
					long transferredLength = oChannel.transferFrom(iChannel,
							doneBytes, iterationBytes);
					if (iterationBytes != transferredLength) {
						throw new IOException(
								"Error during file transfer: expected "
										+ iterationBytes + " bytes, only "
										+ transferredLength + " bytes copied.");
					}
					doneBytes += transferredLength;
					todoBytes -= transferredLength;
				}
			} finally {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			}
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		// boolean successTimestampOp = destFile.setLastModified(
		// srcFile.lastModified() );
		// if ( !successTimestampOp ) {
		// System.out.println("Could not change timestamp for {}. Index synchronization may be slow. "
		// + destFile );
		// }
	}

public static void delete(File file) {
  if ( file.isDirectory() ) {
    for ( File subFile : file.listFiles() ) {
      delete( subFile );
    }
  }
  if ( file.exists() ) {
    if ( !file.delete() ) {
      System.out.println( "Could not delete {}" + file );
    }
  }
}
}