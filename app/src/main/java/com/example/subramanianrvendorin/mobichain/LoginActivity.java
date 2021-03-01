package com.example.subramanianrvendorin.mobichain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.Login);
        login.setOnClickListener(this);
        Button register = findViewById(R.id.Register);
        register.setOnClickListener(this);
        Intent i = getIntent();
        if (i.getExtras() != null) {
            ArrayList<String> details = i.getStringArrayListExtra("login");
            ((EditText) findViewById(R.id.editText)).setText(details.get(0));
            ((EditText) findViewById(R.id.editText2)).setText(details.get(1));
        }

        db = FirebaseDatabase.getInstance("https://mobichain-a5304.firebaseio.com");

    }

    @Override
    public void onClick(View view) {

        final DatabaseReference mobi = db.getReference("SASTRA");
        final String num = ((EditText) findViewById(R.id.editText)).getText().toString();
        final String pin = ((EditText) findViewById(R.id.editText2)).getText().toString();
        switch (view.getId()) {
            case R.id.Login:
                if (num.equals(""))
                    Toast.makeText(LoginActivity.this, "Enter Mobile No.", Toast.LENGTH_SHORT).show();
                else if (pin.equals(""))
                    Toast.makeText(LoginActivity.this, "Enter Pin", Toast.LENGTH_SHORT).show();
                else {
                    mobi.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int flag = 0;
                                for (DataSnapshot group : dataSnapshot.getChildren())
                                    for (DataSnapshot next : group.getChildren())
                                        for (DataSnapshot child : next.getChildren())
                                            if ((child.getKey().toString()).equals(num)) {
                                                flag = 1;
                                                String p = child.getValue().toString();
                                                String dp = "" + p.charAt(p.length() - 1) + String.valueOf(9 - Integer.parseInt(String.valueOf(p.charAt(1)))) + String.valueOf(9 - Integer.parseInt(String.valueOf(p.charAt(2)))) + p.charAt(0);
                                                if (pin.equals(dp)) {
                                                    Intent j = new Intent(LoginActivity.this, com.mobichain.project.mobichain.TransactionActivity.class);
                                                    j.putExtra("login", next.getKey().toString());
                                                    Log.d("Logged in by:", next.getKey().toString());
                                                    startActivity(j);
                                                    break;
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Incorrect Pin", Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                            }
                                if (flag == 0)
                                    Toast.makeText(LoginActivity.this, "User doesn't exists...Register First", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "User doesn't exists...Register First", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                break;
            case R.id.Register:
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }
}
