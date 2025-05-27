package com.fioletowi.farma.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating a new Resource.
 *
 * <p>This class represents the data required to create a new resource,
 * with validation constraints to ensure proper input.</p>
 *
 * <ul>
 *     <li><b>name</b>: The name of the resource. Must not be blank.</li>
 *     <li><b>quantity</b>: The quantity of the resource. Must not be null.</li>
 *     <li><b>resourceType</b>: The type of the resource. Must not be null.</li>
 * </ul>
 *
 */
@Getter
@Setter
@Builder
public class NewResourceRequest {
    @NotBlank(message = "Resource name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;
}
