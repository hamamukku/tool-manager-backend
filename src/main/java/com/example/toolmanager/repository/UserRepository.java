// 🗃️ repositoryブロック：ユーザーデータのDB操作インターフェース

package com.example.toolmanager.repository;

import com.example.toolmanager.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}
