package com.resumebuilder.controller;

import com.resumebuilder.model.User;
import com.resumebuilder.repository.UserRepository;
import com.resumebuilder.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalResumes", resumeRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> usersList = userRepository.findAll().stream().map(user -> {
            Map<String, Object> uMap = new HashMap<>();
            uMap.put("id", user.getId());
            uMap.put("username", user.getUsername());
            uMap.put("email", user.getEmail());
            uMap.put("roles", user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
            uMap.put("resumeCount", user.getResumes().size());
            return uMap;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(usersList);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }
        
        // Prevent deleting the main admin account to prevent lockout
        User user = userRepository.findById(id).orElse(null);
        if (user != null && "admin".equalsIgnoreCase(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot delete the main admin account"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
