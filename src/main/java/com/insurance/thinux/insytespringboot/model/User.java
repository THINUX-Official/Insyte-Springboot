package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.util.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: THINUX
 * @created: 08-Jan-26 - 12:16 PM
 */

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true, length = 15)
    private String phone;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    @JsonIgnoreProperties("subordinates")
    private User supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @OneToMany(mappedBy = "supervisor")
    @JsonIgnore
    private Set<User> subordinates = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private Set<Lead> leads = new HashSet<>();
}
