package com.meitu.opengldemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.meitu.opengldemo.utils.DisplayUtil;

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
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
                       // handleMiddlePoint(x,y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float space = spacing(event);
                        int pointCount = (int)space / 50;
                        float spaceX = x - lastX;
                        float spaceY = y - lastY;
                        //Log.d("zby log","space:"+space+",pointcount:"+pointCount);
                        for (int i = 0; i < pointCount; i++) {
                            handleMiddlePoint(lastX + spaceX / pointCount * i, lastY + spaceY / pointCount * i);
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

    private void handleMiddlePoint(float x,float y){
        float x1 = x - 50;
        float y1 = y - 50;
        float x2 = x + 50;
        float y2 = y - 50;
        float x3 = x - 50;
        float y3 = y + 50;
        float x4 = x + 50;
        float y4 = y + 50;
       /* Log.d("zby log","handleMiddlePoint:"+xPxToNormalized(x)+","+yPxToNormalized(y)+","+xPxToNormalized(x1)+","+yPxToNormalized(y1)
                +","+xPxToNormalized(x2)+","+yPxToNormalized(y2)+","+xPxToNormalized(x3)+","+yPxToNormalized(y3)+","
                +xPxToNormalized(x4)+","+yPxToNormalized(y4));*/
        float[] pointdata = {
                xPxToNormalized(x), yPxToNormalized(y) ,
                xPxToNormalized(x1), yPxToNormalized(y1) ,
                xPxToNormalized(x2), yPxToNormalized(y2) ,
                xPxToNormalized(x4), yPxToNormalized(y4) ,
                xPxToNormalized(x3), yPxToNormalized(y3) ,
                xPxToNormalized(x1),   yPxToNormalized(y1)
        };
        mScrawlRender.handTouchTextureDown(pointdata);
        glSurfaceView.requestRender();
    }

    private float xPxToNormalized(float x){
        return x / DisplayUtil.getScreenWidth(this) * 2 - 1;
    }

    private float yPxToNormalized(float y) {
        return -(y / DisplayUtil.getScreenHeight(this) * 2 - 1);
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
