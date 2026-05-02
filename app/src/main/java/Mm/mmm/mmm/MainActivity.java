package Mm.mmm.mmm;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    SharedPreferences pts, lyr;
    EditText nameField, dataField;
    LinearLayout list;
    TextView counter;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        pts = getSharedPreferences("pts", 0);
        lyr = getSharedPreferences("lyr", 0);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this);
        title.setText("LYRIC CHECKPOINTS");
        title.setTextSize(20); title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 24);

        nameField = new EditText(this); nameField.setHint("Checkpoint Name");
        dataField = new EditText(this); dataField.setHint("Lyrics / Content");
        dataField.setMinLines(3); dataField.setGravity(Gravity.TOP);

        LinearLayout btnRow = new LinearLayout(this);
        Button addBtn = new Button(this); addBtn.setText("ADD CHECKPOINT");
        Button clearBtn = new Button(this); clearBtn.setText("CLEAR ALL");
        btnRow.addView(addBtn); btnRow.addView(clearBtn);

        counter = new TextView(this);
        counter.setTextSize(11); counter.setPadding(0, 16, 0, 8);

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        addBtn.setOnClickListener(v -> {
            String n = nameField.getText().toString().trim();
            String d = dataField.getText().toString().trim();
            if (n.isEmpty()) { Toast.makeText(this, "Enter a name", 1).show(); return; }
            pts.edit().putString(n, d).apply();
            nameField.setText(""); dataField.setText("");
            Toast.makeText(this, "Checkpoint saved: " + n, 0).show();
            refresh();
        });

        clearBtn.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Clear All Checkpoints?")
            .setPositiveButton("Clear", (d, w) -> { pts.edit().clear().apply(); refresh(); })
            .setNegativeButton("Cancel", null).show());

        root.addView(title); root.addView(nameField); root.addView(dataField);
        root.addView(btnRow); root.addView(counter); root.addView(list);
        scroll.addView(root);
        setContentView(scroll);
        refresh();
    }

    void refresh() {
        list.removeAllViews();
        Map<String, ?> all = pts.getAll();
        counter.setText(all.size() + " checkpoint(s) saved");
        for (String k : all.keySet()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            TextView label = new TextView(this);
            label.setText(k); label.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
            label.setPadding(0, 12, 0, 0);

            Button loadBtn = new Button(this); loadBtn.setText("LOAD");
            Button editBtn = new Button(this); editBtn.setText("EDIT");
            Button delBtn = new Button(this); delBtn.setText("DEL");

            loadBtn.setOnClickListener(v -> {
                lyr.edit().putString("data", pts.getString(k, "")).putInt("ptr", 0).apply();
                Toast.makeText(this, "Loaded: " + k, 0).show();
            });

            editBtn.setOnClickListener(v -> {
                nameField.setText(k);
                dataField.setText(pts.getString(k, ""));
                pts.edit().remove(k).apply();
                refresh();
            });

            delBtn.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete \"" + k + "\"?")
                .setPositiveButton("Delete", (d, w) -> { pts.edit().remove(k).apply(); refresh(); })
                .setNegativeButton("Cancel", null).show());

            row.addView(label); row.addView(loadBtn); row.addView(editBtn); row.addView(delBtn);
            list.addView(row);
        }
    }
}