//package com.jsdc.iotpt.util;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import com.google.android.material.tabs.TabLayout;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.logging.Logger;
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    public static final String TAG = "howfor";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        // 红灯，绿灯,黄灯,蓝灯
//        findViewById(R.id.buttonRed).setOnClickListener(this);
//        findViewById(R.id.buttonGreen).setOnClickListener(this);
//        findViewById(R.id.buttonYellow).setOnClickListener(this);
//        findViewById(R.id.buttonBlue).setOnClickListener(this);
//        //继电器
//        findViewById(R.id.buttonHigh).setOnClickListener(this);
//        findViewById(R.id.buttonLow).setOnClickListener(this);
//        //ic 卡演示
//        findViewById(R.id.buttonShowICCard).setOnClickListener(this);
//    }
//
//    //实现OnClickListener接口中的方法
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if(id == R.id.buttonRed) {
//            //红灯
//            set_oem_gpio(32);
//            set_oem_gpio(49);
//            set_oem_gpio(64);
//        }else if(id == R.id.buttonGreen) {
//            //绿灯
//            set_oem_gpio(32);
//            set_oem_gpio(48);
//            set_oem_gpio(65);
//        }else if(id == R.id.buttonYellow) {
//            //黄灯
//            set_oem_gpio(32);
//            set_oem_gpio(49);
//            set_oem_gpio(65);
//        }else if(id == R.id.buttonBlue) {
//            //蓝灯
//            set_oem_gpio(33);
//            set_oem_gpio(48);
//            set_oem_gpio(64);
//        }else if(id == R.id.buttonHigh) {
//            // 继电器
//            set_oem_gpio(81);
//        }else if(id == R.id.buttonLow) {
//            // 继电器
//            set_oem_gpio(80);
//        }else if(id == R.id.buttonShowICCard) {
//            EditText textICCard = findViewById(R.id.textICCard);
//            String icCard = textICCard.getText().toString();
//            if(icCard == ""){
//                Toast.makeText(this, "isEmpty", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, icCard, Toast.LENGTH_LONG).show();
//            }
//        }
//
//
//     }
//
//    public static void set_oem_gpio(int value){
//
//        String mode="io11";
//
//        if(value==0x10)	mode="io10";
//        else if(value==0x11)	mode="io11";
//        else if(value==0x20)	mode="io20";
//        else if(value==0x21)	mode="io21";
//        else if(value==0x30)	mode="io30";
//        else if(value==0x31)	mode="io31";
//        else if(value==0x40)	mode="io40";
//        else if(value==0x41)	mode="io41";
//        else if(value==0x50)	mode="io50";
//        else if(value==0x51)	mode="io51";
//        else if(value==0x60)	mode="io60";
//        else if(value==0x61)	mode="io61";
//
//        Log.d(TAG,"write mode:"+mode);
//        try {
//            FileOutputStream fops = new FileOutputStream("/proc/oem_gpio");
//            fops.write(mode.getBytes());
//            fops.flush();
//            fops.close();
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "found error");
//        } catch (IOException e) {
//            Log.d(TAG, "IO error");
//        }
//    }
//
//}