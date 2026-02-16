package com.insurance.thinux.insytespringboot.model;

import com.insurance.thinux.insytespringboot.util.Auditable;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: THINUX
 * @created: 08-Jan-26 - 12:17 PM
 */

@Entity
@Table(name = "occupations")
public class Occupation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 5)
    private String code;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "occupation", fetch = FetchType.LAZY)
    private Set<Lead> leads = new HashSet<>();
}
