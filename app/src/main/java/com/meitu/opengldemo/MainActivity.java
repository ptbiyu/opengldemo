package com.meitu.opengldemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by zby on 2015/6/12.
 */
public class MainActivity extends Activity{

    private GLSurfaceView glSurfaceView;

    private ScrawlRender mScrawlRender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean suportsES2 = configurationInfo.reqGlEsVersion >= 0x2000;
        if (suportsES2){
            glSurfaceView.setEGLContextClientVersion(2);
            mScrawlRender = new ScrawlRender(this);
            glSurfaceView.setRenderer(mScrawlRender);
        }else{
            Toast.makeText(this,"This device does not support ES 2.0",Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(glSurfaceView);

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float normalizedX = event.getX() / (float) v.getWidth() * 2 - 1;
                float normalizedY = -(event.getY() / (float)v.getHeight() * 2 -1);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mScrawlRender.handTouchDown(normalizedX,normalizedY);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}
