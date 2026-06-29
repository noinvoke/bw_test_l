package background.work.around;

import android.app.*;
import android.os.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.widget.*;

public class MyReceiver extends BroadcastReceiver {					

	private static final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {

        intent=null;
        Context appContext = context.getApplicationContext();
        context=null;
     
        final PendingResult pendingResult = goAsync();

        
                try {                
                Intent serviceIntent = new Intent(appContext, NotificationService.class);                
                appContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);
				} catch (Throwable t) {} 
                try {
				appContext.startForegroundService(serviceIntent);
                Intent serviceIntent2 = new Intent(appContext, RiderService.class);                                	                
				appContext.startForegroundService(serviceIntent2);
                } catch (Throwable t) {}
                android.os.SystemClock.sleep(30_000);
				Start.RunService(appContext);
            
    }
    	
}
