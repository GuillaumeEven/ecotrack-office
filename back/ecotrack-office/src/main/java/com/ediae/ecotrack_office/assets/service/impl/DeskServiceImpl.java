package com.ediae.ecotrack_office.assets.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.mapper.DeskMapper;
import com.ediae.ecotrack_office.assets.model.DeskModel;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.service.DeskService;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import com.ediae.ecotrack_office.incident.model.IncidentModel;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;

@Service
public class DeskServiceImpl implements DeskService {

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private DeskMapper deskMapper;

    @Override
    public List<DeskModel> getDesks() {
        return deskRepository.findAll()
                .stream()
                .map(deskMapper::fromEntity)
                .toList();
    }

    @Override
    public DeskModel getDeskById(Long deskId) {
        DeskEntity entity = deskRepository.findById(deskId)
                .orElseThrow(() -> new NotFoundException("Desk not found with id: " + deskId));
        DeskModel deskModel = deskMapper.fromEntity(entity);
        return deskModel;
    }

    @Override
    public List<IncidentModel> getIncidentsByDeskId(Long deskId) {
        DeskEntity entity = deskRepository.findById(deskId)
                .orElseThrow(() -> new NotFoundException("Desk not found with id: " + deskId));
        return entity.getIncidents()
                .stream()
                .map(IncidentMapper::toModel)
                .toList();
    }

    @Override
    public List<DeskModel> getDesksByRoomId(Long roomId) {
        List<DeskEntity> entities = deskRepository.findByRoom_Id(roomId);
        return entities.stream()
                .map(deskMapper::fromEntity)
                .toList();
    }

    @Override
    public DeskModel createDesk(DeskRequestDto deskRequestDto) {
        DeskModel deskModel = deskMapper.fromRequestDto(deskRequestDto);
        DeskEntity entity = deskMapper.toEntity(deskModel);
        DeskEntity savedEntity = deskRepository.save(entity);
        return deskMapper.fromEntity(savedEntity);
    }

    @Override
    public DeskModel updateDesk(Long deskId, DeskRequestDto deskRequestDto) {
        DeskEntity entity = deskRepository.findById(deskId)
                .orElseThrow(() -> new NotFoundException("Desk not found with id: " + deskId));
        DeskModel deskModel = deskMapper.fromRequestDto(deskRequestDto);
        deskMapper.updateEntityFromModel(deskModel, entity);
        DeskEntity updatedEntity = deskRepository.save(entity);
        return deskMapper.fromEntity(updatedEntity);
    }

    @Override
    public void deleteDeskById(Long deskId) {
        DeskEntity entity = deskRepository.findById(deskId)
                .orElseThrow(() -> new NotFoundException("Desk not found with id: " + deskId));
        deskRepository.delete(entity);
    }

}
