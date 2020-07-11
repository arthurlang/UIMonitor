package com.lj.framemonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lj.framemonitor.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.testAnim);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, getCategory());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.category_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public long mStop;
            public long mStart;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    mStop = SystemClock.elapsedRealtime();
                    Log.e("MainActivity","-----onScroll time="+(mStop-mStart));
                }else if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    mStart = SystemClock.elapsedRealtime();
                }

            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mHandler.sendEmptyMessageDelayed(0,1000);
            }
        };
        Message msg = new Message();
        mHandler.sendEmptyMessageDelayed(1,1000);
    }

    public void onTestAnimator(View view){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_out);
        animation.setDuration(5000);
        animation.setAnimationListener(

                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
/*                        if(mTextView != null){
                            mTextView.startAnimation(animation);
                        }*/
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


        if(mTextView != null){
            mTextView.startAnimation(animation);
        }

    }

    private List getCategory() {
        List dataList = new ArrayList();
        for(int i = 0; i<100; i++){
            dataList.add("item"+i);
        }
        return dataList;
    }
}
