package com.ediae.ecotrack_office.assets.service;

import java.util.List;

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.assets.model.DeskModel;

public interface DeskService {

    public DeskModel getDeskById(Long deskId);

    public DeskModel createDesk(DeskRequestDto deskRequestDto);

    public DeskModel updateDesk(Long deskId, DeskRequestDto deskRequestDto);

    public void deleteDeskById(Long deskId);

    public List<DeskModel> getDesksByRoomId(Long roomId);


}
