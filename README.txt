In order to install Thinklab, you must first install JAI and JAI-ImageIO, which 
you can download from here:

JAI         => https://jai.dev.java.net/binary-builds.html
JAI-ImageIO => https://jai-imageio.dev.java.net/binary-builds.html

Then cd into the toplevel Thinklab directory (which contains this README) and...

1) Set the THINKLAB_HOME variable to a directory where you have write 
   permission and want to install Thinklab.

2) Run 'ant install' to build the core distribution and plugins and install
   them in THINKLAB_HOME.

3) Change directory to THINKLAB_HOME

4) Either use the scripts in bin/ (thinklab.sh or thinklab.bat) to start the 
   shell, or from the command line, run:

   java -jar lib\im-boot.jar        [windows]
   java -jar lib/im-boot.jar        [unix/linux]

That's it.  Enjoy.

