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
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
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

    private static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };
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

    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer textureBuffer;
    private Context context;

    private int program;

    public TextureBg(Context context) {
        this.context = context;
        float[] vertexDate = {
                // Order of coordinates: X, Y, ,S,T
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        textureBuffer = ByteBuffer.allocateDirect(vertexDate.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(vertexDate);
        program = ShaderHelper.createProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        //uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void useProgram(){
        glUseProgram(program);
    }

    public void  bindData(FloatBuffer vertexBuffer,int textureId){
        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexBuffer);

        textureBuffer.position(0);
        glVertexAttribPointer(aTextureCoordinatesLocation, TEXTURE_COMPONENT_COUNT, GL_FLOAT,
                false, 0, textureBuffer);

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
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }


}
