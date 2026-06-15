package com.goalday.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.goalday.app.ui.HabitFragment;
import com.goalday.app.ui.DiaryFragment;
import com.goalday.app.ui.ScheduleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();
            if (id == R.id.tab_schedule) {
                f = new ScheduleFragment();
            } else if (id == R.id.tab_diary) {
                f = new DiaryFragment();
            } else if (id == R.id.tab_habit) {
                f = new HabitFragment();
            }
            if (f != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.tab_schedule);
        }
    }
}
