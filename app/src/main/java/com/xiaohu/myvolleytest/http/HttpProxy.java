package com.xiaohu.myvolleytest.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/7/20.
 */
public class HttpProxy {
    HttpModel model;
    Context myContext;

    public HttpProxy(RequestQueue queue,String url, HttpModel m, Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        model = m;
        myContext = context;
       // String url = model.getServerAddress() + model.getMethod();

        MyStringRequest myStringRequest = new MyStringRequest(context, url, listener, errorListener);
        //超时时间
        myStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myStringRequest);

    }
    public  HttpProxy(RequestQueue queue,String url, Map<String,String> header, Context context, Response.Listener<String> listener, Response.ErrorListener errorListener){
        myContext = context;
        // String url = model.getServerAddress() + model.getMethod();
        //String url="http://192.168.1.113:62020/Service/User.aspx?Action=Online";
        MyStringRequest myStringRequest = new MyStringRequest(context,Request.Method.POST,url,header, listener, errorListener);
        //超时时间
        myStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myStringRequest);
    }

    //用户登录


}
