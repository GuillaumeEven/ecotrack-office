package com.ediae.ecotrack_office.assets.service;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.model.ResourceModel;

public interface ResourceService {

    public void updateStatus(Long resourceId, ResourceStatus newStatus);

    public ResourceModel getResourceById(Long resourceId);

}
