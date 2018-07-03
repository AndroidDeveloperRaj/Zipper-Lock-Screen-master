package com.topandnewapps.newyearzipperlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class TimeChangeReceiver extends BroadcastReceiver
{
  private TextView tvDate;
  private TextView tvTime;
  private TextView tvampm;

  public TimeChangeReceiver(TextView paramTextView1, TextView paramTextView2,TextView paramTextView3)
  {
    this.tvTime = paramTextView1;
    this.tvDate = paramTextView2;
    this.tvampm=paramTextView3;
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.intent.action.TIME_TICK"))
      new TimeAndDateSetter(tvTime, tvDate,tvampm).setTimeAndDate();
  }
}