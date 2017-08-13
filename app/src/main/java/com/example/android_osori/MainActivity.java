package com.example.android_osori;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            //다른 앱 위에 표시 허용
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

            startActivityForResult(intent,CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }else{// 이미 허용되어 있는 경우
            initializeView();
        }
        //setContentView(R.layout.activity_main);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION){//누가 불렀는지 알기 위해서
            if(resultCode == RESULT_OK){//허용시 result_ok
                initializeView();
            }
            else{
                Toast.makeText(this,"Permission is denied",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void initializeView(){
        startService(new Intent(this, FloatingViewService.class));
        finish();
    }
}
