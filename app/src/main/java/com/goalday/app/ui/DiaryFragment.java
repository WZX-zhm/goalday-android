package com.goalday.app.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.goalday.app.R;
import com.goalday.app.db.GoalDayDb;
import com.goalday.app.model.DateUtil;
import com.goalday.app.model.Diary;
import com.goalday.app.model.ScheduleItem;

import java.util.List;

public class DiaryFragment extends Fragment {

    private GoalDayDb db;
    private long today;
    private String currentMood = "🙂";

    private TextView tvDate, tvCompletedCount, tvCompletedList, tvWordCount, tvSavedHint;
    private EditText etDiary;
    private LinearLayout layoutMoods;
    private Button btnSave;

    private static final String[] MOODS = {"😄", "🙂", "😌", "😴", "🥰", "😟", "😢"};
    private static final int[] MOOD_COLORS = {
            0xFFFFD54F, 0xFF81D4FA, 0xFFA5D6A7, 0xFFB0BEC5,
            0xFFFF8A65, 0xFFCE93D8, 0xFF90CAF9
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new GoalDayDb(requireContext());
        today = DateUtil.today();

        tvDate = view.findViewById(R.id.tv_date);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvCompletedList = view.findViewById(R.id.tv_completed_list);
        tvWordCount = view.findViewById(R.id.tv_word_count);
        tvSavedHint = view.findViewById(R.id.tv_saved_hint);
        etDiary = view.findViewById(R.id.et_diary);
        layoutMoods = view.findViewById(R.id.layout_moods);
        btnSave = view.findViewById(R.id.btn_save_diary);

        tvDate.setText(DateUtil.formatDate(today) + " " + DateUtil.formatWeekCN(today));

        buildMoods();

        Diary d = db.getDiaryByDate(today);
        if (d != null) {
            etDiary.setText(d.content);
            currentMood = d.mood == null ? "🙂" : d.mood;
            updateMoodUI();
        } else {
            currentMood = "🙂";
            updateMoodUI();
        }
        updateWordCount();
        etDiary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateWordCount();
            }
        });
        btnSave.setOnClickListener(v -> save());
        refreshCompletedCard();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCompletedCard();
    }

    private void buildMoods() {
        layoutMoods.removeAllViews();
        for (int i = 0; i < MOODS.length; i++) {
            TextView tv = new TextView(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 8, 8, 8);
            tv.setLayoutParams(lp);
            tv.setText(MOODS[i]);
            tv.setTextSize(28);
            tv.setPadding(16, 8, 16, 8);
            tv.setOnClickListener(v -> {
                currentMood = MOODS[i];
                updateMoodUI();
            });
            tv.setTag(i);
            layoutMoods.addView(tv);
        }
        updateMoodUI();
    }

    private void updateMoodUI() {
        for (int i = 0; i < layoutMoods.getChildCount(); i++) {
            TextView tv = (TextView) layoutMoods.getChildAt(i);
            int idx = (int) tv.getTag();
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            if (MOODS[idx].equals(currentMood)) {
                gd.setColor(MOOD_COLORS[idx]);
            } else {
                gd.setStroke(dp(2), MOOD_COLORS[idx]);
                gd.setColor(0x00000000);
            }
            tv.setBackground(gd);
        }
    }

    private int dp(int v) {
        float d = v * getResources().getDisplayMetrics().density;
        return (int) d;
    }

    private void updateWordCount() {
        String s = etDiary.getText().toString().trim();
        tvWordCount.setText(s.length() + " 字");
    }

    private void save() {
        Diary d = db.getDiaryByDate(today);
        if (d == null) d = new Diary();
        d.date = today;
        d.content = etDiary.getText().toString();
        d.mood = currentMood;
        d.updatedAt = System.currentTimeMillis();
        db.saveDiary(d);
        tvSavedHint.setText("✓ 已保存 " + String.format("%02d:%02d",
                (int) ((System.currentTimeMillis() / 3600000) % 24),
                (int) ((System.currentTimeMillis() / 60000) % 60)));
        tvSavedHint.postDelayed(() -> tvSavedHint.setText(""), 2500);
    }

    private void refreshCompletedCard() {
        int total = db.countAllByDate(today);
        List<ScheduleItem> done = db.getCompletedByDate(today);
        tvCompletedCount.setText("已完成 " + done.size() + " / " + total + " 项");
        if (done.isEmpty()) {
            tvCompletedList.setText("今日还没有完成的事项");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < done.size() && i < 5; i++) {
                ScheduleItem it = done.get(i);
                if (i > 0) sb.append("  ·  ");
                sb.append("• ").append(it.content);
            }
            if (done.size() > 5) sb.append("  …");
            tvCompletedList.setText(sb.toString());
        }
    }
}
