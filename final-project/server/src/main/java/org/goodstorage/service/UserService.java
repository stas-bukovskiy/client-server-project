package org.goodstorage.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.goodstorage.domain.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getById(String id);

    User create(CreateUserRequest request);

    User update(String id, UpdateUserRequest request);

    void delete(String id);

    @Data
    @AllArgsConstructor
    class CreateUserRequest {
        private String fullName;
        private String username;
        private String password;
        private String role;
    }

    @Data
    class UpdateUserRequest {
        private String fullName;
        private String username;
        private String password;
    }
}
