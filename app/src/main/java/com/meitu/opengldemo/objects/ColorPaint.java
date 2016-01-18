package com.meitu.opengldemo.objects;

import android.content.Context;

import com.meitu.opengldemo.R;
import com.meitu.opengldemo.utils.ShaderHelper;
import com.meitu.opengldemo.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenFramebuffers;
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

    //private Context context;

    private int[] mFramebuffer = new int[1];

    public ColorPaint(Context context) {
        //this.context = context;

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData);

        program = ShaderHelper.createProgram(context, R.raw.color_vertex_shader, R.raw.color_fragment_shader);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        textureId = TextureHelper.loadTexture(context, R.drawable.dm_2079_1);

         glGenFramebuffers(1,mFramebuffer,0);

    }

    public void useProgram(){
        glUseProgram(program);
    }

    public void  bindData(){

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

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

    }

    public void putVeryexData(float normalizedX,float normalizedY){
    }

    public void putVeryexData(float[] pointData){
        this.pointData = pointData;
    }

    public void drawSelf(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

}
