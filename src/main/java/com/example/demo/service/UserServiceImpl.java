package com.example.demo.service;

import com.example.demo.dao.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserPersistenceService userPersistenceService;

    public UserServiceImpl(UserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User savedUser = userPersistenceService.save(user);

        return UserMapper.toDto(savedUser);
    }

    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    @Override
    public UserDto getUserById(Long id) {
        User user = userPersistenceService.findById(id);

        return UserMapper.toDto(user);
    }

    @Cacheable(cacheNames = "users", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userPersistenceService.findAll(pageable)
                .map(UserMapper::toDto);
    }

    @CachePut(cacheNames = "user", key = "#id")
    @CacheEvict(cacheNames = "users", allEntries = true)
    @Override
    public UserDto updateUser(Long id, UserDto userDetails) {
        User existingUser = userPersistenceService.findById(id);
        existingUser.setName(userDetails.name());
        existingUser.setEmail(userDetails.email());

        User updatedUser = userPersistenceService.save(existingUser);

        return UserMapper.toDto(updatedUser);
    }

    @CacheEvict(cacheNames = "user", key = "#id")
    @Override
    public void deleteUser(Long id) {
        User user = userPersistenceService.findById(id);

        userPersistenceService.delete(user);
    }
}