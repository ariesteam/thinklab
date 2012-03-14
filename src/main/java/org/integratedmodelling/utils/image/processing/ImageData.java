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
package org.integratedmodelling.utils.image.processing ;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ImageData contains either true color rgb(8x8x8) data or 8-bit gray-level data
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 * @see image.ImageIO
 */
public class ImageData extends Object implements Cloneable {
  private static byte[] grayPallete=null;

  private ColorModel clrmdl ;  // support two models: Direct(32) and Index(8)
  /**
   * pixel array used for color image
   */
  protected int[] ipels=null ;
  /**
   * pixel array used for gray-level image
   */
  protected byte[] bpels=null ;
  /**
   * number of rows (height)
   */
  protected int irow ;
  /**
   * number of columns (width)
   */
  protected int icol ;

  static {
    grayPallete=new byte[256] ;
    for(int i=0; i<256; i++)  grayPallete[i]=(byte)i ;
  }

  /**
   * Create ImageData from an integer array using DirectColor model
   * @param pmap The integer array
   * @param r height (or row)
   * @param c width (or column)
   */
  public ImageData(int[] pmap, int r, int c){
    ipels = pmap ; irow=r; icol=c;
    clrmdl=new DirectColorModel(32,0xff0000,0xff00,0xff) ;
  }

  /**
   * Create ImageData from a byte array using IndexColor(8) model
   * @param pmap The integer array
   * @param r height (or row)
   * @param c width (or column)
   */
  public ImageData(byte[] pmap, int r, int c){
    bpels = pmap ; irow=r; icol=c;
    clrmdl= new IndexColorModel(8,256,grayPallete,grayPallete,grayPallete) ; 
  }

  /**
   * Load ImageData from a local file or URL; 
   * Input formats: GIF, JPEG, PPM, PGM; Output formats: PPM, PGM
   * @param filename The local file name or URL of an image
   */
  public ImageData(String filename) throws IOException{
    InputStream inchar ;
    URL url=null ;
    boolean isurl=false ;
    Dimension wh=new Dimension() ;
    int t ;

    File f = new File(filename) ;
    if (f.exists()){
      inchar = new FileInputStream(f) ;
      t=ImageIO.getImageType(inchar) ;
      inchar.close() ;
    } else {
      try {
        url=new URL(filename) ;
        inchar = url.openStream() ;
        t=ImageIO.getImageType(inchar) ;
        inchar.close() ;
        isurl=true ;
      }catch(MalformedURLException e) {
        throw new IOException("Cann't locate the image file!") ;
      }
    }

    switch(t){
      case ImageIO.JPG_IMG:
      case ImageIO.GIF_IMG:
        if(isurl)
          ipels = ImageIO.readGIF_JPG(url,wh) ;
        else
          ipels = ImageIO.readGIF_JPG(filename,wh) ;
        irow=wh.height ; icol=wh.width ; 
        clrmdl= new DirectColorModel(32,0xff0000,0xff00,0xff) ; 
        break ;
      case ImageIO.PPM_IMG:
        inchar = new FileInputStream(f) ;
        ipels= ImageIO.readPPM(inchar, wh) ;
        inchar.close() ;
        irow=wh.height ; icol=wh.width ; 
        clrmdl= new DirectColorModel(32,0xff0000,0xff00,0xff) ; 
        break ;
      case ImageIO.PGM_IMG:
        inchar = new FileInputStream(f) ;
        bpels= ImageIO.readPGM(inchar, wh) ;
        inchar.close() ;
        irow=wh.height ; icol=wh.width ; 
        clrmdl= new IndexColorModel(8,256,grayPallete,grayPallete,grayPallete) ;
        break ;
    }
  }

  /**
   * Get java.awt.Image object
   */
  public Image getImage(){
    return getImage(1.0) ;
  }

  /**
   * Get scaled java.awt.Image object
   * @param s The scale factor
   */
  public Image getImage(double s){
    Image img =null ;
    if(clrmdl.getPixelSize()==32)
      img=Toolkit.getDefaultToolkit().createImage(
            new MemoryImageSource(icol, irow, clrmdl, ipels, 0, icol)) ;
    else {
      img=Toolkit.getDefaultToolkit().createImage(
            new MemoryImageSource(icol, irow, clrmdl, bpels, 0, icol)) ;
    }
    if(s>0 && s!=1.0) {
      int detW=(int)(0.5+s*icol) ;
      int detH=(int)(0.5+s*irow) ;
      return img.getScaledInstance(detW, detH, Image.SCALE_FAST) ;
      /*
      ImageFilter filter=new ReplicateScaleFilter(detW, detH) ;
      ImageProducer producer=new FilteredImageSource(img.getSource(),filter);
      return Toolkit.getDefaultToolkit().createImage(producer) ;
      */
    }else
      return img ;
  }

  /**
   * Scale the image
   * @param s the scale (should be >0)
   */
  public void scale(double s) {
		if(s<=0) return ;
    Image img=getImage(s) ;
    Dimension wh=new Dimension() ;
    try{
    ipels = ImageIO.readImage(img,wh) ;
    }catch(IOException e) {
      System.err.println("Scaling error: "+e.getMessage()) ;
    }
    irow=wh.height ; icol=wh.width ; 
    clrmdl= new DirectColorModel(32,0xff0000,0xff00,0xff) ; 
  }

  /**
   * Save ImageData into a PPM file if it is a true color image
   * @param fname The file name
   */
  public void savePPM(String fname) throws IOException {
    if(clrmdl.getPixelSize()==32){
      FileOutputStream out = new FileOutputStream(fname) ;
      ImageIO.savePPM(out, ipels, icol, irow) ;
      out.close() ;
    }
  }

  /**
   * Save ImageData into a PGM file (convert to intensity for color image)
   * @param fname The file name
   */
  public void savePGM(String fname) throws IOException {
    FileOutputStream out = new FileOutputStream(fname) ;
    if(clrmdl.getPixelSize()==32){
      byte[] imap=new byte[irow*icol] ;

      for(int i=0, ii=0; i<irow; i++)
      for(int j=0; j<icol; j++, ii++)
        imap[ii]=(byte)((((ipels[ii]&0xff0000)>>16)*222+
                           ((ipels[ii]&0x00ff00)>>8)*707+
                           ((ipels[ii]&0xff))*71) / (float)1000.0 + 0.5) ;
      ImageIO.savePGM(out, imap, icol, irow) ;
    }else{
      ImageIO.savePGM(out, bpels, icol, irow) ;
    }
    out.close() ;
  }

  /**
   * Get number of rows (width)
   */
  public final int row() { return irow ; }

  /**
   * Get number of columns (height)
   */
  public final int col() { return icol ; }

  /**
   * Get number of pixels (row*column)
   */
  public final int size() { return irow*icol ; }

  /**
   * Return true if it is a color image
   */
  public final boolean isColor() { return clrmdl.getPixelSize()==32 ; }

  /**
   * Get pixel value, make sure coords are valid and this is a color image 
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public final int pc(int x, int y) { return ipels[y*icol+x]; }

  /**
   * Get pixel value, make sure index is valid and this is a color image 
   * @param i scanline index
   */
  public final int pc(int i) { return ipels[i]; }

  /**
   * Set pixel value, make sure index is valid and this is a color image 
   * @param i scanline index
   * @param v pixel value
   */
  public final void spc(int i, int v) { ipels[i]=v; }

  /**
   * Set pixel value, make sure coords are valid and this is a color image 
   * @param x x-coordinate
   * @param y y-coordinate
   * @param v pixel value
   */
  public final void spc(int x, int y, int v) { ipels[y*icol+x]=v; }

  /**
   * Get pixel value, make sure index is valid and it is a gray-level image
   * @param i scanline index
   */
  public final byte pg(int i) { return bpels[i]; }

  /**
   * Get pixel value, make sure coords are valid and it is a gray-level image
   * @param i scanline index
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public final byte pg(int x, int y) { return bpels[y*icol+x]; }

  /**
   * Set pixel value, make sure index is valid and this is a gray-level image 
   * @param i scanline index
   * @param v pixel value
   */
  public final void spg(int i, byte v) { bpels[i]=v; }

  /**
   * Set pixel value, make sure coords are valid and this is a gray-level image 
   * @param x x-coordinate
   * @param y y-coordinate
   * @param v pixel value
   */
  public final void spg(int x, int y, byte v) { bpels[y*icol+x]=v; }

  /**
   * Convert to gray level image, return intensity map in 2-D float array
   */
  public float[][] getIntensityMap() {
    float[][] imap=new float[irow][icol] ;

    if(isColor()) {
      for(int i=0, ii=0; i<irow; i++)
      for(int j=0; j<icol; j++, ii++)
        imap[i][j]=(float)(((ipels[ii]&0xff0000)>>16)*222+
                           ((ipels[ii]&0x00ff00)>>8)*707+
                           ((ipels[ii]&0xff))*71) / (float)1000.0 ;
    }else{
      for(int i=0, ii=0; i<irow; i++)
      for(int j=0; j<icol; j++, ii++)
        imap[i][j]=(float)(bpels[ii]) ;
    }
    return imap ;
  }

  /**
   * Convert to gray level image, return intensity map in 2-D float array
   * @param bbox bounding box
   */
  public float[][] getIntensityMap(Rectangle bbox) {
    float[][] imap=new float[bbox.height][bbox.width] ;

    if(isColor()) {
      for(int i=0, ii=bbox.y*icol; i<bbox.height; i++, ii+=icol)
      for(int j=0; j<bbox.width; j++){
        int tmp=ii+j+bbox.x ;
        imap[i][j]=(float)(((ipels[tmp]&0xff0000)>>16)*222+
                           ((ipels[tmp]&0x00ff00)>>8)*707+
                           ((ipels[tmp]&0xff))*71) / (float)1000.0 ;
      }
    }else{
      for(int i=0, ii=bbox.y*icol; i<bbox.height; i++, ii+=icol)
      for(int j=0; j<bbox.width; j++){
        imap[i][j]=(float)(bpels[ii+j+bbox.x]) ;
      }
    }
    return imap ;
  }

  /**
   * Convert to Luv iamge, return in 3-D float array
   * @param bbox bounding box (null for whole frame)
   */
  public float[][][] getLuvImage(Rectangle bbox) {
    if(bbox==null)
      bbox=new Rectangle(0,0,icol,irow) ;

    float[][][] imap=new float[bbox.height][bbox.width][3] ;

    if(isColor()) {
      for(int i=0, ii=bbox.y*icol; i<bbox.height; i++, ii+=icol)
      for(int j=0; j<bbox.width; j++){
        int tmp=ii+j+bbox.x ;
        ImageProc.rgb2luv(((ipels[tmp]&0xff0000)>>16),
                ((ipels[tmp]&0x00ff00)>>8), ((ipels[tmp]&0xff)),
               imap[i][j]) ;
      }
    }else{
      for(int i=0, ii=bbox.y*icol; i<bbox.height; i++, ii+=icol)
      for(int j=0; j<bbox.width; j++){
        int tmp=ii+j+bbox.x ;
        ImageProc.rgb2luv((int)(bpels[tmp]),(int)(bpels[tmp]),(int)(bpels[tmp]),
               imap[i][j]) ;
      }
    }
    return imap ;
  }
  
  /** 
   * Draws a line of given gray-level color from (x0,y0) to (x1,y1);<br>
   * implements the Bresenham's incremental midpoint algorithm;<br>
   * (adapted from J D Foley, A Van Dam, S K Feiner, J F Hughes<br>
   * "Computer Graphics Principles and practice", * 2nd ed, 1990);<br>
   * @param x0 x-coordinate of endpoint 1
   * @param y0 y-coordinate of endpoint 1
   * @param x1 x-coordinate of endpoint 2
   * @param y1 y-coordinate of endpoint 2
   * @param color gray-level; R,G and B are all set to "color" for color images 
  */
  public void drawline(int x0, int y0, int x1, int y1, byte color) {
    int xmin,xmax; /* line coordinates */
    int ymin,ymax;
    int dir;       /* scan direction */
    int dx;        /* distance along X */
    int dy;        /* distance along Y */

    /* increments: East, North-East, South, South-East, North */
    int incrE,
        incrNE,
        incrS,
        incrSE,
        incrN;     
    int d;         /* the D */
    int x,y;       /* running coordinates */
    int mpCase;    /* midpoint algorithm's case */
    int done;      /* set to 1 when done */

    xmin=x0;
    xmax=x1;
    ymin=y0;
    ymax=y1;
    
    dx=xmax-xmin;
    dy=ymax-ymin;

    if (dx*dx>dy*dy) /* horizontal scan */ {
      dir=0;
      if (xmax<xmin) {
        {xmin^=xmax; xmax^=xmin; xmin^=xmax;} //Swap
        {ymin^=ymax; ymax^=ymin; ymin^=ymax;} //Swap
      }
      dx=xmax-xmin;
      dy=ymax-ymin;

      if (dy>=0) {
        mpCase=1;
        d=2*dy-dx;      
      } else {
        mpCase=2;
        d=2*dy+dx;      
      }

      incrNE=2*(dy-dx);
      incrE=2*dy;
      incrSE=2*(dy+dx);
    } else {/* vertical scan */
      dir=1;
      if (ymax<ymin) {
        {xmin^=xmax; xmax^=xmin; xmin^=xmax;} //Swap
        {ymin^=ymax; ymax^=ymin; ymin^=ymax;} //Swap
      }
      dx=xmax-xmin;
      dy=ymax-ymin;    

      if (dx>=0) {
        mpCase=1;
        d=2*dx-dy;      
      } else {
        mpCase=2;
        d=2*dx+dy;      
      }

      incrNE=2*(dx-dy);
      incrE=2*dx;
      incrSE=2*(dx+dy);
    }
    
    /* start the scan */
    x=xmin;
    y=ymin;
    done=0;

    int tclr=0xff000000|((0xff&color)<<16)|((0xff&color)<<8)|(0xff&color) ;
    while(done==0) {
      if(x>0 && x<icol && y>0 && y<irow)
        if(isColor())
          ipels[y*icol+x]=tclr ;
        else
          bpels[y*icol+x]=color;
    
      /* move to the next point */
      switch(dir) {
        case 0: /* horizontal */
        {
          if (x<xmax)
          {
          switch(mpCase) {
              case 1:
              if (d<=0)
              {
                d+=incrE;  x++;
              }
                else
                {
                  d+=incrNE; x++; y++;
                }
              break;
    
              case 2:
                if (d<=0)
                {
                  d+=incrSE; x++; y--;
                }
                else
                {
                  d+=incrE;  x++;
                }
              break;
          } /* mpCase */
          } /* x<xmax */
          else
          done=1;
        }  
        break;

        case 1: /* vertical */
        {
          if (y<ymax)
          {
            switch(mpCase)
            {
              case 1:
                if (d<=0) {
                  d+=incrE;  y++;
                } else {
                  d+=incrNE; y++; x++;
                }
              break;
    
              case 2:
                if (d<=0) {
                  d+=incrSE; y++; x--;
                } else {
                  d+=incrE;  y++;
                }
              break;
          } /* mpCase */
          } /* y<ymin */
          else
          done=1;
        }
        break;    
      }
    }
  }

  /** 
   * Draws a polygon with given gray level.
   * @param poly polygon
   * @param color gray-level; R,G and B are all set to "color" for color image 
	 */
  public void drawpolygon(Polygon poly, byte color) {
    int n=poly.npoints ;
    for(int i=0; i<n-1; i++)
      drawline(poly.xpoints[i], poly.ypoints[i],
               poly.xpoints[i+1], poly.ypoints[i+1], color) ;

    drawline(poly.xpoints[n-1], poly.ypoints[n-1],
             poly.xpoints[0], poly.ypoints[0], color) ;
  }

  /**
   * Create a new ImageData object (with same content)
   */
  public Object clone() {
    ImageData tmp=null ;
    try {
      tmp=(ImageData)super.clone() ;
      if(clrmdl.getPixelSize()==32){
        tmp.ipels=(int [])ipels.clone() ;
        tmp.clrmdl=new DirectColorModel(32,0xff0000,0xff00,0xff) ;
      } else{
        tmp.bpels=(byte[])bpels.clone() ;
        tmp.clrmdl= new IndexColorModel(8,256,grayPallete,grayPallete,grayPallete) ;
      }
    }catch(CloneNotSupportedException e) {
      System.err.println("Clone(ImageData): "+ e.getMessage()) ;
    }finally {
      return tmp ;
    }
  }
}
