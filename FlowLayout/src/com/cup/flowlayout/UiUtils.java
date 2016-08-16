package com.itheima.flowlayout82;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Ui相关工具类
 * @author dzl
 *
 */
public class UiUtils {
	
	/** 创建一个随机的颜色 */
	public static int createRandomColor() {
		Random random = new Random();
		int red = 50 + random.nextInt(151);		// 范围：50 ~ 200
		int green = 50 + random.nextInt(151);	// 范围：50 ~ 200
		int blue = 50 + random.nextInt(151);	// 范围：50 ~ 200
		int color = Color.rgb(red, green, blue);
		return color;
	}
	
	/**
	 * 创建一个拥有随机选择器效果的TextView
	 * @return
	 */
	public static TextView createRandomSelectorTextView(Context context) {
		TextView textView = new TextView(context);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), ((TextView) v).getText(), 0).show();
			}
		});
		textView.setClickable(true);
		textView.setTextColor(Color.WHITE);
		int padding = 6;
		textView.setPadding(padding, padding, padding, padding);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundDrawable(createRandomSelector());
		return textView;
	}

	public static Drawable createRandomSelector() {
		StateListDrawable stateListDrawable = new StateListDrawable();
		
		// 创建一个按下状态、正常状态
		int[] pressState = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
		int[] normalState = new int[]{};
		
		// 创建一个按下状态要显示的Drawable、正常状态要显示的Drawable
		Drawable pressDrawable = createRandomColorDrawable();
		Drawable normalDrawable = createRandomColorDrawable();
		
		stateListDrawable.addState(pressState, pressDrawable);
		stateListDrawable.addState(normalState, normalDrawable);
		return stateListDrawable;
	}
	
	/** 创建一个有随机颜色的Drawable */
	public static Drawable createRandomColorDrawable() {
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setShape(GradientDrawable.RECTANGLE);	// 指定图形为矩形
		gradientDrawable.setCornerRadius(5);		// 设置四个角的角度
		int color = UiUtils.createRandomColor();
		gradientDrawable.setColor(color);						// 设置矩形的颜色
		return gradientDrawable;
	}
}
