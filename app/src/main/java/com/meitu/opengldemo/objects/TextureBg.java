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
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by zby on 2016/1/7.
 */
public class TextureBg {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    private int aPositionLocation;
    private int aTextureCoordinatesLocation;

    private int uMatrixLocation;
    private int uTextureUnitLocation;

    private FloatBuffer vertexData;

    private Context context;

    private int program;

    public TextureBg(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, ,S,T

                // Triangle Fan
                0f, 0f, 0.5f, 0.5f,
                -1f, -1f, 0f, 1f,
                1f, -1f, 1f, 1f,
                1f, 1f, 1f, 0f,
                -1f, 1f, 0f, 0f,
                -1f, -1f, 0f, 1f,


        };
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
        program = ShaderHelper.createProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        //uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void useProgram(){
        glUseProgram(program);
    }

    public void  bindData(int textureId){
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aTextureCoordinatesLocation, TEXTURE_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aTextureCoordinatesLocation);

        // Pass the matrix into the shader program.
       // glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);


        if (textureId != TextureHelper.NO_TEXTURE) {
            // Set the active texture unit to texture unit 0.
            glActiveTexture(GL_TEXTURE0);

            // Bind the texture to this unit.
            glBindTexture(GL_TEXTURE_2D, textureId);

            // Tell the texture uniform sampler to use this texture in the shader by
            // telling it to read from texture unit 0.
            glUniform1i(uTextureUnitLocation, 0);
        }


    }

    public void drawSelf(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


}
