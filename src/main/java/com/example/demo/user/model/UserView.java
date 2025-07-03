package com.example.demo.user.model;

import com.example.demo.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserView {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
