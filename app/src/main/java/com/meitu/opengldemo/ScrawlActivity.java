package com.meitu.opengldemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meitu.opengldemo.utils.DisplayUtil;

/**
 * Created by zby on 2015/6/12.
 */
public class ScrawlActivity extends Activity implements View.OnClickListener{

    private static final int REQUEST_PICK = 0;
    /**
     * 三种笔
     */
    public static final int MODE_CARTOON = 0;
    public static final int MODE_FANTASY = 1;
    public static final int MODE_COLOR = 2;
    public static final int MODE_ERASER = 3;
    private RelativeLayout mBtnCartoon, mBtnFantasy, mBtnColor, mBtnEraser;
    /**
     * 当前画笔类型
     */
    private int mCurrScrawlMode = MODE_CARTOON;
    private int mLastScrawlMode = MODE_CARTOON;

    private ScrawlGLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrawl);

       /* Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image*//*");
        startActivityForResult(intent, REQUEST_PICK);*/

        findView();

    }

    private void findView() {

        mBtnCartoon = (RelativeLayout) findViewById(R.id.rl_pen_cartoon);
        mBtnFantasy = (RelativeLayout) findViewById(R.id.rl_pen_fantasy);
        mBtnColor = (RelativeLayout) findViewById(R.id.rl_pen_brush);
        mBtnEraser = (RelativeLayout) findViewById(R.id.rl_eraser);
        mBtnCartoon.setOnClickListener(this);
        mBtnFantasy.setOnClickListener(this);
        mBtnColor.setOnClickListener(this);
        mBtnEraser.setOnClickListener(this);

        mGlSurfaceView = (ScrawlGLSurfaceView) findViewById(R.id.scrawl_glsurfaveview);;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK:
                if (resultCode == RESULT_OK){
                    handleImage(data.getData());
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleImage(Uri data) {
        mGlSurfaceView.setImage(data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //glSurfaceView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_pen_cartoon:
                mLastScrawlMode = mCurrScrawlMode;
                mCurrScrawlMode = MODE_CARTOON;
                doPenUpDownAnimation(mCurrScrawlMode, mLastScrawlMode);
                break;
            case R.id.rl_pen_fantasy:
                mLastScrawlMode = mCurrScrawlMode;
                mCurrScrawlMode = MODE_FANTASY;
                doPenUpDownAnimation(mCurrScrawlMode, mLastScrawlMode);
                break;
            case R.id.rl_pen_brush:
                mLastScrawlMode = mCurrScrawlMode;
                mCurrScrawlMode = MODE_COLOR;
                doPenUpDownAnimation(mCurrScrawlMode, mLastScrawlMode);
                break;
            case R.id.rl_eraser:
                mLastScrawlMode = mCurrScrawlMode;
                mCurrScrawlMode = MODE_ERASER;
                doPenUpDownAnimation(mCurrScrawlMode, mLastScrawlMode);
                break;
        }
    }

    /**
     * 处理笔上升下降的动画
     *
     * @param needUpId   需要上升的笔的id
     * @param needDownId 需要下降的笔的id
     */
    private void doPenUpDownAnimation(final int needUpId, final int needDownId) {
        // 两者相等则说明重复点击当前上升的笔，无需变换动画，直接返回
        if (needUpId == needDownId) {
            return;
        }

        // 构造上升动画
        Animation up = new TranslateAnimation(0, 0, 0, -DisplayUtil.dipToPx(
                ScrawlActivity.this, 15));
        up.setDuration(200);
        up.setFillEnabled(true);
        up.setFillBefore(false);

        up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doPenMarginUp(needUpId);
            }
        });

        // 构造下降动画
        Animation down = new TranslateAnimation(0, 0, 0, DisplayUtil.dipToPx(ScrawlActivity.this, 15));
        down.setDuration(200);
        down.setFillEnabled(true);
        down.setFillBefore(false);
        down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doPenMarginDown(needDownId);
            }
        });

        // switch上升的笔，执行上升动画
        startAnimation(needUpId, up);
        // switch下降的笔，执行下降动画
        startAnimation(needDownId, down);
    }

    /**
     * @param penId switch上升或下降的笔id
     * @param anim  执行动画
     */
    private void startAnimation(int penId, Animation anim) {
        switch (penId) {
            case MODE_CARTOON:
                mBtnCartoon.clearAnimation();
                mBtnCartoon.startAnimation(anim);
                break;
            case MODE_FANTASY:
                mBtnFantasy.clearAnimation();
                mBtnFantasy.startAnimation(anim);
                break;
            case MODE_COLOR:
                mBtnColor.clearAnimation();
                mBtnColor.startAnimation(anim);
                break;
            case MODE_ERASER:
                mBtnEraser.clearAnimation();
                mBtnEraser.startAnimation(anim);
                break;
            default:
                break;
        }
    }

    /**
     * @param penType 让画笔按钮在布局中实际位置上升
     */
    @SuppressLint("ResourceAsColor")
    private void doPenMarginUp(int penType) {
        switch (penType) {
            case MODE_CARTOON:
                LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mBtnCartoon.getLayoutParams();
                lp1.topMargin = 0;
                mBtnCartoon.setLayoutParams(lp1);
                break;
            case MODE_FANTASY:
                LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) mBtnFantasy.getLayoutParams();
                lp2.topMargin = 0;
                mBtnFantasy.setLayoutParams(lp2);
                break;
            case MODE_COLOR:
                LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) mBtnColor.getLayoutParams();
                lp3.topMargin = 0;
                mBtnColor.setLayoutParams(lp3);
                break;
            case MODE_ERASER:
                LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) mBtnEraser.getLayoutParams();
                lp4.topMargin = 0;
                mBtnEraser.setLayoutParams(lp4);
                break;
            default:
                break;
        }
    }

    /**
     * @param penType 让画笔按钮在布局中实际位置下降
     */
    private void doPenMarginDown(int penType) {
        switch (penType) {
            case MODE_CARTOON:
                LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mBtnCartoon.getLayoutParams();
                lp1.topMargin = DisplayUtil.dipToPx(ScrawlActivity.this, 15);
                mBtnCartoon.setLayoutParams(lp1);
                break;
            case MODE_FANTASY:
                LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) mBtnFantasy.getLayoutParams();
                lp2.topMargin = DisplayUtil.dipToPx(ScrawlActivity.this, 15);
                mBtnFantasy.setLayoutParams(lp2);
                break;
            case MODE_COLOR:
                LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) mBtnColor.getLayoutParams();
                lp3.topMargin = DisplayUtil.dipToPx(ScrawlActivity.this, 15);
                mBtnColor.setLayoutParams(lp3);
                break;
            case MODE_ERASER:
                LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) mBtnEraser.getLayoutParams();
                lp4.topMargin = DisplayUtil.dipToPx(ScrawlActivity.this, 15);
                mBtnEraser.setLayoutParams(lp4);
                break;
            default:
                break;
        }
    }

}
