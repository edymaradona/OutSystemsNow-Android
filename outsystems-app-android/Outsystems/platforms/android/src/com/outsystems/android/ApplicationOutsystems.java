/*
 * OutSystems Project
 * 
 * Copyright (C) 2014 OutSystems.
 * 
 * This software is proprietary.
 */
package com.outsystems.android;

import java.util.List;

import com.outsystems.android.core.DatabaseHandler;
import com.outsystems.android.core.EventLogger;
import com.outsystems.android.core.WSRequestHandler;
import com.outsystems.android.core.WebServicesClient;
import com.outsystems.android.helpers.ApplicationSettingsController;
import com.outsystems.android.helpers.HubManagerHelper;
import com.outsystems.android.model.HubApplicationModel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class description.
 *
 * @author <a href="mailto:vmfo@xpand-it.com">vmfo</a>
 * @version $Revision: 666 $
 *
 */
public class ApplicationOutsystems extends Application {

    private boolean isNotificationsTokenRegistered = false;
    public void callRegisterToken(String deviceId) {
        // Only call register token if we didn't already for this run...
        if(!isNotificationsTokenRegistered) {
            WebServicesClient.getInstance().registerToken(this, deviceId, new WSRequestHandler() {

                @Override
                public void requestFinish(Object result, boolean error, int statusCode) {
                    ApplicationOutsystems.this.isNotificationsTokenRegistered = true;
                    EventLogger.logMessage(getClass(), "Register Token in the server");
                }
            });
        }
    }

    /** The demo applications. */
    public boolean demoApplications = false;


    @Override
    public void onCreate() {
        super.onCreate();

        // Application Settings
        ApplicationSettingsController.getInstance().loadSettings(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onTrimMemory(int)
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    /**
     * @return the demoApplications
     */
    public boolean isDemoApplications() {
        return demoApplications;
    }

    /**
     * @param demoApplications the demoApplications to set
     */
    public void setDemoApplications(boolean demoApplications) {
        this.demoApplications = demoApplications;
    }

    /**
     * Register default hub application.
     */
    public void registerDefaultHubApplication() {
        if (isDemoApplications()) {
            HubManagerHelper.getInstance().setApplicationHosted(WebServicesClient.DEMO_HOST_NAME);
        } else {
            DatabaseHandler database = new DatabaseHandler(getApplicationContext());
            List<HubApplicationModel> hubApplications = database.getAllHubApllications();
            if (hubApplications != null && hubApplications.size() > 0) {
                HubApplicationModel hubApplication = hubApplications.get(0);
                if (hubApplication != null) {
                    HubManagerHelper.getInstance().setApplicationHosted(hubApplication.getHost());
                    HubManagerHelper.getInstance().setJSFApplicationServer(hubApplication.isJSF());
                }
            }
            database.close();
        }
    }

    /**
     * Check if there is a available network connection
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}