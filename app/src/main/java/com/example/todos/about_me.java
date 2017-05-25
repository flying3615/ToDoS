package com.example.todos;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class about_me extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        TextView tv_me = (TextView) findViewById(R.id.aboutme);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        int SDK_INT = android.os.Build.VERSION.SDK_INT;

        tv_me.setText("This is made by Yufei Liu (16441834) \n\n"+"SDK:"+SDK_INT+"\n Build Version: "+version+"\n App Info: "+pInfo.applicationInfo);
    }
}
