package com.cloudocs.tenant.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantRegisterDTO {
    private String name;
    private String code;
    private Integer packageType;
    private String contact;
    private String phone;
    private String email;
    private String remark;
}
