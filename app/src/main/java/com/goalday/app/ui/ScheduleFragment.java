package com.goalday.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goalday.app.R;
import com.goalday.app.db.GoalDayDb;
import com.goalday.app.model.DateUtil;
import com.goalday.app.model.ScheduleItem;
import com.goalday.app.model.TodoItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private GoalDayDb db;
    private long currentDate;     // 当前选中的 yyyyMMdd
    private long currentMonday;   // 当前周的周一

    private TextView tvWeekRange, tvCurrentDay;
    private LinearLayout layoutWeekDays;
    private RecyclerView rvSchedule, rvTodo;
    private EditText etTodoInput;
    private Button btnAddTodo;

    private ScheduleAdapter scheduleAdapter;
    private TodoAdapter todoAdapter;

    private final List<ScheduleItem> scheduleList = new ArrayList<>();
    private final List<TodoItem> todoList = new ArrayList<>();
    private final List<TextView> dayChips = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new GoalDayDb(requireContext());

        currentDate = DateUtil.today();
        currentMonday = DateUtil.mondayOfWeek(currentDate);

        tvWeekRange = view.findViewById(R.id.tv_week_range);
        tvCurrentDay = view.findViewById(R.id.tv_current_day);
        layoutWeekDays = view.findViewById(R.id.layout_week_days);
        rvSchedule = view.findViewById(R.id.rv_schedule);
        rvTodo = view.findViewById(R.id.rv_todo);
        etTodoInput = view.findViewById(R.id.et_todo_input);
        btnAddTodo = view.findViewById(R.id.btn_add_todo);

        scheduleAdapter = new ScheduleAdapter();
        todoAdapter = new TodoAdapter();
        rvSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTodo.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSchedule.setAdapter(scheduleAdapter);
        rvTodo.setAdapter(todoAdapter);

        view.findViewById(R.id.btn_prev_week).setOnClickListener(v -> {
            currentMonday = DateUtil.toKey(DateUtil.parseTime(currentMonday) - 7L * 86400 * 1000);
            rebuildWeekChips();
        });
        view.findViewById(R.id.btn_next_week).setOnClickListener(v -> {
            currentMonday = DateUtil.toKey(DateUtil.parseTime(currentMonday) + 7L * 86400 * 1000);
            rebuildWeekChips();
        });
        btnAddTodo.setOnClickListener(v -> addTodo());
        FloatingActionButton fab = view.findViewById(R.id.fab_add_event);
        fab.setOnClickListener(v -> showAddEventDialog());

        rebuildWeekChips();
    }

    private void rebuildWeekChips() {
        layoutWeekDays.removeAllViews();
        dayChips.clear();
        long monday = currentMonday;
        tvWeekRange.setText(weekRangeText(monday));
        for (int i = 0; i < 7; i++) {
            long day = DateUtil.toKey(DateUtil.parseTime(monday) + i * 86400L * 1000);
            TextView chip = new TextView(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 0, 8, 0);
            chip.setLayoutParams(lp);
            chip.setPadding(24, 12, 24, 12);
            chip.setTextSize(13);
            chip.setText(DateUtil.formatShort(day) + "\n" + DateUtil.formatWeekCN(day).substring(2));
            chip.setTextColor(0xFFFFFFFF);
            chip.setGravity(android.view.Gravity.CENTER);
            if (day == currentDate) {
                chip.setBackgroundColor(0xFF3949AB);
            } else {
                chip.setBackgroundColor(0x33FFFFFF);
            }
            chip.setOnClickListener(v -> {
                currentDate = day;
                rebuildWeekChips();
            });
            dayChips.add(chip);
            layoutWeekDays.addView(chip);
        }
        refreshAll();
    }

    private String weekRangeText(long monday) {
        long sunday = DateUtil.toKey(DateUtil.parseTime(monday) + 6 * 86400L * 1000);
        int y = (int) (monday / 10000);
        return y + "  " + DateUtil.formatShort(monday) + " ~ " + DateUtil.formatShort(sunday);
    }

    private void refreshAll() {
        tvCurrentDay.setText(DateUtil.formatDate(currentDate) + " " + DateUtil.formatWeekCN(currentDate));
        scheduleList.clear();
        scheduleList.addAll(db.getScheduleByDate(currentDate));
        scheduleAdapter.notifyDataSetChanged();
        todoList.clear();
        todoList.addAll(db.getAllTodos());
        todoAdapter.notifyDataSetChanged();
    }

    private void addTodo() {
        String s = etTodoInput.getText().toString().trim();
        if (TextUtils.isEmpty(s)) return;
        db.insertTodo(new TodoItem(s));
        etTodoInput.setText("");
        refreshAll();
    }

    private void showAddEventDialog() {
        View root = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_event, null);
        TimePicker tp = root.findViewById(R.id.time_picker);
        EditText et = root.findViewById(R.id.et_event);
        tp.setIs24HourView(true);

        new AlertDialog.Builder(requireContext())
                .setView(root)
                .setPositiveButton(R.string.confirm, (d, w) -> {
                    String content = et.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) return;
                    int hour, minute;
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        hour = tp.getHour();
                        minute = tp.getMinute();
                    } else {
                        hour = tp.getCurrentHour();
                        minute = tp.getCurrentMinute();
                    }
                    db.insertSchedule(new ScheduleItem(currentDate, hour, minute, content));
                    refreshAll();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // ============== Adapter ==============

    class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ScheduleItem it = scheduleList.get(position);
            h.tvTime.setText(it.getTime());
            h.tvEvent.setText(it.content);
            h.tvEvent.setAlpha(it.done ? 0.45f : 1f);
            h.cbDone.setOnCheckedChangeListener(null);
            h.cbDone.setChecked(it.done);
            h.cbDone.setOnCheckedChangeListener((b, c) -> {
                it.done = c;
                db.updateScheduleDone(it.id, c);
                h.tvEvent.setAlpha(c ? 0.45f : 1f);
            });
            h.btnDelete.setOnClickListener(v -> {
                db.deleteSchedule(it.id);
                refreshAll();
            });
        }

        @Override
        public int getItemCount() {
            return scheduleList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTime, tvEvent;
            CheckBox cbDone;
            ImageButton btnDelete;

            VH(View v) {
                super(v);
                tvTime = v.findViewById(R.id.tv_time);
                tvEvent = v.findViewById(R.id.tv_event);
                cbDone = v.findViewById(R.id.cb_done);
                btnDelete = v.findViewById(R.id.btn_delete);
            }
        }
    }

    class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            TodoItem it = todoList.get(position);
            h.tvText.setText(it.content);
            h.btnAdd.setOnClickListener(v -> {
                // 找到当前时间段的下一个空闲 30min
                int hour = 9, minute = 0;
                if (!scheduleList.isEmpty()) {
                    ScheduleItem last = scheduleList.get(scheduleList.size() - 1);
                    minute = last.minute + 30;
                    if (minute >= 60) {
                        hour = last.hour + 1;
                        minute = minute - 60;
                    } else {
                        hour = last.hour;
                    }
                }
                db.insertSchedule(new ScheduleItem(currentDate, hour, minute, it.content));
                refreshAll();
                Toast.makeText(requireContext(), "已加入 " + String.format("%02d:%02d", hour, minute),
                        Toast.LENGTH_SHORT).show();
            });
            h.itemView.setOnLongClickListener(v -> {
                db.deleteTodo(it.id);
                refreshAll();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return todoList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvText;
            Button btnAdd;

            VH(View v) {
                super(v);
                tvText = v.findViewById(R.id.tv_todo_text);
                btnAdd = v.findViewById(R.id.btn_add_to_schedule);
            }
        }
    }
}
