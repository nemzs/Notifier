package ru.ifmo.droid2016.vkdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VkApplication extends AppCompatActivity
        implements View.OnClickListener {

    SharedPreferences sp = null;
    Button settings;
    Button load;
    Button show;

    TextView vibr;
    TextView freq;
    TextView ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(this);

        load = (Button) findViewById(R.id.load);
        load.setOnClickListener(this);

        show = (Button) findViewById(R.id.show);
        show.setOnClickListener(this);

        vibr = (TextView) findViewById(R.id.vibro);
        freq = (TextView) findViewById(R.id.freq);
        ans = (TextView) findViewById(R.id.ans);
/*
        sp = getSharedPreferences(getResources().getString(R.string.shared_preferences_name),
                MODE_PRIVATE);
*/
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }


    @Override
    protected void onResume() {
//        sp = getSharedPreferences(getDefaultSharedPreferencesName(this),MODE_PRIVATE);
//        sp = getSharedPreferences(getResources().getString(R.string.sp_name), MODE_PRIVATE);
        String first = "vibration ";
        String second = "time interval ";
        first += Boolean.toString(sp.getBoolean("vibration_key", false));
        second += sp.getString("time_freq_key", "0");
        second += " min ";
        vibr.setText(first);
        freq.setText(second);
        super.onResume();

    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, 1, 0, "Settings");
        menuItem.setIntent(new Intent(this, SettingsActivity.class));
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.load){

        }
        if(view.getId() == R.id.show){

        }else{
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

    }

}
