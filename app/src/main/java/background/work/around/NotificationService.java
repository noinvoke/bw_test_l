package background.work.around;

import android.os.Handler;
import android.os.HandlerThread;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.app.Service;
import java.util.Locale;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.content.ContentProviderClient;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.SystemClock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.ActivityManager;

public class NotificationService extends NotificationListenerService {
		
	@Override 
	public void onCreate() {
    super.onCreate();
	TryStartEnforcedService();		
	forceBindAndStart();
	}
	
	private void forceBindAndStart() {
	try {
	Intent serviceIntent2 = new Intent(this, RiderService.class);		
    bindService(serviceIntent2, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);    
	startService(serviceIntent2);
    } catch (Throwable t) {}    
    }
	
    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {}
        @Override
        public void onServiceDisconnected(ComponentName name) {
          forceBindAndStart();
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {	
    return START_STICKY;
    }

	private void startEnforcedService() {
	Context context = this;
    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    String pkg = context.getPackageName();    

    List<NotificationChannel> channels = nm.getNotificationChannels();
    String activeId = null;
    boolean needNew = false;

    for (NotificationChannel ch : channels) {
        if (ch.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            nm.deleteNotificationChannel(ch.getId());
            needNew = true;
        } else if (activeId == null) {
            activeId = ch.getId();
        }
    }

    if (needNew || activeId == null) {
        activeId = "background.work.around" + Long.toHexString(new java.security.SecureRandom().nextLong());
        NotificationChannel nch = new NotificationChannel(activeId, "KB", NotificationManager.IMPORTANCE_DEFAULT);
        nch.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
		nch.setSound(null, null);
		nch.enableVibration(false);
		nm.createNotificationChannel(nch);
    }

    Notification notif = new Notification.Builder(context, activeId)
            .setContentTitle("🟢🟢🟢")
            .setContentText("ru".equalsIgnoreCase(Locale.getDefault().getLanguage()) ? "Фоновый сервис запущен" : "Background service is started")
            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
		    .setVisibility(Notification.VISIBILITY_SECRET)
            .build();

    if (android.os.Build.VERSION.SDK_INT >= 34) {
        startForeground(1, notif, 1024);
    } else {
        startForeground(1, notif);
    }
	}	

	private final void TryStartEnforcedService() {		
		try {startEnforcedService();} 
        catch (Throwable t) {}
	}    

    @Override
    public void onDestroy() {        
        Start.RunService(this);
        super.onDestroy();
    }
}
