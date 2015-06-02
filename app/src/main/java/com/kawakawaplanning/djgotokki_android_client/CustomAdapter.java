package com.kawakawaplanning.djgotokki_android_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<CustomList> {
 private LayoutInflater layoutInflater_;
 
 public CustomAdapter(Context context, int textViewResourceId, List<CustomList> objects) {
 super(context, textViewResourceId, objects);
 layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 }
 
 @Override
 public View getView(int position, View convertView, ViewGroup parent) {
 // 特定の行(position)のデータを得る
 CustomList item = (CustomList)getItem(position);
 
 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
 if (null == convertView) {
 convertView = layoutInflater_.inflate(R.layout.list, null);
 }
 
 // CustomListのデータをViewの各Widgetにセットする
// ImageView imageView;
// imageView = (ImageView)convertView.findViewById(R.id.image);
// imageView.setImageBitmap(item.getImageData());
 
 TextView textView;
 textView = (TextView)convertView.findViewById(R.id.text);
 textView.setText(item.getTextData());
 
 return convertView;
 }
}