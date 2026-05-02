package com.lyric.sequencer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#F8FAFC"));

        TextView tv = new TextView(this);
        tv.setText("Lyric Sequencer\nKeyboard App");
        tv.setTextSize(24);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        
        TextView info = new TextView(this);
        info.setText("Enable this keyboard in Android Settings to use your segments.");
        info.setPadding(40, 40, 40, 40);
        info.setGravity(Gravity.CENTER);

        layout.addView(tv);
        layout.addView(info);
        setContentView(layout);
    }
}