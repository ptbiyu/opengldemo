package com.meitu.opengldemo.objects;

import android.content.Context;
import android.opengl.GLES20;

import com.meitu.opengldemo.R;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ZERO;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;

/**
 * Created by zby on 2016/1/23.
 */
public class ColorBrush extends Brush{

    public static int mColorVertexShader = R.raw.color_vertex_shader;
    public static int mColorFragmentShader = R.raw.color_fragment_shader;

    private  static final String U_COLOR ="u_Color";
    private int u_ColorLocation;

    public ColorBrush(Context context) {
        super(context, mColorVertexShader, mColorFragmentShader);
        scrawlId = R.drawable.cover;
    }

    @Override
    public void init() {
        super.init();
        u_ColorLocation = glGetUniformLocation(programId, U_COLOR);
    }

    @Override
    public void draw() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        super.draw();
        glBlendFunc(GL_ONE, GL_ZERO);
    }

    @Override
    public void preDraw() {
        glUniform4f(u_ColorLocation, 1.0f, 0f, 0f, 1f);
    }

}
