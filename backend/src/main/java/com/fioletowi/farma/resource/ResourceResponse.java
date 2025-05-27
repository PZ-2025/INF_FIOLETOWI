package com.fioletowi.farma.resource;

import com.fioletowi.farma.task.TaskResource;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for sending resource information in API responses.
 *
 * <p>Contains details about a resource such as its ID, name, quantity, type, and creation timestamp.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    /**
     * Unique identifier of the resource.
     */
    private Long id;

    /**
     * Name of the resource.
     */
    private String name;

    /**
     * Quantity available of the resource.
     */
    private BigDecimal quantity;

    /**
     * Type of the resource.
     */
    private ResourceType resourceType;

    /**
     * Timestamp when the resource was created.
     */
    private LocalDateTime createdAt;

}
