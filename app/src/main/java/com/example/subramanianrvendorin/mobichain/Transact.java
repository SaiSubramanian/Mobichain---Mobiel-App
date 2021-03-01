package com.example.subramanianrvendorin.mobichain;

import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class Transact {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    //public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;
    public String transactionId;
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;

    public static ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public static ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public Transact(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public static byte[] applyECDSAsign(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[1024];
        try {
            dsa = Signature.getInstance("SHA1withECDSA");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            Log.d("Signature error: ", String.valueOf(e));
        }
        return output;
    }

    public static boolean verifyECDSAsign(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA1withECDSA");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            Log.d("Signature error: ", String.valueOf(e));
            return false;
        }
    }

    public static String getStringFromKey(PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = getStringFromKey(sender) + getStringFromKey(recipient) + Float.toString(value);
        signature = applyECDSAsign(privateKey, data);
    }

    public boolean verifySignature() {
        String data = getStringFromKey(sender) + getStringFromKey(recipient) + Float.toString(value);
        return verifyECDSAsign(sender, data, signature);
    }

    /*private String calulateHash() {
        sequence++;
        return applySha256(
                getStringFromKey(sender) + getStringFromKey(recipient) + Float.toString(value) + sequence
        );
    }*/

    public boolean processTransaction() {

        if (verifySignature() == false) {
            Log.d("", "#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        /*for (TransactionInput i : inputs) {
            i.UTXO = UTXOs.get(i.transactionOutputId);
        }

        //check if transaction is valid:
        if (getInputsValue() < 10) {
            Log.d("", "#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = 100 - value; //get value of inputs then the left over change
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            UTXOs.put(o.id, o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            UTXOs.remove(i.UTXO.id);
        }

        Log.d("IP",inputs.toString());
        Log.d("OP",outputs.toString());*/
        return true;
    }

    /*public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }*/
}
