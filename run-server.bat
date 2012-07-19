@ECHO off
ECHO ----------------------------
REM TODO - must integrate API and thinkql libs; this only works after launching within the Eclipse JVM
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 -cp "bin/;../thinklab-api/bin/;lib/*;../org.integratedmodelling.thinkql/bin/" org.integratedmodelling.thinklab.main.RESTServer > thinklab.log
ECHO -----------------------------
ECHO Thinklab REST server application stopped

