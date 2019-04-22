package com.amazonaws.demo.s3transferutility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.amazonaws.demo.s3transferutility.Util.TAG;

/*
It is the adapter of the list shown the in main interface
 */
public class Myadapter extends BaseAdapter {

    private Context context;
    private LinearLayout layout;
    private  String[] name;
    private String[] time;
    private String[] status;
    static Util util;
    static TransferUtility transferUtility;

    public Myadapter(Context context,String[] name, String [] time, String [] status){

        super();
        util = new Util();
        transferUtility = util.getTransferUtility(context);
        this.context=context;
        this.name = name;
        this.time = time;
        this.status = status;
    }
    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (LinearLayout) inflater.inflate(R.layout.cell,null);
        TextView contenttv = (TextView) layout.findViewById(R.id.list_content);
        TextView timetv = (TextView) layout.findViewById(R.id.list_time);
        TextView statustv = (TextView) layout.findViewById(R.id.list_status);
        contenttv.setText(name[position]);
        timetv.setText(time[position]);
        statustv.setText(status[position]);

        return layout;
    }
}
