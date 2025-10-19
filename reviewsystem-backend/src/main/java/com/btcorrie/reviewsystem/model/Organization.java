package com.btcorrie.reviewsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // One organization can have many departments
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> departments;

    // Constructor without relationships (useful for creation)
    public Organization(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = true;
    }
}
