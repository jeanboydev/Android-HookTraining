package com.jeanboy.app.hooktrainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jeanboy.app.hooktrainning.hook.HookView;

public class MainActivity extends AppCompatActivity {

    private Button btn_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_test = findViewById(R.id.btn_test);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Button Click", Toast.LENGTH_SHORT).show();
            }
        });

        HookView.hookOnClickListener(btn_test);
    }

    public void toStartActivity(View view) {
        startActivity(new Intent(this, UnregisterActivity.class));
    }

    public void toPluginActivity(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.jeanboy.plugin", "com.jeanboy.plugin.PluginActivity"));
        startActivity(intent);
    }
}
