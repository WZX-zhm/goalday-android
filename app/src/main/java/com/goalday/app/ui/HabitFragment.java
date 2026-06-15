package com.goalday.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goalday.app.R;
import com.goalday.app.db.GoalDayDb;
import com.goalday.app.model.DateUtil;
import com.goalday.app.model.Habit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HabitFragment extends Fragment {

    private GoalDayDb db;
    private RecyclerView rv;
    private HabitAdapter adapter;
    private final List<Habit> data = new ArrayList<>();
    private TextView tvEmpty;

    private static final String[] ICONS = {
            "📚", "🏃", "💧", "🧘", "✍️", "🌅", "🌙", "🥗", "💪", "🎯",
            "🌍", "🎨", "🎵", "💻", "☕", "🍎", "🚶", "🚴", "🏋️", "🧠"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new GoalDayDb(requireContext());

        rv = view.findViewById(R.id.rv_habits);
        tvEmpty = view.findViewById(R.id.tv_empty);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HabitAdapter();
        rv.setAdapter(adapter);

        view.findViewById(R.id.fab_add_habit).setOnClickListener(v -> showAddDialog());
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        data.clear();
        data.addAll(db.getAllHabits());
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddDialog() {
        View root = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null);
        EditText et = root.findViewById(R.id.et_habit_name);
        GridLayout grid = root.findViewById(R.id.grid_icons);
        final String[] selectedIcon = {ICONS[0]};

        for (int i = 0; i < ICONS.length; i++) {
            TextView tv = new TextView(requireContext());
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dp(56);
            lp.height = dp(56);
            lp.setMargins(dp(6), dp(6), dp(6), dp(6));
            tv.setLayoutParams(lp);
            tv.setText(ICONS[i]);
            tv.setTextSize(24);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.bg_mood_unselected);
            final int idx = i;
            tv.setOnClickListener(v -> {
                selectedIcon[0] = ICONS[idx];
                // 刷新选中态
                for (int j = 0; j < grid.getChildCount(); j++) {
                    TextView ch = (TextView) grid.getChildAt(j);
                    if (j == idx) {
                        ch.setBackgroundColor(0xFF5C6BC0);
                    } else {
                        ch.setBackgroundResource(R.drawable.bg_mood_unselected);
                    }
                }
            });
            grid.addView(tv);
        }
        // 默认选中第一个
        if (grid.getChildCount() > 0) grid.getChildAt(0).setBackgroundColor(0xFF5C6BC0);

        new AlertDialog.Builder(requireContext())
                .setView(root)
                .setPositiveButton(R.string.confirm, (d, w) -> {
                    String name = et.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(requireContext(), "请输入目标名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.insertHabit(new Habit(name, selectedIcon[0]));
                    refresh();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    // ============== Adapter ==============

    class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_habit, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Habit habit = data.get(position);
            long today = DateUtil.today();
            h.icon.setText(habit.icon);
            h.name.setText(habit.name);

            int streak = habit.getStreak(today);
            h.streak.setText("🔥 连续 " + streak + " 天");

            // 本周进度（周一~周日）
            long monday = DateUtil.mondayOfWeek(today);
            StringBuilder week = new StringBuilder("本周: ");
            StringBuilder weekText = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                long d = DateUtil.toKey(DateUtil.parseTime(monday) + i * 86400L * 1000);
                boolean checked = habit.isCheckedToday(d);
                week.append(checked ? "● " : "○ ");
            }
            h.weekProgress.setText(week.toString());

            boolean checkedToday = habit.isCheckedToday(today);
            h.btnCheck.setText(checkedToday ? "✓ 今日已打卡" : "✓ 打卡");
            h.btnCheck.setEnabled(!checkedToday);
            h.btnCheck.setAlpha(checkedToday ? 0.5f : 1f);
            h.btnCheck.setOnClickListener(v -> {
                habit.addCheckin(today);
                db.updateHabit(habit);
                refresh();
            });
            h.btnDelete.setOnClickListener(v -> {
                db.deleteHabit(habit.id);
                refresh();
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView icon, name, streak, weekProgress;
            Button btnCheck;
            android.widget.ImageButton btnDelete;

            VH(View v) {
                super(v);
                icon = v.findViewById(R.id.tv_habit_icon);
                name = v.findViewById(R.id.tv_habit_name);
                streak = v.findViewById(R.id.tv_streak);
                weekProgress = v.findViewById(R.id.tv_week_progress);
                btnCheck = v.findViewById(R.id.btn_check);
                btnDelete = v.findViewById(R.id.btn_delete_habit);
            }
        }
    }
}
