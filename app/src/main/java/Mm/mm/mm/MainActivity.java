package Mm.mm.mm;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    SharedPreferences store;
    EditText nameField, lyricsField;
    LinearLayout songList;
    TextView counter;
    String editingKey = null;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        store = getSharedPreferences("lyr_store", 0);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this);
        title.setText("ADD LYRICS");
        title.setTextSize(22); title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 4);

        TextView sub = new TextView(this);
        sub.setText("Each saved song becomes a keyboard checkpoint");
        sub.setTextSize(11); sub.setPadding(0, 0, 0, 24);

        nameField = new EditText(this); nameField.setHint("Song Name");

        lyricsField = new EditText(this); lyricsField.setHint("Lyrics (one line per row)");
        lyricsField.setMinLines(5); lyricsField.setGravity(Gravity.TOP);
        lyricsField.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        Button saveBtn = new Button(this); saveBtn.setText("SAVE LYRICS");

        counter = new TextView(this);
        counter.setTextSize(11); counter.setPadding(0, 20, 0, 6);

        songList = new LinearLayout(this);
        songList.setOrientation(LinearLayout.VERTICAL);

        saveBtn.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String lyrics = lyricsField.getText().toString().trim();
            if (name.isEmpty()) { Toast.makeText(this, "Enter a song name", 1).show(); return; }
            if (lyrics.isEmpty()) { Toast.makeText(this, "Enter some lyrics", 1).show(); return; }
            if (editingKey != null && !editingKey.equals(name)) store.edit().remove(editingKey).apply();
            store.edit().putString(name, lyrics).apply();
            editingKey = null;
            nameField.setText(""); lyricsField.setText("");
            saveBtn.setText("SAVE LYRICS");
            Toast.makeText(this, "Saved: " + name, 0).show();
            refresh();
        });

        root.addView(title); root.addView(sub);
        root.addView(nameField); root.addView(lyricsField); root.addView(saveBtn);
        root.addView(counter); root.addView(songList);
        scroll.addView(root);
        setContentView(scroll);
        refresh();
    }

    void refresh() {
        songList.removeAllViews();
        Map<String, ?> all = store.getAll();
        counter.setText(all.size() + " song(s) — available as keyboard checkpoints");
        for (String k : all.keySet()) {
            String lyrics = store.getString(k, "");
            int lines = lyrics.isEmpty() ? 0 : lyrics.split("\n").length;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 10, 0, 10);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));

            TextView nameLabel = new TextView(this); nameLabel.setText(k);
            nameLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            TextView lineLabel = new TextView(this); lineLabel.setText(lines + " lines");
            lineLabel.setTextSize(11);

            info.addView(nameLabel); info.addView(lineLabel);

            Button editBtn = new Button(this); editBtn.setText("EDIT");
            Button delBtn = new Button(this); delBtn.setText("DEL");

            editBtn.setOnClickListener(v -> {
                editingKey = k;
                nameField.setText(k);
                lyricsField.setText(store.getString(k, ""));
                Button sb = (Button) ((LinearLayout) songList.getParent().getParent()).findViewWithTag("saveBtn");
                Toast.makeText(this, "Editing: " + k, 0).show();
            });

            delBtn.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete \"" + k + "\"?")
                .setPositiveButton("Delete", (d, w) -> { store.edit().remove(k).apply(); refresh(); })
                .setNegativeButton("Cancel", null).show());

            row.addView(info); row.addView(editBtn); row.addView(delBtn);
            songList.addView(row);
        }
    }
}