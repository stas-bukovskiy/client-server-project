package org.goodstorage.domain;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class User {
    private String id;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
