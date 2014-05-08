package com.netsoft.netsms;

import java.text.DateFormat;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListContactAdapter extends BaseAdapter {
	private Context context;
	private List<ListContactItem> listContacts;
	private static LayoutInflater inflater = null;

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
		ViewHolderListContact holder;
		if (view == null) {
			view = inflater.inflate(R.layout.contact_row, null);
			holder = new ViewHolderListContact();
			holder.imgThum = (ImageView) view
					.findViewById(R.id.list_image);
			holder.tviAddress = (TextView) view
					.findViewById(R.id.address);
			holder.tviBody = (TextView) view.findViewById(R.id.body);
			holder.tviTime = (TextView) view.findViewById(R.id.time);

			view.setTag(holder);
		} else {
			holder = (ViewHolderListContact) view.getTag();
		}
		final ListContactItem listContactItem = this.listContacts.get(position);
		holder.position = position;

		if (listContactItem.readStatus == 0) {
			holder.tviBody.setTypeface(null, Typeface.BOLD);
			holder.tviAddress.setTypeface(null, Typeface.BOLD);
			holder.tviBody.setTypeface(null, Typeface.BOLD);
		}

		if (listContactItem.name == null || listContactItem.name.equals("")) {
			holder.tviAddress.setText(listContactItem.address);
		} else {
			holder.tviAddress.setText(listContactItem.name);
		}

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
}

class ViewHolderListContact {
	ImageView imgThum;
	TextView tviAddress;
	TextView tviBody;
	TextView tviTime;
	int position;
}
