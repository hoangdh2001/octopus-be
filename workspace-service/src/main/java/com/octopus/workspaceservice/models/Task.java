package com.octopus.workspaceservice.models;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Task implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @javax.persistence.Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @javax.persistence.Column(name = "name")
    private String name;

    @javax.persistence.Column(name = "start_date")
    private Date startDate;

    @javax.persistence.Column(name = "due_date")
    private Date dueDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "assignees", joinColumns = @JoinColumn(name = "task_id"))
    @javax.persistence.Column(name = "assignee_id", nullable = false)
    @ToString.Exclude
    private Set<String> assignees = new HashSet<>();

    @javax.persistence.Column(name="description")
    private String description;

    @javax.persistence.Column(name="status")
    private boolean status;

    @javax.persistence.Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @javax.persistence.Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;

    @javax.persistence.Column(name="deleted_date")
    @LastModifiedDate
    private Date deletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;
}
