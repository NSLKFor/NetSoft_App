package com.netsoft.netsms;

import java.text.DateFormat;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListContactAdapter extends BaseAdapter {
	private Context context;
	private List<ListContactItem> listContacts;
	private static LayoutInflater inflater = null;
	private static Point p;

	public ListContactAdapter(Context context1, List<ListContactItem> listItems) {
		this.context = context1;
		this.listContacts = listItems;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void updateItem(int position, ListContactItem item) {
		listContacts.set(position, item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = convertView;
		
		// using holder to render listview
		final ViewHolderListContact holder;
		if (view == null) {
			view = inflater.inflate(R.layout.contact_row, null);
			holder = new ViewHolderListContact();
			holder.imgThum = (ImageButton) view.findViewById(R.id.list_image);
			holder.tviAddress = (TextView) view.findViewById(R.id.address);
			holder.tviBody = (TextView) view.findViewById(R.id.body);
			holder.tviTime = (TextView) view.findViewById(R.id.time);
			
			holder.imgThum.setFocusable(false);

			view.setTag(holder);
		} else {
			holder = (ViewHolderListContact) view.getTag();
		}
		final ListContactItem listContactItem = this.listContacts.get(position);
		holder.position = position;
		
		holder.imgThum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "on click", Toast.LENGTH_LONG)
						.show();
				int[] location = new int[2];
				holder.imgThum.getLocationOnScreen(location);
				 p = new Point();
				 p.x = location[0];
				 p.y = location[1];
				 showPopup(context, p, listContactItem.address);
			}

		});
		

		//bold work when message no read
		if (listContactItem.readStatus == 0) {
			holder.tviBody.setTypeface(null, Typeface.BOLD);
			holder.tviAddress.setTypeface(null, Typeface.BOLD);
			holder.tviBody.setTypeface(null, Typeface.BOLD);
		} else {
			holder.tviBody.setTypeface(null, Typeface.NORMAL);
			holder.tviAddress.setTypeface(null, Typeface.NORMAL);
			holder.tviBody.setTypeface(null, Typeface.NORMAL);
		}

		if (listContactItem.name == null || listContactItem.name.equals("")) {
			holder.tviAddress.setText(listContactItem.address);
		} else {
			holder.tviAddress.setText(listContactItem.name);
		}

		//set thumnail for element if null set deault thumnail
		if (listContactItem.thumnail == null) {
			holder.imgThum.setImageResource(R.drawable.user_icon);
		} else {
			holder.imgThum.setImageURI(listContactItem.thumnail);
		}

		holder.tviBody.setText(listContactItem.body);
		holder.tviTime.setText(DateFormat.getInstance().format(
				listContactItem.time));
		return view;
	}
	
	//show popup when press to image thumail
	private void showPopup(final Context context, Point p, final String sPhone) {
		   int popupWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				   200, context.getResources().getDisplayMetrics());
		   int popupHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				   50, context.getResources().getDisplayMetrics());
		   
		 
		   // Inflate the popup_layout.xml
		   RelativeLayout viewGroup = (RelativeLayout) ((Activity) context).findViewById(R.id.popup);
		   LayoutInflater layoutInflater = (LayoutInflater) context
		     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);
		 
		   // Creating the PopupWindow
		   final PopupWindow popup = new PopupWindow(context);
		   popup.setContentView(layout);
		   popup.setWidth(popupWidth);
		   popup.setHeight(popupHeight);
		   popup.setFocusable(true);
		 
		   // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
		   int OFFSET_X = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				   30, context.getResources().getDisplayMetrics());
		   int OFFSET_Y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				   30, context.getResources().getDisplayMetrics());
		 
		   // Clear the default translucent background
		   popup.setBackgroundDrawable(new BitmapDrawable());
		 
		   // Displaying the popup at the specified location, + offsets.
		   popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
		   
		   ImageButton call = (ImageButton) layout.findViewById(R.id.Call);
		   call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + sPhone));
				context.startActivity(callIntent);
				popup.dismiss();
				
			}
		});
		}
}

//define viewHolder 
class ViewHolderListContact {
	ImageButton imgThum;
	TextView tviAddress;
	TextView tviBody;
	TextView tviTime;
	int position;
}
