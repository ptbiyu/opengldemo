package com.meitu.opengldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.meitu.opengldemo.objects.Brush;
import com.meitu.opengldemo.objects.CartoonBrush;
import com.meitu.opengldemo.objects.TextureBg;
import com.meitu.opengldemo.objects.TextureBrush;
import com.meitu.opengldemo.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

/**
 * Created by zby on 2015/6/12.
 */
public class ScrawlRender implements GLSurfaceView.Renderer {

    private static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };
    private Context context;

    private int textureId = TextureHelper.NO_TEXTURE;

    private Queue<Runnable> mRunOnDraw;
    private TextureBg textureBg;
    private Brush mBrush;
    private TextureBrush mTextureBrush;
    private int mOutputWidth;
    private int mOutputHeight;
    private int mImageWidth;
    private int mImageHeight;

    private int[] mFrameBuffers = new int[1];
    private int[] mFrameBufferTextures = new int[1];
    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mBuffer;
    float ratioWidth;
    float ratioHeight;
    boolean init = false;


    public ScrawlRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.96f, 0.96f, 0.96f, 0.0f);
        mRunOnDraw = new LinkedList<Runnable>();
        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mBuffer.put(CUBE).position(0);

        textureBg = new TextureBg(context);
        mBrush = new CartoonBrush(context);
        mBrush.init();
        mTextureBrush = new TextureBrush(context);
        mTextureBrush.init();
        //creatFrameBuffer(DisplayUtil.getScreenWidth(context), DisplayUtil.getScreenHeight(context));
        glEnable(GL_BLEND); // 打开混合
        glDisable(GL_DEPTH_TEST); // 关闭深度测试
    }

    private void creatFrameBuffer(int width,int height){
        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

        Log.d("zby log", "mFrameBuffers:" + mFrameBuffers[0] + ",mFrameBufferTextures:" + mFrameBufferTextures[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        onOutputSizeChanged(width, height);
        Log.d("zby log", "onSurfaceChanged:" + width + ",height:" + height);

    }

    private void onOutputSizeChanged(int width, int height){
        Log.d("zby log", "width:" + width + ",height:" + height);
        mOutputWidth = width;
        mOutputHeight = height;
    }

    public int getFrameWidth(){
        return mOutputWidth;
    }

    public int getFrameHeigth(){
        return mOutputHeight;
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        runAll(mRunOnDraw);


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        if (!init){
            textureBg.useProgram();
            textureBg.bindData(mGLCubeBuffer, textureId);
            textureBg.drawSelf();
            init = true;
        }

        mBrush.draw();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        mTextureBrush.draw(mBuffer,mFrameBufferTextures[0]);

    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue){
            while (!queue.isEmpty()){
                queue.poll().run();
            }
        }

    }

    public void setImage(final Bitmap bitmap) {
        if (bitmap == null)
            return;
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                deleteImage();
                Bitmap resizedBitmap = null;
                if (bitmap.getWidth() % 2 == 1) {
                    resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(resizedBitmap);
                    canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                }
                textureId = TextureHelper.loadTexture(textureId, resizedBitmap == null ? bitmap : resizedBitmap);
                if (resizedBitmap != null) {
                    resizedBitmap.recycle();
                }
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                adjustIamgeScaling();
            }
        });
    }

    private void adjustIamgeScaling() {
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;
        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        ratioWidth = imageWidthNew / outputWidth;
        ratioHeight = imageHeightNew / outputHeight;

        float[] cube = CUBE;
        cube = new float[]{
                CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
        };
        mBuffer.clear();
        mBuffer.put(cube).position(0);
        Log.d("zby log", "imageWidthNew:" + mImageWidth  + ",imageHeightNew" + imageHeightNew);
        creatFrameBuffer(1440, 1800);
        init = false;
    }

    public void deleteImage() {
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        textureId = -1;
    }

    public void handTouch(float touchX,float touchY) {
        float x = xPxToNormalized(touchX) * ratioHeight;
        float y = yPxToNormalized(touchY) * ratioWidth;
        float x1 = x - 0.1f;
        float y1 = y - 0.1f;
        float x2 = x + 0.1f;
        float y2 = y - 0.1f;
        float x3 = x + 0.1f;
        float y3 = y + 0.1f;
        float x4 = x - 0.1f;
        float y4 = y + 0.1f;
        float[] vertexdata = {
                x1, y1,
                x2, y2,
                x4, y4,
                x3, y3,
        };
        Log.d("zby log","x:"+x+",y"+y+",x1:"+x1+",y1"+y1);

        mBrush.putVertexData(vertexdata);

    }

    private float xPxToNormalized(float x){
        return x / mOutputWidth * 2 - 1;
    }

    private float yPxToNormalized(float y) {
        return -(y / mOutputHeight * 2 - 1);
    }

    public void handTouch(float[] vertexdata) {
        mBrush.putVertexData(vertexdata);
    }

    public void changeBrush(final Brush brush){
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                mBrush = null;
                mBrush = brush;
                mBrush.init();
            }
        });
    }

    protected void runOnDraw(final Runnable runnale){
        synchronized (mRunOnDraw){
            mRunOnDraw.add(runnale);
        }
    }
}
