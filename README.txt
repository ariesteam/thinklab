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

For Eclipse: a sample configuration to run would use thinklab as the project, im-boot.jar
as the only runtime library, and org.java.plugin.boot.Boot as the boot class. The parameters
for the VM could be something like

-Xms1512M -Xmx1512M -Dthinklab.inst=. -Dthinklab.plugins=../xxxx/plugins -Djava.library.path=./plugins/org.integratedmodelling.thinklab.riskwiz/common

where the thinklab.plugins refers to other projects with thinklab plugins in them; it's
unnecessary if none are installed.