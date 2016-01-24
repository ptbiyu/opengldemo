package com.meitu.opengldemo.objects;

import android.content.Context;

import com.meitu.opengldemo.R;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_ZERO;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;

/**
 * Created by zby on 2016/1/23.
 */
public class EarserBrush extends Brush{

    public static int mColorVertexShader = R.raw.eraser_vertex_shader;
    public static int mColorFragmentShader = R.raw.eraser_fragment_shader;
    protected static final String U_SRC_TEXTURE = "srcTexture";
    private int u_Src;

    public EarserBrush(Context context) {
        super(context, mColorVertexShader, mColorFragmentShader);
        scrawlId = R.drawable.cover;
    }

    @Override
    public void init() {
        super.init();
        u_Src = glGetUniformLocation(programId, U_SRC_TEXTURE);
    }

    @Override
    public void draw() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // 基于源象素alpha通道值的半透明混合函数
        super.draw();
        glBlendFunc(GL_ONE, GL_ZERO);
    }

    @Override
    public void draw(int textureId) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // 基于源象素alpha通道值的半透明混合函数
        srcTexureId = textureId;
        super.draw();
        glBlendFunc(GL_ONE, GL_ZERO);
    }

    @Override
    public void preDraw() {
        super.preDraw();
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE1);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, srcTexureId);

        glUniform1i(u_Src, 1);

    }
}
