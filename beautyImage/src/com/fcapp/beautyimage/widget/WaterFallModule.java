package com.fcapp.beautyimage.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.fcapp.beautyimage.ClickListener;
import com.fcapp.beautyimage.LazyScrollView;
import com.fcapp.beautyimage.LazyScrollView.OnScrollListener;
import com.fcapp.beautyimage.R;
import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.myinterface.WaterFallImage;
import com.fcapp.beautyimage.util.ImageUtil;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.util.WaterFallImageLoader;

public class WaterFallModule {
	
	private boolean isShowImgName = true;
	
	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	
	//存放column_count个LinearLayout 每个LinearLayout表示一列
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
	private Handler handler;
	private int item_width;

	private int column_count = Constant.COLUMN_COUNT;// 显示列数
	private int page_count = Constant.PICTURE_COUNT_PER_LOAD;// 每次加载30张图片

	private int current_page = 0;// 当前页数

	private int[] topIndex;
	
	//表示当前瀑布流最底部的三张图的索引 比如第一列有21张图 索引为20 第二列有19张 索引为18 第三列有22张 索引为21
	private int[] bottomIndex; 
	
	//每列当前图片的索引,相当于临时变量 比如第一列的第9张图 lineIndex[0]=9;  
	private int[] lineIndex; 
	
	// 每列总共的高度 当前列加入10张图时,为10张图的总高度
	private int[] column_height;

	//存放所有图片的ID和文件名
	private HashMap<String, String> pins;

	private int loaded_count = 0;// 已加载数量
	
	//存column_count列数组 存放每列每张图的总高度 
	//比如第一列第一张图高90  总高度为90、第一列第2张图高120 总高度为210... 也就是第几列的总高度
	private HashMap<Integer, Integer>[] pin_mark = null;

	private Activity context;

	private HashMap<String, RelativeLayout> iviews;//存放所有图片对象,以ID为Key
	int scroll_height;
	
	private Activity activity;
	private ArrayList<Home> list;	//拿到
	
	private ClickListener clickListener;
	Context context1;
	private int Swidth,Sheight;
	private HashMap<String ,RelativeLayout> reloadRL;
	public WaterFallModule(Activity activity,ArrayList<Home> list,ClickListener clickListener,Context context1,boolean isShowImgName){
		this.activity = activity;
		this.list = list;
		this.clickListener = clickListener;
		this.context1 = context1;
		Display display = activity.getWindowManager().getDefaultDisplay();
        Swidth = display.getWidth();
        Sheight = display.getHeight();
        this.isShowImgName = isShowImgName;
	}
	
	
	
	
	public  void initLayout() {

		reloadRL = new HashMap<String ,RelativeLayout>();
		display = activity.getWindowManager().getDefaultDisplay();
		item_width = display.getWidth() / column_count;// 根据屏幕大小计算每列大小

		column_height = new int[column_count];
		context = activity;
		iviews = new HashMap<String, RelativeLayout>();
		pins = new HashMap<String, String>();
		pin_mark = new HashMap[column_count];

		this.lineIndex = new int[column_count];
		this.bottomIndex = new int[column_count];
		this.topIndex = new int[column_count];

		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}

		waterfall_scroll = (LazyScrollView) activity.findViewById(R.id.waterfall_scroll);

		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Logger.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {
				
			}

			@Override
			public void onBottom() {
				// 滚动到最底端
				
				AddItemToContainer(current_page, page_count);
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
				// Logger.d("MainActivity",
				// String.format("%d  %d  %d  %d", l, t, oldl, oldt));

				// Logger.d("MainActivity", "range:" + range);
				// Logger.d("MainActivity", "range-t:" + (range - t));
				scroll_height = waterfall_scroll.getMeasuredHeight();

				if (t > oldt) {// 向下滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后

						for (int k = 0; k < column_count; k++) {

							if (pin_mark[k].get(Math.min(bottomIndex[k] + 1,
									lineIndex[k])) <= t + 3 * scroll_height) {// 最底部的图片位置小于当前t+3*屏幕高度
								RelativeLayout rl = (RelativeLayout) waterfall_items.get(k).getChildAt(Math.min(1 + bottomIndex[k],lineIndex[k]));
								final ImageView tag_image = (ImageView) rl.getTag();
								final Home home = (Home) tag_image.getTag();
								reloadRL.put(home.getId(), rl);
								setBitmap(home, tag_image);
								bottomIndex[k] = Math.min(1 + bottomIndex[k],lineIndex[k]);
							}
							
							if (pin_mark[k].get(topIndex[k]) < t - 2 * scroll_height) {// 未回收图片的最高位置<t-两倍屏幕高度

								int i1 = topIndex[k];
								topIndex[k]++;
								LinearLayout localLinearLayout = waterfall_items.get(k);
								RelativeLayout rl = (RelativeLayout) localLinearLayout.getChildAt(i1);
								ImageView tag_image = (ImageView) rl.getTag();
								ImageUtil.recycleImageViewBitMap(tag_image);
							}
						}
					}
				} else {// 向上滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后
						for (int k = 0; k < column_count; k++) {
							LinearLayout localLinearLayout = waterfall_items.get(k);
							if (pin_mark[k].get(bottomIndex[k]) > t + 3 * scroll_height) {
								RelativeLayout rll = (RelativeLayout) localLinearLayout.getChildAt(bottomIndex[k]);
								ImageView tag_image = (ImageView) rll.getTag();
								ImageUtil.recycleImageViewBitMap(tag_image);

								bottomIndex[k]--;
							}

							if (pin_mark[k].get(Math.max(topIndex[k] - 1, 0)) >= t - 2 * scroll_height) {
								RelativeLayout rlll = (RelativeLayout) localLinearLayout.getChildAt(Math.max(-1 + topIndex[k], 0));
								final ImageView tag_image = (ImageView) rlll.getTag();
								final Home home = (Home) tag_image.getTag();
								reloadRL.put(home.getId(), rlll);
								setBitmap(home, tag_image);
								topIndex[k] = Math.max(topIndex[k] - 1, 0);
							}
						}
					}
				}
			}
		});

		
		waterfall_container = (LinearLayout) activity.findViewById(R.id.waterfall_container);
		waterfall_items = new ArrayList<LinearLayout>();

		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(activity);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);

			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setLayoutParams(itemParam);
			
			//两个LinearLayout
			waterfall_items.add(itemLayout);
			//根LinearLayout
			waterfall_container.addView(itemLayout);
		}

		// 第一次加载
		AddItemToContainer(current_page, page_count);
	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		/*
		 *这里需要做的是：
		 *	1、拿到数据 list里面的一部分 并做记录
		 *2、给出图片地址，并且设置图片的标题 
		 */
		
		ArrayList<Home> pageList = getDataFromList(list,current_page,pagecount);
		if(pageList == null){
			return;
		}
		for(int i = 0;i<pageList.size();i++){
			Home home = pageList.get(i); 
			loaded_count++;
			home.getCover_img().setWidth(Swidth/2);
			AddImage(home,(int)Math.ceil(loaded_count / (double) column_count));
		}
		current_page = current_page + 1;
	}
	
	/**
	 * 
	 * @param filename   图片名字 
	 * @param rowIndex	 图片属于第几行
	 */
	private void AddImage(Home home, int rowIndex) {
		addItme(home);
	}
	
	
	boolean hasData = true;
	/**
	 * list：数据集合
	 * pageNum : 第几页
	 * pageCount : 每一页的数量
	 * @param list
	 */
	private ArrayList<Home> getDataFromList(List list,int current_page,int pageCount) {
	/*
	 * 需要完成的任务
	 * 1 从list中取出数据并返回
	 */
		if(!hasData){
				Toast.makeText(context, "已经没有更多的数据了", 0).show();
			return null;
		}
		int start = current_page * pageCount;
		int end = start + pageCount-1;
		ArrayList<Home> pageList = new ArrayList<Home>();
		if(end>=list.size()){
			end = list.size()-1;
			hasData = false;
		}
		Logger.d("start end ", "start = "+start+"  end = "+end);
		for(int i = start;i<=end;i++){
			pageList.add((Home)list.get(i));
		}
		return pageList;
		
	}

	private int GetMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {
			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}
	

	public void addItme(final Home home) {
		int columnIndex = GetMinValue(column_height);
		column_height[columnIndex] += home.getCover_img().getHeight();
		RelativeLayout itemRl = (RelativeLayout) View.inflate(activity, R.layout.watallfall_item, null);
		final ImageView image = (ImageView) itemRl.findViewById(R.id.flowview);
		TextView tv = (TextView) itemRl.findViewById(R.id.tv_waterfall_title);
		if(!TextUtils.isEmpty(home.getName())){
			if(isShowImgName){
				tv.setVisibility(View.VISIBLE);
				tv.setText(home.getName());
			}else{
				tv.setVisibility(View.INVISIBLE);
			}
		}
		if(!TextUtils.isEmpty(home.getTitle())){
			if(isShowImgName){
				tv.setVisibility(View.VISIBLE);
				tv.setText(home.getTitle());
			}else{
				tv.setVisibility(View.INVISIBLE);
			}
		}
		
		image.setTag(home);
		
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickListener.onShortClickImage(v);
			}
		});
		image.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				clickListener.onLongClickImage(v);
				return true;
			}
		});
		itemRl.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,home.getCover_img().getHeight()));
		itemRl.setTag(image);
		
		pins.put(home.getId(), home.getName());
		iviews.put(home.getId(), itemRl);
		waterfall_items.get(columnIndex).addView(itemRl);
		lineIndex[columnIndex]++;
		pin_mark[columnIndex].put(lineIndex[columnIndex],
				column_height[columnIndex]);
		bottomIndex[columnIndex] = lineIndex[columnIndex];
		setBitmap(home, image);
	}


	public void setBitmap(final Home home, final ImageView image) {
		WaterFallImageLoader task = new WaterFallImageLoader(new ImageTask(image), home);
		task.execute();
	}
	
	class ImageTask implements WaterFallImage{
		
		public ImageView image;

		public ImageTask(ImageView image){
			this.image = image;
		}
		
		@Override
		public void beforLoadImage() {
			image.setBackgroundColor(Color.TRANSPARENT);
		}

		@Override
		public void afterLoadImage(Bitmap bitmap) {
			image.setImageBitmap(bitmap);
		}
		
	}
	
	
	public void onClickImage(ClickListener click,View v){
		click.onShortClickImage(v);
	}
	
	public void onLongClickImage(ClickListener click,View v){
		click.onLongClickImage(v);
	}
	
	
	private MyCustomView mView;
    private Movie mMovie;
    private long mMovieStart;
	 // 自定义一个类，继承View
    class MyCustomView extends View {
        public MyCustomView(Context context) {
            super(context);
            // 以文件流的方式读取文件
            mMovie = Movie.decodeStream(getResources().openRawResource(
                    R.drawable.dd));
        }
 
        @Override
        protected void onDraw(Canvas canvas) {
            long curTime = android.os.SystemClock.uptimeMillis();
            // 第一次播放
            if (mMovieStart == 0) {
                mMovieStart = curTime;
            }
 
            if (mMovie != null) {
                int duration = mMovie.duration();
 
                int relTime = (int) ((curTime - mMovieStart) % duration);
                mMovie.setTime(relTime);
                mMovie.draw(canvas, 0, 0);
 
                // 强制重绘
                invalidate();
            }
            super.onDraw(canvas);
        }
    }
}
