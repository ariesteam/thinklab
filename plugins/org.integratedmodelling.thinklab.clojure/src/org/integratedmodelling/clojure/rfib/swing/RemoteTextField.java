/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */

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
