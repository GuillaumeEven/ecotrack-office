package com.ediae.ecotrack_office.assets.service;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public interface ResourceService {

    /**
     * Updates the status of any resource (desk or room).
     * Applies resource-type-specific business rules before persisting.
     *
     * @param resourceId the ID of the resource to update
     * @param newStatus  the target status
     * @throws IllegalStateException if business rules prevent the status change
     */
    void updateStatus(Long resourceId, ResourceStatus newStatus);
}
