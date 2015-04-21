package com.num.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.num.Constants;
import com.num.controller.services.SignalService;
import com.num.controller.utils.DataUsageUtil;
import com.num.controller.utils.DeviceUtil;

public class BootUpReceiver extends BroadcastReceiver {

    final static String TAG = "BootUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Bootstrapping");
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        // Setting up signal strength listener
        context.startService(new Intent(context, SignalService.class));

        // Initialize data usage database
        DataUsageUtil.updateOnBoot(context);

        // Monthly alarm resets the data usage table
        if(prefs.getInt(Constants.NEXT_MONTHLY_RESET, 0) == (new DeviceUtil().getCurrentMonth())){
            // Reset usage data
            DataUsageUtil.resetMobileData(context);
            DataUsageUtil.clearTable(context);
            DataUsageUtil.setFirstMonthOfTheMonthFlag(context, new DeviceUtil().getNextMonth());
        }

        // Set alarms for resetting data usage and periodic measurement
        MonthlyResetAlarmReceiver monthlyAlarm = new MonthlyResetAlarmReceiver();
        MeasurementAlarmReceiver alarm = new MeasurementAlarmReceiver();
        monthlyAlarm.setAlarm(context);
        alarm.setAlarm(context);
    }
}
