package com.example.subramanianrvendorin.mobichain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mobichain.project.mobichain.SignUpActivity.block;
import static com.mobichain.project.mobichain.Transact.*;
import static com.mobichain.project.mobichain.Wallet.getBalance;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    int difficulty = 3, flag;
    Transact genesisTransaction;
    String blnc;
    TextView bedit;
    Double b = 1000.0;
    final ArrayList<String> phone = new ArrayList<>();
    static Spinner spin;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://mobichain-a5304.firebaseio.com");
    static String user;
    final DatabaseReference mobi = db.getReference("SASTRA");
    boolean val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Button send = findViewById(R.id.Send);
        send.setOnClickListener(this);
        spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        bedit = findViewById(R.id.Text);
        bedit.setText(String.valueOf(getBalance(b)));
        Intent i = getIntent();
        final String login = i.getStringExtra("login");
        user = login;
        mobi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot group = dataSnapshot.child("Users");
                for (DataSnapshot child : group.getChildren())
                    if (!((child.getKey().toString()).equals(login)))
                        phone.add(child.getKey().toString());

                if (phone.isEmpty())
                    Toast.makeText(TransactionActivity.this, "No other members in this Group", Toast.LENGTH_SHORT).show();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(TransactionActivity.this, android.R.layout.simple_spinner_item, phone);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /*Thread t = new Thread() {
        public void run() {
            res = isChainValid();
        }
    };*/

    @Override
    public void onClick(View view) {

        EditText e = findViewById(R.id.editText3);
        final String amt = e.getText().toString();
        blnc = bedit.getText().toString();

        if (phone.isEmpty())
            Toast.makeText(TransactionActivity.this, "No other members in this Group...Transaction not possible", Toast.LENGTH_SHORT).show();
        else if (amt.equals(""))
            Toast.makeText(TransactionActivity.this, "Provide necessary Information", Toast.LENGTH_SHORT).show();
        else if (Float.valueOf(amt) > Float.valueOf(blnc))
            Toast.makeText(TransactionActivity.this, "Insufficient funds", Toast.LENGTH_SHORT).show();
        else if (Float.valueOf(amt) <= 0.0)
            Toast.makeText(TransactionActivity.this, "Invalid fund", Toast.LENGTH_SHORT).show();
        else {

            //t.start();
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            walletA = new Wallet();
            walletB = new Wallet();

            Log.d("Key", "A: " + walletA.publicKey);
            Log.d("Key", "B: " + walletB.publicKey);
            /*genesisTransaction = new Transact(walletA.publicKey, walletB.publicKey, 0, null);
            genesisTransaction.generateSignature(walletA.privateKey);
            genesisTransaction.transactionId = "0";
            genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
            //UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

            //block1.addTransaction(genesisTransaction, "" + 0);*/
            Transact newOne = walletA.sendFunds(walletB.publicKey, Float.valueOf(amt));
            block.addTransaction(newOne, amt);

            if (isChainValid()) {
                Toast.makeText(TransactionActivity.this, "Money transferred Successfully", Toast.LENGTH_SHORT).show();
                mobi.child("Users").child(user).child("Transaction").child("Outgoing").child("" + spin.getSelectedItem()).setValue(Float.valueOf(amt));
                mobi.child("Users").child("" + spin.getSelectedItem()).child("Transaction").child("Incoming").child(user).setValue(Float.valueOf(amt));
                b = getBalance(b);
                //b = String.valueOf(Float.valueOf(blnc) - Float.valueOf(amt));
                bedit.setText(b.toString());
                e.setText("");
                System.gc();
            } else {
                Toast.makeText(TransactionActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                e.setText("");
                System.gc();
            }
        }
    }

    public Boolean isChainValid() {

        final String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        //HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        //tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
        mobi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //int i = 1;
                long cnt = dataSnapshot.child("Blockchain").getChildrenCount();
                for (int i = 1; i < cnt; i++) {

                    DataSnapshot next = dataSnapshot.child("Blockchain");
                    Block currentBlock, previousBlock;
                    flag = 0;
                    //Log.d("TA:", String.valueOf(blockchain.size()));
                    //Log.d("TA:", String.valueOf(blockchain));
                    currentBlock = new Block(next.child((String.valueOf(i + 1))));
                    previousBlock = new Block(next.child(String.valueOf(i)));
                    if (currentBlock.hash.equals("")) {
                        Log.d("TransactionActivity: ", "Hashing hasnot been done");
                        flag = 1;
                    } else
                        Log.d("TransactionActivity: ", "Hashing has been done");

                    if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                        Log.d("TransactionActivity: ", "Previous Hashes not equal");
                        flag = 1;

                    } else
                        Log.d("TransactionActivity: ", "Previous Hashes are equal");

                    if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                        Log.d("TransactionActivity: ", "This block hasn't been mined");
                        flag = 1;
                    } else
                        Log.d("TransactionActivity: ", "This block has been mined");


            /*TransactionOutput tempOutput;
            Log.d("CBT:", String.valueOf(currentBlock.transactions.size()));
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transact currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    Log.d("TransactionActivity: ", "Signature on Transaction is Invalid");
                    flag = 1;
                } else
                    Log.d("TransactionActivity: ", "Signature on Transaction is Valid");

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    Log.d("TransactionActivity: ", "Inputs are not equal to outputs on Transaction");
                    flag = 1;
                } else
                    Log.d("TransactionActivity: ", "Inputs are equal to outputs on Transaction");

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        Log.d("TransactionActivity: ", "Referenced input on Transaction is Missing");
                        flag = 1;
                    } else
                        Log.d("TransactionActivity: ", "Referenced input on Transaction exists");

                    if (input.UTXO.value != tempOutput.value)
                        Log.d("TransactionActivity: ", "Referenced input Transaction value is Invalid");
                    else
                        Log.d("TransactionActivity: ", "Referenced input Transaction value is Valid");

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    Log.d("TransactionActivity: ", "Transaction output recipient is not who it should be");
                    flag = 1;
                } else
                    Log.d("TransactionActivity: ", "Transaction output recipient is the one who it should be");

                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    Log.d("TransactionActivity: ", "Transaction output 'change' is not sender");
                    flag = 1;
                } else
                    Log.d("TransactionActivity: ", "Transaction output 'change' is sender");

            }*/

                }
                if (flag == 0) {
                    val = true;
                    Log.d("TransactionActivity: ", "Chain is Valid");
                    Log.d("TransactionActivity: ", "Money transferred to " + spin.getSelectedItem() + " Successfully");
                } else {
                    val = false;
                    Log.d("TransactionActivity: ", "Chain is Invalid");
                    Log.d("TransactionActivity: ", "Transaction failed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Log.d("OP", String.valueOf(val));
        return val;
    }
}


