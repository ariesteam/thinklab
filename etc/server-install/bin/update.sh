mkdir -f ~/logs
mkdir -f ~/opt
mkdir -f ~/thinklab

cd ~/thinklab

if [ -d thinklab ] ; then
	git clone https://github.com/ariesteam/thinklab.git
fi

cd thinklab
git checkout $THINKLAB_DEFAULT_BRANCH
git pull
ant build install

cd ~/thinklab
if [ -d aries ] ; then
	git clone https://github.com/ariesteam/aries.git
fi

cd aries
git checkout $THINKLAB_DEFAULT_BRANCH
cd ~/thinklab/aries
git pull
ant build install

