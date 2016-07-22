package com.xiaohu.myvolleytest.http;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2016/7/22.
 */
public class HttpHandler {
    Handler handler;

    public HttpHandler(final HttpResultInterfaceClass threadHttp) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // if(msg.what==0){
                threadHttp.HttpResultInterfaceMethod();
                //  }
            }
        };
        update_thread.run();

    }

    //线程中运行该接口的run函数
    Runnable update_thread = new Runnable() {
        public void run() {
            //线程每次执行时输出"UpdateThread..."文字,且自动换行
            //textview的append功能和Qt中的append类似，不会覆盖前面
            //的内容，只是Qt中的append默认是自动换行模式
            //延时1s后又将线程加入到线程队列中
            // handler.postDelayed(update_thread, 1000);
            Message message = new Message();
            handler.sendMessage(message);
        }
    };

    public interface HttpResultInterfaceClass {
        void HttpResultInterfaceMethod();
    }
}
