package com.netsoft.netsms;

import java.util.Comparator;

import android.content.Context;
import android.net.Uri;

public class SMSHelper {
	private Context mContext;
	public SMSHelper(Context context){
		mContext = context;
	}

	public boolean deleteSMS(String smsId) {
        boolean isSmsDeleted = false;
        try {
        	mContext.getContentResolver().delete(
                    Uri.parse("content://sms/" + smsId), null, null);
            isSmsDeleted = true;

        } catch (Exception ex) {
            isSmsDeleted = false;
        }
        return isSmsDeleted;
    }
	
	public static String formatToStandardNumber(String temp) {
		String phone = "";
		if (temp.substring(0, 3).equals("+84")) {
			switch (temp.length()) {
			case 13:
				phone = "0" + temp.substring(3);
				break;
			case 12:
				phone = "0" + temp.substring(3);
				break;
			case 16:
				if (temp.subSequence(6, 7).equals(" ")) {
					phone = "0" + temp.substring(4, 6) + temp.substring(7, 10)
							+ temp.substring(11, 13) + temp.substring(14);
				} else {
					phone = "0" + temp.substring(4, 7) + temp.substring(8, 11)
							+ temp.substring(12);
				}
				break;
			}
			;
		}
		if (temp.substring(0, 2).equals("84")) {
			switch (temp.length()) {
			case 12:
				phone = "0" + temp.substring(2);
				break;
			case 11:
				phone = "0" + temp.substring(2);
				break;
			}
			;
		}

		if (temp.length() == 13 && !temp.substring(0, 3).equals("+84")) {
			if (temp.substring(4, 5).equals(" ")) {
				phone = temp.substring(0, 4) + temp.substring(5, 8)
						+ temp.substring(9);
			} else {
				phone = temp.substring(0, 3) + temp.substring(4, 7)
						+ temp.substring(8, 10) + temp.substring(11);

			}
		}

		if (phone == null || "".equals(phone)) {
			phone = temp;
		}

		return phone;
	}
	
	public static class SMSTimeComparator implements Comparator<SmsItem> {
		@Override
		public int compare(SmsItem arg1, SmsItem arg0) {
			// TODO Auto-generated method stub
			return (arg0.date > arg1.date) ? -1 : (arg0.date == arg1.date) ? 0
					: 1;
		}
	}
	

}
