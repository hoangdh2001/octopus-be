package com.octopus.authservice.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "connections")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Connection implements Serializable {
    @Id
    @Column(name = "connection_id")
    private String connectionID;
}
