package com.sk392.kr.carmony.Library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by sk392 on 2016-10-14.
 */

public class SquareImageButton extends ImageButton{

    public SquareImageButton(Context context){
        super(context);
    }
    public SquareImageButton(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public SquareImageButton(Context context,AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //가로세로중 낮은 것을 기준으로 정사각형을 만든다.
        if (width > height) {
            //가로보다 세로가 더 작다면
            setMeasuredDimension(height, height);
        } else {
            setMeasuredDimension(width, width);

        }
    }
}
