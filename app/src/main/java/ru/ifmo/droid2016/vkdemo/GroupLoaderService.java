package ru.ifmo.droid2016.vkdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.ifmo.droid2016.vkdemo.GroupListDataBase.GroupListDB;
import ru.ifmo.droid2016.vkdemo.NewsDataBase.NewsDB;
import ru.ifmo.droid2016.vkdemo.model.GroupEntry;
import ru.ifmo.droid2016.vkdemo.model.Post;

/**
 * Created by Nemzs on 28.12.2016.
 */
public class GroupLoaderService extends Service {
    public static final String MY_SERVICE = ".GroupLoaderService.MY_SERVICE";
    Timer timer;
    String timeFrom="0:00";
    String timeTo="0:00";
    Boolean time_from_to=false;
    Boolean vibration=false;
    SharedPreferences preferences;
    long interval = 1000*300;//каждые 5 минут изначально обновление
    TimerTask tTask;
    Intent ourIntent;
    NotificationManager notify;
    long last_notice = 0;
    private FragmentManager fragmentManager;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("My","OnBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_settings);
        }
    }

    public GroupLoaderService() {
        //super("GroupLoaderService");
        timer = new Timer();
        schedule();
        Log.d("My", "Life is good1");
    }

    void schedule() {
       // long time_freq = Long.parseLong(preferences.getString("time_freq_key","5"));
        interval = 5*60*1000;
        if (tTask != null) tTask.cancel();
        if (interval > 0) {
            tTask = new TimerTask() {
                public void run() {
//                    Log.d("My", ourIntent.getStringExtra("token"));
                    try {
                        NewsDB gg = new NewsDB(GroupLoaderService.this.getApplicationContext(),1);
                        gg.delete();
                        updateGroup();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(tTask, 1000, interval);
        }
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("My", "onStartCommand");
        //Log.d("My",sp.getBoolean("notif", false));
        SharedPreferences sp;
        preferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ourIntent = intent;
        notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        timer.cancel();
        Log.d("My", "onDestroyServ");
        super.onDestroy();
    }
    public void killSheluder(){
        Log.d("My","KillTimer");
        timer.cancel();
    }
    void sendNotif() {
        long[] pattern = {0, 200, 1000};
        PendingIntent resultPendingIntent;
        Intent intent = new Intent(this, RecyclerNews.class);
        resultPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        vibration = preferences.getBoolean("vibration",false);
        NotificationCompat.Builder kek = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_ab_done)
                .setContentTitle("Интересные новости!")
                .setContentText("Нажми, чтобы перейти к ленте!")
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        if(vibration){
            kek.setVibrate(pattern);
        }
        notify.notify(777,kek.build());
    }
    void updateGroup() throws InterruptedException {

        Log.d("My",preferences.getAll().toString());
        timeFrom = preferences.getString("time_from","0:00");
        timeTo = preferences.getString("time_to","0:00");
        time_from_to = preferences.getBoolean("time_from_to",true);
        GroupListDB gg = new GroupListDB(this.getApplicationContext(),1);
        List<GroupEntry> groupList = null;
        try {
            groupList = gg.get();
        } catch (Exception e){
        }
        if(groupList==null) return;

        for(int i=0;i<groupList.size();i++) {
            final GroupEntry item = groupList.get(i);
            if(item.ignore==1) break;
            String request = "-" + item.id;
            SharedPreferences sp = null;
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            String s = sp.getString("time_freq_key","2000");
           // Thread.sleep(Integer.getInteger(s));
            Thread.sleep(1000);//каждые сколько миллисекунд обновляется группа
            VKRequest rq = VKApi.wall().get(VKParameters.from(VKApiConst.OWNER_ID, request,VKApiConst.EXTENDED,100));
            rq.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response)  {
                    JSONObject pItem = null;
                    JSONArray pArr = null;
                    //Log.d("My",response.json.toString());
                    ArrayList<Post> groupsTmp = new ArrayList<>();
                    try {
                        pItem = response.json.getJSONObject("response");
                        pArr = pItem.getJSONArray("items");
                    } catch (JSONException e) {
                        Log.d("My","Error");
                        e.printStackTrace();
                    }
                    if(pArr==null) return;
                    for (int i = 0; i < pArr.length(); i++) {
                        JSONObject movieJS = pArr.optJSONObject(i);

                        try {
                            if (movieJS != null&&movieJS.getString("marked_as_ads")!="1") {

                                String text = null;
                                String date = null;
                                String id = null;
                                String likes = "0";
                                String reposts = "0";
                                try {
                                    text = movieJS.getString("text");
                                    if(text.length()>250){
                                        text = text.substring(0,250)+"...";
                                    }
                                    date = movieJS.getString("date");
                                    id = movieJS.getString("id");
                                    likes = movieJS.getJSONObject("likes").getString("count");
                                    reposts = movieJS.getJSONObject("reposts").getString("count");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                /*SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
                                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date toDate = newFormat.parse(date);
                                String formatedDate = newFormat.format(toDate);*/
                                //Log.d("My",formatedDate);
                                 Post post = new Post(Integer.parseInt(id),item.icon_url,item.header,date,text,Integer.toString(item.id),Integer.parseInt(likes),Integer.parseInt(reposts));
                                groupsTmp.add(post);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    NewsDB gg = new NewsDB(GroupLoaderService.this.getApplicationContext(),1);
                    gg.createTable();
                    gg.put(groupsTmp,Integer.toString(item.id));
                }

                @Override
                public void onError(VKError error) {
                    Log.d("My", "error " + error.toString());
//Do error stuff
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    Log.d("My", "failed " + Integer.toString(attemptNumber));
//I don't really believe in progress
                }
            });
        }

        if(time_from_to)
            sendNotif();
    }
}