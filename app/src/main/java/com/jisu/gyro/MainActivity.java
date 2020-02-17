package com.jisu.gyro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {

    private long lastTime;

    private float x, y, z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    private File recordFile;
    private FileWriter writer;
    private int cnt=0;

    private boolean isFileOk() {
        return recordFile != null && writer != null;
    }

    private boolean openFile() {
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd-HH-mm-ss");
        Date time = new Date();
        String format_time1 = format1.format(time.getTime());

        try {
            recordFile = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/"+ format_time1.toString()+".csv");
            writer = new FileWriter(recordFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean closeFile() {
        if (isFileOk()) {
            try {
                writer.close();
                Toast.makeText(this, "성공 ! "+recordFile.getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                writer = null;
                recordFile = null;
            }

            return true;
        } else {
            return true;
        }
    }


    private void startRecord() {
        openFile();
        String text = "Time,x,y,z";

        try {
            writer.write(text + "\r\n");
        } catch (Exception e) {
            //Toast.makeText(this, "꾸앵 " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerormeterSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void finishRecord() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);

        closeFile();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/camdata"); // 저장 경로
// 폴더 생성


        //FileWriter fw = null ;
        Button a_start = (Button)findViewById(R.id.a_start);
        Button.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cnt%2==0)
                {
                    startRecord();
                }
                else
                {
                    finishRecord();
                }
                cnt++;
            }
        };
        a_start.setOnClickListener(clickListener);
    }

   /* @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isFileOk()) {
            Toast.makeText(this, "WTF", Toast.LENGTH_SHORT).show();
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String text = timestamp+","+x+","+y+","+z;

                try {
                    writer.write(text + "\r\n");
                } catch (Exception e) {
                    Toast.makeText(this, "이거는 있을수가 없는 일이야 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        }

    }
}
