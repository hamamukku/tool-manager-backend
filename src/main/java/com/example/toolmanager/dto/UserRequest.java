// 📦 dtoブロック：ユーザー情報の受け渡し用データ構造

package com.example.toolmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String email;
}
