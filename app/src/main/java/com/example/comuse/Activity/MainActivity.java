package com.example.comuse.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.comuse.DataManager.MemberDataManager;
import com.example.comuse.DataManager.ScheduleDataManager;
import com.example.comuse.Fragment.MemberFragment;
import com.example.comuse.Fragment.SettingsFragment;
import com.example.comuse.Fragment.TimeTableFragment;
import com.example.comuse.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private MemberFragment memberFragment;
    private TimeTableFragment timeTableFragment;
    private SettingsFragment settingsFragment;
    public static MemberDataManager memberDataManager;
    public static ScheduleDataManager scheduleDataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fragmentManager = getSupportFragmentManager();
        memberFragment = new MemberFragment();
        timeTableFragment = new TimeTableFragment();
        settingsFragment = new SettingsFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout,memberFragment).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.bottomMenuItem_Member:

                        transaction.replace(R.id.frameLayout, memberFragment).commit();

                        break;
                    case R.id.bottomMenuItem_TimeTable:

                        transaction.replace(R.id.frameLayout, timeTableFragment).commit();
                        break;
                    case R.id.bottomMenuItem_Settings:

                        transaction.replace(R.id.frameLayout, settingsFragment).commit();
                        break;
                }
                return true;
            }
        });
    }
}
