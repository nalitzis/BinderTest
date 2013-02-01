package com.example.testbinder;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.widget.Toast;

public class ClientAidl extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	private final IMultiplier.Stub binder = new IMultiplier.Stub() {
		
		@Override
		public void multiply(final int a, final int b) throws RemoteException {
			final int result = a*b;
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable(){

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), a+"*"+b+"= "+result, Toast.LENGTH_SHORT).show();
					
				}
				
			});
			
		}
	};

}
