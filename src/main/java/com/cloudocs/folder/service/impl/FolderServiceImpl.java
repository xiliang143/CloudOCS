package com.cloudocs.folder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.folder.entity.Folder;
import com.cloudocs.folder.mapper.FolderMapper;
import com.cloudocs.folder.service.FolderService;
import com.cloudocs.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {

    @Override
    @Transactional
    public Folder createFolder(Folder folder) {
        if (folder.getName() == null || folder.getName().trim().isEmpty()) {
            folder.setName("新建文件夹");
        }
        if (folder.getParentId() == null) {
            folder.setParentId(0L);
        }
        folder.setCreatorId(UserContext.getUserId());
        if (folder.getCreatorName() == null) {
            folder.setCreatorName(UserContext.getUser() != null ? UserContext.getUser().getNickname() : null);
        }
        folder.setDeleted(0);
        this.save(folder);
        return folder;
    }

    @Override
    @Transactional
    public void updateFolder(Folder folder) {
        if (folder.getId() == null) {
            throw new IllegalArgumentException("文件夹ID不能为空");
        }
        Folder existing = this.getById(folder.getId());
        if (existing == null) {
            throw new IllegalArgumentException("文件夹不存在");
        }
        this.updateById(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long id) {
        Folder folder = this.getById(id);
        if (folder == null) {
            throw new IllegalArgumentException("文件夹不存在");
        }
        folder.setDeleted(1);
        this.updateById(folder);
    }

    @Override
    public Folder getFolderById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Folder> getFoldersByParentId(Long parentId) {
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Folder::getParentId, parentId)
               .eq(Folder::getDeleted, 0)
               .orderByAsc(Folder::getId);
        return this.list(wrapper);
    }

    @Override
    public List<Folder> getFolderTree() {
        // 获取所有文件夹，构建树形结构
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Folder::getDeleted, 0)
               .orderByAsc(Folder::getId);
        List<Folder> allFolders = this.list(wrapper);

        // 构建树形结构
        return buildTree(allFolders, 0L);
    }

    /**
     * 构建树形结构
     */
    private List<Folder> buildTree(List<Folder> allFolders, Long parentId) {
        List<Folder> tree = new ArrayList<>();
        for (Folder folder : allFolders) {
            Long folderParentId = folder.getParentId();
            // 处理 parentId 为 0L 或 null 的情况，都视为根目录
            if ((parentId == null && (folderParentId == null || folderParentId == 0L))
                    || (parentId != null && parentId.equals(folderParentId))) {
                List<Folder> children = buildTree(allFolders, folder.getId());
                if (!children.isEmpty()) {
                    folder.setChildren(children);
                }
                tree.add(folder);
            }
        }
        return tree;
    }
}
