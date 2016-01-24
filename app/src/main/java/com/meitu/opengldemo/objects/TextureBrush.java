package com.meitu.opengldemo.objects;

import android.content.Context;

import com.meitu.opengldemo.R;

/**
 * Created by zby on 2016/1/23.
 */
public class TextureBrush extends Brush{

    public static int mTextureVertexShader = R.raw.texture_vertex_shader;
    public static int mTextureFragmentShader = R.raw.texture_fragment_shader;

    public TextureBrush(Context context) {
        super(context, mTextureVertexShader, mTextureFragmentShader);
    }

    @Override
    public void init() {
        super.init();
        float[] vertexData = {
                // Order of coordinates: X, Y, ,S,T

                // Triangle Fan
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,


        };
        putVertexData(vertexData);

        float[] textureData = {
                // Order of coordinates: X, Y, ,S,T

                // Triangle Fan
                0.0f, 0f,
                1.0f, 0f,
                0.0f, 1.0f,
                1.0f, 1.0f,


        };
        putTextureData(textureData);


    }

}
