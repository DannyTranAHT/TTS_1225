package com.arrowhitech.trungnp.todo_project.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ví dụ: ROLE_USER, ROLE_ADMIN
}

