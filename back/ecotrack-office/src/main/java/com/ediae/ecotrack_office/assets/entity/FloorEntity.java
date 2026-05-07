package com.ediae.ecotrack_office.assets.entity;

import java.util.List;

<<<<<<< Updated upstream
=======
import java.util.List;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
>>>>>>> Stashed changes
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
<<<<<<< Updated upstream
=======
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
>>>>>>> Stashed changes
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;

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

}
