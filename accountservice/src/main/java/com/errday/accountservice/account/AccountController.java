package com.errday.accountservice.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/no-tx/from/{fromId}/to/{toId}/amount/{amount}")
    public ResponseEntity<String> transferWithoutTx(@PathVariable Long fromId, @PathVariable Long toId, @PathVariable long amount) {
        try {
            accountService.transferWithoutTransaction(fromId, toId, amount);
            return ResponseEntity.ok("이체 성공 (No Transaction)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/with-tx/from/{fromId}/to/{toId}/amount/{amount}")
    public ResponseEntity<String> transferWithTx(@PathVariable Long fromId, @PathVariable Long toId, @PathVariable long amount) {
        try {
            accountService.transferWithTransaction(fromId, toId, amount);
            return ResponseEntity.ok("이체 성공 (With Transaction)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
