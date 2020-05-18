package com.example.comuse.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.comuse.DataManager.FirebaseVar;
import com.example.comuse.DataManager.ScheduleDataViewModel;
import com.example.comuse.R;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
    Edit/AddTimeActivity 는 TimeTableFragment 의 addScheduleButton 을 클릭했을 때, HandleTimeDialog 의 Edit 버튼을 클릭했을 때 생성된다.
    addScheduleButton 을 클릭했을 때에는 Schedule 타입의 get 객체가 전달되지 않아 null 이고, addUI 함수가 호출된다.
    HandleTimeDialog 의 Edit 버튼을 클릭했을 때에는 get 객체에 클릭된 Schedule 의 데이터가 전달되고 editUI 함수가 호출된다.
 */
public class Edit_AddTimeActivity extends AppCompatActivity {
    TimePicker picker_start;
    TimePicker picker_end;
    Spinner day_spinner;
    int day;
    int start_hour;
    int start_min;
    int end_hour;
    int end_min;
    Schedule get;
    TextView confirmButton;
    TextView cancelButton;
    EditText titleEdit;
    ScheduleDataViewModel scheduleDataViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__add_time);

        // get ViewModel
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication());
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        scheduleDataViewModel = provider.get(ScheduleDataViewModel.class);

        //find Views
        confirmButton = findViewById(R.id.editTime_confirm);
        cancelButton = findViewById(R.id.editTime_cancel);
        picker_start = findViewById(R.id.time_picker_start);
        picker_end = findViewById(R.id.time_picker_end);
        titleEdit = findViewById(R.id.editText3);

        //picker settings
        setTimePickerInterval(picker_start);
        setTimePickerInterval(picker_end);
        ArrayList<String> DAY_INDEX = new ArrayList<>();
        DAY_INDEX.add("월");
        DAY_INDEX.add("화");
        DAY_INDEX.add("수");
        DAY_INDEX.add("목");
        DAY_INDEX.add("금");
        DAY_INDEX.add("토");
        DAY_INDEX.add("일");

        //day spinner settings
        day_spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Edit_AddTimeActivity.this, R.layout.day_spinner_dialog_item, R.id.text_spinner, DAY_INDEX);
        day_spinner.setAdapter(adapter);
        day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // get 데이터 받기
        Intent intent = getIntent();
        get = (Schedule) intent.getSerializableExtra("get");
        try {
            get.getProfessorName();
            editUI();
        } catch (NullPointerException e) {
            addUI();
        }
    }

    //get 객체를 받아와 view 들의 초기 표시 데이터 초기화
    private void editUI() {
        // 초기 데이터로 view 초기화
        titleEdit.setText(get.getClassTitle());
        picker_start.setHour(get.getStartTime().getHour());
        picker_start.setMinute(get.getStartTime().getMinute());
        picker_end.setHour(get.getEndTime().getHour());
        picker_end.setMinute(get.getEndTime().getMinute());
        day_spinner.setSelection(get.getDay());

        // button 초기
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = titleEdit.getText().toString();
                //Get Time
                start_hour = picker_start.getHour();
                start_min = picker_start.getMinute()*30;
                end_hour = picker_end.getHour();
                end_min = picker_end.getMinute()*30;
                day = day_spinner.getSelectedItemPosition();
                if(checkTimeValid(start_hour,start_min,end_hour,end_min,day,title)) {
                    Schedule newData = new Schedule();
                    newData.setClassTitle(title);
                    newData.setEndTime(new Time(end_hour,end_min));
                    newData.setStartTime(new Time(start_hour,start_min));
                    newData.setDay(day);
                    newData.setProfessorName(get.getProfessorName());
                    String documentName = ""+get.getStartTime().getHour()+get.getStartTime().getMinute()+get.getEndTime().getHour()+get.getEndTime().getMinute()+get.getDay();
                    scheduleDataViewModel.updateScheduleData(newData,documentName);
                    finish();
                } else {
                    //TimeInvalid
                    return;
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 화면에 표시할 초기 데이터가 없기 때문에 button init
    private void addUI() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEdit.getText().toString();
                start_hour = picker_start.getHour();
                start_min = picker_start.getMinute()*30;
                end_hour = picker_end.getHour();
                end_min = picker_end.getMinute()*30;
                day = day_spinner.getSelectedItemPosition();
                if(checkTimeValid(start_hour,start_min,end_hour,end_min,day,title)) {
                    Schedule newData = new Schedule();
                    newData.setProfessorName(FirebaseVar.user.getUid());
                    newData.setDay(day);
                    newData.setStartTime(new Time(start_hour,start_min));
                    newData.setEndTime(new Time(end_hour,end_min));
                    newData.setClassTitle(title);
                    scheduleDataViewModel.addScheduleData(newData);
                    finish();
                } else {
                    //TimeInvalid
                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //MARK: -Privates
    private Boolean checkTimeValid(int start_hour, int start_min, int end_hour, int end_min, int day, String title) {
        if(title.isEmpty() == true) {
            return false;
        }
        // 시작 시간과 종료 시간이 알맞은지 검사
        if((start_hour > end_hour) || (start_hour == end_hour && start_min >= end_min)) {
            return false;
        }

        // 다른 scheduledata와 비교하여 겹치면 edit 불가
        for(Schedule scheduleData : scheduleDataViewModel.schedules) {
            if(scheduleData.getDay() == day) {
                        /* editData의 endTime과 비교데이터의 startTime 비교
                         editData의 startTime과 비교데이터의 endTime 비교 */
                if(((scheduleData.getStartTime().getHour() == end_hour) && (scheduleData.getEndTime().getMinute()< end_min)) ||
                        ((scheduleData.getEndTime().getHour() == start_hour) && (scheduleData.getEndTime().getMinute() > start_min))) {
                    return false;
                }
                if ((scheduleData.getEndTime().getHour() < end_hour) || (scheduleData.getStartTime().getHour() > start_hour)) {
                    return false;
                }
            }
        }
        return true;
    }
    // timepicker의 minutePicker의 분 간격을 30분으로 설정
    private void setTimePickerInterval(TimePicker timePicker) {
        int TIME_PICKER_INTERVAL = 30;
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");

            Field field_min = classForid.getField("minute");
            NumberPicker mMinuteSpinner = (NumberPicker) timePicker
                    .findViewById(field_min.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues_min = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues_min.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(displayedValues_min
                    .toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
