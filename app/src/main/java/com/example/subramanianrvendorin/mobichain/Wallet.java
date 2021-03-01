package com.example.subramanianrvendorin.mobichain;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.*;
import java.security.KeyPairGenerator;
//import java.security.spec.ECGenParameterSpec;
//import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import static com.mobichain.project.mobichain.Transact.UTXOs;
import static com.mobichain.project.mobichain.Transact.inputs;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;
    static FirebaseDatabase db = FirebaseDatabase.getInstance("https://mobichain-a5304.firebaseio.com");
    final static DatabaseReference mobi = db.getReference("SASTRA");

    public Wallet() {
        generateKeys();
    }

    public void generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            //SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            //ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(256);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            Log.d("Key Generation error: ", String.valueOf(e));
        }
    }

    public static double getBalance(double b) {
        final ArrayList<Float> inc = new ArrayList<>();
        final ArrayList<Float> otg = new ArrayList<>();
        mobi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot group = dataSnapshot.child("Users");
                DataSnapshot next;
                for (DataSnapshot user : group.getChildren()) {
                    next = user.child("Transaction");
                    for (DataSnapshot trans : next.getChildren()) {
                        if ((trans.toString()).equals("Incoming"))
                            for (DataSnapshot amt : trans.getChildren())
                                inc.add(Float.parseFloat(amt.getValue().toString()));
                        if ((trans.toString()).equals("Outgoing"))
                            for (DataSnapshot amt : trans.getChildren())
                                otg.add(Float.parseFloat(amt.getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        for (int i = 0; i < inc.size(); i++)
            b += inc.get(i);
        for (int i = 0; i < otg.size(); i++)
            b -= otg.get(i);
        /*float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO); //add it to our list of unspent transactions.
                total += UTXO.value;
            }
        }*/
        Log.d("Blnc:", String.valueOf(b));
        return b;
    }

    public Transact sendFunds(PublicKey _recipient, float value) {
        /*if (getBalance() < value) {
            return null;
        }*/

        /*ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) break;
        }*/

        Transact newTransaction = new Transact(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        /*for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        Log.d("UTXO:", UTXOs.values().toString());
        Log.d("Inputs:", inputs.toString());*/
        return newTransaction;
    }

}

