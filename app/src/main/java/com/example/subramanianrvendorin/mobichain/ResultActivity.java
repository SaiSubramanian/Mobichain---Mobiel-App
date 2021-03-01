package com.example.subramanianrvendorin.mobichain;

import android.content.Intent;
//import android.provider.FontRequest;
//import android.support.text.emoji.EmojiCompat;
//import android.support.text.emoji.FontRequestEmojiCompatConfig;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        /*FontRequest fontRequest = new FontRequest(
                "com.example.fontprovider",
                "com.example",
                "emoji compat Font Query",
                CERTIFICATES);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);*/
        TextView msg = findViewById(R.id.Res);
        msg.setText("Money transferred Sucessfully...");
        for (int m = 0; m < 100; m++)
            for (int n = 0; n < 100; n++) ;
        Intent i = new Intent(ResultActivity.this, TransactionActivity.class);
        startActivity(i);
        finish();

    }

}
