PATH=$HOME/opt/thinklab/bin:$PATH:$HOME/bin
export THINKLAB_HOME=$HOME/opt/thinklab
export ARIES_HOME=$HOME/opt/aries
export THINKLAB_PLUGINS=$ARIES_HOME/plugins
alias startup.sh='thinklab.sh $THINKLAB_STARTUP_SCRIPT >> $HOME/logs/thinklab.log 2>&1 &'

# ------------------------------------------------------------------------
# only change the following three instructions
# ------------------------------------------------------------------------
export THINKLAB_REST_PORT=8182
export THINKLAB_STARTUP_SCRIPT=$HOME/etc/rest.tl
export THINKLAB_DEFAULT_BRANCH=dev
export THINKLAB_PUBLISH_DIRECTORY=/raid/nc_outputs

echo ---------------------------------------------------------------------
echo This account is created to run thinklab as a server
echo Relevant commands are
echo
echo     update.sh    -- creates and/or updates the thinklab installation
echo     startup.sh   -- starts the thinklab server 
echo 
echo Current settings:
echo
echo     THINKLAB_REST_PORT = $THINKLAB_REST_PORT
echo     THINKLAB_STARTUP_SCRIPT = $THINKLAB_STARTUP_SCRIPT
echo     THINKLAB_DEFAULT_BRANCH = $THINKLAB_DEFAULT_BRANCH
echo     THINKLAB_PUBLISH_DIRECTORY = $THINKLAB_PUBLISH_DIRECTORY
echo
echo Change environmental variables in .bash_profile to modify the default 
echo thinklab branch, ports, startup scripts etc.
echo echo ---------------------------------------------------------------------
