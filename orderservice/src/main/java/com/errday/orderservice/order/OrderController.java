package com.errday.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders/problematic")
    public ResponseEntity<String> problematicOrder(@RequestBody OrderRequestDto request) {
        try {
            orderService.placeOrder_Problematic(request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("주문 실패: " + e.getMessage());
        }

        return ResponseEntity.ok("주문 성공!");
    }

    @PostMapping("/orders/solution")
    public ResponseEntity<String> solutionOrder(@RequestBody OrderRequestDto request) {
        try {
            orderService.placeOrder_Withoutbox(request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("주문 실패: " + e.getMessage());
        }

        return ResponseEntity.ok("주문 선공!");
    }

    @PostMapping("/orders/saga-fail-test")
    public ResponseEntity<String> createOrderWithSagaFail(@RequestBody OrderRequestDto request) {
        orderService.placeOrderSaga(request);

        return ResponseEntity.ok("주문 실패?");
    }
}
