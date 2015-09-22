package com.brobox.phonerescue_server.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

import com.brobox.phonerescue_server.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Main extends Fragment {

    private WifiManager wifiManager;
    private Switch wlanSwitch;
    private Switch dataSwitch;

    public Fragment_Main() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

        wlanSwitch = (Switch) rootView.findViewById(R.id.switch1);
        dataSwitch = (Switch) rootView.findViewById(R.id.switch2);

        if (wifiManager.isWifiEnabled()) wlanSwitch.setChecked(true);
        else wlanSwitch.setChecked(false);

        if (isMobileDataEnabled()) dataSwitch.setChecked(true);
        else dataSwitch.setChecked(false);

        wlanSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    wlanSwitch.setChecked(false);
                }
                else {
                    wifiManager.setWifiEnabled(true);
                    wlanSwitch.setChecked(true);
                }
            }
        });

        dataSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isMobileDataEnabled()) {
                    try {
                        setMobileDataEnabled(getActivity(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error: Data is enabled", Toast.LENGTH_SHORT).show();
                    }
                    dataSwitch.setChecked(false);
                }
                else {
                    try {
                        setMobileDataEnabled(getActivity(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "ErrorError: Data is disabled", Toast.LENGTH_SHORT).show();
                    }
                    dataSwitch.setChecked(true);
                }
            }
        });

        return rootView;
    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

    private boolean isMobileDataEnabled() {

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }

        return mobileDataEnabled;
    }

}
