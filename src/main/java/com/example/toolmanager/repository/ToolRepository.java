// 🗃️ repositoryブロック：工具エンティティのDB操作

package com.example.toolmanager.repository;

import com.example.toolmanager.entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
    Optional<ToolEntity> findByIdAndIsDeletedFalse(Long id);
}