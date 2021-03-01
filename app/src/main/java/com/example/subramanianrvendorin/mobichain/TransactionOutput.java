package com.example.subramanianrvendorin.mobichain;

import java.security.PublicKey;

import static com.mobichain.project.mobichain.Block.applySha256;
import static com.mobichain.project.mobichain.Transact.getStringFromKey;

public class TransactionOutput {
    public String id;
    public PublicKey recipient; //also known as the new owner of these coins.
    public float value; //the amount of coins they own
    public String parentTransactionId; //the id of the transaction this output was created in

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = applySha256(getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

}