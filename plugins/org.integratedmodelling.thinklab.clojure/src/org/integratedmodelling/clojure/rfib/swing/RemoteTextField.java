// Copyright Stuff

package org.integratedmodelling.clojure.rfib.swing;

import javax.swing.JTextField;

import org.integratedmodelling.clojure.rfib.RemoteComponent;

public class RemoteTextField extends JTextField implements RemoteComponent
{
	private static final long serialVersionUID = -5456570140798226898L;
	private boolean readyForPull = false;

    public void enablePull()
    {
	readyForPull = true;
    }

    public void disablePull()
    {
	readyForPull = false;
    }

    public boolean enableRemote()
    {
	return true;
    }

    public boolean disableRemote()
    {
	return true;
    }

    public void pushRemote(Object remoteData)
    {
	this.setText((String)remoteData);
    }

    public Object pullRemote()
    {
	try {
	    while (! readyForPull) {
		Thread.sleep(1000);
	    }
	    return this.getText();
	} catch (InterruptedException e) {
	    return this.pullRemote();
	}
    }

	@Override
	public boolean isRemoteEnabled() {
		return true;
	}
}
