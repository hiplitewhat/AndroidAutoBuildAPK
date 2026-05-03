package Mm.mm.mm;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.view.*;
import android.widget.*;
import java.util.*;

public class LyricIME extends InputMethodService {
    SharedPreferences store, active;
    TextView statusView, checkpointLabel;
    LinearLayout checkpointRow;
    boolean showingCheckpoints = false;

    @Override public View onCreateInputView() {
        store = getSharedPreferences("lyr_store", 0);
        active = getSharedPreferences("lyr_active", 0);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(12, 6, 12, 6);

        // Top bar: checkpoint selector
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);

        checkpointLabel = new TextView(this);
        checkpointLabel.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        checkpointLabel.setPadding(4, 10, 4, 0);
        checkpointLabel.setTextSize(12);
        updateCheckpointLabel();

        Button pickBtn = new Button(this); pickBtn.setText("CHECKPOINTS");
        pickBtn.setTextSize(10);

        topBar.addView(checkpointLabel);
        topBar.addView(pickBtn);

        // Checkpoint list (hidden by default)
        checkpointRow = new LinearLayout(this);
        checkpointRow.setOrientation(LinearLayout.VERTICAL);
        checkpointRow.setVisibility(View.GONE);

        // Status line
        statusView = new TextView(this);
        statusView.setTextSize(10);
        statusView.setPadding(4, 2, 4, 2);
        updateStatus();

        // Action buttons
        LinearLayout btnRow = new LinearLayout(this);
        LinearLayout btnRow2 = new LinearLayout(this);
        Button prev = new Button(this); prev.setText("◀");
        Button next = new Button(this); next.setText("NEXT ▶");
        Button setChk = new Button(this); setChk.setText("📌 SET CHECKPOINT");
        setChk.setTextSize(10);
        Button undo = new Button(this); undo.setText("↩ UNDO TO CHECKPOINT");
        undo.setTextSize(10);

        next.setOnClickListener(v -> {
            String data = active.getString("data", "");
            if (data.isEmpty()) { Toast.makeText(this, "No checkpoint loaded", 1).show(); return; }
            String[] lines = data.split("\n");
            int ptr = active.getInt("ptr", 0);
            if (ptr < lines.length) {
                String line = lines[ptr] + "\n";
                getCurrentInputConnection().commitText(line, 1);
                int newChars = active.getInt("committed_chars", 0) + line.length();
                active.edit().putInt("ptr", ptr + 1).putInt("committed_chars", newChars).apply();
                updateStatus();
            } else {
                Toast.makeText(this, "End of lyrics", 0).show();
            }
        });

        prev.setOnClickListener(v -> {
            int ptr = active.getInt("ptr", 0);
            if (ptr > 0) { active.edit().putInt("ptr", ptr - 1).apply(); updateStatus(); }
        });

        setChk.setOnClickListener(v -> {
            int ptr = active.getInt("ptr", 0);
            int chars = active.getInt("committed_chars", 0);
            active.edit().putInt("ckpt_ptr", ptr).putInt("ckpt_chars", chars).apply();
            Toast.makeText(this, "Checkpoint set at line " + ptr, 0).show();
            updateStatus();
        });

        undo.setOnClickListener(v -> {
            int ckptPtr = active.getInt("ckpt_ptr", -1);
            if (ckptPtr < 0) { Toast.makeText(this, "No checkpoint set — use 📌 first", 1).show(); return; }
            int currentChars = active.getInt("committed_chars", 0);
            int ckptChars = active.getInt("ckpt_chars", 0);
            int deleteCount = currentChars - ckptChars;
            if (deleteCount > 0) getCurrentInputConnection().deleteSurroundingText(deleteCount, 0);
            active.edit().putInt("ptr", ckptPtr).putInt("committed_chars", ckptChars).apply();
            Toast.makeText(this, "Restored to checkpoint: line " + ckptPtr, 0).show();
            updateStatus();
        });

        pickBtn.setOnClickListener(v -> {
            showingCheckpoints = !showingCheckpoints;
            if (showingCheckpoints) {
                buildCheckpointList();
                checkpointRow.setVisibility(View.VISIBLE);
            } else {
                checkpointRow.setVisibility(View.GONE);
            }
        });

        btnRow.addView(prev); btnRow.addView(next);
        btnRow2.addView(setChk); btnRow2.addView(undo);
        root.addView(topBar);
        root.addView(checkpointRow);
        root.addView(statusView);
        root.addView(btnRow);
        root.addView(btnRow2);
        return root;
    }

    void buildCheckpointList() {
        checkpointRow.removeAllViews();
        Map<String, ?> all = store.getAll();
        if (all.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No lyrics saved yet. Add them in the app.");
            empty.setPadding(8, 8, 8, 8);
            checkpointRow.addView(empty);
            return;
        }
        for (String k : all.keySet()) {
            Button btn = new Button(this);
            btn.setText("▶ " + k);
            btn.setTextSize(11);
            btn.setOnClickListener(v -> {
                String lyrics = store.getString(k, "");
                active.edit()
                    .putString("data", lyrics)
                    .putString("name", k)
                    .putInt("ptr", 0)
                    .putInt("committed_chars", 0)
                    .putInt("ckpt_ptr", -1)
                    .putInt("ckpt_chars", 0)
                    .apply();
                checkpointRow.setVisibility(View.GONE);
                showingCheckpoints = false;
                updateCheckpointLabel();
                updateStatus();
                Toast.makeText(this, "Loaded: " + k, 0).show();
            });
            checkpointRow.addView(btn);
        }
    }

    void updateCheckpointLabel() {
        if (active == null) return;
        String name = active.getString("name", "");
        checkpointLabel.setText(name.isEmpty() ? "No checkpoint loaded" : "▶ " + name);
    }

    void updateStatus() {
        if (active == null || statusView == null) return;
        String data = active.getString("data", "");
        int ptr = active.getInt("ptr", 0);
        int total = data.isEmpty() ? 0 : data.split("\n").length;
        statusView.setText("Line " + ptr + " / " + total);
    }
}