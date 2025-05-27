package com.fioletowi.farma.task;

/**
 * Enum representing the type of task resource action.
 *
 * ASSIGNED - resource has been assigned/consumed by the task.
 * RETURNED - resource has been returned to inventory after the task.
 */
public enum TaskResourceType {
    ASSIGNED,
    RETURNED
}
