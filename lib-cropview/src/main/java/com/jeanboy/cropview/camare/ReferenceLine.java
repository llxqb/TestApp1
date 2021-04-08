package com.jeanboy.cropview.camare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jeanboy.cropview.utils.Utils;


/**
 * @Class: ReferenceLine
 * @Description: 网格参考线
 * @author: lling(www.cnblogs.com/liuling)
 * @Date: 2015/10/20
 */
public class ReferenceLine extends View {

	private Paint mLinePaint;
	private boolean isLand=false;//是否是横屏方向

	public ReferenceLine(Context context) {
		super(context);
		init();
	}

	public ReferenceLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ReferenceLine(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void setLand(boolean land) {
		isLand = land;
	}

	private void init() {
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.parseColor("#45e0e0e0"));
		mLinePaint.setStrokeWidth(1);
	}



	@Override
	protected void onDraw(Canvas canvas) {
		Log.e("---","isLand="+isLand);
		int screenWidth;
		int screenHeight;
		if(isLand){
			screenWidth = Utils.getScreenWH(getContext()).widthPixels-146;
			screenHeight = Utils.getScreenWH(getContext()).heightPixels;
		}else {
			screenWidth = Utils.getScreenWH(getContext()).widthPixels;
			screenHeight = Utils.getScreenWH(getContext()).heightPixels-146;
		}

		int width = screenWidth/3;
		int height = screenHeight/3;

		for (int i = width, j = 0;i < screenWidth && j<2;i += width, j++) {
			canvas.drawLine(i, 0, i, screenHeight, mLinePaint);
		}
		for (int j = height,i = 0;j < screenHeight && i < 2;j += height,i++) {
			canvas.drawLine(0, j, screenWidth, j, mLinePaint);
		}
	}


}
