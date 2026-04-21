package com.cloudocs.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.sms.entity.SmsCode;
import com.cloudocs.sms.mapper.SmsCodeMapper;
import com.cloudocs.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsServiceImpl extends ServiceImpl<SmsCodeMapper, SmsCode> implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5;

    @Override
    public String sendCode(String phone, String type) {
        // 生成6位验证码
        String code = generateCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

        // 标记旧验证码为已使用
        LambdaQueryWrapper<SmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCode::getPhone, phone)
               .eq(SmsCode::getType, type)
               .eq(SmsCode::getUsed, 0);
        SmsCode old = new SmsCode();
        old.setUsed(1);
        this.update(old, wrapper);

        // 保存新验证码
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setUsed(0);
        smsCode.setExpireTime(expireTime);
        this.save(smsCode);

        // 模拟发送短信 - 实际生产环境应调用阿里云API
        log.info("========== 模拟短信发送 ==========");
        log.info("手机号: {}", phone);
        log.info("验证码: {}", code);
        log.info("类型: {}", type);
        log.info("过期时间: {}", expireTime);
        log.info("===================================");

        return code;
    }

    public boolean verifyCode(String phone, String code, String type) {
        LambdaQueryWrapper<SmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCode::getPhone, phone)
               .eq(SmsCode::getCode, code)
               .eq(SmsCode::getType, type)
               .eq(SmsCode::getUsed, 0)
               .gt(SmsCode::getExpireTime, LocalDateTime.now());
        return this.count(wrapper) > 0;
    }

    public void markAsUsed(String phone, String code, String type) {
        LambdaQueryWrapper<SmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCode::getPhone, phone)
               .eq(SmsCode::getCode, code)
               .eq(SmsCode::getType, type);
        SmsCode smsCode = new SmsCode();
        smsCode.setUsed(1);
        this.update(smsCode, wrapper);
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
