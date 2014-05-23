package com.netsoft.netsms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class CanvasHelper extends View {

	String mText;
	Context mContext;
	Paint paint = new Paint();

	public CanvasHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawTextCanvas(canvas, mText);
	}

	private void drawTextCanvas(Canvas canvas, String text) {
		// TODO Auto-generated method stub

		// set color the background
		canvas.drawPaint(paint);

		// Setup textView like your normanlly would with your activity context
		TextView tv = new TextView(mContext);

		tv.setMaxWidth(300);

		// setup Text
		tv.setText(mText);

		// set Textcolor
		tv.setTextColor(Color.BLUE);

		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

		// enable SetDrawing cache
		tv.setDrawingCacheEnabled(true);

		tv.measure(MeasureSpec.makeMeasureSpec(300, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(canvas.getHeight(),
						MeasureSpec.EXACTLY));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.khung_xanh).copy(Bitmap.Config.ARGB_8888, true);
		bm = Bitmap.createScaledBitmap(bm, tv.getMeasuredWidth() + 40,
				(int) ((int) tv.getLineCount() * (tv.getLineHeight() + 8)),
				true);
		canvas.drawBitmap(bm, 0, 0, paint);

		// draw bitmap from drawingcache to the canvas
		canvas.drawBitmap(tv.getDrawingCache(), 20, 20, paint);

		// disable drawing cache
		tv.setDrawingCacheEnabled(false);

	}

	public void setText(String text) {
		mText = text;
	}

}
