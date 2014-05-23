package com.netsoft.netsms;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SmsAdapter extends BaseAdapter {

	private final Context context;
	private final List<SmsItem> smsItems;
	private static LayoutInflater inflater = null;
	private static float wight;
	private static float height;
	private float density ;

	public SmsAdapter(Context context1, List<SmsItem> smsItems, float fwight, float fheight) {
		this.context = context1;
		this.smsItems = smsItems;
		this.wight = fwight;
		this.height = fheight;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		density = context.getResources().getDisplayMetrics().density;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return smsItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return smsItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_sms_row, null);
			holder = new ViewHolder();
			holder.mmsImage = (ImageView) convertView.findViewById(R.id.imgMMS);
			// holder.body = (TextView) convertView.findViewById(R.id.body);
			// holder.time = (TextView) convertView.findViewById(R.id.type);
			// holder.list_sms = (RelativeLayout) convertView
			// .findViewById(R.id.list_sms);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SmsItem item = smsItems.get(position);
		holder.position = position;
		//
		// holder.body.setText(item.body.toString());
		//
		// SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm, MMM dd");
		// holder.time.setText(sdf.format(item.date));
		//
		RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		//
		// RelativeLayout.LayoutParams bodyParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		//
		// RelativeLayout.LayoutParams timeParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		//
		// bodyParams.addRule(RelativeLayout.BELOW, R.id.imgMMS);
		// timeParams.addRule(RelativeLayout.BELOW, R.id.body);

		if (item.type == 2) {
			// bodyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			holder.mmsImage.setLayoutParams(imgParams);
			// holder.body.setLayoutParams(bodyParams);
			// holder.time.setLayoutParams(timeParams);

			if (item.imgMMS != null) {

				// int width = 300;
				// int height = 250;
				// RelativeLayout.LayoutParams parms = new
				// RelativeLayout.LayoutParams(
				// width, height);
				// holder.mmsImage.setLayoutParams(imgParams);
				Bitmap bmp = decodeSampledBitmapFromResource(item.imgMMS, 200,
						200);
				// holder.mmsImage.setImageBitmap(bmp);

				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage.setImageDrawable(writeCanvasRightBitmap(
						context, R.drawable.green, bmp, item.body,
						kkk.format(item.date)));
			} else {
				// holder.mmsImage.setImageBitmap(null);
				// holder.mmsImage.destroyDrawingCache();

				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage.setImageDrawable(writeOnDrawable(context,
						R.drawable.green, item.body, kkk.format(item.date)));
			}

			// holder.list_sms.setBackgroundColor(Color.parseColor("#A9E2F3"));
		} else {
			// bodyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// timeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			imgParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

			holder.mmsImage.setLayoutParams(imgParams);
			// holder.body.setLayoutParams(bodyParams);
			// holder.time.setLayoutParams(timeParams);

			if (item.imgMMS != null) {

				// int width = 300;
				// int height = 250;
				// RelativeLayout.LayoutParams parms = new
				// RelativeLayout.LayoutParams(
				// width, height);
				// holder.mmsImage.setLayoutParams(parms);

				// Bitmap bmp = BitmapFactory.decodeByteArray(item.imgMMS, 0,
				// item.imgMMS.length);

				Bitmap bmp = decodeSampledBitmapFromResource(item.imgMMS, 200,
						200);
				// holder.mmsImage.setImageBitmap(bmp);

				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage
						.setImageDrawable(writeCanvasLeftBitmap(context,
								R.drawable.gray, bmp, item.body,
								kkk.format(item.date)));
			} else {
				// holder.mmsImage.setImageBitmap(null);
				// holder.mmsImage.destroyDrawingCache();
				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage.setImageDrawable(writeOnDrawable(context,
						R.drawable.gray, item.body, kkk.format(item.date)));
			}

			// holder.list_sms.setBackgroundColor(Color.parseColor("#E0F8F7"));

		}
		return convertView;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] res,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeByteArray(res, 0, res.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(res, 0, res.length, options);
	}

	public BitmapDrawable writeOnDrawable(Context context, int drawableId,
			String body, String time) {
		
		TextView j = new TextView(context);
		j.setText("O");
		j.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		
		
		
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);
		
		TextPaint mTextPaint = new TextPaint();
		float textSize = j.getTextSize();
		float charWight = textSize;
		float charHeight = textSize;
		float dimension =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSize, context.getResources().getDisplayMetrics());
		mTextPaint.setColor(0xff003366);
		mTextPaint.setTextSize(dimension);
		
		StaticLayout mTextLayout = new StaticLayout(body, mTextPaint, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wight, context.getResources().getDisplayMetrics())
				,Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

		
		float textWight = wight;

		if (body.length() * charWight < wight) {
			textWight = body.length() * charWight >  wight / 2 ? wight :( wight / 2) * body.length() * charWight;
		}
		
		int lin = mTextLayout.getLineCount();
		bm = Bitmap.createScaledBitmap(bm,(int) textWight, (int) charHeight* (lin + 3), true);
		Canvas canvas = new Canvas(bm);

		canvas.save();
		// calculate x and y position where your text will be placed

		float textX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25 * density, context.getResources().getDisplayMetrics());
		float textY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15 * density, context.getResources().getDisplayMetrics());

		canvas.translate(textX, textY);
		mTextLayout.draw(canvas);
		canvas.restore();

		mTextPaint.setColor(0xff3385FF);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextLayout = new StaticLayout(time, mTextPaint, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wight, context.getResources().getDisplayMetrics()),
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
		
		textX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25 * density, context.getResources().getDisplayMetrics());
		textY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,j.getHeight() * (lin + 1), context.getResources().getDisplayMetrics());

		canvas.translate(textX, textY);
		mTextLayout.draw(canvas);

		return new BitmapDrawable(bm);
	}

	public BitmapDrawable writeCanvasLeftBitmap(Context context,
			int drawableId, Bitmap bitmap, String body, String time) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);
		
		TextPaint mTextPaint = new TextPaint();
		StaticLayout mTextLayout = null;
		int lin = 0;
		int tx = 300;
		if (body != null || "".equals(body)) {
			// mTextPaint.setColor(Color.RED);
			int size = 100;
			int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    size, context.getResources().getDisplayMetrics());
			mTextPaint.setTextSize(pixels);
			
			mTextLayout = new StaticLayout(body, mTextPaint, 250,
					Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

			lin = mTextLayout.getLineCount();

			if (body.length() * 10 < 300) {
				tx = body.length() * 10 > 150 ? body.length() * 10 : 150;
			}
		}

		bm = Bitmap.createScaledBitmap(bm, tx > bitmap.getWidth() ? tx + 45
				: bitmap.getWidth() + 45,
				(16 + 3) * (lin + 4) + bitmap.getHeight(), true);
		Canvas canvas = new Canvas(bm);

		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, 25, 20, paint);

		float txtX = 30;
		float txtY = (16 + 4) * (lin + 1) + bitmap.getHeight();

		if (body != null || !"".equals(body)) {
			canvas.save();
			// calculate x and y position where your text will be placed

			float textX = 30;
			float textY = (16 + 8) + bitmap.getHeight();

			canvas.translate(textX, textY);
			mTextLayout.draw(canvas);
			canvas.restore();

			txtY = (16 + 8) * (lin + 1) + bitmap.getHeight();
		}

		// mTextPaint.setColor(Color.BLUE);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextLayout = new StaticLayout(time, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

		canvas.translate(txtX, txtY);
		mTextLayout.draw(canvas);

		return new BitmapDrawable(bm);
	}

	public BitmapDrawable writeCanvasRightBitmap(Context context,
			int drawableId, Bitmap bitmap, String body, String time) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);

		TextPaint mTextPaint = new TextPaint();
		StaticLayout mTextLayout = null;
		int lin = 0;
		int tx = 300;
		if (body != null || "".equals(body)) {
			// mTextPaint.setColor(Color.RED);
			mTextPaint.setTextSize(16);
			mTextLayout = new StaticLayout(body, mTextPaint, 250,
					Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

			lin = mTextLayout.getLineCount();

			if (body.length() * 10 < 300) {
				tx = body.length() * 10 > 150 ? body.length() * 10 : 150;
			}
		}

		bm = Bitmap.createScaledBitmap(bm, tx > bitmap.getWidth() ? tx + 50
				: bitmap.getWidth() + 50,
				(16 + 3) * (lin + 4) + bitmap.getHeight(), true);
		Canvas canvas = new Canvas(bm);

		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, 20, 25, paint);

		float txtX = 25;
		float txtY = (16 + 4) * (lin + 1) + bitmap.getHeight();

		if (body != null || !"".equals(body)) {
			canvas.save();
			// calculate x and y position where your text will be placed

			float textX = 25;
			float textY = (16 + 8) + bitmap.getHeight();

			canvas.translate(textX, textY);
			mTextLayout.draw(canvas);
			canvas.restore();

			txtY = (16 + 8) * (lin + 1) + bitmap.getHeight();
		}

		// mTextPaint.setColor(Color.BLUE);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextLayout = new StaticLayout(time, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

		canvas.translate(txtX, txtY);
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

class ViewHolder {
	ImageView mmsImage;
	// TextView body;
	// TextView time;
	// RelativeLayout list_sms;
	int position;
}
