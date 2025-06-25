package com.example.demo.service;

import com.example.demo.dao.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {


    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto updateUser(Long id, UserDto userDetails);

    void deleteUser(Long id);
}
