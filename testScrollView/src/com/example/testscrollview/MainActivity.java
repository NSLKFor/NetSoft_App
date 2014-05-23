package com.example.testscrollview;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private ImageView img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ScrollView kkk = (ScrollView) findViewById(R.id.myscroll1);
		TableLayout tableLayout = new TableLayout(this);
		for (int i = 1; i < 2; i++) {
			TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT);
			
			int leftMargin=40;
			int topMargin=10;
			int rightMargin=10;
			int bottomMargin=10;

			tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
			
			TableRow tableRow = new TableRow(this);
			ImageView img = new ImageView(this);

			tableRow.setLayoutParams(tableRowParams);
			img.setImageDrawable(writeOnDrawable(this, R.drawable.khung_xanh, "tran trong linh", "12/12/2012"));
			
			tableRow.addView(img);
			tableLayout.addView(tableRow);
		}
		
		TextView tt = new TextView(this);
		tt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		tt.setText(Float.toString(tt.getTextSize()));
		
		TableRow tableRow = new TableRow(this);
		tableRow.addView(tt);
		tableLayout.addView(tableRow);
		
		

		kkk.addView(tableLayout);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public BitmapDrawable writeOnDrawable(Context context, int drawableId,
			String body, String time) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);

		TextPaint mTextPaint = new TextPaint();
		float density = context.getResources().getDisplayMetrics().density;
		float size = 16 * density;
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
		mTextPaint.setColor(0xff003366);
		mTextPaint.setTextSize(px);
		
		StaticLayout mTextLayout = new StaticLayout(body, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

	Canvas canvas = new Canvas(bm);
		mTextLayout.draw(canvas);

		return new BitmapDrawable(bm);
	}
	
	public static float convertDpToPixels(float dp, Context context) {
		
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

}
