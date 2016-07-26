package com.xiaohu.myvolleytest.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaohu.myvolleytest.activity.LoginActivity;
import com.xiaohu.myvolleytest.activity.MainActivity;

/**
 * Created by Administrator on 2016/7/22.
 */
public class MyReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        String msg=intent.getStringExtra("msg");
        if(msg.equals("false")) {
            Intent intenLogin = new Intent(context, MainActivity.class);
            intenLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setAction("android.intent.category.LAUNCHER");
           // intent.addCategory("android.intent.action.MAIN");
            context.startActivity(intenLogin);
            Intent intent1=new Intent(context,HeartBeatService.class);
            context.stopService(intent1);
        }
    }
}
