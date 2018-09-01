package de.ewitt.screenoff;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(MainActivity.this, MyDeviceAdminReceiver.class);

        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        boolean immediate = sharedPref.getBoolean(getString(R.string.preferences_key_immediate), false);
        boolean doExit = sharedPref.getBoolean(getString(R.string.preferences_key_exit), false);
        if (immediate && (savedInstanceState == null)) {
            doLockScreen(true, doExit);
        }
    }

    public void lockScreen(View view) {
        doLockScreen(false, false);
    }

    public void gotoSettings(View view) {
        doGotoSettings();
    }

    private void doGotoSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void doLockScreen(boolean hide, boolean doExit) {
        if (! mDPM.isAdminActive(mDeviceAdmin)) {
            doGotoSettings();
        } else {
            mDPM.lockNow();
            if (hide) {
                moveTaskToBack(true);
            }
            if (doExit) {
                finish();
            }
        }
    }

}
