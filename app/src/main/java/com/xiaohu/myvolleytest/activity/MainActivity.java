package com.xiaohu.myvolleytest.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xiaohu.myvolleytest.R;
import com.xiaohu.myvolleytest.http.HttpProxy;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RequestQueue mqueue;
    Button btnGET;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("APPROVAL", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mqueue = Volley.newRequestQueue(this);
        btnGET = (Button) findViewById(R.id.btn_get);
        initEvent();
    }

    private void ceshi() {
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=GetRandomNumber";
//        HttpProxy stringRequest = new HttpProxy(url, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println(error.getMessage());
//            }
//        });
//        mqueue.add(stringRequest);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("ok::" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error::" + error.getMessage());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Map<String, String> responseHeaders = response.headers;
                    String rawCookies = responseHeaders.get("Set-Cookie");//cookie值
                    String dataString = new String(response.data, "UTF-8");//返回值
                    System.out.println("cookie:" + rawCookies + "---");
                    if (rawCookies != null) {
                        int num = rawCookies.indexOf(";");
                        if (num > 0) {
                            editor.putString("SessionId", rawCookies.substring(0, num));
                            editor.commit();
                        }
                    }
                    //cookie:SessionId=15C9425D170100610D93455BCC; path=/; HttpOnly---{"RandomNumber":"15C9425D170300610D93455BCC",
                    // "Success":true,"Message":null,"Time":"\/Date(1469067754393)\/"}
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
                String sid = sharedPreferences.getString("SessionId", "");
//                if (sid.length() > 0) {
//                    StringBuilder builder = new StringBuilder();
//                   builder.append(sid);
//                    if (headers.containsKey("Cookie")) {
//                        builder.append("; ");
//                        builder.append(headers.get("Cookie"));
//                    }
//                    headers.put("Cookie", builder.toString());
//                }
                System.out.println(";;;;;;;;" + sid);
                headers.put("Cookie", sid);
                return headers;
            }
        };

        mqueue.add(stringRequest);

    }

    private void initEvent() {
        btnGET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ceshi();
            }
        });
    }
}
