package com.meitu.opengldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.meitu.opengldemo.objects.Brush;
import com.meitu.opengldemo.objects.CartoonBrush;
import com.meitu.opengldemo.objects.TextureBg;
import com.meitu.opengldemo.objects.TextureBrush;
import com.meitu.opengldemo.utils.DisplayUtil;
import com.meitu.opengldemo.utils.TextureHelper;

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

    private Context context;

    private int textureId = TextureHelper.NO_TEXTURE;


    private Queue<Runnable> mRunOnDraw;
    private TextureBg textureBg;
    private Brush mBrush;
    private TextureBrush mTextureBrush;
    private int mOutputWidth;
    private int mOutputHeight;

    private int[] mFrameBuffers = new int[1];
    private int[] mFrameBufferTextures = new int[1];

    public ScrawlRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.96f, 0.96f, 0.96f, 0.0f);
        mRunOnDraw = new LinkedList<Runnable>();
        textureBg = new TextureBg(context);
        mBrush = new CartoonBrush(context);
        mBrush.init();
        mTextureBrush = new TextureBrush(context);
        mTextureBrush.init();
        glEnable(GL_BLEND); // 打开混合
        glDisable(GL_DEPTH_TEST); // 关闭深度测试
        creatFrameBuffer();
    }

    private void creatFrameBuffer(){
        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, DisplayUtil.getScreenWidth(context), DisplayUtil.getScreenHeight(context), 0,
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

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        onOutputSizeChanged(width, height);
        Log.d("zby log","onSurfaceChanged:"+width+",height:"+height);

    }

    private void onOutputSizeChanged(int width, int height){
        Log.d("zby log","width:"+width+",height:"+height);
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
        textureBg.useProgram();
        textureBg.bindData(textureId);
        textureBg.drawSelf();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

        mBrush.draw();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        mTextureBrush.draw(mFrameBufferTextures[0]);

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
                textureId = TextureHelper.loadTexture(textureId,bitmap);
                Log.d("zby log","tex:"+textureId);
            }
        });
    }

    public void deleteImage() {
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        textureId = -1;
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
