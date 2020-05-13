package com.example.comuse;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.comuse.Fragment.HandleTimeDialogFragment;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

import static com.example.comuse.DataManager.FirebaseVar.user;

public class ScheduleOnClickListener implements TimetableView.OnStickerSelectedListener {
    Context context;
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
        Schedule schedule = schedules.get(0);
        if(!(schedule.getProfessorName().equals(user.getUid()))) {
            return;
        }
        HandleTimeDialogFragment dialog = new HandleTimeDialogFragment(context,schedule);
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        dialog.show(fm,"handleTimeDailog");
    }
}
