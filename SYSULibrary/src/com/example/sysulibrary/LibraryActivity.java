package com.example.sysulibrary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

public class LibraryActivity extends Activity {
	//自动补全
	private Cursor cursor;
	private MyDatabaseHelper dbHelper;
	private final String TABLE_NAME1 = "Records";
	private final String TABLE_NAME2 = "Books";
	private final String DB_NAME = "Database.db";
	private ArrayAdapter<String> av;
	private ArrayList<String> userList = new ArrayList<String>();

	private List<Map<String,Object>> mDataList=new ArrayList<Map<String,Object>>();
	private LinearLayout drawerLinear;
	private ListView drawerListView;
	private DrawerLayout mDrawerLayout;
	private boolean arrowOrLine, clicked;
	private ImageButton imgBtn;
	private Button search_btn;
	private RadioButton boxChn, boxEng;
	public static AutoCompleteTextView editText;
	public static boolean type; //false代表中文,true代表西文

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_library);
		
		search_btn = (Button) findViewById(R.id.page1_search_btn);
		editText = (AutoCompleteTextView) findViewById(R.id.page1_editText);
		boxChn = (RadioButton) findViewById(R.id.page1_boxChn);
		boxEng = (RadioButton) findViewById(R.id.page1_boxEng);
		imgBtn = (ImageButton) findViewById(R.id.page1_menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.page1_drawerLayout);
		drawerListView = (ListView) findViewById(R.id.page1_drawerListView);
		drawerLinear = (LinearLayout) findViewById(R.id.page1_drawerLinear);
		type = false;
		boxChn.setChecked(true);
		boxEng.setChecked(false);
		
        drawerListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0){
					Intent mainIntent = new Intent(LibraryActivity.this,
							RecordActivity.class);
					LibraryActivity.this.startActivity(mainIntent);
					LibraryActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 1){
					Intent mainIntent = new Intent(LibraryActivity.this,
							CollectionActivity.class);
					LibraryActivity.this.startActivity(mainIntent);
					LibraryActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 2){
					Intent mainIntent = new Intent(LibraryActivity.this,
							LibraryActivity.class);
					LibraryActivity.this.startActivity(mainIntent);
					LibraryActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 3){
					Intent mainIntent = new Intent(LibraryActivity.this,
							LoginActivity.class);
					LibraryActivity.this.startActivity(mainIntent);
					LibraryActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
			}
        	
        });
        
		//读取autocomplete
        dbHelper = new MyDatabaseHelper(this, DB_NAME, 1);
        //用于调试
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		//this.deleteDatabase(DB_NAME);
		//db.execSQL("drop table" + TABLE_NAME1);
		//db.execSQL("drop table" + TABLE_NAME2);
		if (dbHelper != null){
			cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + TABLE_NAME1, null);
			int contentCol = cursor.getColumnIndex("content");
			for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				String user = cursor.getString(contentCol);
				userList.add(user);
			}
		}

		av = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, userList);
		editText.setAdapter(av);
		
		//设置列表
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
		search_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Date dt = new Date();
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				//System.out.println(df.format(dt));
				String [] str = new String[]{editText.getText().toString(), df.format(dt)};
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				db.execSQL("insert into " + TABLE_NAME1 + " values(null, ?, ?)", str);
				
				Intent mainIntent = new Intent(LibraryActivity.this,
						SearchResultActivity.class);
				LibraryActivity.this.startActivity(mainIntent);
				LibraryActivity.this.finish();

				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
			}
		});
		boxChn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if (type){
					type = false;
					boxChn.setChecked(true);
					boxEng.setChecked(false);
				}
			}
		});
		boxEng.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if (!type){
					type = true;
					boxChn.setChecked(false);
					boxEng.setChecked(true);
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
