package com.example.comuse.DataManager;

import android.util.Log;

import com.example.comuse.UpdateUI;
import com.github.tlaabs.timetableview.Schedule;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ScheduleDataManager {
    //MARK: -My Schedule Control Methods
    public static void addScheduleData(final Schedule new_time) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                final String document = ""+new_time.getStartTime().getHour()+new_time.getStartTime().getMinute()+new_time.getEndTime().getHour()+new_time.getEndTime().getMinute()+new_time.getDay();
                FirebaseVar.db.collection("TimeTable")
                        .document(document)
                        .set(new_time)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
            }
        }
    }
    public static void removeScheduleData(final Schedule remove) {
        if (FirebaseVar.user != null && FirebaseVar.db != null) {
            final String document = ""+remove.getStartTime().getHour()+remove.getStartTime().getMinute()+remove.getEndTime().getHour()+remove.getEndTime().getMinute()+remove.getDay();
            FirebaseVar.db.collection("TimeTable")
                    .document(document)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
        }
    }
    public static void updateScheduleData(final Schedule update, String getDocumentName) {
        if (FirebaseVar.user != null && FirebaseVar.db != null) {
            final String document = ""+update.getStartTime().getHour()+update.getStartTime().getMinute()+update.getEndTime().getHour()+update.getEndTime().getMinute()+update.getDay();
            FirebaseVar.db.collection("TimeTable")
                    .document(getDocumentName)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
            FirebaseVar.db.collection("TimeTable")
                    .document(document)
                    .set(update)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
        }
    }
    //MARK: -Members Control Methods
    public static ArrayList<Schedule> schedules = new ArrayList<>();
    public static void getSchedules(final UpdateUI updateUI) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.schedulesListener = FirebaseVar.db.collection("TimeTable")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "listen:error", e);
                                    return;
                                }
                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    Schedule schedule= dc.getDocument().toObject(Schedule.class);
                                    switch (dc.getType()) {
                                        case ADDED:
                                            schedules.add(schedule);
                                            break;
                                        case REMOVED:
                                            int position = -1;
                                            for(Schedule data : schedules) {
                                                if(compareSchedule(data,schedule)) {
                                                    position = schedules.indexOf(data);
                                                    break;
                                                }
                                            }
                                            try {
                                                schedules.remove(position);
                                            } catch (ArrayIndexOutOfBoundsException indexException) {
                                            }
                                            break;
                                    }
                                }
                                //reload timeTable
                                if (updateUI != null) {
                                    updateUI.updateUI();
                                }
                            }
                        });
            }
        }
    }
    public static Boolean compareSchedule(Schedule s1, Schedule s2) {
        if(s1.getStartTime().getHour()==s2.getStartTime().getHour() && s1.getStartTime().getMinute()==s2.getStartTime().getMinute() && s1.getDay()==s2.getDay() &&
                s1.getEndTime().getHour()==s2.getEndTime().getHour() && s1.getEndTime().getMinute()==s2.getEndTime().getMinute()) {
            return true;
        } else {
            return false;
        }
    }
}
