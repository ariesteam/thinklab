#!/bin/sh

case "`uname`" in
  Darwin*) if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
esac

PRG="$0"
CWD=`pwd`

if [ -z "$THINKLAB_HOME" ] ; then
	THINKLAB_HOME=`dirname "$PRG"`/..
	# make it fully qualified
	THINKLAB_HOME=`cd "$THINKLAB_HOME" && pwd`
fi

if [ ! -f "$THINKLAB_HOME/lib/im-boot.jar" ] ; then
  echo "Error: Could not find $THINKLAB_HOME/lib/im-boot.jar"  
  exit 1
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ -z "$THINKLAB_INST" ] ; then
  export THINKLAB_INST=$THINKLAB_HOME
fi

THINKLAB_CMD="$JAVACMD $JAVA_OPTS -Djava.library.path=$THINKLAB_HOME/plugins/org.integratedmodelling.thinklab.riskwiz/common -Djpf.boot.config=$THINKLAB_HOME/boot.properties -Dthinklab.library.path=$THINKLAB_LIBRARY_PATH -Dthinklab.plugins=$THINKLAB_PLUGINS -Dthinklab.inst=$THINKLAB_INST -Djava.endorsed.dirs=$THINKLAB_HOME/lib/endorsed -jar $THINKLAB_HOME/lib/im-boot.jar org.java.plugin.boot.Boot"

while true; do

  cd $THINKLAB_HOME

  if [ "$1" = "start" ] ; then
    cd $THINKLAB_INST
    mkdir -p $THINKLAB_INST/var/log
    sh -c "exec $THINKLAB_CMD $@ $THINKLAB_ARGS >> $THINKLAB_INST/var/log/thinklab.out 2>&1"
  else
    exec $THINKLAB_CMD $@ $THINKLAB_ARGS
  fi

  # 
  # check if shutdown service has inserted hooks for us to run
  #
  if [ -d $THINKLAB_INST/tmp/hooks ] ; then
    shopt -s nullglob
    for i in $THINKLAB_INST/tmp/hooks/*
    do
  	  . $i
    done
    rm -f $THINKLAB_INST/tmp/hooks/*
  fi

  #
  # these may have been set by hooks
  #
  if [ -n "$THINKLAB_UPGRADE_BRANCH" ] ; then
	cd $HOME/thinklab/thinklab
	git checkout $1
	git pull
	ant build install
	cd $HOME/thinklab/aries
	git checkout $1
	git pull
	ant build install
	unset THINKLAB_UPGRADE_BRANCH
  fi

  #
  # exit loop unless THINKLAB_RESTART was defined by a hook
  #
  if [ -z "$THINKLAB_RESTART" ] ; then
	break
  fi

  unset THINKLAB_RESTART
  
done

cd $CWD
