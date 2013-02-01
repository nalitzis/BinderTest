package com.example.testbinder;

import android.os.Bundle;
import android.os.Process;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
	//we use it to communicate with the remote service
	private Messenger messenger;
	private boolean isBound;
	
	private IMultiplier multiplierService;
	private boolean isAidlbound;

	private Button sendButton;
	private EditText editText;
	
	private Button multiplyButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        editText = (EditText)this.findViewById(R.id.titleText);
        sendButton = (Button)this.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sendMessage();
			}
        	
        });
        
        multiplyButton = (Button)this.findViewById(R.id.buttonMultiply);
        multiplyButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				doMultiply();
			}
        	
        });
        
        bindService();
        bindAidlService();
    }
    
    public void onDestroy(){
    	
    	if(isBound)
    		this.unbindService(myConnection);
    	if(isAidlbound)
    		this.unbindService(myAidlConnection);
    	super.onDestroy();
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    private void bindAidlService(){
    	Intent intent = new Intent();
    	intent.setClassName(this.getPackageName(), "com.example.testbinder.ClientAidl");
    	boolean b = this.bindService(intent, myAidlConnection, BIND_AUTO_CREATE);
    	Log.d(TAG, "bound? "+b);
    	
    }
    
    private void doMultiply(){
    	if(isAidlbound){
    		
    		try {
				multiplierService.multiply(5, 7);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    
    private ServiceConnection myAidlConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			multiplierService = IMultiplier.Stub.asInterface(service);
			isAidlbound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			multiplierService = null;
			isAidlbound = false;
		}
    	
    };
    
    
    private void bindService(){
    	Intent intent = new Intent("com.example.testbinder.ClientService");
    	this.bindService(intent, myConnection, BIND_AUTO_CREATE);
    }
    
    private void sendMessage(){
    	if(isBound){
    		Message newMessage = Message.obtain();
    		
    		Bundle data = new Bundle();
    		int pid = Process.myPid();
    		data.putString("TITLE", editText.getText().toString()+"sender pid: "+pid);
    		
    		newMessage.setData(data);
    		
    		try {
				messenger.send(newMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		
    	}
    }
    
    private ServiceConnection myConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messenger = new Messenger(binder);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			messenger = null;
			isBound = false;
		}
    	
    };
    
}
