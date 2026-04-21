package com.cloudocs.folder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.folder.entity.Folder;

import java.util.List;

public interface FolderService extends IService<Folder> {

    /**
     * 创建文件夹
     */
    Folder createFolder(Folder folder);

    /**
     * 更新文件夹
     */
    void updateFolder(Folder folder);

    /**
     * 删除文件夹（软删除）
     */
    void deleteFolder(Long id);

    /**
     * 获取文件夹树形结构
     */
    List<Folder> getFolderTree();

    /**
     * 获取文件夹详情
     */
    Folder getFolderById(Long id);

    /**
     * 获取指定文件夹下的子文件夹
     */
    List<Folder> getFoldersByParentId(Long parentId);
}
