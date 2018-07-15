package com.dzavorin.transfer.model;

/**
 * Entity representing pair of accounts involved into money transfer
 */
public class AccountTransferPair {

    private Account from;
    private Account to;

    public Account getFrom() {
        return from;
    }

    public void setFrom(Account from) {
        this.from = from;
    }

    public Account getTo() {
        return to;
    }

    public void setTo(Account to) {
        this.to = to;
    }
}
