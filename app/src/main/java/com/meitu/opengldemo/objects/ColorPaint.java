package com.meitu.opengldemo.objects;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.meitu.opengldemo.R;
import com.meitu.opengldemo.utils.DisplayUtil;
import com.meitu.opengldemo.utils.ShaderHelper;
import com.meitu.opengldemo.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by zby on 2016/1/7.
 */
public class ColorPaint {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPONENT_COUNT = 2;

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    // Uniform constants
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    private int aPositionLocation;
    private int aTextureCoordinatesLocation;

    private int uTextureUnitLocation;

    private int program;

    private float[] pointData ={};
    private float[] textureData = {
            // Triangle Fan
            0.5f, 0.5f,
             0f, 1f,
            1f, 1f,
             1f, 0f,
            0f, 0f,
             0f, 1f,
    };


    private FloatBuffer pointBuffer;
    private FloatBuffer textureBuffer;

    private int textureId;

    private Context context;

    private int[] mFrameBuffers = new int[1];
    private int[] mFrameBufferTextures = new int[1];

    private FloatBuffer vertexData;

    public ColorPaint(Context context) {
        this.context = context;

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData);

        program = ShaderHelper.createProgram(context, R.raw.color_vertex_shader, R.raw.color_fragment_shader);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        textureId = TextureHelper.loadTexture(context, R.drawable.dm_2060_1);

        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, ,S,T

                // Triangle Fan
                0f, 0f, 0.5f, 0.5f,
                -1f, 1f, 0f, 1f,
                1f, 1f, 1f, 1f,
                1f, -1f, 1f, 0f,
                -1f, -1f, 0f, 0f,
                -1f, 1f, 0f, 1f,


        };
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

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

    public void useProgram(){
        glUseProgram(program);
    }

    public void  bindData(){
       if (pointData.length > 0)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA); // 基于源象素alpha通道值的半透明混合函数
        if (pointBuffer!=null)
            pointBuffer.clear();

        pointBuffer = ByteBuffer.allocateDirect(pointData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        pointBuffer.put(pointData);

        pointBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, pointBuffer);

        textureBuffer.position(0);
        glVertexAttribPointer(aTextureCoordinatesLocation, TEXTURE_COMPONENT_COUNT, GL_FLOAT,
                false, 0, textureBuffer);

        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aTextureCoordinatesLocation);

        if (pointData.length > 0) {
            // Set the active texture unit to texture unit 0.
            glActiveTexture(GL_TEXTURE0);

            // Bind the texture to this unit.
            glBindTexture(GL_TEXTURE_2D, textureId);

            Log.d("zby log","textureId:"+textureId);
            // Tell the texture uniform sampler to use this texture in the shader by
            // telling it to read from texture unit 0.
            glUniform1i(uTextureUnitLocation, 0);
        }

    }

    public void putVeryexData(float normalizedX,float normalizedY){
    }

    public void putVeryexData(float[] pointData){
        this.pointData = pointData;
    }

    public void drawSelf() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
       if (pointData.length > 0) {
           GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aTextureCoordinatesLocation, TEXTURE_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aTextureCoordinatesLocation);

            // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

            // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, mFrameBufferTextures[0]);

            // Tell the texture uniform sampler to use this texture in the shader by
            // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

}
