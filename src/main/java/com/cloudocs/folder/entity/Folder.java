package com.cloudocs.folder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("folders")
public class Folder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父文件夹ID，0表示根目录
     */
    private Long parentId;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 是否删除：0-正常，1-已删除
     */
    private Integer deleted;

    /**
     * 子文件夹（仅用于树形结构，不存储到数据库）
     */
    @TableField(exist = false)
    private List<Folder> children = new ArrayList<>();
}
