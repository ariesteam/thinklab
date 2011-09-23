cd %THINKLAB_HOME%
call %THINKLAB_JRE%\bin\java.exe %JAVA_OPTS% -Djava.library.path=%THINKLAB_HOME%\plugins\org.integratedmodelling.thinklab.riskwiz\common -Djpf.boot.config=%THINKLAB_HOME%\boot.properties -Dthinklab.plugins=%THINKLAB_PLUGINS% -Dthinklab.inst=%THINKLAB_HOME% -Djava.endorsed.dirs=%THINKLAB_HOME%\lib\endorsed -jar %THINKLAB_HOME%\lib\im-boot.jar org.java.plugin.boot.Boot
pause