package de.ewitt.screenoff;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;

    @Override
    protected void onResume() {
        super.onResume();
        updateStates(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(SettingsActivity.this, MyDeviceAdminReceiver.class);

        updateStates(false);
    }

    private void updateStates(boolean forceNoAdmin) {
        boolean active = (!forceNoAdmin) && mDPM.isAdminActive(mDeviceAdmin);

        TextView tv  = findViewById(R.id.tvAdminState);
        TextView tvW  = findViewById(R.id.tvWarning);
        Button btn = findViewById(R.id.btnSwitchAdmin);
        Switch swS = findViewById(R.id.swStartMode);
        Switch swH = findViewById(R.id.swHideMode);
        Switch swE = findViewById(R.id.swEndMode);

        tv.setText(getString(active ? R.string.tv_admin_state_positive : R.string.tv_admin_state_negative));
        tv.setTextColor(active ? Color.BLACK : tvW.getCurrentTextColor());
        btn.setText(getString(active ? R.string.btn_switch_admin_off : R.string.btn_switch_admin_on));

        Context context = SettingsActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        boolean immediate = sharedPref.getBoolean(getString(R.string.preferences_key_immediate), false);
        boolean hide = sharedPref.getBoolean(getString(R.string.preferences_key_hide), false);
        boolean exit = sharedPref.getBoolean(getString(R.string.preferences_key_exit), false);

        swS.setChecked(immediate);
        swH.setChecked(hide);
        swE.setChecked(exit);

        tvW.setVisibility((immediate && exit && active) ? View.VISIBLE : View.GONE);
    }

    public void switchStartMode(View view) {
        switchMode(R.id.swStartMode, R.string.preferences_key_immediate);
    }

    public void switchHideMode(View view) {
        switchMode(R.id.swHideMode, R.string.preferences_key_hide);
    }

    public void switchEndMode(View view) {
        switchMode(R.id.swEndMode, R.string.preferences_key_exit);
    }

    private void  switchMode(int viewId, int keyStringId) {
        Switch sw = findViewById(viewId);
        Context context = SettingsActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(getString(keyStringId), sw.isChecked());
        prefEditor.apply();
        updateStates(false);
    }

    public void switchAdminMode(View view) {
        if (! mDPM.isAdminActive(mDeviceAdmin)) {
            // Launch the activity to have the user enable our admin.
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_description));
            startActivity(intent);
        } else {
            mDPM.removeActiveAdmin(mDeviceAdmin);
            updateStates(true);
        }
    }

}
