package com.meitu.opengldemo;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.meitu.opengldemo.objects.ColorPaint;
import com.meitu.opengldemo.objects.TextureBg;
import com.meitu.opengldemo.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by zby on 2015/6/12.
 */
public class ScrawlRender implements GLSurfaceView.Renderer {

    private Context context;

    private float[] projectionMatrix = new float[16];

    private float[] modelMatrix = new float[16];

    private int texture;

    private ColorPaint colorPaint;
    private TextureBg textureBg;

    public ScrawlRender(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        colorPaint = new ColorPaint(context);
        textureBg = new TextureBg(context);
        texture = TextureHelper.loadTexture(context, R.drawable.test);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ? ((float) width / (float) height) : ((float) height / (float) width);
        if (width > height)
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, -1, 1);
        else
            orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);

      /*  MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1, 10);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0, 0, -3f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);*/

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        textureBg.useProgram();
        textureBg.bindData(projectionMatrix,texture);
        textureBg.drawSelf();

        colorPaint.useProgram();
        colorPaint.bindData(projectionMatrix);
        colorPaint.drawSelf();

    }

    public void handTouchDown(float normalizedX,float normalizedY){
        colorPaint.putVeryexData(normalizedX,normalizedY);
    }

    public void handTouchUp(float normalizedX,float normalizedY){

    }
}
