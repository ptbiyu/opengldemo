package com.meitu.opengldemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.meitu.opengldemo.objects.CartoonBrush;
import com.meitu.opengldemo.objects.ColorBrush;
import com.meitu.opengldemo.objects.EarserBrush;
import com.meitu.opengldemo.objects.FantasyBrush;

import java.io.FileNotFoundException;

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


    public void handTouch(float[] vertexdata) {
        mScrawlRender.handTouch(vertexdata);
        requestRender();
    }

    public void handTouch(float x,float y) {
        mScrawlRender.handTouch(x,y);
        requestRender();
    }

    public void handTouchUp() {
        mScrawlRender.handTouchUp();
    }

    public void changeBrush(int modeBrush) {
        switch (modeBrush){
            case ScrawlActivity.MODE_CARTOON:
                mScrawlRender.changeBrush(new CartoonBrush(mContext));
                break;
            case ScrawlActivity.MODE_FANTASY:
                mScrawlRender.changeBrush(new FantasyBrush(mContext));
                break;
            case ScrawlActivity.MODE_COLOR:
                mScrawlRender.changeBrush(new ColorBrush(mContext));
                break;
            case ScrawlActivity.MODE_ERASER:
                mScrawlRender.changeBrush(new EarserBrush(mContext));
                break;
        }
    }

    public void setImage(Uri uri) {

        new LoadImageUriTask(this, uri).execute();
    }

    private class LoadImageUriTask extends AsyncTask<Void,Void,Bitmap>{
        private Uri mUri;
        private int mOutputWidth;
        private int mOutputHeight;
        public LoadImageUriTask(ScrawlGLSurfaceView scrawlGLSurfaceView, Uri uri) {
            mUri = uri;
            Log.d("zby log","LoadImageUriTask");
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            mOutputWidth = mScrawlRender.getFrameWidth();
            mOutputHeight = mScrawlRender.getFrameHeigth();
            return loadResizedImage();
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            deleteImage();
            mScrawlRender.setImage(bitmap);
            requestRender();
        }

        private Bitmap loadResizedImage() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            int inSampleSize = 1;
            int[] size = getBitmapSize();
            if (size != null){
                inSampleSize = Math.max(size[0]/mOutputWidth,size[1]/mOutputHeight);
            }
            if (inSampleSize > 1)
                options.inSampleSize = inSampleSize;
            options.inDither = false;
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            Bitmap bitmap = decodeBitmap(options);
            if (bitmap == null){
                return null;
            }
            bitmap = rotateImage(bitmap);
            bitmap = resizeBitmap(bitmap);
            return bitmap;
        }

        private  int[] getBitmapSize(){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeBitmap(options);
            return new int[]{options.outWidth,options.outHeight};
        }

        private Bitmap decodeBitmap(BitmapFactory.Options options){
            try {
                return BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(mUri),null,options);
              } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
             }
        }

        private Bitmap rotateImage(Bitmap bitmap){
            if (bitmap == null)
                return null;

            int orientation = getImageOrientation();
            if (orientation != 0){
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),
                        bitmap.getHeight(),matrix,true);
                bitmap.recycle();
                return  rotatedBitmap;
            }
            return bitmap;
        }

        private int getImageOrientation(){
            Cursor cursor = mContext.getContentResolver().query(mUri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

            if (cursor == null || cursor.getCount() != 1)
                return 0;

            cursor.moveToFirst();
            int orientation = cursor.getInt(0);
            cursor.close();
            return orientation;
        }

        private Bitmap resizeBitmap(Bitmap input) {
             if (input == null || input.isRecycled())
                 return null;

             if (mOutputWidth <= 0 || mOutputHeight <= 0)
                 return input;

            int srcWidth = input.getWidth();
            int srcHeight = input.getHeight();
            boolean needResize = false;
            float scaleRation;
            if (srcWidth > mOutputWidth || srcHeight > mOutputHeight) {
                needResize = true;
                if (srcWidth > srcHeight && srcWidth > mOutputHeight) {
                    scaleRation = (float) mOutputWidth / (float) srcWidth;
                    mOutputHeight = (int) (mOutputHeight * scaleRation);
                } else {
                    scaleRation = (float) mOutputHeight / (float) srcHeight;
                    mOutputWidth = (int) (srcWidth * scaleRation);
                }
            }else{
                mOutputWidth = srcWidth;
                mOutputHeight = srcWidth;
            }

            if (needResize){
                Bitmap output = Bitmap.createScaledBitmap(input,mOutputWidth,mOutputHeight,true);
                input.recycle();
                return output;
            }else {
                return input;
            }
        }
    }

    /**
     * Deletes the current image.
     */
    public void deleteImage() {
        mScrawlRender.deleteImage();
        requestRender();
    }
}
