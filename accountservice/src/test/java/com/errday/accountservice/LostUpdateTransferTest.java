package com.errday.accountservice;

import com.errday.accountservice.account.Account;
import com.errday.accountservice.account.AccountRepository;
import com.errday.accountservice.account.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LostUpdateTransferTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        fromAccount = accountRepository.save(new Account("홍길동", 10000L));
        toAccount = accountRepository.save(new Account("임꺽정", 10000L));
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    void lostUpdateOnConcurrentTransfer() throws InterruptedException {
        int numberOfThreads = 2;
        long transferAmount = 1000L;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    accountService.transferWithoutTransaction(fromAccount.getId(), toAccount.getId(), transferAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Account finalFromAccount = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account finalToAccount = accountRepository.findById(toAccount.getId()).orElseThrow();


        long expectedFromBalance = 10000L - (transferAmount * numberOfThreads);
        long expectedToBalance = 10000L + (transferAmount * numberOfThreads);


        System.out.println("--- 결과 ---");
        System.out.println("홍길동 최종 잔액 (기대): " + expectedFromBalance);
        System.out.println("홍길동 최종 잔액 (실제): " + finalFromAccount.getBalance());
        System.out.println("임꺽정 최종 잔액 (기대): " + expectedToBalance);
        System.out.println("임꺽정 최종 잔액 (실제): " + finalToAccount.getBalance());
        System.out.println("총액 (기대): 20000");
        System.out.println("총액 (실제): " + (finalFromAccount.getBalance() + finalToAccount.getBalance()));
        System.out.println("-----------");


        assertThat(finalFromAccount.getBalance()).isNotEqualTo(expectedFromBalance);
        assertThat(finalToAccount.getBalance()).isNotEqualTo(expectedToBalance);


    }

    @Test
    void updateOnConcurrentTransfer() throws InterruptedException {
        int numberOfThreads = 2;
        long transferAmount = 1000L;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    accountService.transferWithTransaction(fromAccount.getId(), toAccount.getId(), transferAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Account finalFromAccount = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account finalToAccount = accountRepository.findById(toAccount.getId()).orElseThrow();


        long expectedFromBalance = 10000L - (transferAmount * numberOfThreads);
        long expectedToBalance = 10000L + (transferAmount * numberOfThreads);


        assertThat(finalFromAccount.getBalance()).isEqualTo(expectedFromBalance);
        assertThat(finalToAccount.getBalance()).isEqualTo(expectedToBalance);

    }


}
