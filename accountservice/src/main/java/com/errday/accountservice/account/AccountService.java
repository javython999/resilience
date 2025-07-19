package com.errday.accountservice.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;


    public void transferWithoutTransaction(Long fromId, Long toId, long amount) {
        Account fromAccount = accountRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 계좌가 존재하지 않습니다."));
        fromAccount.withdraw(amount);
        accountRepository.save(fromAccount);

        Account toAccount = accountRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("받는 계좌가 존재하지 않습니다."));
        toAccount.deposit(amount);
        accountRepository.save(toAccount);
    }

    @Transactional
    public void transferWithTransaction(Long fromId, Long toId, long amount) {
        Account fromAccount = accountRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 계좌가 존재하지 않습니다."));
        fromAccount.withdraw(amount);
        accountRepository.save(fromAccount);

        Account toAccount = accountRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("받는 계좌가 존재하지 않습니다."));
        toAccount.deposit(amount);
        accountRepository.save(toAccount);
    }
}
