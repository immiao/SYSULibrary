package com.example.sysulibrary;

import java.util.ArrayList;
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
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends Activity{
	//读取数据库
	private Cursor cursor;
	private MyDatabaseHelper dbHelper;
	private final String TABLE_NAME1 = "Records";
	//private final String TABLE_NAME2 = "Books";
	private final String DB_NAME = "Database.db";
	
	private ImageButton imgBtn;
	private boolean clicked, arrowOrLine;
	private DrawerLayout mDrawerLayout;
	private LinearLayout drawerLinear;
	private ListView drawerListView;
	private TextView clearTextView;
	private List<Map<String,Object>> mDataList;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_record);

		listView = (ListView) findViewById(R.id.page3_listView);
		clearTextView = (TextView) findViewById(R.id.page3_clear);
		listView.setVisibility(View.VISIBLE);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.page3_drawerLayout);
		drawerListView = (ListView) findViewById(R.id.page3_drawerListView);
		drawerLinear = (LinearLayout) findViewById(R.id.page3_drawerLinear);
		imgBtn = (ImageButton) findViewById(R.id.page3_menu);
		
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
					Intent mainIntent = new Intent(RecordActivity.this,
							RecordActivity.class);
					RecordActivity.this.startActivity(mainIntent);
					RecordActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 1){
					Intent mainIntent = new Intent(RecordActivity.this,
							CollectionActivity.class);
					RecordActivity.this.startActivity(mainIntent);
					RecordActivity.this.finish();
	
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 2){
					Intent mainIntent = new Intent(RecordActivity.this,
							LibraryActivity.class);
					RecordActivity.this.startActivity(mainIntent);
					RecordActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
				else if (position == 3){
					Intent mainIntent = new Intent(RecordActivity.this,
							LoginActivity.class);
					RecordActivity.this.startActivity(mainIntent);
					RecordActivity.this.finish();

					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				}
			}
        	
        });
        
		dbHelper = new MyDatabaseHelper(this, DB_NAME, 1);
		if (dbHelper != null){
			cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + TABLE_NAME1, null);
			int contentCol = cursor.getColumnIndex("content");
			int dateCol = cursor.getColumnIndex("date");
			//System.out.println("sss" + contentCol);
			 mDataList = new ArrayList<Map<String,Object>>();
			for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
		    	map=new HashMap<String,Object>();
		    	map.put("content", cursor.getString(contentCol));
		    	map.put("date", cursor.getString(dateCol));
		    	mDataList.add(map);
			}
		}
        SimpleAdapter mlistItemAdapter = new SimpleAdapter(this,
        		mDataList,
        		R.layout.recordlistitem,
        		new String[]{"content","date"},
        		new int[]{R.id.page3_record,R.id.page3_date}
        		);
        listView.setAdapter(mlistItemAdapter); 
		/*				Intent mainIntent = new Intent(RecordActivity.this,
						LibraryActivity.class);
				RecordActivity.this.startActivity(mainIntent);
				RecordActivity.this.finish();

				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);*/	
		clearTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				db.execSQL("delete from " + TABLE_NAME1);
				listView.setVisibility(View.GONE);
				Toast.makeText(RecordActivity.this, "清空成功", Toast.LENGTH_SHORT).show();
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
