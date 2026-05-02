package Mm.mm.mm;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(1); l.setPadding(40, 40, 40, 40);
        EditText name = new EditText(this); name.setHint("Song Name");
        EditText data = new EditText(this); data.setHint("Lyrics Content");
        Button add = new Button(this); add.setText("ADD CHECKPOINT");
        LinearLayout list = new LinearLayout(this); list.setOrientation(1);
        add.setOnClickListener(v -> {
            getSharedPreferences("pts", 0).edit().putString(name.getText().toString(), data.getText().toString()).apply();
            refresh(list);
        });
        l.addView(name); l.addView(data); l.addView(add); l.addView(list);
        setContentView(l); refresh(list);
    }
    void refresh(LinearLayout list) {
        list.removeAllViews();
        for (String k : getSharedPreferences("pts", 0).getAll().keySet()) {
            Button b = new Button(this); b.setText("Load: " + k);
            b.setOnClickListener(v -> {
                String lyrics = getSharedPreferences("pts", 0).getString(k, "");
                getSharedPreferences("lyr", 0).edit().putString("data", lyrics).putInt("ptr", 0).apply();
                Toast.makeText(this, "Loaded " + k, 0).show();
            });
            list.addView(b);
        }
    }
}