@ECHO off
ECHO ----------------------------
REM TODO - must integrate API and thinkql libs
java -cp "bin/;../thinklab-api/bin/;lib/*;../org.integratedmodelling.thinkql/bin/" org.integratedmodelling.thinklab.main.RESTServer
ECHO -----------------------------
ECHO Thinklab REST server application stopped
PAUSE
