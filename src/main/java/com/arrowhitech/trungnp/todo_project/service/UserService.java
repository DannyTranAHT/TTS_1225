package com.arrowhitech.trungnp.todo_project.service;

import com.arrowhitech.trungnp.todo_project.entity.Role;
import com.arrowhitech.trungnp.todo_project.entity.User;
import com.arrowhitech.trungnp.todo_project.repository.RoleRepository;
import com.arrowhitech.trungnp.todo_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@NullMarked
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final RoleRepository roleRepository;

    public User register(String username, String rawPassword, String roleName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role " + roleName + " không tồn tại"));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tên người dùng."));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().getName())
                .build();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
