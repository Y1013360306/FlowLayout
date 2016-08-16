1、创建一个简单的ViewGroup

      a、创建一个Android项目，然后创建一个FlowLayout继承于ViewGroup：

		public class FlowLayout extends ViewGroup {

		/** 因为只在代码中直接new，所以创建这个构造方法就可以了 */
			public FlowLayout(Context context) {
				super(context);
			}
	
		/**
		* 测量FlowLayout及它的子View
	 	* @param widthMeasureSpec 父容器希望FlowLayout的宽
	 	* @param heightMeasureSpec 父容器希望FlowLayout的高
	 	*/
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// 调用父类的测量方法进行测量
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	
		// 对FlowLayout的子View进行排版
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		}

		}

 
      b、 在MainActivity的onCreate方法中：
			
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		
			// 把一个ScrollView设置为界面
		ScrollView scrollView = new ScrollView(this);
		setContentView(scrollView);	

		// 把FlowLayout添加到ScrollView
		FlowLayout flowLayout = new FlowLayout(this);
		scrollView.addView(flowLayout);		
		
		// 创建一个TextView
		TextView textView = new TextView(this);
		textView.setTextSize(30);
		textView.setBackgroundColor(Color.RED);
		textView.setText("心想事成");
		
		// 把TextView添加到FlowLayout	
		flowLayout.addView(textView);
		
		}
            
      c、运行项目，发现什么也看不见， 这是因为我们在onMeasure方法和onLayout方法什么事情都没有做，如         	 添加进来的TextView应该摆在哪里我们并没有指定，这是在onLayout方法中要做的事情，完成如下：

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			// 获取子View
			View child = getChildAt(0);	

			// 获取子View的宽和高
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			System.out.println("childWidth = " + childWidth + ", childHeight = " + childHeight);

			// 设置子View在容器中的位置
			int childLeft = 0;
			int childTop = 0;
			int childRight = childLeft + childWidth;
			int childBottom = childTop + childHeight;
			child.layout(childLeft, childTop, childRight, childBottom);
		}

            
      d、运行项目，还是什么也看不见，查看Log：
			
			Tag             		Text

		System.out      childWidth = 0,childHeight = 0
		System.out      childWidth = 0,childHeight = 0
            
       从上面Log可知onLayout方法被调用了两次。获取子View的测量的宽高都为0，这是因为我们从来没有测量过子View，所以获取不到测量值。在onMeasure中完成测量，如下：
			
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				// 遍历所有的子View，测量所有的子View
				for (int i = 0; i < getChildCount(); i++) {
					View child = getChildAt(i);	// 获取子View
				// 把测量规格传给子View，让子View完成测量
					child.measure(0, 0);
				}
		
			// 调用父类的测量方法进行测量
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}

             
        再次运行项目，还是什么也看不见，再查看Log：
			
			Tag             		Text

		System.out      childWidth = 120，childHeight = 41
		System.out      childWidth = 120，childHeight = 41
              
        从Log可知，我们对子View的测量起作用了，已经拿到了测量的宽和高了，那测量的时（child.measure(0, 0)）传的两个0是什么意思呢？把MeasureSpecUtil.java复制过来，在onMeasure方法中调用：MeasureSpecUtil.printMeasureSpec(0, 0);然后查看Log的输出：

			Tag             		Text

		System.out		宽模式：UNSPECIFIED，宽：0
		System.out		高模式：UNSPECIFIED，高：0
		System.out      childWidth = 120，childHeight = 41
		System.out		宽模式：UNSPECIFIED，宽：0
		System.out		高模式：UNSPECIFIED，高：0
		System.out      childWidth = 120，childHeight = 41
		
        
       从上面的Log可知，除了onLayout方法会扏行两次以外，onMeasure方法也会扏行两次，我们看到调用child.measure(0, 0)传的两个0，其实它表示的是一个未指定模式并且大小为0的测量规格，相当于调用MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)创建的测量规格，由与父容器（FlowLayout）没有限定子View的宽高，所以子View测量的时候可以给自己指定任意的宽和高。子View测量之后就可以拿到测量之后的宽和高了。这里我们顺便打印一下容器（FlowLayout）的测量规格：Utils.printMeasureSpec(widthMeasureSpec, heightMeasureSpec);并把之前打印的Log语句注释掉，运行，查看Log如下：

		Tag             		Text

		System.out		宽模式：EXACTELY，宽：320
		System.out		高模式：UNSPECIFIED，高：0
		
                
      从log可知，宽的模式是精确的，值是320，而高的模式是未指定，值是0，了解了这个之后，我们再看测量方法中的super.onMeasure方法做了什么事情：

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

			setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

   			getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));

		}

                
     上面的方法是View类中的onMeasure方法，从代码我们可以知道它没有做任何测量的事情，只是调用了setMeasuredDimension(measuredWidth, measuredHeight)方法来保存FlowLayout测量出来的宽和高，我们再看getDefaultSize方法的源码：

		public static int getDefaultSize(int size, int measureSpec) {
			int result = size;
			int specMode = MeasureSpec.getMode(measureSpec);
			int specSize = MeasureSpec.getSize(measureSpec);

			switch (specMode) {
				case MeasureSpec.UNSPECIFIED:
				result = size;
				break;
				case MeasureSpec.AT_MOST:
				case MeasureSpec.EXACTLY:
				result = specSize;
				break;
			}
			return result;
		}

        
    从源码我们可知，如果模式为UNSPECIFIED，则大小使用方法传进来的变量size，如果是其它两个模式，则大小就是测量规格中的大小。之前我们看到FlowLayout的高度的模式为UNSPECIFIED，所以FlowLayout的高会使用变量size的值，我们再看变量size，它是由getSuggestedMinimumHeight()方法获取到的：

		protected int getSuggestedMinimumHeight() {

			return (mBackground == null) ? mMinHeight : max(mMinHeight, mBackground.getMinimumHeight());

		}

				
        
   	从源代码可知，这个方法是获取容器（FlowLayout）的背景最小高或者设置的最小高。那我们就可以给FlowLayout设置一个最小高试试看：flowLayout.setMinimumHeight(100);再次运行项目，就可看到
	应有的效果了。
        
    从上面我们了解到super.onMeasure方法调用的是View的onMeasure方法，它只是调用了setMeasuredDimension(measuredWidth, measuredHeight)方法来保存FlowLayout测量出来的宽和高而已，那我们就不用它的默认设置了，我们自己设置就可以了，把flowLayout.setMinimumHeight(100);这句代码删除，修改FlowLayout的onMeasure方法：

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// 获取FlowLayout测量的宽
		int containerMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			
		// 遍历所有的子View，测量所有的子View
		for (int i = 0; i < getChildCount(); i++) {
		View child = getChildAt(i);	// 获取子View
		// 把测量规格传给子View，让子View完成测量
		child.measure(0, 0);
		}
			
		// 获取子View的测量高，用于容器的测量高
		int containerMeasuredHeight = getChildAt(0).getMeasuredHeight();
			
		// 设置FlowLayout的宽和高，宽就用父容器传的宽，高用子View的高
		setMeasuredDimension(containerMeasuredWidth, containerMeasuredHeight);
	}

               
    再次运行项目，也能正常显示界面了。至此，最重要的内容已经学完了，把上面的内容学好是基础，后面的就只是对上面知识的进一步应用而已了。
    
2、创建多个TextView添加到FlowLayout

	public static ArrayList<String> list = new ArrayList<String>();

	static {
	list.add("你好");
	list.add("好帅");
	list.add("人挺不错");
	list.add("善良体贴");
	list.add("有本启奏无本退朝");
	list.add("so good");
	list.add("没有什么可以阻挡");
	list.add("我对自由的向往");
	list.add("好吧");
	list.add("呵呵");
	list.add("对不起");
	list.add("给我个机会");
	list.add("要我做什么都行");
	list.add("你好");
	list.add("你好");
	list.add("三年之后又三年");
	list.add("你好");
	list.add("挺利索的");
	list.add("迟早要还的");
	list.add("出来跑");
	list.add("怎么给你机会");
	list.add("你好");
	list.add("你好");
	list.add("和警察去说");
	list.add("有什么");
	list.add("你好");
	list.add("怎么给你机会");
	list.add("你好");
	list.add("对不起我是警察");
	list.add("你好");
	list.add("那就是要我死");
	list.add("谁知道");
	list.add("你好");
	list.add("都是小事");
	list.add("我想做个好人");
	list.add("出来混嘛");
	list.add("从来只有事情改变人");
	list.add("多做善事");
	list.add("你好");
	list.add("我也读过警校");
	list.add("老大啊");
	list.add("抓贼啊");
	list.add("我要个向窗的位置");
	list.add("都快十年了");
	}

       
    上面的代码复制到MainActivity中，通过For循环创建上面文字对应的TextView并添加到FlowLayout中：

	for (String text : list) {
		TextView textView = UiUtils.createRandomSelectorTextView(this);
		textView.setText(text);
		flowLayout.addView(textView);
	}
  
   	关于UiUtils.createRandomSelectorTextView(this)方法大家可以自由发挥想像力了，只要能返回一个TextView即可，TextView属性可以随意设置。

3、重新测量

      FlowLayout的宽我们是已经知道了的，而高则不知道，FlowLayout的高是所有子View行的高的总和，那如何知道有几行呢？
      创建两个集合，一个集合用于保存一行，另一个集合用于保存多行，一行就是：ArrayList<View> oneLine，我们需要的是多行，在FlowLayout中声明一个保存多行的成员变量：
		
		/** 多行View */
		private ArrayList<ArrayList<View>> allLines = new ArrayList<ArrayList<View>>();

      在测量的时候，把View一行一行地封装到allLines中集合中，如下：

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// 因为onMeasure方法会执行多次，所以每次测量前先把之前的数据清空
			allLines.clear();	
			// 获取FlowLayout测量的宽
			int containerMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			
			ArrayList<View> oneLine = null;	// 一行View
			
			// 遍历所有的子View，测量所有的子View 
			for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);	// 获取子View
			// 把测量规格传给子View，让子View完成测量
			child.measure(0, 0);
				
			// 如果是第0个需要创建新行，或者当前View的宽度大于一行中剩余的可用宽度也需要创建新行
			if (i == 0 || child.getMeasuredWidth() > getUsableWidth(containerMeasuredWidth, oneLine)) {
				oneLine = new ArrayList<View>();
				allLines.add(oneLine);
			}
			oneLine.add(child);
		}
			
		// 设置FlowLayout的宽和高，宽就用父容器传的宽，高用所有行的高
		setMeasuredDimension(containerMeasuredWidth, getAllLinesHeight());
	}

		/** 获取可用宽度 */
		private int getUsableWidth(int containerMeasuredWidth, ArrayList<View> oneLine) {
			return containerMeasuredWidth - getOneLineWidth(oneLine);
		}
	
		/** 获取一行的宽 */
		private int getOneLineWidth(ArrayList<View> oneLine) {
			int oneLineWidth = 0;
			for (View view : oneLine) {
				oneLineWidth = oneLineWidth + view.getMeasuredWidth();
			}
			return oneLineWidth;
		}
		
		/** 获取所有行的高 */
		private int getAllLinesHeight() {
			if (allLines.isEmpty()) {
					return 0;
			} else {
				return getChildAt(0).getMeasuredHeight() * allLines.size();
			}
		}

       
4、重新排版

    测量的工作已经完成，接下来就是如何对这些View进行排版：

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 遍历所有的行
		int tempBottom = 0;	// 用于临时保存当前行底部的坐标
		for (int i = 0; i < allLine.size(); i++) {
			ArrayList<View> oneLine = allLine.get(i);	// 获取一行
		// 遍历一行中所有的列
			int tempRight = 0; // 用于临时保存当前子View的Right坐标 
			for (int j = 0; j < oneLine.size(); j++) {
				View child = oneLine.get(j);	// 获取一行中的子View
						
				// 获取子View的测量宽和测量高
				int childMeasuredWidth = child.getMeasuredWidth();
				int childMeasuredHeight = child.getMeasuredHeight();
						
				// 子View的left坐标为上一个子View的right坐标
				int childLeft = tempRight;
					
				// 子View的top坐标为上一个子View的bottom坐标
				int childTop = tempBottom;
					
				int childRight 	= childLeft + childMeasuredWidth;
				int childBottom = childTop + childMeasuredHeight;
					
				// 设置子View的位置，位置设置后子View的真实大小就确定了
				child.layout(childLeft, childTop, childRight, childBottom);
						
				// 保存当前列的right坐标，用于下次使用
				tempRight = childRight;
		}
					
		// 保存当行行的bottom坐标，用于下次使用
		tempBottom = oneLine.get(0).getBottom();
		System.out.println("tempBottom = " + tempBottom);
	}

	运行查看效果。
 
5、给FlowLayout加上padding

	flowLayout.setPadding(6, 6, 6, 6);
 
	再次运行查看效果。
 
	发现TextView显示不全，是因为TextView画到了Padding的地方去，而这些地方是不支持显示的。我们不能让TextView画在padding之内。
	在计算高度的时候，除了所有行的高，还有两个padding的高也得加上，修改onMeasure方法，如下：

	// 设置FlowLayout的宽和高，宽就用父容器期望的宽，高为所有行的高加上两个padding的高
	int containerMeasuredHeight = getAllLinesHeight() + getPaddingTop() + getPaddingBottom();
	setMeasuredDimension(containerMeasuredWidth, containerMeasuredHeight);

	在计算剩余可用宽度的时候需要把两个padding减掉，修改getUsableWidth，如下：
 
	/** 获取可用宽度 */
	private int getUsableWidth(int containerMeasuredWidth, ArrayList<View> oneLine) {
		return containerMeasuredWidth - (getPaddingLeft() + getPaddingRight()) - getOneLineWidth(oneLine);
	}


	排版的时候也需要做对应的修改：

	int childLeft = (j== 0) ? getPaddingLeft() : tempRight ;
	int childTop = (i == 0) ? getPaddingTop() : tempBottom ;
 
	运行查看效果。
 
6、给子View加上spacing

   	给子View的行和列之间都加上间距：

	private int horizotalSpacing = 6;
	private int verticalSpacing = 6;

	水平间距：除了第一列，其它列都需要加上一个水平间距，水平间距个数为一行View的数量 - 1

	垂直间距：除了第一行，其它行都需要加上垂直间距，垂直间距个数为行数 - 1

	所以在计算一行的宽的时候需要加上水平间距，在计算所有行的高度的时候则需要加上垂直间距，修改如下两个方法：
  	
	/** 获取一行的宽 */
	private int getOneLineWidth(ArrayList<View> oneLine) {
		int oneLineWidth = 0;
		for (View view : oneLine) {
			oneLineWidth = oneLineWidth + view.getMeasuredWidth();
		}
		int allSpacing = horizotalSpacing * (oneLine.size() - 1);
		return oneLineWidth + allSpacing;
	}
		
	/** 获取所有行的高 */
	private int getAllLinesHeight() {
		if (allLines.isEmpty()) {
			return 0;
		} else {
			int allSpacing = verticalSpacing * (allLines.size() - 1);
			return getChildAt(0).getMeasuredHeight() * allLines.size() + allSpacing;
		}
	}


	在排版的时候，如果不是第1列则left位置就需要加上一个水平间距，如果不是第一行，则top位置需要加上一个垂直间距，修改onLayout方法中的如下地方:

	int childLeft = (j== 0) ? getPaddingLeft() : tempRight + horizotalSpacing;
	int childTop = (i == 0) ? getPaddingTop() : tempBottom + verticalSpacing;
 
7、平分剩余的空间

    把一行中剩余的空间平均分给一行中的所有view即可。

	a、计算每个View可分到的宽度，并设置给子View，在onLayout方法中做如下修改：

	// 每个View平均可以多获得的宽度
	int averageUsableWidth = getUsableWidth(getMeasuredWidth(), oneLine) / oneLine.size();
	
	int childRight = childLeft + childMeasuredWidth + averageUsableWidth;
 
	运行查看效果。
 
	b、从上面的效果中，我们发现文字不居中了，这是因为子View在onLayout中所获得的宽度与在onMeasure中测量时的宽度就不一样了，居中属性还是按照在onMeasure中测量的宽来设置的，现在宽变了，则需要重新测量，以重新让居中属性重新按照新的宽来确定居中的位置在哪，修改onLayout方法如下：

	// 设置子View的位置，位置设置后子View的真实大小就确定了
	child.layout(childLeft, childTop, childRight, childBottom);
				
	// 由于子View的宽被改变了，需要重新测量，以便让TextView的居中属性刷新位置
	int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childRight - childLeft, MeasureSpec.EXACTLY);
	int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childBottom - childTop, MeasureSpec.EXACTLY);
	child.measure(widthMeasureSpec, heightMeasureSpec);
					
	// 保存当前列的right坐标，用于下次使用
	tempRight = childRight;

    运行查看效果。
             
8、让右边对齐

    查看上面的效果，会发现右边对得不是很整齐，这是因为在让每个子View平分剩余空间的时候计算平均值时出现的误差，解决办法就是让最后一个子View的right坐标与容器的right坐标一样即可，当然还需要减去paddingRight，如下：

	int childRight = (j == oneLine.size() - 1)
					? getMeasuredWidth() - getPaddingRight()
					: childLeft + childMeasuredWidth + averageUsableWidth;

 
