package com.xiaohu.myvolleytest.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
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
    MyReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
         receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MY_BROADCAST");
        registerReceiver(receiver, filter);
        new Thread(runnable).start();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 2) {
                        YanZhengMethod();
                    }

                }
            };
    }
//需要使用bindservice
//    public class YanzhengBind extends Binder {
//        public IBinder YanzhengBinder(final LoginInterface face) {
//            new Thread(runnable).start();
//            handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    if (msg.what == 2) {
//                        YanZhengMethod();
//                    } else {
//                        face.isLoginMethod(msg.obj.toString());
//                    }
//
//                }
//            };
//
//            return null;
//        }
//    }

    public interface LoginInterface {
        void isLoginMethod(String isTrue);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.obj = 0;
            msg.what = 2;
            handler.sendMessage(msg);
            handler.postDelayed(this, 10000);
        }
    };

    private void YanZhengMethod() {
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=Online";
        HttpModel model = new HttpModel();
        HttpProxy proxy = new HttpProxy(queue, url, model, HeartBeatService.this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("心跳包验证：" + response);
                JsonModel model1 = GsonUtils.analysisJson(response, "");
//                Message msg = handler.obtainMessage();
//                msg.obj = model1.getSuccess();
//                msg.what = 3;

               // handler.sendMessage(msg);
                Intent intent=new Intent("android.intent.action.MY_BROADCAST");
                intent.putExtra("msg",model1.getSuccess());
                sendBroadcast(intent);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        Intent intent = new Intent("android.intent.action.MY_BROADCAST");
                        intent.putExtra("msg", "false");
                        sendBroadcast(intent);
                        //这样做，等接受到广播的时候，不能跳到登录页面
                        //handler.removeCallbacks(runnable);
                       //unregisterReceiver(receiver);
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
        unregisterReceiver(receiver);
    }
}
