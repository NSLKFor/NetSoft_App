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

	public SmsAdapter(Context context1, List<SmsItem> smsItems) {
		this.context = context1;
		this.smsItems = smsItems;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			holder.body = (TextView) convertView.findViewById(R.id.body);
			holder.time = (TextView) convertView.findViewById(R.id.type);
			holder.list_sms = (RelativeLayout) convertView
					.findViewById(R.id.list_sms);

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
		// RelativeLayout.LayoutParams imgParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
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
			// imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			//
			// holder.mmsImage.setLayoutParams(imgParams);
			// holder.body.setLayoutParams(bodyParams);
			// holder.time.setLayoutParams(timeParams);

			if (item.imgMMS != null) {

//				int width = 300;
//				int height = 250;
//				RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
//						width, height);
//				holder.mmsImage.setLayoutParams(parms);
				Bitmap bmp = decodeSampledBitmapFromResource(item.imgMMS, 200,
						200);
				// holder.mmsImage.setImageBitmap(bmp);

				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage.setImageDrawable(writeCanvasBitmap(context,
						R.drawable.khung_xanh, bmp, item.body,
						kkk.format(item.date)));
			} else {
				// holder.mmsImage.setImageBitmap(null);
				// holder.mmsImage.destroyDrawingCache();

				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage
						.setImageDrawable(writeOnDrawable(context,
								R.drawable.khung_xanh, item.body,
								kkk.format(item.date)));
			}

			// holder.list_sms.setBackgroundColor(Color.parseColor("#A9E2F3"));
		} else {
			// bodyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// timeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// imgParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			//
			// holder.mmsImage.setLayoutParams(imgParams);
			// holder.body.setLayoutParams(bodyParams);
			// holder.time.setLayoutParams(timeParams);

			if (item.imgMMS != null) {

				int width = 300;
				int height = 250;
				RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
						width, height);
				holder.mmsImage.setLayoutParams(parms);

				// Bitmap bmp = BitmapFactory.decodeByteArray(item.imgMMS, 0,
				// item.imgMMS.length);

				Bitmap bmp = decodeSampledBitmapFromResource(item.imgMMS, 200,
						200);
				// holder.mmsImage.setImageBitmap(bmp);

				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage.setImageDrawable(writeCanvasBitmap(context,
						R.drawable.khung_xanh_duong, bmp, item.body,
						kkk.format(item.date)));
			} else {
				// holder.mmsImage.setImageBitmap(null);
				// holder.mmsImage.destroyDrawingCache();
				SimpleDateFormat kkk = new SimpleDateFormat(" HH:mm, MMM dd");
				holder.mmsImage.setImageDrawable(writeOnDrawable(context,
						R.drawable.khung_xanh_duong, item.body,
						kkk.format(item.date)));
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
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);
		// String body =
		// "Trong dam gi dep bang sen. La xanh bong trang lai chen nhi vang. Nhi vang bong trang la xanh. Gan bun ma chang hoi tanh mui bun.";
		// +
		// "Trong dam gi dep bang sen. La xanh bong trang lai chen nhi vang. Nhi vang bong trang la xanh. Gan bun ma chang hoi tanh mui bun.";
		// +"Trong dam gi dep bang sen. La xanh bong trang lai chen nhi vang. Nhi vang bong trang la xanh. Gan bun ma chang hoi tanh mui bun.";
		// String time = "10:12 June 15";

		TextPaint mTextPaint = new TextPaint();
		mTextPaint.setColor(Color.RED);
		mTextPaint.setTextSize(16);
		StaticLayout mTextLayout = new StaticLayout(body, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

		int lin = mTextLayout.getLineCount();

		bm = Bitmap.createScaledBitmap(bm, 300, (16 + 3) * (lin + 5), true);
		Canvas canvas = new Canvas(bm);

		canvas.save();
		// calculate x and y position where your text will be placed

		float textX = 30;
		float textY = 20;

		canvas.translate(textX, textY);
		mTextLayout.draw(canvas);
		canvas.restore();

		mTextPaint.setColor(Color.BLUE);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextLayout = new StaticLayout(time, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
		textX = 30;
		textY = (16 + 4) * (lin + 1);

		canvas.translate(textX, textY);
		mTextLayout.draw(canvas);

		return new BitmapDrawable(bm);
	}

	public BitmapDrawable writeCanvasBitmap(Context context, int drawableId,
			Bitmap bitmap, String body, String time) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);

		TextPaint mTextPaint = new TextPaint();
		mTextPaint.setColor(Color.RED);
		mTextPaint.setTextSize(16);
		StaticLayout mTextLayout = new StaticLayout(body, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

		int lin = mTextLayout.getLineCount();

		bm = Bitmap.createScaledBitmap(bm, 300, (16 + 3) * (lin + 5) + bitmap.getHeight(), true);
		Canvas canvas = new Canvas(bm);

		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, 30, 20, paint);

		canvas.save();
		// calculate x and y position where your text will be placed

		float textX = 30;
		float textY = 20 + bitmap.getHeight();

		canvas.translate(textX, textY);
		mTextLayout.draw(canvas);
		canvas.restore();

		mTextPaint.setColor(Color.BLUE);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextLayout = new StaticLayout(time, mTextPaint, 250,
				Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
		textX = 30;
		textY = (16 + 4) * (lin + 1) + bitmap.getHeight();

		canvas.translate(textX, textY);
		mTextLayout.draw(canvas);

		return new BitmapDrawable(bm);
	}

}

class ViewHolder {
	ImageView mmsImage;
	TextView body;
	TextView time;
	RelativeLayout list_sms;
	int position;
}
