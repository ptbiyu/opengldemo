package com.meitu.opengldemo.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zby on 2015/6/12.
 */
public class TextResourceReader {

    public static String readTextFileFromResource(Context context,int resId){
        StringBuilder sb = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader stringReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine =stringReader.readLine()) != null){
                sb.append(nextLine);
                sb.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return sb.toString();


    }
}
