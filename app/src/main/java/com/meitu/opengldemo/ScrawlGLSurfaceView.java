package com.meitu.opengldemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zby on 2016/1/21.
 */
public class ScrawlGLSurfaceView extends GLSurfaceView{

    private ScrawlRender mScrawlRender;

    private Context mContext;

    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;

    public ScrawlGLSurfaceView(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public ScrawlGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        if (!supportsOpenGLES2(mContext)) {
            throw new IllegalStateException("OpenGL ES 2.0 is not supported on this phone.");
        }
        setEGLContextClientVersion(2);
        mScrawlRender = new ScrawlRender(mContext);
        setRenderer(mScrawlRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /**
     * Checks if OpenGL ES 2.0 is supported on the current device.
     *
     * @param context the context
     * @return true, if successful
     */
    private boolean supportsOpenGLES2(final Context context) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    public void setImage(Uri uri) {

        new LoadImageUriTask(this, uri).execute();
    }

    private class LoadImageUriTask extends AsyncTask<Void,Void,Bitmap>{
        private Uri mUri;
        private int mOnutputWidth;
        private int mOutputHeight;
        public LoadImageUriTask(ScrawlGLSurfaceView scrawlGLSurfaceView, Uri uri) {
            mUri = uri;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return loadResizedImage();
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mScrawlRender.setImage(bitmap);
            requestRender();
        }

        private Bitmap loadResizedImage() {
            return null;
        }
    }
}
