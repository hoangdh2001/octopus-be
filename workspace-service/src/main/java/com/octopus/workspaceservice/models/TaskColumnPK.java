package com.octopus.workspaceservice.models;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TaskColumnPK implements Serializable {
    private UUID task;
    private UUID column;

    public TaskColumnPK() {
    }

    public TaskColumnPK(UUID task, UUID column) {
        this.task = task;
        this.column = column;
    }

    public UUID getTask() {
        return task;
    }

    public void setTask(UUID task) {
        this.task = task;
    }

    public UUID getColumn() {
        return column;
    }

    public void setColumn(UUID column) {
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskColumnPK that = (TaskColumnPK) o;
        return Objects.equals(task, that.task) && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, column);
    }
}
