package com.example.android_osori;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by mypc on 2017-07-16.
 */

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private LinearLayout menuBox;
    private Button btn1, btn2, btn3;
    private ImageView icon;
    private int mState=0;

    private Timer timer;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //this가 runnable이 되지 않기 위해서 floatingviewservice.this로 함
            switch (mState) {
                case 0:
                    Glide.with(FloatingViewService.this).load(R.drawable.osori_ji).into(icon);
                    break;
                case 1:
                    Glide.with(FloatingViewService.this).load(R.drawable.osori_1).into(icon);
                    break;
                case 2:
                    Glide.with(FloatingViewService.this).load(R.drawable.osori_2).into(icon);
                    break;
            }
            mState = (mState + 1) % 3;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT; //Initially view will be added to top-left corner
//        params.x=0;
//        params.y =100;

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);//inflate 는 layout에 있는 것을 view로 리턴
        menuBox = (LinearLayout) mFloatingView.findViewById(R.id.menu);
        btn1 = (Button) mFloatingView.findViewById(R.id.btn1);
        btn2 = (Button) mFloatingView.findViewById(R.id.btn2);
        btn3 = (Button) mFloatingView.findViewById(R.id.btn3);
        icon = (ImageView) mFloatingView.findViewById(R.id.icon);

        icon.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X& Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return false;

                }
                return false;

            }
        });

        timer= new Timer();

        //timer 에 새로운 timerTask를 부여한다.
        //100ms 이후 실행하며 20ms 주기로 run method가 실행된다.->x
        //15분 이후 1번째 움짤
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(getMainLooper()).post(runnable);
            }
        }, 0, 9000);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (menuBox.getVisibility() == View.VISIBLE) {
                    menuBox.setVisibility(View.GONE);
                } else {
                    menuBox.setVisibility(View.VISIBLE);
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //END버튼 클릭시 어플 종료
                Log.d("TAG", "RESPONSE:ONCLICK");
                stopSelf();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GROW 버튼 클릭시 아이콘 크기 커짐.
                Log.d(TAG, "onClick mFloatingView is done");
                ViewGroup.LayoutParams viewParam = icon.getLayoutParams();
                Log.d(TAG, "viewParam width:" + viewParam.width + " height:" + viewParam.height);
                viewParam.height += 10;
                viewParam.width += 10;
                icon.setLayoutParams(viewParam);
//
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change 버튼 클릭시 아이콘 변경
                Log.d("TAG", "RESPONSE:ONCLICK");
                timer.cancel();
                mState=(mState+1)%3;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Handler(getMainLooper()).post(runnable);
                    }
                }, 0, 9000);



//
            }
        });

        // Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "DESTROY");
        if (mFloatingView != null)
            mWindowManager.removeView(mFloatingView);
        if(timer!=null){
            timer.cancel();
        }
    }

}
