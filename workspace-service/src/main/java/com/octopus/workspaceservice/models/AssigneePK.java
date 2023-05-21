package com.octopus.workspaceservice.models;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class AssigneePK implements Serializable {
    private String userID;

    private UUID task;

    public AssigneePK() {
    }

    public AssigneePK(String userID, UUID task) {
        this.userID = userID;
        this.task = task;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public UUID getTask() {
        return task;
    }

    public void setTask(UUID task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssigneePK that = (AssigneePK) o;

        if (!Objects.equals(userID, that.userID)) return false;
        return Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        int result = userID != null ? userID.hashCode() : 0;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        return result;
    }
}
