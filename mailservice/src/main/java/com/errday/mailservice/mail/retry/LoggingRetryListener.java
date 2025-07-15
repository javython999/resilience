package com.errday.mailservice.mail.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component("loggingRetryListener")
public class LoggingRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        // 첫 시도 전에 호출됩니다. 특별한 처리가 필요 없으면 true를 반환합니다.
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // 모든 재시도가 끝난 후 호출됩니다.
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // 재시도할 때마다 호출됩니다.
        log.info("재시도 {}회: {}", context.getRetryCount(), throwable.getMessage());
    }
}
