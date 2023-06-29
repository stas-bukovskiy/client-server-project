package org.goodstorage.repository;

import org.goodstorage.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    boolean existsById(String id);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdIsNot(String username, String id);

    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    User save(User userToCreate);

    User update(User userToUpdate);

    void delete(String id);
}
