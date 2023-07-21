package de.ewitt.screenoff;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(MainActivity.this, MyDeviceAdminReceiver.class);

        Button btn = findViewById(R.id.btnConfig);
        btn.setOnClickListener(this::gotoSettings);
        ImageButton imgBtn = findViewById(R.id.imBtnOff);
        imgBtn.setOnClickListener(this::lockScreen);


        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        boolean immediate = sharedPref.getBoolean(getString(R.string.preferences_key_immediate), false);
        boolean doExit = sharedPref.getBoolean(getString(R.string.preferences_key_exit), false);
        boolean doHide = sharedPref.getBoolean(getString(R.string.preferences_key_hide), false);
        if (immediate && (savedInstanceState == null)) {
            doLockScreen(doHide, doExit);
        }
    }

    public void lockScreen(View view) {
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        boolean doExit = sharedPref.getBoolean(getString(R.string.preferences_key_exit), false);
        boolean doHide = sharedPref.getBoolean(getString(R.string.preferences_key_hide), false);
        doLockScreen(doHide, doExit);
    }

    public void gotoSettings(View view) {
        doGotoSettings();
    }

    private void doGotoSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void doLockScreen(boolean doHide, boolean doExit) {
        if (! mDPM.isAdminActive(mDeviceAdmin)) {
            doGotoSettings();
        } else {
            mDPM.lockNow();
            if (doHide) {
                moveTaskToBack(true);
            }
            if (doExit) {
                finish();
            }
        }
    }

}
