package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private Integer transferStatus;
    private int transferType;
    private BigDecimal amount;
    private int accountFrom;
    private int accountTo;
    private User user;

    public User getUser(String username) {
        return user;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public Integer getTransferStatus() {
        return transferStatus;
    }

    public int getTransferType() {
        return transferType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setTransferStatus(Integer transferStatus) {
        this.transferStatus = transferStatus;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferStatus='" + transferStatus + '\'' +
                ", transferType=" + transferType +
                ", amount=" + amount +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                '}';
    }
}
