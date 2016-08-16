package com.itheima.flowlayout82;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
	
	/** 用于保存多行View的集合 */
	private ArrayList<ArrayList<View>> allLines = new ArrayList<ArrayList<View>>();
	private int horizotalSpacing = 6;
	private int verticalSpacing = 6;

	public FlowLayout(Context context) {
		super(context);
	}
	
	/**
	 * 容器的onMeasure方法功能：测量自己的宽高、测量所有子View的宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 因为onMeasure会执行多次，所以每次测量的时候清空一下集合
		allLines.clear();
		MeasureSpecUtil.printMeasureSpec(widthMeasureSpec, heightMeasureSpec);
		
		// 容器测量的宽
		int containerMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
		
		// 装一行View的集合
		ArrayList<View> oneLine = null;
		
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(0, 0);
			// 0就是这样的一个测量规格： MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			
			if (i == 0 || child.getMeasuredWidth() > getUsableWidth(containerMeasureWidth, oneLine)) {
				oneLine = new ArrayList<View>();
				allLines.add(oneLine);
			}
			
			oneLine.add(child);
			
		}
		
		int containerMeasureHeight = getAllLinesHeight();
		
		setMeasuredDimension(containerMeasureWidth, containerMeasureHeight);
	}

	/** 获取所有行的高 */
	private int getAllLinesHeight() {
		if (allLines.isEmpty()) {
			return 0;
		}
		
		int containerPadding = getPaddingTop() + getPaddingBottom();
		int totalVerticalSpacing = verticalSpacing * (allLines.size() - 1);
		return getChildAt(0).getMeasuredHeight() * allLines.size() + containerPadding + totalVerticalSpacing;
	}

	/**
	 * 获取一行当中可用的宽
	 * @param containerMeasureWidth
	 * @param oneLine
	 * @return
	 */
	private int getUsableWidth(int containerMeasureWidth, ArrayList<View> oneLine) {
		int oneLineWidth = 0;
		for (View view : oneLine) {
			oneLineWidth = oneLineWidth + view.getMeasuredWidth();
		}
		int containerPadding = getPaddingLeft() + getPaddingRight();
		int totalHorizotalSpacing = horizotalSpacing * (oneLine.size() - 1);
		return containerMeasureWidth - containerPadding - oneLineWidth - totalHorizotalSpacing;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int tempBottom = 0;	// 第一行的View从paddingTop开始画
		
		// 遍历所有的行
		for (int rowIndex = 0; rowIndex < allLines.size(); rowIndex++) {
			ArrayList<View> oneLine = allLines.get(rowIndex);	// 取出一行View
			
			int tempRight = 0;	// 第一列View从paddingLeft的位置开始画
			
			// 每个View平均可以多获得的宽度
			int averageUsableWidth = getUsableWidth(getMeasuredWidth(), oneLine) / oneLine.size();
			
			// 遍历一行View
			for (int columnIndex = 0; columnIndex < oneLine.size(); columnIndex++) {
				View child = oneLine.get(columnIndex);	// 取出一行中的一个View
				
				int childMeasuredWidth = child.getMeasuredWidth();
				int childMeasuredHeight = child.getMeasuredHeight();
				
				int childLeft = (columnIndex == 0) ? getPaddingLeft() : tempRight + horizotalSpacing;
				int childTop = (rowIndex == 0) ? getPaddingTop() : tempBottom + verticalSpacing;
				
				// 如果是最后一列，则把View的right安排在容器的最右边
				int childRight = (columnIndex == oneLine.size() - 1)
						         ? getMeasuredWidth() - getPaddingRight()
						         : childLeft + childMeasuredWidth + averageUsableWidth;
				int childBottom = childTop + childMeasuredHeight;
				child.layout(childLeft, childTop, childRight, childBottom);
				
				// 这个时候给子View的宽设置的跟以前测量的宽不一样，所以重新再测量一下，让居中属性生效
				int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childRight - childLeft, MeasureSpec.EXACTLY);
				int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childBottom - childTop, MeasureSpec.EXACTLY);
				child.measure(widthMeasureSpec , heightMeasureSpec);
				
				// 保存Right，下一个子View的left就是这个坐标
				tempRight = childRight;
			}
			
			// 保存上一行View的Bottom位置，方便下一行使用，下一行的View的top就是这个Bottom值
			tempBottom = oneLine.get(0).getBottom();
		}
		
	}
	
}
