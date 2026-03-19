package com.sinergia.logistics.infrastructure.adapter.in.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}