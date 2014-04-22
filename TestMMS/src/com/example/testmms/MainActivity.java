package com.example.testmms;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends Activity {

	private static final int SELECT_PHOTO = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);   // Whatever your bitmap is that you want to send

		
		
		
		String filepath = "/sdcard/Pictures/Cool Wallpapers/119480.jpg";
		File imagefile = new File(filepath);
		FileInputStream fis = null;
		try {
		    fis = new FileInputStream(imagefile);
		    } catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(fis);
		ImageView image = (ImageView)findViewById(R.id.test_image);
		image.setImageBitmap(bm);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		MMSPart[] parts = new MMSPart[1];
		parts[0] = new MMSPart();
		parts[0].Name = "Image";
		parts[0].MimeType = "image/jpeg";
		parts[0].Data = byteArray;
		
		
		
//		APNHelper aHelper = new APNHelper(this);
//		aHelper.sendMMS("01252840600", parts);
		
		Button bt = (Button)findViewById(R.id.bt1);
		bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
	    switch(requestCode) { 
	    case SELECT_PHOTO:
	        if(resultCode == RESULT_OK){ 
	            Uri selectedImage = imageReturnedIntent.getData();
	            InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
		            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

		            Button btn = (Button)findViewById(R.id.bt1);
		            btn.setText(GetMimeType(this, selectedImage));
		            
		    		ImageView image = (ImageView)findViewById(R.id.test_image);
		    		image.setImageBitmap(yourSelectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	        }
	    }
	}
	
	public static String GetMimeType(Context context, Uri uriImage)
	{
	    String strMimeType = null;

	    Cursor cursor = context.getContentResolver().query(uriImage,
	                        new String[] { MediaStore.MediaColumns.MIME_TYPE },
	                        null, null, null);

	    if (cursor != null && cursor.moveToNext())
	    {
	        strMimeType = cursor.getString(0);
	    }

	    return strMimeType;
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
