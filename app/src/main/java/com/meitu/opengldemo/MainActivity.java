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

    float lastX,lastY;
    float openglLastX,openglLastY;
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

                int action = event.getAction() & MotionEvent.ACTION_MASK;
                float normalizedX = event.getX() / (float) v.getWidth() * 2 - 1;
                float normalizedY = -(event.getY() / (float)v.getHeight() * 2 -1);
                float x = event.getX();
                float y = event.getY();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        openglLastX = normalizedX;
                        openglLastY = normalizedY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float space = spacing(event);
                        int pointCount = (int)space / 2;
                        float spaceX = normalizedX - openglLastX;
                        float spaceY = normalizedY - openglLastY;
                        //Log.d("zby log","space:"+space+",pointcount:"+pointCount);
                        for (int i = 0; i < pointCount; i++) {
                            mScrawlRender.handTouchMove(openglLastX + spaceX / pointCount * i, openglLastY + spaceY / pointCount * i);
                        }
                        lastX = x;
                        lastY = y;
                        openglLastX = normalizedX;
                        openglLastY = normalizedY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

    }

    private float spacing(MotionEvent event) {
        float x = event.getX() - lastX;
        float y = event.getY() - lastY;
        return (float)Math.sqrt(x * x + y * y);
    }

    private void putPointData(){

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
