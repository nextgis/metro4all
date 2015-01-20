package com.nextgis.metroaccess;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class LimitationsActivity extends SherlockPreferenceActivity implements Preference.OnPreferenceChangeListener {
    public static final String KEY_PREF_HAVE_LIMITS = "limits";
    public static final String KEY_PREF_MAX_WIDTH = "max_width";
    public static final String KEY_PREF_WHEEL_WIDTH = "wheel_width";

    private ActionBar actionBar;
    private SharedPreferences prefs;
    private Preference mPreferenceWheel, mPreferenceMaxWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.limitations);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        actionBar = getSupportActionBar();

        boolean isLimitationsEnabled = hasLimitations(this);
        mPreferenceMaxWidth = findPreference(KEY_PREF_MAX_WIDTH);
        mPreferenceWheel = findPreference(KEY_PREF_WHEEL_WIDTH);
        mPreferenceMaxWidth.setSummary(prefs.getString(KEY_PREF_MAX_WIDTH, "40") + " " + getString(R.string.sCM));
        mPreferenceWheel.setSummary(prefs.getString(KEY_PREF_WHEEL_WIDTH, "40") + " " + getString(R.string.sCM));
        mPreferenceMaxWidth.setOnPreferenceChangeListener(this);
        mPreferenceWheel.setOnPreferenceChangeListener(this);
        setDependency(isLimitationsEnabled);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            initSwitch(isLimitationsEnabled);
        else
            findPreference(KEY_PREF_HAVE_LIMITS).setOnPreferenceChangeListener(this);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initSwitch(boolean state) {
        actionBar.setCustomView(R.layout.actionbar_switch);
        ((TextView) actionBar.getCustomView().findViewById(R.id.tvLimitationsTitle)).setText(R.string.sLimits);

        Switch swLimits = (Switch) actionBar.getCustomView().findViewById(R.id.swLimitations);
        swLimits.setChecked(state);
        getPreferenceScreen().removePreference(findPreference(KEY_PREF_HAVE_LIMITS));

        swLimits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(KEY_PREF_HAVE_LIMITS, b).apply();
                switchLimitations(b);
            }
        });

        int opts = android.app.ActionBar.DISPLAY_SHOW_HOME | android.app.ActionBar.DISPLAY_SHOW_CUSTOM;
        actionBar.setDisplayOptions(opts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchLimitations(boolean bHaveLimits) {
        if (bHaveLimits)
            ((Analytics) getApplication()).addEvent(Analytics.SCREEN_PREFERENCE, "Enable " + Analytics.LIMITATIONS, Analytics.PREFERENCE);
        else
            ((Analytics) getApplication()).addEvent(Analytics.SCREEN_PREFERENCE, "Disable " + Analytics.LIMITATIONS, Analytics.PREFERENCE);

        setDependency(bHaveLimits);
    }

    private void setDependency(boolean state) {
        mPreferenceMaxWidth.setEnabled(state);
        mPreferenceWheel.setEnabled(state);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(KEY_PREF_HAVE_LIMITS)) {
            switchLimitations((Boolean) newValue);
            return true;
        } else {//if (preference.getKey().equals(KEY_PREF_MAX_WIDTH) || preference.getKey().equals(KEY_PREF_WHEEL_WIDTH))
        	String sNewValue = ((String) newValue).trim();

            if (isInt(sNewValue)) {
                prefs.edit().putInt(preference.getKey() + "_int", Integer.parseInt(sNewValue) * 10).apply();
                preference.setSummary(sNewValue + " " + getString(R.string.sCM));
                return true;
            } else return false;
        }
    }

    private static boolean isInt(String str) {
        return str.matches("^[0-9]+$");
    }

    public static boolean hasLimitations(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_PREF_HAVE_LIMITS, false);
    }

    public static int getMaxWidth(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(LimitationsActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
    }

    public static int getWheelWidth(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(LimitationsActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);
    }

}
