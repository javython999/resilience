package com.errday.accountservice.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;

    private long balance;

    public Account(String owner, long balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public void withdraw(long amount) {
        if (balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        balance -= amount;
    }

    public void deposit(long amount) {
        balance += amount;
    }
}
