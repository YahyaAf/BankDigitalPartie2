package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    public enum TransactionType{
        DEPOSIT,
        WITHDRAW,
        TRANSFER_IN,
        TRANSFER_OUT,
        TRANSFER_EXTERNAL,
        FEE,
        FEEINCOME
    }
    public enum TransactionStatus{
        PENDING,
        SETTLED,
        FAILED
    }

    private UUID id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private String externalReceiverAccount;

    public Transaction(TransactionType type, BigDecimal amount, UUID senderAccountId, UUID receiverAccountId){
        this.id = UUID.randomUUID();
        this.amount = (amount == null) ? BigDecimal.ZERO : amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        this.timestamp = LocalDateTime.now();
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;

    }

    public Transaction(UUID id, BigDecimal amount, TransactionType type,TransactionStatus status,LocalDateTime timestamp, UUID senderAccountId, UUID receiverAccountId, String externalReceiverAccount){
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.externalReceiverAccount = externalReceiverAccount;
    }

    public Transaction(TransactionType type,BigDecimal amount,
                        UUID senderAccountId, String externalReceiverAccount) {
        this.id = UUID.randomUUID();
        this.amount = (amount == null) ? BigDecimal.ZERO : amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        this.timestamp = LocalDateTime.now();
        this.senderAccountId = senderAccountId;
        this.externalReceiverAccount = externalReceiverAccount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(UUID senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public UUID getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(UUID receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public String getExternalReceiverAccount() {
        return externalReceiverAccount;
    }

    public void setExternalReceiverAccount(String externalReceiverAccount) {
        this.externalReceiverAccount = externalReceiverAccount;
    }
}
