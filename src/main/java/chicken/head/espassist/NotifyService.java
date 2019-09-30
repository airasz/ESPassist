package chicken.head.espassist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotifyService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;
    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("chicken.head.espassist.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    public static String getAppNameFromPkgName(Context context, String Packagename) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(Packagename, PackageManager.GET_META_DATA);
            String appName = (String) packageManager.getApplicationLabel(info);
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


//        String ticker = sbn.getNotification().tickerText.toString();
        final String pn=sbn.getPackageName();
        PackageManager packageManager= getApplicationContext().getPackageManager();
//        String appname=(String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pn,packageManager.GET_META_DATA));

        String appName = getAppNameFromPkgName(this,sbn.getPackageName());
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new  Intent("chicken.head.espassist.NOTIFICATION_LISTENER_EXAMPLE");
        Integer textlenght=text.length();

        i.putExtra("notification_event",appName +  "\n"+text);
//        if(!title.equals("")){
//            if(textlenght>55){
//
//
//                i.putExtra("notification_event",appName + "\n"+text.substring(0,55)+"...");
////                i.putExtra("notification_event",appName + "\n"+title+"\n"+text.substring(0,50)+"...");
//            }else {
//
//                i.putExtra("notification_event",appName + "\n"+text);
////                i.putExtra("notification_event",appName+ "\n"+title + "\n"+text);
//            }
////            i.putExtra("notification_event","onNotificationPosted :" + appName + "\n"+text.substring(0,5)+"...");
//        }
//        i.putExtra("notification_event","onNotificationPosted :" + title + "\n");
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//                String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        Intent i = new  Intent("chicken.head.espassist.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        //sendBroadcast(i);           // dont sent
    }

    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                NotifyService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("chicken.head.espassist.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NotifyService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("chicken.head.espassist.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("chicken.head.espassist.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }
}
