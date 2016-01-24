package com.meitu.opengldemo.objects;

import android.content.Context;

import com.meitu.opengldemo.R;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_ZERO;
import static android.opengl.GLES20.glBlendFunc;

/**
 * Created by zby on 2016/1/23.
 */
public class CartoonBrush extends Brush{

    public static int mColorVertexShader = R.raw.cartoon_vertex_shader;
    public static int mColorFragmentShader = R.raw.cartoon_fragment_shader;

    public CartoonBrush(Context context) {
        super(context, mColorVertexShader, mColorFragmentShader);
        scrawlId = R.drawable.dm_2060_1;
    }

    @Override
    public void draw() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // 基于源象素alpha通道值的半透明混合函数
        super.draw();
        glBlendFunc(GL_ONE, GL_ZERO);
    }
}
