package com.ediae.ecotrack_office.assets.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ast_floors")
public class FloorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "floor")
    private List<RoomEntity> rooms;

    // @ManyToOne(optional = true)
    // @JoinColumn(name = "organization_id", nullable = true)
    // private OrganizationEntity organization;

    public FloorEntity() {
    }

    public FloorEntity(Integer level, Boolean isActive) {
        this.level = level;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<RoomEntity> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntity> rooms) {
        this.rooms = rooms;
    }

    // public OrganizationEntity getOrganization() {
    //     return organization;
    // }

    // public void setOrganization(OrganizationEntity organization) {
    //     this.organization = organization;
    // }
}
