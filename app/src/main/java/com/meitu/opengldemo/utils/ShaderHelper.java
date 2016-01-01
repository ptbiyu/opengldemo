package com.meitu.opengldemo.utils;

import android.content.Context;
import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by zby on 2015/6/12.
 */
public class ShaderHelper {


    public  static  int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER,shaderCode);
    }

    public  static  int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER,shaderCode);
    }

    public static int compileShader(int type,String shaderCode){
        int shaderObjectId = glCreateShader(type);
        if(shaderObjectId == 0){
            return 0;
        }
        glShaderSource(shaderObjectId, shaderCode);
        glCompileShader(shaderObjectId);

        final int[] complieStatus = new int[1];
        glGetShaderiv(shaderObjectId,GL_COMPILE_STATUS,complieStatus,0);

        if(complieStatus[0] == 0){
           glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShader,int fragmentShader){
        int programObjtectId = glCreateProgram();

        if(programObjtectId == 0){
            return 0;
        }
        glAttachShader(programObjtectId,vertexShader);
        glAttachShader(programObjtectId, fragmentShader);

        glLinkProgram(programObjtectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjtectId, GL_LINK_STATUS, linkStatus, 0);

        if(linkStatus[0] == 0){
            glDeleteProgram(programObjtectId);
            return 0;
        }
        validateProgram(programObjtectId);
        return programObjtectId;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d("zby log", "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static int createProgram(Context context,int vertexRes,int fragmentRes){
        String vertexShaderCode = TextResourceReader.readTextFileFromResource(context, vertexRes);
        String fragmentShaderCode = TextResourceReader.readTextFileFromResource(context,fragmentRes);
        int vertexShder = compileVertexShader(vertexShaderCode);
        int fragmentShader = compileFragmentShader(fragmentShaderCode);

        int program = linkProgram(vertexShder,fragmentShader);

        return program;

    }

}
