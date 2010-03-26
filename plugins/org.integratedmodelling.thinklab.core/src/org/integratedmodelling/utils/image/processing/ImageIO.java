package org.integratedmodelling.utils.image.processing ;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.net.URL;

/**
 * This class contains static method to load/save images into memory
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 */
public class ImageIO extends Object {
	private static final int GIFMAGIC=0x4749 ;
	private static final int JPGMAGIC=0xffd8 ;
	private static final int PPMMAGIC=0x5033 ;
	private static final int PPMMAGIC1=0x5036 ;
	private static final int PGMMAGIC=0x5032 ;
	private static final int PGMMAGIC1=0x5035 ;

	public static final int JPG_IMG=1 ;
	public static final int GIF_IMG=2 ;
	public static final int PPM_IMG=3 ;
	public static final int PGM_IMG=4 ;

	/**
   * private methods, load a GIF or JPEG image into memory
	 */
	private static int[] load(Image image, Dimension wh) throws IOException {
    PixelGrabber grabber = new PixelGrabber(image, 0, 0, -1, -1, true);
    try {
      if(grabber.grabPixels() != true)
        throw new IOException("Grabber false: " + grabber.status());
    } catch (InterruptedException e) {
      System.out.println("Grabber interrupted by anohter thread!\n") ;
    }
		wh.width= grabber.getWidth();
		wh.height= grabber.getHeight();
    return (int [])(grabber.getPixels());
/*
		wh.width=-1; wh.height=-1 ;
		while( wh.width==-1) wh.width= image.getWidth(null);
		while( wh.height==-1) wh.height = image.getHeight(null);

		int size=wh.width*wh.height ;
    int values[] = new int[size];
    PixelGrabber grabber = new PixelGrabber(
      image, 0, 0, wh.width, wh.height, values, 0, wh[0]);

    try {
      if(grabber.grabPixels() != true)
        throw new IOException("Grabber false: " + grabber.status());
    } catch (InterruptedException e) {
      System.out.println("Grabber interrupted by anohter thread!\n") ;
    }
    return values ;
*/
	}

	/**
   * Get image type (JPG_IMG, GIF_IMG, PPM_IMG or PGM_IMG)
   * Return 0 if it is a un-supported image type
	 * @param in InputStream of the desired image
	 */
	public static int getImageType(InputStream in) {
		byte[] mag=new byte[2] ;
		try {
		in.read(mag) ;
		int t=((int)mag[0])<<8 | mag[1] ;
		switch(t){
		  case GIFMAGIC: return GIF_IMG ;
			case JPGMAGIC: return JPG_IMG ;
  		case PPMMAGIC:
		  case PPMMAGIC1: return PPM_IMG ;
			case PGMMAGIC:
			case PGMMAGIC1: return PGM_IMG ;
		  default: return 0 ;
		}
		}catch(IOException e) {
			return 0;
		}
	}

	/**
   * Read Image into an integer array (rgb-alpha fromat)
	 * @param img the Image object
	 * @param wh return width and height of the loaded image
	 */
  public static int[] readImage(Image img, Dimension wh) throws IOException{
		return load(img, wh) ;
	}

	/**
   * Read GIF or JPEG images into an integer array (rgb-alpha fromat)
	 * @param fname file name of the image
	 * @param wh return width and height of the loaded image
	 */
  public static int[] readGIF_JPG(String fname, Dimension wh) throws IOException {
		Image image=Toolkit.getDefaultToolkit().getImage(fname) ;
		return load(image, wh) ;
	}

	/**
   * Read GIF or JPEG images into an integer array (rgb-alpha fromat)
	 * @param url URL of the image
	 * @param wh return width and height of the loaded image
	 */
  public static int[] readGIF_JPG(URL url, Dimension wh) throws IOException {
    Image image=Toolkit.getDefaultToolkit().getImage(url) ;
    return load(image, wh) ;
  }

	/**
   * Read PPM images into an integer array (rgb-alpha fromat)
	 * @param in InputStream of the desired image (2 bytes already fetched)
	 * @param wh return width and height of the loaded image
	 */
	public static int[] readPPM(InputStream in, Dimension wh)
		throws IOException {
		BufferedInputStream bin=new BufferedInputStream(in) ;
		DataInputStream fin=new DataInputStream(bin) ;
		int i, j ;
		byte buf[]=new byte[100] ;
		byte c=' ' ;
		
		byte[] mag=new byte[2] ;
		fin.read(mag) ;
		int magic=((int)mag[0])<<8 | mag[1] ;

		while(c==' '||c=='\t'||c=='\r'||c=='\n') c=fin.readByte() ;
		while(c=='#'){
			while(c!='\n') c=fin.readByte() ;
		  while(c==' '||c=='\t'||c=='\r'||c=='\n') c=fin.readByte() ;
		}
		i=0 ;
		while(c!=' '&&c!='\t'&&c!='\r'&&c!='\n'){
		  buf[i++]=c ;
			c=fin.readByte() ;
		}
		wh.width=Integer.parseInt(new String(buf,0,i)) ;

		c=fin.readByte() ;
		i=0 ;
		while(c!=' '&&c!='\t'&&c!='\r'&&c!='\n'){
		  buf[i++]=c ;
			c=fin.readByte() ;
		}
		wh.height=Integer.parseInt(new String(buf,0,i)) ;

		c=fin.readByte() ;
		i=0 ;
		while(c!=' '&&c!='\t'&&c!='\r'&&c!='\n'){
		  buf[i++]=c ;
			c=fin.readByte() ;
		}

		int size=wh.width*wh.height ;

	  //Notice: assume the header part ended by CR
		int[] value=new int[size] ;
		if(magic==PPMMAGIC){
			StreamTokenizer sf=new StreamTokenizer(new InputStreamReader(fin)) ;
			for(i=0; i<size;i++){
				sf.nextToken() ;
				value[i]=(((int)(sf.nval))&0xff)<<16 ;
				sf.nextToken() ;
				value[i]|=(((int)(sf.nval))&0xff)<<8 ;
				sf.nextToken() ;
				value[i]|=(((int)(sf.nval))&0xff) ;
			}
		} else{
			byte[] tmp=new byte[3*size] ;
			fin.readFully(tmp) ;

			for(i=0, j=0; i<size; i++, j+=3)
				value[i]=0xff000000|((tmp[j]&0xff)<<16)|((tmp[j+1]&0xff)<<8)|(tmp[j+2]&0xff) ;
		}
		return value ;
	}

	/**
   * Read PGM images into a byte array
	 * @param in InputStream of the desired image (2 bytes already fetched)
	 * @param wh return width and height of the loaded image
	 */
	public static byte[] readPGM(InputStream in, Dimension wh)
		throws IOException {
		BufferedInputStream bin=new BufferedInputStream(in) ;
		DataInputStream fin=new DataInputStream(bin) ;
		int i, j ;
		byte buf[]=new byte[100] ;
		byte c=' ' ;
		
		byte[] mag=new byte[2] ;
		fin.read(mag) ;
		int magic=((int)mag[0])<<8 | mag[1] ;

		while(c==' '||c=='\t'||c=='\r'||c=='\n') c=fin.readByte() ;
		while(c=='#'){
			while(c!='\n') c=fin.readByte() ;
		  while(c==' '||c=='\t'||c=='\r'||c=='\n') c=fin.readByte() ;
		}
		i=0 ;
		while(c!=' '&&c!='\t'&&c!='\r'&&c!='\n'){
		  buf[i++]=c ;
			c=fin.readByte() ;
		}
		wh.width=Integer.parseInt(new String(buf,0,i)) ;

		c=fin.readByte() ;
		i=0 ;
		while(c!=' '&&c!='\t'&&c!='\r'&&c!='\n'){
		  buf[i++]=c ;
			c=fin.readByte() ;
		}
		wh.height=Integer.parseInt(new String(buf,0,i)) ;

		c=fin.readByte() ;
		i=0 ;
		while(c!=' '&&c!='\t'&&c!='\r'&&c!='\n'){
		  buf[i++]=c ;
			c=fin.readByte() ;
		}

		int size=wh.height*wh.width ;

	  //Notice: assume the header part ended by CR
		byte[] value=new byte[size] ;
		if(magic==PGMMAGIC) {
			StreamTokenizer sf=new StreamTokenizer(new InputStreamReader(fin)) ;
			for(i=0; i<size;i++){
				sf.nextToken() ;
				value[i]=(byte)(sf.nval) ;
			}
		} else{
			fin.readFully(value) ;
		}
		return value ;
	}

	/**
   * Save an integer pixel array into PPM format
	 * @param out OutputStream of the file
	 * @param pels pixel array, pixel format: Alpha+RGB
	 * @param w width of the image
	 * @param h height of the image
	 */
	public static void savePPM(OutputStream out, int[] pels, int w, int h)
		throws IOException {
		BufferedOutputStream bout=new BufferedOutputStream(out) ;
		DataOutputStream fout=new DataOutputStream(bout) ;
		
		fout.writeByte('P') ;
		fout.writeByte('6') ;
		fout.writeByte('\n') ;
		fout.writeBytes(String.valueOf(w)) ;
		fout.writeByte(' ') ;
		fout.writeBytes(String.valueOf(h)) ;
		fout.writeByte('\n') ;
		fout.writeBytes(String.valueOf(255)) ;
		fout.writeByte('\n') ;

		int size=w*h ;

		byte[] tmp=new byte[3*size] ;
		for(int i=0, j=0; i<size; i++, j+=3){
			tmp[j]=(byte)((pels[i]&0xff0000)>>16) ;
			tmp[j+1]=(byte)((pels[i]&0xff00)>>8) ;
			tmp[j+2]=(byte)((pels[i]&0xff)) ;
		}
		fout.write(tmp, 0, 3*size) ;
	}

	/**
   * Save a byte pixel array into PGM format
	 * @param out OutputStream of the file
	 * @param pels pixel array, gray level
	 * @param w width of the image
	 * @param h height of the image
	 */
	public static void savePGM(OutputStream out, byte[] pels, int w, int h)
		throws IOException {
		BufferedOutputStream bout=new BufferedOutputStream(out) ;
		DataOutputStream fout=new DataOutputStream(bout) ;
		
		fout.writeByte('P') ;
		fout.writeByte('5') ;
		fout.writeByte('\n') ;
		fout.writeBytes(String.valueOf(w)) ;
		fout.writeByte(' ') ;
		fout.writeBytes(String.valueOf(h)) ;
		fout.writeByte('\n') ;
		fout.writeBytes(String.valueOf(255)) ;
		fout.writeByte('\n') ;

		int size=w*h ;

		fout.write(pels, 0, size) ;
	}
}
