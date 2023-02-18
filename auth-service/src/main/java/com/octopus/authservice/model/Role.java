package com.octopus.authservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Role {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", length = 40, nullable = false, unique = true)
    private String name;

    @Column(name = "description",length = 150, nullable = false)
    private String description;
}
