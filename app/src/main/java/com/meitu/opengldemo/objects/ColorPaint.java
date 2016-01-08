package com.meitu.opengldemo.objects;

import android.content.Context;

import com.meitu.opengldemo.R;
import com.meitu.opengldemo.utils.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by zby on 2016/1/7.
 */
public class ColorPaint {
    private static final int BYTES_PER_FLOAT = 4;

    private static int POSITION_COMPONENT_CONUT = 2;

    //private static String U_MATRIX = "u_Matrix";
    private static String A_POSITION = "a_Position";
    private static String U_COLOR ="u_Color";

    //private int uMatrixLocation;
    private int aPositionLocation;
    private int uColorLocation;

    private int program;

   private ArrayList<Float> vertexData = new ArrayList<Float>();

    private FloatBuffer floatBuffer;

    private Context context;

    public ColorPaint(Context context) {
        this.context = context;
        program = ShaderHelper.createProgram(context, R.raw.color_vertex_shader, R.raw.color_fragment_shader);

       // uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
    }

    public void useProgram(){
        glUseProgram(program);
    }

    public void  bindData(float[] projectionMatrix){
        Float[] vertexArray = vertexData.toArray(new Float[vertexData.size()]);

        float[] data = new float[vertexArray.length];
        for (int i=0;i<vertexArray.length;i++){
            data[i] = vertexArray[i].floatValue();
        }
        if (floatBuffer != null)
            floatBuffer.clear();
        floatBuffer = ByteBuffer.allocateDirect(vertexArray.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(data);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        //glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        floatBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_CONUT, GL_FLOAT, false, 0, floatBuffer);
        glEnableVertexAttribArray(aPositionLocation);
    }

    public void putVeryexData(float x,float y){
        vertexData.add(x);
        vertexData.add(y);
        //Log.d("zby log","vertexData:"+vertexData.size());
    }

    public void drawSelf(){
        glDrawArrays(GL_POINTS,0,vertexData.size()/2);
    }

}
