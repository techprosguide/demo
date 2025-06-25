package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.exception.DuplicateEmailException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserPersistenceService userPersistenceService;

    public UserServiceImpl(UserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating user with email: {}", userDto.email());

        User user = UserMapper.toEntity(userDto);
        User savedUser = userPersistenceService.save(user);

        logger.info("User created with ID: {}", savedUser.getId());

        return UserMapper.toDto(savedUser);
    }

    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    @Override
    public UserDto getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);

        User user = userPersistenceService.findById(id);

        return UserMapper.toDto(user);
    }

    @Cacheable(cacheNames = "users", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return userPersistenceService.findAll(pageable)
                .map(UserMapper::toDto);
    }

    @CachePut(cacheNames = "user", key = "#id")
    @CacheEvict(cacheNames = "users", allEntries = true)
    @Override
    public UserDto updateUser(Long id, UserDto userDetails) {
        logger.debug("Updating user with ID: {}", id);

        validateEmailUniqueness(id, userDetails);

        User existingUser = userPersistenceService.findById(id);
        existingUser.setName(userDetails.name());
        existingUser.setEmail(userDetails.email());

        User updatedUser = userPersistenceService.save(existingUser);

        logger.info("User updated with ID: {}", updatedUser.getId());

        return UserMapper.toDto(updatedUser);
    }

    private void validateEmailUniqueness(Long id, UserDto userDetails) {
        User userWithEmail = userPersistenceService.findByEmail(userDetails.email());
        if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
            logger.error("Duplicate email detected: {}", userDetails.email());

            throw new DuplicateEmailException(userDetails.email());
        }
    }

    @CacheEvict(cacheNames = {"user", "users"}, key = "#id", allEntries = true)
    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        User user = userPersistenceService.findById(id);

        userPersistenceService.delete(user);

        logger.info("User deleted with ID: {}", id);
    }
}