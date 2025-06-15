package com.example.toolmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tools")
@Where(clause = "deleted = false")
public class ToolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String tagId; // BLEタグ（MACアドレスやUUIDなど）

    @Builder.Default
    @Column(nullable = false)
    private String status = "available"; // available / borrowed / damaged など

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // 貸出中ユーザー（nullable）

    private LocalDateTime borrowedAt;
    private LocalDateTime returnAt;

    // 初期位置（登録時）
    private Double initialLat;
    private Double initialLng;

    // 現在位置（最新）
    private Double currentLat;
    private Double currentLng;

    @Builder.Default
    private boolean deleted = false; // 論理削除
}
