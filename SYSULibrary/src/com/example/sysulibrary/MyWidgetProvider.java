package com.example.sysulibrary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider{
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds){
		Intent clickInt = new Intent(context, LibraryActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context,0,clickInt,0);
		RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
		rv.setOnClickPendingIntent(R.id.widget_image, pi);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);
		//super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
