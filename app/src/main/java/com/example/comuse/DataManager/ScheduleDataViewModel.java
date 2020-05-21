package com.example.comuse.DataManager;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.tlaabs.timetableview.Schedule;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;
/*
    Class Schedule {
        String classTitle="";
        String classPlace="";
        String professorName="";
        private int day = 0;
        private Time startTime;
        private Time endTime;
    }
    Class Time {
        private int hour = 0;
        private int minute = 0;
    }
    TimeTable 의 데이터 모델은 Schedule class 이다. FireStore/TimeTable Collection 에 저장되고 문서 이름은
    startTimeHour|startTimeMinute|endTimeHour|endTimeMinute|Day 9자리 숫자로 저장된다 ex) 203021304 = 20:30 ~ 21:30 금요일
    Day 인덱스는 월(0) ~ 일(6) 까지이다.
    앱 내에서는 schedule 의 onclickListener 를 통해 자신이 작성한 schedule 만 수정할 수 있다.

    classTitle : schedule 제목 저장, 시간표에 표시되는 문자열
    classPlace : 시간표에 표시될 때 classTitle 밑에 표시되는 문자열, 현재는 쓰지않아 null 로 저장 (추후에 schedule 작성자 이름을 저장하여 시간표에 표시 고려중)
    professorName : 작성자의 email 저장, 자신이 작성한 schedule 인지 검사하기 위해 사용
    day : day index 저장
    startTime : schedule 시작 시간 저장
    endTime : schedule 종료 시간 저장
 */

/*
    Schedule Data 를 추가/수정하더라도 TimeTable View 에 따로 notify 해주지 않아도 된다.
    -> Snapshot listener 가 FireStore/TimeTable Collection 내의 데이터가 추가/수정/삭제 되면 실시간으로TimeTableView 에 notify 해준다.
*/
public class ScheduleDataViewModel extends ViewModel {
    //MARK: -My Schedule Control Methods

    // FireStore/TimeTable Collection 에 document 생성
    public void addScheduleData(final Schedule new_time) {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                final String document = ""+new_time.getStartTime().getHour()+new_time.getStartTime().getMinute()+new_time.getEndTime().getHour()+new_time.getEndTime().getMinute()+new_time.getDay();
                FirebaseVar.db.collection("TimeTable")
                        .document(document)
                        .set(new_time)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // User 에게 Data add 에 대한 notify(Dialog, Toast)
                            }
                        });
            }
        }
    }
    //FireStore/TimeTable Collection 의 document 삭제
    public void removeScheduleData(final Schedule remove) {
        if (FirebaseVar.user != null && FirebaseVar.db != null) {
            final String document = ""+remove.getStartTime().getHour()+remove.getStartTime().getMinute()+remove.getEndTime().getHour()+remove.getEndTime().getMinute()+remove.getDay();
            FirebaseVar.db.collection("TimeTable")
                    .document(document)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // User 에게 Data remove 에 대한 notify(Dialog, Toast)
                        }
                    });
        }
    }
    //FireStore/TimeTable Collection 의 특정 document 의 property 수정
    public void updateScheduleData(final Schedule update, String getDocumentName) {
        /*
            Document 의 이름은 데이터의 시작시간, 종료시간, 요일로 저장된다.
            데이터가 수정되면 document 의 이름도 수정해야하는데 FireStore 은 이 기능을 지원하지 않는 것으로 확인된다.
            따라서 업데이트되기 전의 document 를 삭제하고 업데이트된 document 를 추가하는 작업이 필요하다.
         */
        if (FirebaseVar.user != null && FirebaseVar.db != null) {
            final String document = ""+update.getStartTime().getHour()+update.getStartTime().getMinute()+update.getEndTime().getHour()+update.getEndTime().getMinute()+update.getDay();
            // 업데이트 되기 전의 document 삭제
            FirebaseVar.db.collection("TimeTable")
                    .document(getDocumentName)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
            // 업데이트 된 document 추가
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

    //MARK: -All schedules control methods
    public MutableLiveData<ArrayList<Schedule>> schedulesLiveData = new MutableLiveData<>();
    public ArrayList<Schedule> schedules = new ArrayList<>();
    /*
        모든 Schedule 은 SnapShot 리스너를 통해 실시간으로 View(TimeTableView) 에 파급된다.
        TimeTableFragment 내의 observer 가 schedulesLiveData 를 구독하여 DocumentChange 가 발생하면 schedules 데이터를 수정하고
        schedulesLiveData.setValue 를 통해 notify 한다.
     */
    public void getSchedules() {
        if (FirebaseVar.user != null) {
            if (FirebaseVar.db != null) {
                FirebaseVar.schedulesListener = FirebaseVar.db.collection("TimeTable")  // snapshot listener 분리
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
                                // notify LiveData
                                schedulesLiveData.setValue(schedules);
                            }
                        });
            }
        }
    }

    //MARK: -Privates
    public Boolean compareSchedule(Schedule s1, Schedule s2) {
        if(s1.getStartTime().getHour()==s2.getStartTime().getHour() && s1.getStartTime().getMinute()==s2.getStartTime().getMinute() && s1.getDay()==s2.getDay() &&
                s1.getEndTime().getHour()==s2.getEndTime().getHour() && s1.getEndTime().getMinute()==s2.getEndTime().getMinute()) {
            return true;
        } else {
            return false;
        }
    }
}
