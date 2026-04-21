package com.cloudocs.common;

import lombok.Data;

@Data
public class UploadResult {
    private String url;
    private String filename;
    private long size;
}
