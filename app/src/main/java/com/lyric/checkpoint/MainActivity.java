package com.lyric.checkpoint;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.*;
import android.view.*;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private LinearLayout list;
    private SharedPreferences p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p = getSharedPreferences("cp_lyrics", MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 60);
        root.setBackgroundColor(Color.parseColor("#0F0F0F"));

        TextView h = new TextView(this);
        h.setText("Lyric & Checkpoint Editor");
        h.setTextSize(24);
        h.setTextColor(Color.WHITE);
        root.addView(h);

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, 40, 0, 40);
        root.addView(list);

        Button add = new Button(this);
        add.setText("+ Add Line");
        add.setOnClickListener(v -> addRow("", false));
        root.addView(add);

        Button save = new Button(this);
        save.setText("SYNC DATA");
        save.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<list.getChildCount(); i++){
                LinearLayout r = (LinearLayout)list.getChildAt(i);
                String txt = ((EditText)r.getChildAt(0)).getText().toString();
                boolean isCP = ((CheckBox)r.getChildAt(1)).isChecked();
                if(!txt.isEmpty()) sb.append(txt).append(":::").append(isCP ? "Y" : "N").append("|||");
            }
            p.edit().putString("data", sb.toString()).putInt("idx", 0).apply();
            Toast.makeText(this, "Lyrics & Checkpoints Synced!", Toast.LENGTH_SHORT).show();
        });
        root.addView(save);

        scroll.addView(root);
        setContentView(scroll);
        load();
    }

    private void addRow(String t, boolean cp) {
        LinearLayout row = new LinearLayout(this);
        EditText e = new EditText(this);
        e.setHint("Lyric text..."); e.setText(t); e.setTextColor(Color.WHITE);
        CheckBox c = new CheckBox(this);
        c.setText("Checkpoint"); c.setChecked(cp); c.setTextColor(Color.GRAY);
        row.addView(e, new LinearLayout.LayoutParams(0, -2, 1));
        row.addView(c);
        list.addView(row);
    }

    private void load() {
        String data = p.getString("data", "Verse 1:::Y|||I'm feeling good:::N|||");
        for(String item : data.split("\\\|\\\|")){
            String[] parts = item.split(":::");
            if(parts.length == 2) addRow(parts[0], parts[1].equals("Y"));
        }
    }
}