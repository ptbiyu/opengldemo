package com.meitu.opengldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.meitu.opengldemo.objects.ColorPaint;
import com.meitu.opengldemo.objects.TextureBg;
import com.meitu.opengldemo.utils.TextureHelper;

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

    private float[] projectionMatrix = new float[16];

    private float[] modelMatrix = new float[16];

    private int texture = -1;

    private ColorPaint colorPaint;
    private TextureBg textureBg;

    private int mOutputWidth;
    private int mOutputHeight;

    public ScrawlRender(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.96f, 0.96f, 0.96f, 0.0f);
        texture = TextureHelper.loadTexture(context, R.drawable.test);

       // colorPaint = new ColorPaint(context);
        textureBg = new TextureBg(context);
        glEnable(GL_BLEND); // 打开混合
        glDisable(GL_DEPTH_TEST); // 关闭深度测试
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        onOutputSizeChanged(width, height);
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

        textureBg.useProgram();
        textureBg.bindData(projectionMatrix, texture);
        textureBg.drawSelf();

       /* colorPaint.useProgram();
        colorPaint.bindData();
        colorPaint.drawSelf();*/

    }

    public void handTouchDown(float normalizedX,float normalizedY){
        colorPaint.putVeryexData(normalizedX, normalizedY);
    }

    public void handTouchMove(float normalizedX,float normalizedY){
        colorPaint.putVeryexData(normalizedX, normalizedY);
    }

    public void handTouchUp(float normalizedX,float normalizedY){

    }

    public void handTouchTextureDown(float[] pointData){
        colorPaint.putVeryexData(pointData);
    }

    public void setImage(Bitmap bitmap) {
        deleteImage();
        texture = TextureHelper.loadTexture(texture, bitmap);
        Log.d("zby log", "texture:" + texture + ",bitmap:" + bitmap);
    }

    public void deleteImage() {
        GLES20.glDeleteTextures(1, new int[]{texture}, 0);
        texture = -1;
    }
}
