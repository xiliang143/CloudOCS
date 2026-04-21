package com.cloudocs.sms.service;

public interface SmsService {
    String sendCode(String phone, String type);
    boolean verifyCode(String phone, String code, String type);
    void markAsUsed(String phone, String code, String type);
}
