package com.xiaohu.myvolleytest.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.xiaohu.myvolleytest.http.GsonUtils;
import com.xiaohu.myvolleytest.http.HttpModel;
import com.xiaohu.myvolleytest.http.HttpProxy;
import com.xiaohu.myvolleytest.http.JsonModel;

/**
 * Created by Administrator on 2016/7/22.
 */
public class HeartBeatService extends Service {
    private Handler handler = new Handler();
    RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        new Thread(runnable).start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                YanZhengMethod();
            }
        };
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.obj = 0;
            handler.sendMessage(msg);
            handler.postDelayed(this, 60000);
        }
    };

    private void YanZhengMethod() {
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=Online";
        HttpModel model = new HttpModel();
        HttpProxy proxy = new HttpProxy(queue, url, model, HeartBeatService.this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("心跳包验证：" + response);
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        handler.removeCallbacks(runnable);
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
