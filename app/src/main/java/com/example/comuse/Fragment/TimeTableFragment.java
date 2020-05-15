package com.example.comuse.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.comuse.DataManager.FirebaseVar;
import com.example.comuse.DataManager.ScheduleDataViewModel;
import com.example.comuse.Activity.Edit_AddTimeActivity;
import com.example.comuse.R;
import com.example.comuse.ScheduleOnClickListener;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;




public class TimeTableFragment extends Fragment {
    Context context;
    TimetableView timeTable;
    ScheduleOnClickListener onClickListener;
    Button addScheduleButton;
    ScheduleDataViewModel scheduleDataViewModel;
    public TimeTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(((AppCompatActivity)context).getApplication());
        scheduleDataViewModel = new ViewModelProvider((ViewModelStoreOwner) context,factory).get(ScheduleDataViewModel.class);
        onClickListener = new ScheduleOnClickListener();
        onClickListener.setContext(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_time_table, container, false);

        timeTable = mView.findViewById(R.id.timetable);
        addScheduleButton = mView.findViewById(R.id.addSchedule_btn);
        timeTable.setOnStickerSelectEventListener(onClickListener);
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseVar.user != null && FirebaseVar.db != null) {
                    Intent intent = new Intent(context, Edit_AddTimeActivity.class);
                    intent.putExtra("scheduleDatas", scheduleDataViewModel.schedules);
                    startActivity(intent);
                }

            }
        });
        if (FirebaseVar.schedulesListener == null) {
            scheduleDataViewModel.getSchedules();
        }
        scheduleDataViewModel.schedulesLiveData.observe((LifecycleOwner) context, new Observer<ArrayList<Schedule>>() {
            @Override
            public void onChanged(ArrayList<Schedule> schedules) {
                timeTable.removeAll();
                for(Schedule schedule : schedules) {
                    ArrayList<Schedule> append = new ArrayList<>();
                    append.add(schedule);
                    timeTable.add(append);
                }
            }
        });
        return mView;
    }
}
