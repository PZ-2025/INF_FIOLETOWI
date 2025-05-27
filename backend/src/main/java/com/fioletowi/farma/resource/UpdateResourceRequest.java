package com.fioletowi.farma.resource;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for updating an existing Resource.
 *
 * Fields are optional and can be partially updated.
 */
@Getter
@Setter
@Builder
public class UpdateResourceRequest {
    /** Updated name of the resource. */
    private String name;

    /** Updated quantity of the resource. */
    private BigDecimal quantity;

    /** Updated type of the resource. */
    private ResourceType resourceType;
}
