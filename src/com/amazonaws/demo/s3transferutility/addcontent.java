package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
It is the add project information interface.
Users can add the information in the app and app will upload the information to the amazon cloud
 */

public class addcontent extends Activity implements View.OnClickListener{

    public EditText ProjectName;
    public EditText CourseTitle;
    public EditText CourseNumber;
    public EditText InstructorName;
    public EditText ProjectNumber;
    public EditText projectDescription;
    public EditText Duedata;
    public RadioGroup status;
    public RadioButton yes;
    public RadioButton no;
    private Button savebtn;
    private Button backbtn;
    private Intent i;
    static Util util;
    private RelativeLayout ll;
    static TransferUtility transferUtility;
    String data;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.addcontent);
        savebtn = (Button) findViewById(R.id.save);
        backbtn = (Button) findViewById(R.id.back);

        ProjectName = (EditText) findViewById(R.id.ProjectName);
        CourseTitle = (EditText) findViewById(R.id.CourseTitle);
        CourseNumber = (EditText) findViewById(R.id.CourseNumber);
        InstructorName = (EditText) findViewById(R.id.InstructorName);
        ProjectNumber = (EditText) findViewById(R.id.ProjectNumber);
        projectDescription = (EditText) findViewById(R.id.projectDescription);
        Duedata = (EditText) findViewById(R.id.Duedata);


        status = (RadioGroup) findViewById(R.id.radio);
        yes = (RadioButton) findViewById(R.id.y);
        no = (RadioButton) findViewById(R.id.n);

        savebtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }
    //save the information entered by the user into a text file and update the recorder.txt//
    private String initData(){
        String ProjectName1 = ProjectName.getText().toString();
        String CourseTitle1=CourseTitle.getText().toString();
        String CourseNumber1=CourseNumber.getText().toString();
        String InstructorName1=InstructorName.getText().toString();
        String ProjectNumber1=ProjectNumber.getText().toString();
        String ProjectDescription1=projectDescription.getText().toString();
        String DueData1=Duedata.getText().toString();
        String state1 = "";
        if (yes.isChecked()){
            state1 = "completed";
        }
        if (no.isChecked()){
            state1 = "uncompleted";
        }

        String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/";
        String fileuploads=Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/upload/";
        File fileupload = new File(filePath+"upload");
        if(!fileupload.exists()) {
            fileupload.mkdirs();
        }

        String fileName = ProjectName1+".txt";
        String content=
                ProjectName1+"\r\n"+CourseTitle1+"\r\n"+CourseNumber1+"\r\n"+InstructorName1+"\r\n"+ProjectNumber1+"\r\n"+DueData1+"\r\n"+ProjectDescription1+"\r\n"+state1;

        writeTxtToFile(content, fileuploads, fileName);
        String Filenamerecorder="Recorder.txt";
        File file=new File(filePath+Filenamerecorder);
        File filedownload = new File(filePath+"download");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block    
                e.printStackTrace();
            }
        }

        if(!filedownload.exists()) {
                filedownload.mkdirs();
        }

        Filenametxt(filePath+Filenamerecorder,ProjectName1+"\r\n"+DueData1+"\r\n"+state1 + "\r\n");
            return fileuploads+fileName;
    }
    //write the information in the recorder.txt//
    public static void Filenametxt(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //upload the file to the amazon cloud//
    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(filePath);

        util = new Util();
        transferUtility = util.getTransferUtility(this);
        TransferObserver observer = transferUtility.upload(
                file.getName(),
                file
        );
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED.equals(state)) {
                    deleteFile(data);
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
    
    //write the project information that users create to the file//
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        makeFilePath(filePath, fileName);
        String strFilePath = filePath+fileName;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(strcontent);
            bw.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save:
                data=initData();
                beginUpload(data);
                finish();
                break;

            case R.id.back:
                finish();
                break;


        }
    }
    //delete the file after file upload//
    public boolean deleteFile(String sPath) {
        Boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

}

