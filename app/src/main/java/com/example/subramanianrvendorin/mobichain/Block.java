package com.example.subramanianrvendorin.mobichain;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mobichain.project.mobichain.Transact.getStringFromKey;
import static com.mobichain.project.mobichain.TransactionActivity.spin;
import static com.mobichain.project.mobichain.TransactionActivity.user;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    //public ArrayList<Transact> transactions = new ArrayList<Transact>();
    public long timeStamp;
    public int nonce = 10;
    static FirebaseDatabase db = FirebaseDatabase.getInstance("https://mobichain-a5304.firebaseio.com");
    static final DatabaseReference trans = db.getReference("Transactions");

    public Block(DataSnapshot dataSnapshot) {
        Map<String, String> ph = (HashMap<String, String>) (dataSnapshot.child("Block").getValue());
        this.previousHash = ph.get("previousHash");
        this.timeStamp = new Date().getTime();
        this.hash = ph.get("hash");
        Log.d("Hash Value: ", this.hash);
    }

    public Block(String previousHash, int i) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = mineBlock(3);
        Log.d("Hash Value: ", this.hash);
    }

    public String calculateHash() {
        getMerkleRoot();
        String calculatedhash = applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedhash;
    }

    public String mineBlock(int difficulty) {
        String h = calculateHash();
        while (!h.substring(0, difficulty).equals("000")) {
            nonce++;
            h = calculateHash();
        }
        return h;
    }

    public static boolean addTransaction(final Transact transaction, final String amt) {
        trans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String count = String.valueOf(dataSnapshot.getChildrenCount() + 1);
                String id = String.valueOf(applySha256(getStringFromKey(transaction.recipient) + amt));
                trans.child(count).child("ID").setValue(id);
                trans.child(count).child(user + " -> " + spin.getSelectedItem()).setValue(Float.valueOf(amt));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //if (transaction == null) return false;
        if ((transaction.processTransaction() != true))
            return false;

        //this.transactions.add(transaction);
        //Log.d("Transactions ", transactions.toString());
        return true;
    }

    public void getMerkleRoot() {

        trans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                ArrayList<String> previousTreeLayer = new ArrayList<String>();
                for (DataSnapshot t : dataSnapshot.getChildren()) {
                    previousTreeLayer.add((t.child("ID").getValue()).toString());
                }
                ArrayList<String> treeLayer = previousTreeLayer;
                while (count > 1) {
                    treeLayer = new ArrayList<>();
                    for (int i = 1; i < previousTreeLayer.size(); i++) {
                        treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
                    }
                    count = treeLayer.size();
                    previousTreeLayer = treeLayer;
                }
                merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public static String applySha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            Log.d("Hashing error: ", String.valueOf(ex));
            throw new RuntimeException(ex);
        }
    }
}
