package com.meitu.opengldemo.objects;

import android.content.Context;
import android.opengl.GLES20;

import com.meitu.opengldemo.R;
import com.meitu.opengldemo.utils.ShaderHelper;
import com.meitu.opengldemo.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by zby on 2016/1/23.
 */
public class Brush {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPLETE_COUNT = 8;

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    // Uniform constants
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    private int aPositionLocation;
    private int aTextureCoordinatesLocation;

    private int uTextureUnitLocation;

    public int programId;

    public int scrawlId = R.drawable.dm_2060_1;

    public int srcTexureId;

    private float[] vertexData = {};
    private float[] textureData = {
            // Triangle Fan
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    protected int mVertexShader;
    protected int mFragmentShader;

    private int textureId;

    private Context context;

    public Brush(Context context) {
        this.context = context;
    }

    public Brush(Context context, int vertexShader, int fragmentShader) {
        this.context = context;
        this.mVertexShader = vertexShader;
        this.mFragmentShader = fragmentShader;
    }

    public void init() {
        programId = ShaderHelper.createProgram(context, mVertexShader, mFragmentShader);
        uTextureUnitLocation = glGetUniformLocation(programId, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(programId, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(programId, A_TEXTURE_COORDINATES);

        textureId = TextureHelper.loadTexture(context, scrawlId);
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData);

        vertexBuffer = ByteBuffer.allocateDirect(TEXTURE_COMPLETE_COUNT * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexData);

    }


    public void preDraw(){

    }
    public void draw() {
        glUseProgram(programId);
        preDraw();
        if (vertexBuffer != null)
            vertexBuffer.clear();
        vertexBuffer.put(vertexData);

        //if (vertexData.length >0)
           // Log.d("zby log","vertexData:"+vertexData[0]+","+vertexData[1]);

        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexBuffer);

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

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glDisableVertexAttribArray(aPositionLocation);
        glDisableVertexAttribArray(aTextureCoordinatesLocation);
        glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }


    public void draw(int texture) {
        glUseProgram(programId);

        if (vertexBuffer != null)
            vertexBuffer.clear();
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexData);

        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexBuffer);

        if (textureBuffer != null)
            textureBuffer.clear();
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData);

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

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glDisableVertexAttribArray(aPositionLocation);
        glDisableVertexAttribArray(aTextureCoordinatesLocation);
        glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }

    public void draw(FloatBuffer vertexBuffer,int textureId) {
        glUseProgram(programId);


        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexBuffer);

        if (textureBuffer != null)
            textureBuffer.clear();
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData);
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

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glDisableVertexAttribArray(aPositionLocation);
        glDisableVertexAttribArray(aTextureCoordinatesLocation);
        glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }

    public void putVertexData(float[] vertexData){
        this.vertexData = vertexData;
    }

    public float[] getVertexData(){
        return this.vertexData;
    }
    public void putTextureData(float[] textureData){
        this.textureData = textureData;
    }

}
