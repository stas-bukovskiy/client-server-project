package org.goodstorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.User;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.UserRepository;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user", id));
    }

    @Override
    public User create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AlreadyExistException("User already exists with username <%s>", request.getFullName());

        User userToCreate = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole().toUpperCase())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        User createdUser = userRepository.save(userToCreate);
        log.info("Created new user <{}> from request <{}>", createdUser, request);
        return createdUser;
    }

    @Override
    public User update(String id, UpdateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AlreadyExistException("User already exists with username <%s>", request.getFullName());

        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user", id));
        userToUpdate.setFullName(request.getFullName());
        userToUpdate.setUsername(request.getUsername());
        userToUpdate.setPassword(request.getPassword());

        User uptededUser = userRepository.update(userToUpdate);
        log.info("Updated user <{}> from request <{}>", uptededUser, request);
        return uptededUser;
    }

    @Override
    public void delete(String id) {
        if (userRepository.existsById(id))
            throw new NotFoundException("user", id);

        userRepository.delete(id);
        log.info("Deleted user with id <{}>", id);
    }
}
