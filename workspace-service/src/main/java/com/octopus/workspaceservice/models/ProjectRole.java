package com.octopus.workspaceservice.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public enum ProjectRole {
    VIEWER,
    OWNER,
    MEMBER;
}
