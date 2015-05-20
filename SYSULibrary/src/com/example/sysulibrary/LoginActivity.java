package com.example.sysulibrary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

class historyBook{
	static int bookCounter = 0;
	String bookName = null;
	String author = null;
	String year = null;
	String shouldDate = null;
	String actualDate = null;
	String place = null;
	String ISBN = null;
	Bitmap bookImg;
}

public class LoginActivity extends Activity{
	private int pbMax, pbProgress;
	private ProgressBar pb1, pb2;
	private Dialog dialog;
	private String forTextView;
	private TextView textView, pbTextView;
	private ListView listView;
	private historyBook [] historyBookArray = new historyBook[100];
	private String res;
	private EditText usr, psw;
	private Button loginBtn;
	private ImageButton imgBtn;
	private boolean clicked, arrowOrLine;
	private DrawerLayout mDrawerLayout;
	private LinearLayout drawerLinear;
	private ListView drawerListView;
	private List<Map<String,Object>> mDataList;
	private DefaultHttpClient httpClient = new DefaultHttpClient();
	
	//获取图片
	public InputStream HttpGetBookImg(String isbn){
		InputStream imgStream = null;
		try{
			//isbn带空格会导致崩溃，将空格换为%20
			System.out.println(isbn);
			isbn = isbn.replaceAll(" ", "%20");
			isbn = isbn.replace(""+(char)160,"%20");
			HttpGet httpGet = new HttpGet("http://202.112.150.126/index.php?client=libcode&isbn=" + isbn + "/cover");
			
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
			//HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();  
            imgStream = entity.getContent();  
		}catch (Exception e){
			e.printStackTrace();
		}
		return imgStream;
	}
	//获取借阅信息后更新视图
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			//dialog更新
			if (msg.what == 0x0003){
				pb1.setProgress(pbProgress);
				pbTextView.setText((pbProgress/10) + "/" + (pbMax/10));
			}
			//dialog初始化
			else if (msg.what == 0x0002){
				System.out.println("pbMAX:" + pbMax);
				pb1.setMax(pbMax);
				pb1.setVisibility(View.VISIBLE);
				pb2.setVisibility(View.GONE);
			}
			//无法获取借阅信息
			else if (msg.what == 0x0001){
				dialog.dismiss();
				Toast.makeText(LoginActivity.this, "无法查询到借阅信息", Toast.LENGTH_LONG).show();
			}
			else if (msg.what == 0x0000){
				mDataList=new ArrayList<Map<String,Object>>();
		    	Map<String,Object> map;
		    	for (int i=0;i<historyBook.bookCounter;i++)
		    	{
			    	map=new HashMap<String,Object>();
			    	map.put("bookImage", historyBookArray[i].bookImg);
			    	map.put("bookName", historyBookArray[i].bookName);
			    	map.put("bookAuthor","作者：" + (historyBookArray[i].author==""?"暂无":historyBookArray[i].author));
			    	map.put("bookNum", "年份：" + (historyBookArray[i].year==""?"暂无":historyBookArray[i].year));
			    	map.put("bookIsbn", "应还日期：" + (historyBookArray[i].shouldDate==""?"暂无":historyBookArray[i].shouldDate));
			    	map.put("bookPublisher", "归还日期：" + (historyBookArray[i].actualDate==""?"暂无":historyBookArray[i].actualDate));
			    	map.put("bookYear", "分馆：" + (historyBookArray[i].place==""?"暂无":historyBookArray[i].place));
			    	mDataList.add(map);
			    	//System.out.println(map);
		    	}
		    	//System.out.println("OK2");
		        SimpleAdapter listItemAdapter = new SimpleAdapter(LoginActivity.this,
		        		mDataList,
		        		R.layout.readlistitem,
		        		new String[]{"bookImage", "bookName", "bookAuthor", "bookNum", "bookIsbn", "bookPublisher",
		        		"bookYear"},
		        		new int[]{R.id.page5_listBookImage, R.id.page5_listBookName, R.id.page5_listAuthor,
		        		R.id.page5_listYear, R.id.page5_listShould, R.id.page5_listActual,
		        		R.id.page5_Place}
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
		        listView.setAdapter(listItemAdapter);
		        textView.setText(forTextView);
		        listView.setVisibility(View.VISIBLE);
		        textView.setVisibility(View.VISIBLE);
		        usr.setVisibility(View.GONE);
		        psw.setVisibility(View.GONE);
		        loginBtn.setVisibility(View.GONE);
		        dialog.dismiss();
			}
		}
	};
	//获取isbn
	public String getISBN(String url){
		String result = null;
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resultEntity = response.getEntity();
				result = EntityUtils.toString(resultEntity, "UTF-8");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(result);
		String isbn = doc.select("td.td1").get(3).text();
		//System.out.println(isbn);
		char [] charArray = isbn.toCharArray();
		int pos = 0;
		for (int i = 0; i <isbn.length(); i++){
			if (charArray[i] == ':'){
				pos = i;
				break;
			}
		}
		String ISBN;
		if (pos != 0)
			ISBN= new String(charArray, 0, pos - 1);
		else
			ISBN = isbn;
		System.out.println(ISBN);
		return ISBN;
	}
	//获取随机码
	public String getSearchURL(){
		String result = null;
		HttpGet httpGet = new HttpGet("http://202.116.64.108:8991/F/");
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resultEntity = response.getEntity();
				result = EntityUtils.toString(resultEntity, "UTF-8");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(result);
		Element e = doc.getElementById("advlist");
		String url = e.getElementsByTag("a").first().attr("href").substring(0, 79);
		return url;
	}
	//用于GET借阅列表
	public String HttpGetFunc(String url, LinkedList<BasicNameValuePair> params) {
		String searchResult = null;
		String param = URLEncodedUtils.format(params, "UTF-8");
		HttpGet httpGet = new HttpGet(url + "?" + param);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
		//HttpClient httpClient = new DefaultHttpClient();
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
	
	//用于登陆的POST请求
	public String HttpPostFunc(String url, LinkedList<BasicNameValuePair> params) {
		String searchResult = null;
		//String param = URLEncodedUtils.format(params, "UTF-8");
		HttpPost httpPost = new HttpPost(url);
		//DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
			HttpResponse response = httpClient.execute(httpPost);
			//System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resultEntity = response.getEntity();
				searchResult = EntityUtils.toString(resultEntity, "UTF-8");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //System.out.println("JESSISON --- " + JSESSIONID);
		return searchResult;
	}
	//用于发送POST请求的线程
	private String searchURL;
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			searchURL = getSearchURL();
			
			LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("func", "login-session"));
			params.add(new BasicNameValuePair("login_source", "bor-info"));
			params.add(new BasicNameValuePair("bor_id", usr.getText().toString()));
			params.add(new BasicNameValuePair("bor_verification", psw.getText().toString()));
			params.add(new BasicNameValuePair("bor_library", "ZSU50"));
			res = HttpPostFunc(searchURL, params);
			//System.out.println(res);
			//解析html，获取url
			Document doc = Jsoup.parse(res);
			Element e = doc.getElementById("header");
			String url = e.getElementsByTag("a").first().attr("href").substring(0, 79);
			
			//获取借阅列表
			params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("func", "bor-history-loan"));
			params.add(new BasicNameValuePair("adm_library", "ZSU50"));
			res = HttpGetFunc(url, params);
			//System.out.println(res);
			//System.out.println(searchURL);
			//System.out.println(url);
			//获取借阅书本信息，每本书对应10个
			doc = Jsoup.parse(res);
			//获取名字

			if (doc.select("p.title").first() != null){
				//System.out.println(doc.select("p.title").first().text());
				forTextView = doc.select("p.title").first().text();
				Elements ee = doc.select("td.td1");
				ee = ee.select("td[valign=top]");
				//System.out.println(ee);
				pbMax = ee.size();
				handler.sendEmptyMessage(0x0002);
				int counter = 0;
				for (Element eE : ee){
					pbProgress = counter;
					handler.sendEmptyMessage(0x0003);
					int which = counter % 10;
					int index = counter / 10;
					if (counter % 10 == 0)
						historyBookArray[index] = new historyBook();
					//System.out.println(counter);
					if (which == 1)
					{
						//System.out.println("index:" + index);
						historyBookArray[index].ISBN = getISBN(eE.getElementsByTag("a").first().attr("href"));
						//System.out.println("AAA0");
						historyBookArray[index].bookImg = BitmapFactory.decodeStream(HttpGetBookImg(historyBookArray[index].ISBN));
						//System.out.println("AAA1");
						historyBookArray[index].author = eE.text();
						//System.out.println("AAA2");
					}
					else if (which == 2)
						historyBookArray[index].bookName = eE.text();
					else if (which == 3)
						historyBookArray[index].year = eE.text();
					else if (which == 4)
						historyBookArray[index].shouldDate = eE.text();
					else if (which == 5)
						historyBookArray[index].shouldDate = historyBookArray[index].shouldDate + " " + eE.text();
					else if (which == 6)
						historyBookArray[index].actualDate = eE.text();
					else if (which == 7)
						historyBookArray[index].actualDate = historyBookArray[index].actualDate + " " + eE.text();
					else if (which == 9)
						historyBookArray[index].place = eE.text();
					counter++;
					//System.out.println(historyBookArray[index].shouldDate);
					//System.out.println(historyBookArray[index].actualDate);
				}
				historyBook.bookCounter = counter / 10;
				//System.out.println("OK1");
				handler.sendEmptyMessage(0x0000);
			}
			else{
				handler.sendEmptyMessage(0x0001);
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_login);

		//和其他activity相同的布局控件
		mDrawerLayout = (DrawerLayout) findViewById(R.id.page5_drawerLayout);
		drawerListView = (ListView) findViewById(R.id.page5_drawerListView);
		drawerLinear = (LinearLayout) findViewById(R.id.page5_drawerLinear);
		imgBtn = (ImageButton) findViewById(R.id.page5_menu);
		
		//主要控件
		loginBtn = (Button) findViewById(R.id.page5_login_btn);
		usr = (EditText) findViewById(R.id.page5_editText1);
		psw = (EditText) findViewById(R.id.page5_editText2);
		listView = (ListView) findViewById(R.id.page5_listView);
		textView = (TextView) findViewById(R.id.page5_name);
		//初始化dialog
		dialog = new Dialog(LoginActivity.this);				
		dialog.setContentView(R.layout.dialog);	
		dialog.setTitle("请稍后...");
		pb1 = (ProgressBar) dialog.findViewById(R.id.page5_progressBar1);
		pb2 = (ProgressBar) dialog.findViewById(R.id.page5_progressBar2);
		pbTextView = (TextView) dialog.findViewById(R.id.page5_progressTextView);
		//登陆监听
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
				new Thread(runnable).start();
			}
		});
		
		//以下为布局及其他内容
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
		
        String [] mPlanetTitles = new String[]{"浏览记录", "我的收藏", "在线搜索", "借阅历史"};
        int [] picture = new int[]{R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5
        };
        Map<String,Object> map;
        mDataList = new ArrayList<Map<String,Object>>();
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
					Intent mainIntent = new Intent(LoginActivity.this,
							RecordActivity.class);
					LoginActivity.this.startActivity(mainIntent);
					LoginActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 1){
					Intent mainIntent = new Intent(LoginActivity.this,
							CollectionActivity.class);
					LoginActivity.this.startActivity(mainIntent);
					LoginActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 2){
					Intent mainIntent = new Intent(LoginActivity.this,
							LibraryActivity.class);
					LoginActivity.this.startActivity(mainIntent);
					LoginActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 3){
					Intent mainIntent = new Intent(LoginActivity.this,
							LoginActivity.class);
					LoginActivity.this.startActivity(mainIntent);
					LoginActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
			}
        	
        });
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
