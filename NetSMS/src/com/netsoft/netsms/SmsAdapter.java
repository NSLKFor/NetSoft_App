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
import android.graphics.Color;
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

		holder.body.setText(item.body.toString());

		SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm, MMM dd");
		// tviTime.setText(DateFormat.getInstance().format(
		// listContactItem.time));
		holder.time.setText(sdf.format(item.date));

		RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams bodyParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams timeParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		bodyParams.addRule(RelativeLayout.BELOW, R.id.imgMMS);
		timeParams.addRule(RelativeLayout.BELOW, R.id.body);

		if (item.type == 2) {
			bodyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			holder.mmsImage.setLayoutParams(imgParams);
			holder.body.setLayoutParams(bodyParams);
			holder.time.setLayoutParams(timeParams);

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
				holder.mmsImage.setImageBitmap(bmp);
			} else {
				holder.mmsImage.setImageBitmap(null);
				holder.mmsImage.destroyDrawingCache();
			}

			holder.list_sms.setBackgroundColor(Color.parseColor("#A9E2F3"));
		} else {
			bodyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			timeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			imgParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

			holder.mmsImage.setLayoutParams(imgParams);
			holder.body.setLayoutParams(bodyParams);
			holder.time.setLayoutParams(timeParams);

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
				holder.mmsImage.setImageBitmap(bmp);
			} else {
				holder.mmsImage.setImageBitmap(null);
				holder.mmsImage.destroyDrawingCache();
			}

			holder.list_sms.setBackgroundColor(Color.parseColor("#E0F8F7"));

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

}

class ViewHolder {
	ImageView mmsImage;
	TextView body;
	TextView time;
	RelativeLayout list_sms;
	int position;
}
