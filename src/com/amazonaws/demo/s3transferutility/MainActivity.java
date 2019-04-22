/*
 * Copyright 2015-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * This is the beginning screen that lets the user select if they want to upload or download
 */
public class MainActivity extends Activity {

    private Myadapter adapter;
    private Button textbtn;
    private Button overviewbtn;
    private Button completedbtn;
    private Button uncompletedbtn;
    private Button aboutbtn;
    private Button searchbtn;
    private ListView lv;
    public EditText search;

    String[] array;
    String[] projectname;
    String[] duedate;
    String[] status;
    static Util util;
    static TransferUtility transferUtility;
    List<String> name= new ArrayList<>();
    private TransferObserver observer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn);
        util = new Util();
        transferUtility = util.getTransferUtility(this);
        initView();

        try {
            compute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bgm();
    }
    //Initialization interface//
    private void initView() {
        lv = (ListView) findViewById(R.id.list);
        textbtn = (Button) findViewById(R.id.create);
        overviewbtn = (Button) findViewById(R.id.overview);
        completedbtn = (Button) findViewById(R.id.completed);
        uncompletedbtn = (Button)findViewById(R.id.uncompleted);
        aboutbtn =(Button)findViewById(R.id.about);
        searchbtn = (Button) findViewById(R.id.searchbtn);
        search = (EditText) findViewById(R.id.search);

        try {
            selectDB();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //click the item in the list to enter the modify interface//
        textbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, addcontent.class);
                startActivity(intent);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final Intent i = new Intent(MainActivity.this,SelectActivity.class);
                i.putExtra("Xianyang",projectname[position]);
                i.putExtra("position",position+"");
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + projectname[position]+".txt");
                TransferObserver observer = transferUtility.download(projectname[position]+".txt", file);

                observer.setTransferListener(new TransferListener() {
                                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(state)) {
                    startActivity(i);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("onError", ex.toString());
                }
                });
                }
        });
        //show all items//
        overviewbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //show the completed item//
        completedbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    completed();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        //show the uncompleted item//
        uncompletedbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uncompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //show authors information//
        aboutbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("About");
                dialog.setMessage("Yuxiao Che yche007@uottawa.ca" + "\r\n" + "Zefan Liang zlian037@uottawa.ca");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();
            }
        });
        //According to the projectname to search the project//
        searchbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String search1 = search.getText().toString();
                try {
                    Search(search1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                search.setText("");
            }
        });
    }
    //read information(project name, due time, status) about all items//
    //add the information in the list//
    public void selectDB() throws Exception {
        name=getFileContext(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/"+"Recorder.txt");
        array = new String[name.size()];
        array = name.toArray(array);
        projectname = new String[(array.length/3)];
        duedate = new String[(array.length/3)];
        status = new String[(array.length/3)];
        int j = 0;
        for (int i = 0; i <array.length; i = i +3){
            projectname[j] = array[i];
            duedate [j] = array [i+1];
            status [j] = array [i+2];
            j++;
        }
        adapter = new Myadapter(this,projectname,duedate,status);
        lv.setAdapter(adapter);
    }
    //read information(project name,due time, status) about all the completed item//
    //add the information in the list//
    public void completed() throws Exception{
        name=getFileContext(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/"+"Recorder.txt");
        array = new String[name.size()];
        array = name.toArray(array);
        projectname = new String[(array.length/3)];
        duedate = new String[(array.length/3)];
        status = new String[(array.length/3)];
        int j = 0;
        for (int i = 2; i < array.length; i = i + 3){
            if (array[i].equals("completed")){
                status [j] = array[i];
                duedate [j] = array[i-1];
                projectname [j] = array [i-2];
                j++;
            }
        }
        adapter = new Myadapter(this,projectname,duedate,status);
        lv.setAdapter(adapter);
    }
    //read information(project name, due time, status) about all uncompleted items//
    //add the information in the list//
    public void uncompleted() throws Exception{
        name=getFileContext(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/"+"Recorder.txt");
        array = new String[name.size()];
        array = name.toArray(array);
        projectname = new String[(array.length/3)];
        duedate = new String[(array.length/3)];
        status = new String[(array.length/3)];
        int j = 0;
        for (int i = 2; i < array.length; i = i + 3){
            if (array[i].equals("uncompleted")){
                status [j] = array[i];
                duedate [j] = array[i-1];
                projectname [j] = array [i-2];
                j++;
            }
        }
        adapter = new Myadapter(this,projectname,duedate,status);
        lv.setAdapter(adapter);
    }
    //search the project name//
    //add the result in list//
    public void Search(String search2) throws Exception {
        name=getFileContext(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/"+"Recorder.txt");
        array = new String[name.size()];
        array = name.toArray(array);
        projectname = new String[(array.length/3)];
        duedate = new String[(array.length/3)];
        status = new String[(array.length/3)];
        String [] searchname = new String[1];
        String [] searchdate = new String[1];
        String [] searchstatus = new String[1];
        int j = 0;
        for (int i = 0; i <array.length; i = i +3){
            projectname[j] = array[i];
            duedate [j] = array [i+1];
            status [j] = array [i+2];
            j++;
        }
        for (int i = 0; i < projectname.length; i++){
            if (search2.equals(projectname[i])){
                searchname [0] = projectname[i];
                searchdate [0] = duedate[i];
                searchstatus [0] = status[i];
            }
        }

        adapter = new Myadapter(this,searchname,searchdate,searchstatus);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            selectDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //read the content of the file//
    public static List<String> getFileContext(String path) throws Exception {
        FileReader fileReader =new FileReader(path);
        BufferedReader bufferedReader =new BufferedReader(fileReader);
        List<String> list =new ArrayList<String>();
        String str=null;
        while((str=bufferedReader.readLine())!=null) {
                list.add(str);
        }
        return list;
    }
    // get current time//
    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String str = format.format(date);
        return  str;
    }
    //converse time format//
    public static Date strToDateLong(String strDate) throws ParseException {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date strtodate = formatter.parse(strDate);
            return strtodate;
    }
    //calculate the time between the current and the due date of the uncompleted project//
    //if the time is less than two days, pop up the reminder when opened app//
    public void compute() throws Exception {
        name=getFileContext(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/"+"Recorder.txt");
        array = new String[name.size()];
        array = name.toArray(array);
        int count = 0;
        for (int i = 2; i < array.length; i = i + 3){
            if (array[i].equals("uncompleted")){
                count++;
            }
        }
        projectname = new String[count];
        duedate = new String[count];
        status = new String[count];
        int j = 0;
        for (int i = 2; i < array.length; i = i + 3){
            if (array[i].equals("uncompleted")){
                status [j] = array[i];
                duedate [j] = array[i-1];
                projectname [j] = array [i-2];
                j++;
            }
        }
        int count1 = 0;
        Date nowtime = strToDateLong(getTime());
        for (int i = 0; i < duedate.length; i++){
            Date duetime = strToDateLong(duedate[i]);
            long diff = duetime.getTime() - nowtime.getTime();
            final long days = diff / (1000 * 60 * 60 * 24);
            if (days < 2){
                count1++;
            }
        }
        String [] warn = new String[count1];
        String str = "";
        int k = 0;
        for (int i = 0; i < duedate.length; i++){
            Date duetime = strToDateLong(duedate[i]);
            long diff = duetime.getTime() - nowtime.getTime();
            final long days = diff / (1000 * 60 * 60 * 24);
            if (days < 2){
                warn [k] = projectname[i] + " due on " + duedate[i];
                k++;
            }
        }
        for (int i = 0; i <warn.length; i++){
            str = str + warn[i] + "\r\n";
        }

        if (! str.equals("")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Warn");
            dialog.setMessage(str);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }
    }
    //play bgm//
    public void bgm() {
        Intent bgm = new Intent(MainActivity.this, MusicService.class);
        startService(bgm);
    }
}
