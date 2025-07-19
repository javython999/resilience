package com.errday.accountservice.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (accountRepository.count() == 0) {
            log.info("기초 데이터를 생성합니다.");
            accountRepository.save(new Account("홍길동", 10000L));
            accountRepository.save(new Account("임꺽정", 10000L));
            log.info("데이터 생성 완료.");
        }
    }
}
