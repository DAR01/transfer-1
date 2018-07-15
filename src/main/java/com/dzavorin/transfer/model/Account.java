package com.dzavorin.transfer.model;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;

/**
 * Entity representing account info
 */
public class Account {

    private Long id;

    private BigDecimal amount;

    public Account(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public boolean notEmpty() {
        return nonNull(id) && nonNull(amount);
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", amount=" + amount +
            '}';
    }
}
