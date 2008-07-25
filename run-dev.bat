@ECHO off
ECHO JPF-demo application started
ECHO ----------------------------
java -jar lib\im-boot.jar
REM java -Dcom.sun.management.jmxremote -jar lib\im-boot.jar
REM java -javaagent:D:\Java\profiler4j\agent.jar -jar lib\im-boot.jar
REM java -javaagent:D:\Java\jip\profile\profile.jar -Dprofile.properties=D:\Java\jip\profile\profile-jpf.properties -jar lib\im-boot.jar
REM java -javaagent:.\profile\profile.jar -Dprofile.properties=.\profile\profile.properties -jar lib\im-boot.jar
ECHO -----------------------------
ECHO JPF-demo application stopped
PAUSE
