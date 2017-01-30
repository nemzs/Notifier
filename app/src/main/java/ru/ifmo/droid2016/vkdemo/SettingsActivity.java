package ru.ifmo.droid2016.vkdemo;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TimePicker;


/**
 * Created by Andrey on 17.12.2016.
 */

public class SettingsActivity extends PreferenceActivity implements TimePickerDialog.OnTimeSetListener {

    int MODE = 0;
    SharedPreferences sp;
    TimePickerDialog tmd;
    Preference time_from;
    Preference time_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        tmd = new TimePickerDialog(this, this, 0, 0, true);

        ListPreference lp = (ListPreference) findPreference(getResources().getString(R.string
                .time_freq_key));
/*
        + " " +
                getResources().getString(R.string.minutes)*/

        lp.setSummary(sp.getString(getResources().getString(R.string.time_freq_key) , "hello") +
                " " +
                getResources().getString(R.string.minutes));


        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString() + " " + getResources().getString(R.string
                        .minutes));
                return true;
            }
        });

        time_from = findPreference(getResources().getString(R.string.time_from_key));
        time_to = findPreference(getResources().getString(R.string.time_to_key));

        time_to.setSummary(sp.getString(getResources().getString(R.string.time_to_key), null));
        time_from.setSummary(sp.getString(getResources().getString(R.string.time_from_key), null));


        time_from.setDependency(getResources().getString(R.string.time_from_to_key));
        time_to.setDependency(getResources().getString(R.string.time_from_to_key));


        time_from.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MODE = 1;
                tmd.show();
                Log.d("My","timefrom");
                return true;
            }
        });

        time_to.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MODE = 0;
                tmd.show();
                Log.d("My","timeTo");
                return true;
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        Log.d("My","onTimeSet");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        String time = "" + i + ":" + (i1 < 10 ? "0" : "") + i1;
        if (MODE == 1) {
            editor.putString(getResources().getString(R.string.time_from_key), time);
        } else {
            editor.putString(getResources().getString(R.string.time_to_key), time);
        }
        showChanges(time);
        editor.apply();
    }

    private void showChanges(String time) {
        Log.d("My","ShowChanges");
        if (MODE == 1) {
            time_from.setSummary(time);
        } else {
            time_to.setSummary(time);
        }
    }

}
