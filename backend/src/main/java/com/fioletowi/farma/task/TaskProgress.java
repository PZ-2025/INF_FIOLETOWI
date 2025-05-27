package com.fioletowi.farma.task;

/**
 * Enum representing the progress status of a task.
 * Each value is associated with a percentage indicating completion level.
 */
public enum TaskProgress {

    /**
     * Task has not been started yet.
     */
    NOT_STARTED(0),

    /**
     * Task is in early progress (e.g., just begun).
     */
    EARLY_PROGRESS(25),

    /**
     * Task is halfway done.
     */
    MIDWAY(50),

    /**
     * Task is past the halfway point.
     */
    PAST_HALF(75),

    /**
     * Task is close to completion.
     */
    NEAR_COMPLETION(90),

    /**
     * Task is completed but not yet approved.
     */
    COMPLETED(99),

    /**
     * Task is completed and approved.
     */
    COMPLETED_ACCEPTED(100),

    /**
     * Task was completed but became overdue or was not finished on time.
     */
    COMPLETED_TERMINATED(100),

    /**
     * Task was cancelled and will not be completed.
     */
    CANCELLED(0),

    /**
     * Task was marked as failed.
     */
    FAILED(100);

    /**
     * The percentage of task completion associated with the progress status.
     */
    private final int percentage;

    TaskProgress(int percentage) {
        this.percentage = percentage;
    }

    /**
     * Returns the percentage of completion for the progress status.
     *
     * @return the associated completion percentage
     */
    public int getPercentage() {
        return percentage;
    }
}
