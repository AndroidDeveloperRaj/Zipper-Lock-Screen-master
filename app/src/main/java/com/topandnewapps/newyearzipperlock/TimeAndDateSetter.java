package com.topandnewapps.newyearzipperlock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.widget.TextView;

public class TimeAndDateSetter
{
  private String[] days = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
  private String[] months = { "Jan", "Feb", "Mar", "Apr","May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
  private TextView tvDate;
  private TextView tvTime;
  private TextView tvampm;

	public TimeAndDateSetter(TextView paramTextView1, TextView paramTextView2,TextView paramTextView3) {
		tvTime = paramTextView1;
		tvDate = paramTextView2;
		tvampm = paramTextView3;
		
	}

	@SuppressLint("SimpleDateFormat")
	public void setTimeAndDate() {
		if ((tvTime != null) && (tvDate != null)) {
			Calendar localCalendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
			String strDate = sdf.format(localCalendar.getTime());
			SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm");
			String strTime= sdf1.format(localCalendar.getTime());
			SimpleDateFormat sdf2 = new SimpleDateFormat("aa");
			String strampm= sdf2.format(localCalendar.getTime());
			int i = localCalendar.get(11);
			int j = localCalendar.get(12);
			String str1 = this.days[localCalendar.get(Calendar.DAY_OF_WEEK)-1];
			int k = localCalendar.get(5);
			
			String str2 = this.months[localCalendar.get(Calendar.MONTH)];
			String str3 = "";
			if (i < 10)
				str3 = str3 + 0;
			String str4 = str3 + i + ":";
			if (j < 10)
				str4 = str4 + 0;
			@SuppressWarnings("unused")
			String str5 = str4 + j;
			@SuppressWarnings("unused")
			String str6 = str1  + "  " + k + " " + str2;
			tvTime.setText(strTime);
			tvDate.setText(strDate);
			tvampm.setText(strampm);
		}
		
	}
}