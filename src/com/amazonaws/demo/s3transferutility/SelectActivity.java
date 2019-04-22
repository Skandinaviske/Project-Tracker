package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.Toast;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static com.amazonaws.auth.policy.actions.S3Actions.DeleteObject;

/*
It is the modify project information interface.
Users can modify the information that they used to save or delete the project
 */
public class SelectActivity extends Activity implements View.OnClickListener{

    public EditText ProjectName;
    public EditText CourseTitle;
    public EditText CourseNumber;
    public EditText InstructorName;
    public EditText ProjectNumber;
    public EditText ProjectDescription;
    public EditText Duedata;
    private Button s_delete,s_back,s_save;

    public RadioGroup status;
    public RadioButton yes;
    public RadioButton no;

    String data;
    static Util util;
    static TransferUtility transferUtility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select);
        CourseTitle = (EditText) findViewById(R.id.CourseTitle);
        CourseNumber = (EditText) findViewById(R.id.CourseNumber);
        InstructorName = (EditText) findViewById(R.id.InstructorName);
        ProjectNumber = (EditText) findViewById(R.id.ProjectNumber);
        ProjectDescription = (EditText) findViewById(R.id.projectDescription);
        Duedata = (EditText) findViewById(R.id.Duedata);
        ProjectName = (EditText) findViewById(R.id.ProjectName);

        status = (RadioGroup) findViewById(R.id.radio);
        yes = (RadioButton) findViewById(R.id.y);
        no = (RadioButton) findViewById(R.id.n);


        s_delete=(Button)findViewById(R.id.s_delete);
        s_back=(Button)findViewById(R.id.s_back);
        s_save=(Button)findViewById(R.id.s_save);

        s_back.setOnClickListener(this);
        s_delete.setOnClickListener(this);
        s_save.setOnClickListener(this);

        //read the information saved in the amazon cloud and show the information//
        String title="";
        title=getIntent().getStringExtra("Xianyang");
        try {
            String projectname = readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title + ".txt", 0);
            String coursetilte=readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title+".txt",1);
            String courseNumber=readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title+".txt",2);
            String instructorName=readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title+".txt",3);
            String projectNumber=readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title+".txt",4);
            String duedata=readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title+".txt",5);
            String projectDescription=readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title+".txt",6);
            String status1 = readLineVarFile(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/" + title + ".txt", 7);
            ProjectName.setText(projectname);
            CourseTitle.setText(coursetilte);
            CourseNumber.setText(courseNumber);
            InstructorName.setText(instructorName);
            ProjectNumber.setText(projectNumber);
            ProjectDescription.setText(projectDescription);
            Duedata.setText(duedata);
            if (status1.equals("completed") ){
                status.check(yes.getId());
            }
            if (status1.equals("uncompleted")){
                status.check(no.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //save the information modified by the user into a text file and update the recorder.txt//
    private String initData(){
        String ProjectName1 = ProjectName.getText().toString();
        String CourseTitle1=CourseTitle.getText().toString();
        String CourseNumber1=CourseNumber.getText().toString();
        String InstructorName1=InstructorName.getText().toString();
        String ProjectNumber1=ProjectNumber.getText().toString();
        String ProjectDescription1=ProjectDescription.getText().toString();
        String DueData1=Duedata.getText().toString();
        String state1 = "";
        if (yes.isChecked()){
            state1 = "completed";
        }
        if (no.isChecked()){
            state1 = "uncompleted";
        }

        String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/download/";
        String recorderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScheduleApp/";
        String fileName = ProjectName1+".txt";
        String content=
                ProjectName1+"\r\n"+CourseTitle1+"\r\n"+CourseNumber1+"\r\n"+InstructorName1+"\r\n"+ProjectNumber1+"\r\n"+DueData1+"\r\n"+ ProjectDescription1+ "\r\n" + state1;

        writeTxtToFile(content, filePath, fileName);
        String Filenamerecorder="Recorder.txt";
        File file=new File(recorderPath+Filenamerecorder);

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Filenametxt(recorderPath+Filenamerecorder,ProjectName1+"\r\n"+DueData1+"\r\n"+state1 + "\r\n");

        String title=getIntent().getStringExtra("Xianyang");
        delete(Environment.getExternalStorageDirectory().toString() +title+".txt");
        return filePath+fileName;
    }
    //modify the content of the recorder.txt//
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
    //modify the information of project in the file//
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

    //delete the file on the amazon cloud//
    public void delete(final String objectName){

        int position= Integer.parseInt(getIntent().getStringExtra("position"));
        util = new Util();
        util.deleteFileFromS3Bucket(objectName,this);
        try{
            File file = new File(objectName);
            if(file.delete()){
                System.out.println(file.getName() + "deleted");
            }else{
                System.out.println("failed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            deleteLineVarFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScheduleApp/"+"Recorder.txt",(position*3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s_delete:
                String title=getIntent().getStringExtra("Xianyang");
                delete(Environment.getExternalStorageDirectory().toString() + "/ScheduleApp/download/"+title+".txt");
                finish();
                break;


            case R.id.s_back:
                finish();
                break;

            case R.id.s_save:
                data=initData();
                beginUpload(data);
                finish();
                break;

        }
    }
    //upload the modified file to the amazon cloud//
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
    }
    //delete the project information in recorder.txt by line//
    static void deleteLineVarFile(String fileName, int lineNumber) throws IOException {
        final List<String> lines = new LinkedList<>();
        final Scanner reader = new Scanner(new FileInputStream(fileName), "UTF-8");
        while(reader.hasNextLine())
            lines.add(reader.nextLine());
        reader.close();
        assert lineNumber >= 0 && lineNumber <= lines.size() - 1;
        for (int i = 0; i < 3; i ++){
            lines.remove(lineNumber);
        }
        final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
        for(final String line : lines)
            writer.write(line+"\r\n");
        writer.flush();
        writer.close();
    }
    //read file contents by line//
    static String readLineVarFile(String fileName, int lineNumber) throws IOException {

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            for (int i = 0; i < lineNumber; i++)
                br.readLine();
            line = br.readLine();
        }
        return  line;
    }

}
