package com.errday.mailservice.exception;

import java.util.List;

public class ExceptionDemo {

    public static void main(String[] args) {
        List<String> emails = List.of("user@example.com", "invalid-email", "admin@exception.com");

        List<String> validEmailsChecked = emails.stream()
                .map(email -> {
                    try {
                        return validateEmailChecked(email);
                    } catch (CustomCheckedException e) {
                        System.out.println("checked Exception 처리: " + e.getMessage());
                        return null;
                    }
                })
                .filter(email -> email != null)
                .toList();
        System.out.println("유효한 이메일 (Checked): " + validEmailsChecked);

        try {
            List<String> validEmailUnchecked = emails.stream()
                    .map(email -> validateEmailUnchecked(email))
                    .toList();
            System.out.println("유효한 이메일 (Unchecked): " + validEmailUnchecked);
        } catch (CustomUncheckedException e) {
            System.out.println("unchecked Exception 처리: " + e.getMessage());
        }
    }

    private static String validateEmailChecked(String email) throws CustomCheckedException {
        if (email.contains("@")) {
            return email;
        } else {
            throw new CustomCheckedException("유효하지 않은 이메일" + email);
        }
    }

    private static String validateEmailUnchecked(String email) {
        if (email.contains("@")) {
            return email;
        } else {
            throw new CustomUncheckedException("유효하지 않은 이메일: " + email);
        }
    }


}
