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
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    int flag;
    FirebaseDatabase db;
    static Block block;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button register = findViewById(R.id.Register);
        register.setOnClickListener(this);
        db = FirebaseDatabase.getInstance("https://mobichain-a5304.firebaseio.com");

    }

    @Override
    public void onClick(View view) {

        final ArrayList<String> login = new ArrayList<>();
        final String name = ((EditText) findViewById(R.id.editText1)).getText().toString();
        final String mobile = ((EditText) findViewById(R.id.editText2)).getText().toString();
        final String pin = ((EditText) findViewById(R.id.editText3)).getText().toString();
        final DatabaseReference mobi = db.getReference("SASTRA");
        switch (view.getId()) {
            case R.id.Register:
                if (name.equals(""))
                    Toast.makeText(SignUpActivity.this, "Type in your Name", Toast.LENGTH_SHORT).show();
                else if (mobile.equals(""))
                    Toast.makeText(SignUpActivity.this, "Type in your Mobile No.", Toast.LENGTH_SHORT).show();
                else if (pin.equals(""))
                    Toast.makeText(SignUpActivity.this, "Type in Pin", Toast.LENGTH_SHORT).show();
                else {
                    if (mobile.length() != 10)
                        Toast.makeText(SignUpActivity.this, "Incorrect Mobile no.", Toast.LENGTH_SHORT).show();
                    else if (pin.length() != 4)
                        Toast.makeText(SignUpActivity.this, "Pin Should be of 4digits", Toast.LENGTH_SHORT).show();
                    else {
                        flag = 0;
                        mobi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot c = dataSnapshot.child("Blockchain");
                                long count = (c.getChildrenCount()) + 1;
                                String pH;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot group : dataSnapshot.getChildren())
                                        for (DataSnapshot next : group.getChildren())
                                            for (DataSnapshot child : next.getChildren())
                                                if ((child.getKey().toString()).equals(mobile)) {
                                                    Toast.makeText(SignUpActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                                                    flag = 1;
                                                }
                                    if (flag == 0) {
                                        Log.d("Count", String.valueOf(count));
                                        //Log.d("pH", (dataSnapshot.child("Blockchain").child(String.valueOf(count - 1)).child("Block").getValue(String.class)).toString());
                                        Map<String, String> ph = (HashMap<String, String>) (dataSnapshot.child("Blockchain").child(String.valueOf(count - 1)).child("Block").getValue());
                                        pH = ph.get("hash");
                                        Log.d("pH", pH);
                                        block = new Block(pH, 0);
                                        mobi.child("Users").child(name).child(mobile).setValue(encry(pin));
                                        mobi.child("Blockchain").child(count + "").child("Name").setValue(name);
                                        mobi.child("Blockchain").child(count + "").child("Block").setValue(block);
                                        Toast.makeText(SignUpActivity.this, "Successfully Registered...Login again", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    block = new Block("0", 0);
                                    mobi.child("Users").child(name).child(mobile).setValue(encry(pin));
                                    mobi.child("Blockchain").child(count + "").child("Name").setValue(name);
                                    mobi.child("Blockchain").child(count + "").child("Block").setValue(block);
                                    Toast.makeText(SignUpActivity.this, "Successfully Registered...Login again", Toast.LENGTH_SHORT).show();
                                }

                                login.add(mobile);
                                login.add(pin);
                                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                i.putStringArrayListExtra("login", login);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                }
        }

    }

    public static String encry(String p) {
        String ep = "" + p.charAt(p.length() - 1) + String.valueOf(9 - Integer.parseInt(String.valueOf(p.charAt(1)))) + String.valueOf(9 - Integer.parseInt(String.valueOf(p.charAt(2)))) + p.charAt(0);
        return ep;
    }
}

