package com.example.sysulibrary;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

class Book{
	static int bookCounter = 0;
	String name, isbn, author, number, publisher, year, collection;
	Bitmap bookImg;
	Book(){}
};

class SearchInform{
	static String pageNow;
	static String total;
	static String url;
	static int intTotalPage;
	static int intPageNow;
	static int NextOne;
};

public class SearchResultActivity extends Activity{
	//private Dialog dialog;
	//收藏数据库
	private MyDatabaseHelper dbHelper;
	private final String TABLE_NAME2 = "Books";
	private final String DB_NAME = "Database.db";
	
	private LinearLayout drawerLinear;
	private List<Map<String,Object>> mDataList=new ArrayList<Map<String,Object>>();
	private DrawerLayout mDrawerLayout;
	private SwipeRefreshLayout swipeLayout;
	private TextView scrollTextView, title, next, pageNum;
	private ImageButton imgBtn;
	//private ProgressDialog progressDialog;
	private ListView listView, drawerListView;
	private String res;//点击搜索后get到的html代码
	private String local_base;
	//private Bitmap bookImg;
	private Book bookArray[] = new Book[20];
	private boolean clicked, arrowOrLine;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (msg.what == 0x0000){
				//img.setImageBitmap(bookImg);	
				//设置ListView
				List<Map<String,Object>> mDataList=new ArrayList<Map<String,Object>>();
		    	Map<String,Object> map;
		    	for (int i=0;i<Book.bookCounter;i++)
		    	{
			    	map=new HashMap<String,Object>();
			    	map.put("bookImage", bookArray[i].bookImg);
			    	map.put("bookName", bookArray[i].name);
			    	map.put("bookAuthor","作者:" + (bookArray[i].author==""?"暂无":bookArray[i].author));
			    	map.put("bookNum", "索书号:" + (bookArray[i].number==""?"暂无":bookArray[i].number));
			    	map.put("bookIsbn", "ISBN:" + (bookArray[i].isbn==""?"暂无":bookArray[i].isbn));
			    	map.put("bookPublisher", "出版社:" + (bookArray[i].publisher==""?"暂无":bookArray[i].publisher));
			    	map.put("bookYear", "出版年份:" + (bookArray[i].year==""?"暂无":bookArray[i].year));
			    	map.put("bookCollection", bookArray[i].collection);
			    	mDataList.add(map);
		    	}
		        SimpleAdapter listItemAdapter = new SimpleAdapter(SearchResultActivity.this,
		        		mDataList,
		        		R.layout.listitem,
		        		new String[]{"bookImage", "bookName", "bookAuthor", "bookNum", "bookIsbn", "bookPublisher",
		        		"bookYear", "bookCollection"},
		        		new int[]{R.id.page2_listBookImage, R.id.page2_listBookName, R.id.page2_listAuthor,
		        		R.id.page2_listBookNum, R.id.page2_listBookIsbn, R.id.page2_listPublisher,
		        		R.id.page2_listYear, R.id.page2_listCollection}
		        );
		        //用于ListView显示Bitmap
		        listItemAdapter.setViewBinder(new ViewBinder() {  
		        	@Override  
		        	public boolean setViewValue(View view, Object data,  
		        	        String textRepresentation) {  
		        	    // TODO Auto-generated method stub  
		        	    if(view instanceof ImageView && data instanceof Bitmap){  
		        	        ImageView i = (ImageView)view;  
		        	        i.setImageBitmap((Bitmap) data);  
		        	        return true;  
		        	    }  
		        	    return false;  
		        	}  
		        });  
		        
		        //搜索完毕
		        listView.setVisibility(View.VISIBLE);
				final TranslateAnimation animation1 = new TranslateAnimation(150, 500, 0, 0);
				animation1.setDuration(1000);
				scrollTextView.setAnimation(animation1);
				scrollTextView.setVisibility(View.GONE);
				
				title.setVisibility(View.VISIBLE);
				final TranslateAnimation animation2 = new TranslateAnimation(-500, 0, 0, 0);
				animation2.setDuration(1000);
				title.setAnimation(animation2);
				
				imgBtn.setVisibility(View.VISIBLE);
				next.setVisibility(View.VISIBLE);
				pageNum.setVisibility(View.VISIBLE);
				final AlphaAnimation animation3 = new AlphaAnimation(0.0f, 1.0f);
				animation3.setStartOffset(1000);
				animation3.setDuration(1000);
				imgBtn.setAnimation(animation3);
				next.setAnimation(animation3);
				pageNum.setAnimation(animation3);
				listView.setAnimation(animation3);
				
		        listView.setAdapter(listItemAdapter);	
		        pageNum.setText(SearchInform.intPageNow + "/" + SearchInform.intTotalPage);
		        swipeLayout.setRefreshing(false);
		        //progressDialog.dismiss();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_searchresult);

		listView = (ListView)findViewById(R.id.page2_listView);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.page2_swipe_container);
		scrollTextView = (TextView) findViewById(R.id.page2_scrollTitle);
		next = (TextView) findViewById(R.id.page2_Next);
		pageNum = (TextView) findViewById(R.id.page2_pageNum);
		title = (TextView) findViewById(R.id.page2_title);
		imgBtn = (ImageButton) findViewById(R.id.page2_menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.page2_drawerLayout);
		drawerListView = (ListView) findViewById(R.id.page2_drawerListView);
		drawerLinear = (LinearLayout) findViewById(R.id.page2_drawerLinear);
		
		//日后完善页码跳转
		/*pageNum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//dialog = new Dialog(SearchResultActivity.this);
				//dialog.setContentView(layoutResID)
				SearchInform.NextOne = SearchInform.intPageNow * 20 + 1;
				final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
				fadeIn.setDuration(1000);
				final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
				fadeOut.setDuration(1000);
				swipeLayout.setRefreshing(true);
				
				scrollTextView.setVisibility(View.VISIBLE);
				listView.startAnimation(fadeOut);
				title.startAnimation(fadeOut);
				imgBtn.startAnimation(fadeOut);
				next.startAnimation(fadeOut);
				pageNum.startAnimation(fadeOut);
				scrollTextView.startAnimation(fadeIn);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						listView.setVisibility(View.INVISIBLE);
						title.setVisibility(View.GONE);
						imgBtn.setVisibility(View.GONE);
						next.setVisibility(View.GONE);
						pageNum.setVisibility(View.GONE);
					}
				}, 900);

				new Thread(runnableForJump).start();
			}
		});*/
		//收藏书本
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final int pos = position;
				new AlertDialog.Builder(SearchResultActivity.this)
				.setTitle("确认")
				.setMessage("是否收藏该书本？")
				.setPositiveButton("否", null)
				.setNegativeButton("是", new DialogInterface.OnClickListener()
	               {
	                 public void onClick(DialogInterface dialog, int which)
	                 {                                      
	                	 ContentValues values = new ContentValues();  
	                	 final ByteArrayOutputStream os = new ByteArrayOutputStream();   
	                	 bookArray[pos].bookImg.compress(Bitmap.CompressFormat.PNG, 100, os);    
	                	 values.put("bookimg", os.toByteArray());  
	                	 values.put("bookname",bookArray[pos].name);  
	                	 values.put("author",bookArray[pos].author);  
	                	 values.put("number",bookArray[pos].number);  
	                	 values.put("isbn",bookArray[pos].isbn);  
	                	 values.put("publisher",bookArray[pos].publisher);  
	                	 values.put("year",bookArray[pos].year);  
	                	 dbHelper = new MyDatabaseHelper(SearchResultActivity.this, DB_NAME, 1);
	                	 dbHelper.getReadableDatabase().insert(TABLE_NAME2, "bookimg", values);
		     			 /*String [] str = new String[]{ bookArray[pos].name, bookArray[pos].author, bookArray[pos].number,
		     					bookArray[pos].isbn, bookArray[pos].publisher, bookArray[pos].year};
		    			 SQLiteDatabase db = dbHelper.getReadableDatabase();
		    			 db.execSQL("insert into " + TABLE_NAME2 + " values(null, ?, ?, ?, ?, ?, ?)", str);*/
	                 }
	               })
				.show();
			}
        	
        });
		//配置侧拉栏
        String [] mPlanetTitles = new String[]{"浏览记录", "我的收藏", "在线搜索", "借阅历史"};
        int [] picture = new int[]{R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5
        };
        Map<String,Object> map;
    	for (int i=0;i<4;i++)
    	{
	    	map=new HashMap<String,Object>();
	    	map.put("name", mPlanetTitles[i]);
	    	map.put("image", picture[i]);
	    	mDataList.add(map);
    	}
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,
        		mDataList,
        		R.layout.drawerlistitem,
        		new String[]{"image","name"},
        		new int[]{R.id.drawerListImage,R.id.drawerListText}
        		);
        drawerListView.setAdapter(listItemAdapter); 
        drawerListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0){
					Intent mainIntent = new Intent(SearchResultActivity.this,
							RecordActivity.class);
					SearchResultActivity.this.startActivity(mainIntent);
					SearchResultActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 1){
					Intent mainIntent = new Intent(SearchResultActivity.this,
							CollectionActivity.class);
					SearchResultActivity.this.startActivity(mainIntent);
					SearchResultActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 2){
					Intent mainIntent = new Intent(SearchResultActivity.this,
							LibraryActivity.class);
					SearchResultActivity.this.startActivity(mainIntent);
					SearchResultActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 3){
					Intent mainIntent = new Intent(SearchResultActivity.this,
							LoginActivity.class);
					SearchResultActivity.this.startActivity(mainIntent);
					SearchResultActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
			}
        	
        });
		//下一页按钮
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchInform.NextOne = SearchInform.intPageNow * 20 + 1;
				final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
				fadeIn.setDuration(1000);
				final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
				fadeOut.setDuration(1000);
				swipeLayout.setRefreshing(true);
				
				scrollTextView.setVisibility(View.VISIBLE);
				listView.startAnimation(fadeOut);
				title.startAnimation(fadeOut);
				imgBtn.startAnimation(fadeOut);
				next.startAnimation(fadeOut);
				pageNum.startAnimation(fadeOut);
				scrollTextView.startAnimation(fadeIn);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						listView.setVisibility(View.INVISIBLE);
						title.setVisibility(View.GONE);
						imgBtn.setVisibility(View.GONE);
						next.setVisibility(View.GONE);
						pageNum.setVisibility(View.GONE);
					}
				}, 900);

				new Thread(runnableForJump).start();
			}
		});
		mDrawerLayout.setDrawerListener(new DrawerListener(){
			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				if (!clicked){
					arrowOrLine = !arrowOrLine;
					ArrowToLine();
				}
				clicked = false;
			}

			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				if (!clicked){
					arrowOrLine = !arrowOrLine;
					LineToArrow();
				}
				clicked = false;
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		arrowOrLine = false;
		clicked = false;
		//设置imgBtn监听,菜单栏动画
		imgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!arrowOrLine)
				{
					mDrawerLayout.openDrawer(drawerLinear);
					LineToArrow();
				}
				else{
					mDrawerLayout.closeDrawers();
					ArrowToLine();
				}
				clicked = true;
				arrowOrLine = !arrowOrLine;
			}
		});
		//设置swipeLayout颜色样式
		swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		//不设置该监听会崩溃
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						int temp = SearchInform.intPageNow * 20 - 39;
						SearchInform.NextOne =  temp > 1 ? temp : 1;
						final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
						fadeIn.setDuration(1000);
						final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
						fadeOut.setDuration(1000);
						swipeLayout.setRefreshing(true);
						
						scrollTextView.setVisibility(View.VISIBLE);
						listView.startAnimation(fadeOut);
						title.startAnimation(fadeOut);
						imgBtn.startAnimation(fadeOut);
						next.startAnimation(fadeOut);
						pageNum.startAnimation(fadeOut);
						scrollTextView.startAnimation(fadeIn);
						new Thread(runnableForJump).start();
					}
				}, 500);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						listView.setVisibility(View.INVISIBLE);
						title.setVisibility(View.GONE);
						imgBtn.setVisibility(View.GONE);
						next.setVisibility(View.GONE);
						pageNum.setVisibility(View.GONE);
					}
				}, 1500);
			}
		});
		//下面难以实现，抛弃
		//设置拖动监听.用于判断滑动到顶部
		/*isTop = false;
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					if (listView.getChildAt(0)!=null){
						System.out.println(listView.getChildAt(0).getTop());
						if (listView.getChildAt(0).getTop() == 0){
							if (isTop){
								System.out.println("DO");
								title.setVisibility(View.GONE);
								
								final TranslateAnimation animation = new TranslateAnimation(0, 0, -200, 0);
								animation.setDuration(100);
								scrollTextView.setAnimation(animation);
								animation.setFillAfter(true);
								scrollTextView.setVisibility(View.VISIBLE);
							}
							else{
								title.setVisibility(View.VISIBLE);
								
								final TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -200);
								animation.setDuration(100);
								scrollTextView.setAnimation(animation);
								animation.setFillAfter(true);
								scrollTextView.setVisibility(View.GONE);
							}
							isTop = !isTop;
						}
					}
				}
			}
		});*/
		//中文文献还是西文文献
		local_base = LibraryActivity.type? "ZSU09": "ZSU01";
		Book.bookCounter = 0;
		
		swipeLayout.setRefreshing(true);
		//progressDialog = ProgressDialog.show(SearchResultActivity.this, "正在奋力搜索...", "请稍后");
		new Thread(runnable).start();
	}
	
	private String searchURL = "http://202.116.64.108:8991/F/-";
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			String content = LibraryActivity.editText.getText().toString();
			LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("func", "find-b"));
			params.add(new BasicNameValuePair("find_code", "WRD"));
			params.add(new BasicNameValuePair("request", content));
			params.add(new BasicNameValuePair("local_base", local_base));
			res = HttpGetFunc(searchURL, params);
			//bookImg = BitmapFactory.decodeStream(HttpGetBookImg(""));
			getBookContent(res);
			getPageInform(res);
			handler.sendEmptyMessage(0x0000);
		}

	};
	private Runnable runnableForJump = new Runnable() {

		@Override
		public void run() {
			Book.bookCounter = 0;
			HttpGet httpGet = new HttpGet(SearchInform.url + SearchInform.NextOne);
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity resultEntity = response.getEntity();
					res = EntityUtils.toString(resultEntity, "UTF-8");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//bookImg = BitmapFactory.decodeStream(HttpGetBookImg(""));
			getBookContent(res);
			getPageInform(res);
			handler.sendEmptyMessage(0x0000);
		}
	};
	
	public void getPageInform(String html){
		if (Book.bookCounter != 0){
			Document doc = Jsoup.parse(res);
			Element divTag = doc.getElementById("hitnum");
			String script = divTag.select("script").first().data();
			SearchInform.total = script.substring(26,35).trim();
			SearchInform.url = script.substring(99,206);
			String record = divTag.text();
			SearchInform.pageNow = record.substring(record.indexOf("-") + 1, record.indexOf("o")).trim();
			SearchInform.intTotalPage = (Integer.parseInt(SearchInform.total) - 1) / 20 + 1;
			SearchInform.intPageNow = (Integer.parseInt(SearchInform.pageNow) - 1) / 20 + 1;
			
			//System.out.println("Script:" + divTag.text());
			//System.out.println("TOTAL:" + SearchInform.total);
			//System.out.println("URL:" + SearchInform.url);
			//System.out.println("pageNow:" + SearchInform.pageNow);
			//System.out.println(SearchInform.intPageNow + "/" + SearchInform.intTotalPage);
		}
	}
	
	public void getBookContent(String html){
		Document doc = Jsoup.parse(res);
		Elements member = doc.select("table.items");
		
		int loopCounter;
		//member为搜索到的20项书
		
		for (Element e : member){
			bookArray[Book.bookCounter] = new Book();
			
			Elements aTag = e.select("a");
			loopCounter = 0;
			for (Element eE : aTag){
				//获取书名
				if (loopCounter == 2){
					//System.out.println(eE.text());
					bookArray[Book.bookCounter].name = eE.text();
				}
				//获取书本馆藏信息
				else if (loopCounter == 3){
					//System.out.println(eE.text());
					bookArray[Book.bookCounter].collection = eE.text();
				}
				loopCounter++;
			}
			//获取ISBN
			Elements divTag = e.select("div.itemtitle");
			String scriptContent = divTag.select("script").first().data();
			bookArray[Book.bookCounter].isbn = scriptContent.substring(10, scriptContent.length()-2);
			//根据ISBN获取书本图片
			bookArray[Book.bookCounter].bookImg = BitmapFactory.decodeStream(HttpGetBookImg(bookArray[Book.bookCounter].isbn));
			//System.out.println("HH2");
			//获取书本其他信息，可能为空
			Iterator<Element> iter = e.select("td.content").iterator();
			bookArray[Book.bookCounter].author = iter.next().text();
			bookArray[Book.bookCounter].number = iter.next().text();
			bookArray[Book.bookCounter].publisher = iter.next().text();
			bookArray[Book.bookCounter].year = iter.next().text();
			/*System.out.println(bookArray[Book.bookCounter].author);
			System.out.println(bookArray[Book.bookCounter].number);
			System.out.println(bookArray[Book.bookCounter].publisher);
			System.out.println(bookArray[Book.bookCounter].year);*/
			
			Book.bookCounter++;
		}
		
		//textView.setText(bookname.text().replace(""+(char)160,""));
	}
	
	public String HttpGetFunc(String url, LinkedList<BasicNameValuePair> params) {
		String searchResult = null;
		String param = URLEncodedUtils.format(params, "UTF-8");
		HttpGet httpGet = new HttpGet(url + "?" + param);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resultEntity = response.getEntity();
				searchResult = EntityUtils.toString(resultEntity, "UTF-8");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResult;
	}
	
	public InputStream HttpGetBookImg(String isbn){
		InputStream imgStream = null;
		try{
			//isbn带空格会导致崩溃，将空格换为%20
			isbn = isbn.replaceAll(" ", "%20");
			isbn = isbn.replace(""+(char)160,"%20");
			HttpGet httpGet = new HttpGet("http://202.112.150.126/index.php?client=libcode&isbn=" + isbn + "/cover");
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpGet);
			//System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();  
            imgStream = entity.getContent();  
		}catch (Exception e){
			e.printStackTrace();
		}
		return imgStream;
	}
	public void LineToArrow(){
		final RotateAnimation animation1 =new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation1.setDuration(250);
		imgBtn.startAnimation(animation1);
		Handler h = new Handler();
		Runnable r = new Runnable(){
			@Override
			public void run(){
				imgBtn.setImageResource(R.drawable.arrow);
			}
		};
		h.postDelayed(r, 100);
	}
	public void ArrowToLine(){
		final RotateAnimation animation1 =new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation1.setDuration(250);
		imgBtn.startAnimation(animation1);
		Handler h = new Handler();
		Runnable r = new Runnable(){
			@Override
			public void run(){
				imgBtn.setImageResource(R.drawable.menu);
			}
		};
		h.postDelayed(r, 100);
	}
}
