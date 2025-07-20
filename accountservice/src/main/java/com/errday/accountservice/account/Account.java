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
        long currentBalance = balance;

        if (currentBalance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        try {
            System.out.println("[" + Thread.currentThread().getName() + "] 출금 시도. 현재 잔액:" + currentBalance);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        balance = currentBalance - amount;
        System.out.println("[" + Thread.currentThread().getName() + "] 출금 완료. 최종 잔액:" + currentBalance);
    }

    public void deposit(long amount) {
        balance += amount;
    }
}
