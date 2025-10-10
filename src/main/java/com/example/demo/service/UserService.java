package com.example.demo.service;

import com.example.demo.dto.CreateUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;
    
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
    
    /**
     * Регистрация пользователя - создает в БД и в Keycloak
     */
    public UserDto registerUser(RegisterUserDto registerDto) {
        log.info("Registering new user: {}", registerDto.getUsername());
        
        // 1. Создаем пользователя в Keycloak
        String keycloakUserId = keycloakService.createUser(
            registerDto.getUsername(),
            registerDto.getEmail(),
            registerDto.getFirstName(),
            registerDto.getLastName(),
            registerDto.getPassword()
        );
        
        log.info("User created in Keycloak with ID: {}", keycloakUserId);
        
        // 2. Создаем пользователя в локальной БД
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        // Генерируем fullName из firstName и lastName
        user.setFullName((registerDto.getFirstName() + " " + registerDto.getLastName()).trim());
        
        User savedUser = userRepository.save(user);
        log.info("User created in database with ID: {}", savedUser.getId());
        
        return userMapper.toDto(savedUser);
    }
    
    /**
     * Получить пользователя по username
     */
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }
}














