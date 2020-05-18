package com.example.comuse;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.comuse.Fragment.HandleTimeDialogFragment;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

import static com.example.comuse.DataManager.FirebaseVar.user;

/*
    TimeTable 내의 Schedule(Sticker)을 터치했을 경우 호출되는 콜백 함수
 */
public class ScheduleOnClickListener implements TimetableView.OnStickerSelectedListener {
    Context context;
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
        Schedule schedule = schedules.get(0);
        // schedule class 의 professorName 과 사용자의 uid 가 같을 경우에만 HandleTimeDialog 생성
        if(!(schedule.getProfessorName().equals(user.getUid()))) {
            return;
        }
        HandleTimeDialogFragment dialog = new HandleTimeDialogFragment(context,schedule);
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        dialog.show(fm,"handleTimeDailog");
    }
}
