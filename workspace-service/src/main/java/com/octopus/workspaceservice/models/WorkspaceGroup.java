package com.octopus.workspaceservice.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "workspace_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WorkspaceGroup {
    @Id
    @javax.persistence.Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    @javax.persistence.Column(name = "name")
    private String name;
    @javax.persistence.Column(name = "description")
    private String description;
    @javax.persistence.Column(name = "created_date")
    @CreatedDate
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;
    @javax.persistence.Column(name = "deleted_date")
    private Date deletedDate;
}
