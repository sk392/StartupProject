package com.sk392.kr.carmony.Library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by sk392 on 2016-10-12.
 */

public class SquareImageView extends ImageView{

    public SquareImageView(Context context){
        super(context);
    }
    public SquareImageView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public SquareImageView(Context context,AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width,height;
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //가로세로중 낮은 것을 기준으로 정사각형을 만든다.
        if(width>height){
            //가로보다 세로가 더 작다면
            setMeasuredDimension(height,height);
        }else{
            setMeasuredDimension(width,width);

        }
    }
}
