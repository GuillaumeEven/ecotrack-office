package com.ediae.ecotrack_office.assets.service;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public interface ResourceService {

    public void updateStatus(Long resourceId, ResourceStatus newStatus);

}
