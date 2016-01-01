package com.meitu.opengldemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by zby on 2015/6/12.
 */
public class MainActivity extends Activity{

    private GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean suportsES2 = configurationInfo.reqGlEsVersion >= 0x2000;
        if (suportsES2){
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new TextureRender(this));
        }else{
            Toast.makeText(this,"This device does not support ES 2.0",Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(glSurfaceView);

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
