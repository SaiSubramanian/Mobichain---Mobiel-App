package com.mobichain.project.mobichain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> nums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Button add = findViewById(R.id.Add);
        add.setOnClickListener(this);
        Button move = findViewById(R.id.Transact);
        move.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String n1 = ((EditText) findViewById(R.id.editText1)).getText().toString();
        String n2 = ((EditText) findViewById(R.id.editText2)).getText().toString();
        switch (view.getId()) {
            case R.id.Add:
                if (n1.equals(""))
                    Toast.makeText(ContactsActivity.this, "Enter Contact name..", Toast.LENGTH_SHORT).show();
                else if (n2.equals(""))
                    Toast.makeText(ContactsActivity.this, "Enter Contact number..", Toast.LENGTH_SHORT).show();
                else if (n2.length() != 10)
                    Toast.makeText(ContactsActivity.this, "Contact number Invalid..", Toast.LENGTH_SHORT).show();
                else {
                    names.add(n1);
                    nums.add(n2);
                    Toast.makeText(ContactsActivity.this, "Added to Contacts", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.editText1)).setText("");
                    ((EditText) findViewById(R.id.editText2)).setText("");
                }

                break;
            case R.id.Transact:
                if (names.size() == 0 || nums.size() == 0)
                    Toast.makeText(ContactsActivity.this, "Register some Contacts", Toast.LENGTH_SHORT).show();
                else {
                    Intent i = new Intent(ContactsActivity.this, TransactionActivity.class);
                    i.putStringArrayListExtra("contacts", names);
                    startActivity(i);
                    finish();
                }
        }
    }
}
