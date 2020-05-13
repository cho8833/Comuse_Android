package com.example.comuse.Activity;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.comuse.DataManager.ScheduleDataManager;
import com.example.comuse.R;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__add_time);

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

        Intent intent = getIntent();
        get = (Schedule)intent.getSerializableExtra("get");
        try {
            get.getProfessorName();
            editUI();
        } catch (NullPointerException e) {
            addUI();
        }
    }

    private void editUI() {
        titleEdit.setText(get.getClassTitle());

        picker_start.setHour(get.getStartTime().getHour());
        picker_start.setMinute(get.getStartTime().getMinute());
        picker_end.setHour(get.getEndTime().getHour());
        picker_end.setMinute(get.getEndTime().getMinute());

        day_spinner.setSelection(get.getDay());
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
                    ScheduleDataManager.updateScheduleData(newData,documentName);
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
                    ScheduleDataManager.addScheduleData(newData);
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
    private Boolean checkTimeValid(int start_hour, int start_min, int end_hour, int end_min, int day, String title) {
        if(title.isEmpty() == true) {
            return false;
        }
        // 시작 시간과 종료 시간이 알맞은지 검사
        if((start_hour > end_hour) || (start_hour == end_hour && start_min >= end_min)) {
            return false;
        }

        // 다른 scheduledata와 비교하여 겹치면 edit 불가
        for(Schedule scheduleData : ScheduleDataManager.schedules) {
            if(scheduleData.getDay() == day) {
                        /* editData의 endTime과 비교데이터의 startTime 비교
                         editData의 startTime과 비교데이터의 endTime 비교 */
                if(((scheduleData.getStartTime().getHour() == end_hour) && (scheduleData.getEndTime().getMinute()< end_min)) ||
                        ((scheduleData.getEndTime().getHour() == start_hour) && (scheduleData.getEndTime().getMinute() > start_min))) {
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
