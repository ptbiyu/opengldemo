package com.meitu.opengldemo.objects;

import android.content.Context;

import com.meitu.opengldemo.R;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_ZERO;
import static android.opengl.GLES20.glBlendFunc;

/**
 * Created by zby on 2016/1/23.
 */
public class FantasyBrush extends Brush{

    public static int mColorVertexShader = R.raw.fantasy_vertex_shader;
    public static int mColorFragmentShader = R.raw.fantasy_fragment_shader;

    public FantasyBrush(Context context) {
        super(context, mColorVertexShader, mColorFragmentShader);
        scrawlId = R.drawable.dm_1067_8;
    }

    @Override
    public void draw() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        super.draw();
        glBlendFunc(GL_ONE, GL_ZERO);
    }
}
