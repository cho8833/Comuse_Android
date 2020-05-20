package com.example.comuse.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.comuse.DataManager.MemberDataViewModel;
import com.example.comuse.DataManager.ScheduleDataViewModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewModel 의 데이터 통신 활성화
        ViewModelProvider.Factory memberFactory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MemberDataViewModel();
            }
        };
        ViewModelProvider memberViewModelProvider = new ViewModelProvider(getViewModelStore(),memberFactory);
        MemberDataViewModel memberViewModel = memberViewModelProvider.get(MemberDataViewModel.class);
        memberViewModel.getMemberData(MainActivity.this);
        memberViewModel.getMembers();
        ViewModelProvider.Factory scheduleFactory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ScheduleDataViewModel();
            }
        };
        ViewModelProvider scheduleViewModelProvider = new ViewModelProvider(getViewModelStore(),scheduleFactory);
        ScheduleDataViewModel scheduleDataViewModel = scheduleViewModelProvider.get(ScheduleDataViewModel.class);
        scheduleDataViewModel.getSchedules();

        // Fragment Setting
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
    public static void generateSimpleAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
