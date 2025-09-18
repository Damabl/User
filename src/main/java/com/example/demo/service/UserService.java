package com.example.demo.service;

import com.example.demo.dto.CreateUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDto(user);
    }
    
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = userMapper.toEntity(createUserDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    public UserDto updateUser(Long id, CreateUserDto createUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userMapper.updateEntity(createUserDto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}



