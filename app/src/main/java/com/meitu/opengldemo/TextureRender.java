package com.meitu.opengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.meitu.opengldemo.utils.ShaderHelper;
import com.meitu.opengldemo.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by zby on 2015/6/12.
 */
public class TextureRender implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    /*private static final String A_POSITION = "a_Position";
    private static final String A_TEXTURECOORDINATES = "a_TextureCoordinates";
    private static final String U_MATRIX = "u_Matrix";
    private static final String U_TEXTUREUNIT = "u_TextureUnit";*/

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    private int aPositionLocation;
    private int aTextureCoordinatesLocation;

    private int uMatrixLocation;
    private int uTextureUnitLocation;

    private FloatBuffer vertexData;

    private Context context;

    private int program;

    private float[] projectionMatrix = new float[16];

    private float[] modelMatrix = new float[16];

    private int texture;

    public TextureRender(Context context) {
        this.context = context;

        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, ,S,T

                // Triangle Fan
        /*        0f, 0f, 0.5f, 0.5f,
                -1f, -1f, 0f, 1f,
                1f, -1f, 1f, 1f,
                1f, 1f, 1f, 0f,
                -1f, 1f, 0f, 0f,
                -1f, -1f, 0f, 1f,*/

                0f,    0f, 0.5f, 0.5f,
                -0.5f, -0.8f,   0f, 0.9f,
                0.5f, -0.8f,   1f, 0.9f,
                0.5f,  0.8f,   1f, 0.1f,
                -0.5f,  0.8f,   0f, 0.1f,
                -0.5f, -0.8f,   0f, 0.9f

        };

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();


        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        program = ShaderHelper.createProgram(context, R.raw.vertex_shader, R.raw.fragment_shader);
        glUseProgram(program);

        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

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

        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        Log.d("zby log","texture:"+texture);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, texture);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aTextureCoordinatesLocation, TEXTURE_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aTextureCoordinatesLocation);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

    }
}
