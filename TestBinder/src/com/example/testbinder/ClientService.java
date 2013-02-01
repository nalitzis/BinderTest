package com.example.testbinder;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;
import android.os.Process;

public class ClientService extends Service {
	
	/**
	 * the service receives here the messages from a remote process
	 * @author ado
	 *
	 */
	private class IncomingHandler extends Handler{
		public void handleMessage(Message msg){
			Bundle data = msg.getData();
			String title = data.getString("TITLE");
			int pid = Process.myPid();
			Toast.makeText(getApplicationContext(), title + "\nservice pid: "+pid, Toast.LENGTH_SHORT).show();
		}
	}

	private final Messenger messenger = new Messenger(new IncomingHandler());
	
	//the IBinder returned here is used by the messenger to communicate with the associated handler
	@Override
	public IBinder onBind(Intent arg0) {
		return messenger.getBinder();
	}

}
