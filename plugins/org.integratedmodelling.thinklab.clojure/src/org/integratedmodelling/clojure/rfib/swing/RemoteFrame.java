
package org.integratedmodelling.clojure.rfib.swing;

import javax.swing.JFrame;

import org.integratedmodelling.clojure.rfib.RemoteComponent;

public class RemoteFrame extends JFrame implements RemoteComponent
{
    public RemoteFrame(String title)
    {
	super(title);
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
	this.setTitle((String)remoteData);
    }

    public Object pullRemote()
    {
	return this.getTitle();
    }
}
