package com.ediae.ecotrack_office.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.users.repository.UserRepository;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final DeskRepository deskRepository;
    private final ReservationRepository reservationRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataLoader(OrganizationRepository organizationRepository,
                      UserRepository userRepository,
                      FloorRepository floorRepository,
                      RoomRepository roomRepository,
                      DeskRepository deskRepository,
                      ReservationRepository reservationRepository,
                      JdbcTemplate jdbcTemplate) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.deskRepository = deskRepository;
        this.reservationRepository = reservationRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        resetDatabase();
        OrganizationEntity org = createOrgIfMissing();
        createFloorsIfMissing(org);
        createAdminIfMissing(org);
        createTechAndEmployeeIfMissing(org);
        createRoomsAndDesksIfMissing(org);

        OrganizationEntity org2 = createSecondOrgIfMissing();
        createFloorsIfMissing(org2);
        createRoomsAndDesksIfMissing(org2);

        createReservationsIfMissing(org);
    }

    /**
     * Wipes all dev data and resets AUTO_INCREMENT to 1 so seeded IDs are always predictable:
     * org=1, admin=1, tech=2, employee=3, floor0=1, floor1=2, roomA=1, roomB=2, desk1-3=1-3.
     * MySQL TRUNCATE resets AUTO_INCREMENT automatically; FK checks disabled during truncation.
     */
    private void resetDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE analitics_report");
        jdbcTemplate.execute("TRUNCATE TABLE incidents");
        jdbcTemplate.execute("TRUNCATE TABLE reservation");
        jdbcTemplate.execute("TRUNCATE TABLE ast_desks");
        jdbcTemplate.execute("TRUNCATE TABLE ast_rooms");
        jdbcTemplate.execute("TRUNCATE TABLE ast_resources");
        jdbcTemplate.execute("TRUNCATE TABLE ast_floors");
        jdbcTemplate.execute("TRUNCATE TABLE usr_users");
        jdbcTemplate.execute("TRUNCATE TABLE organizations");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        log.info("Dev database reset: all tables truncated, AUTO_INCREMENT reset to 1.");
    }

    private OrganizationEntity createOrgIfMissing() {
        final String name = "EcoTrack";
        return organizationRepository.findAll()
                .stream()
                .filter(o -> name.equalsIgnoreCase(o.getName()))
                .findFirst()
                .orElseGet(() -> {
                    OrganizationEntity o = new OrganizationEntity();
                    o.setName(name);
                    o.setCif("CIF-000000");
                    o.setAddress("Unknown address");
                    o.setEmail("contact@ecotrack.local");
                    o.setEndSubscription(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
                    o.setIsActive(Boolean.TRUE);
                    o.setCreatedAt(LocalDateTime.now());
                    OrganizationEntity saved = organizationRepository.save(o);
                    log.info("Seeded organization '{}'", saved.getName());
                    return saved;
                });
    }

    private OrganizationEntity createSecondOrgIfMissing() {
        final String name = "GreenOffice Solutions";
        return organizationRepository.findAll()
                .stream()
                .filter(o -> name.equalsIgnoreCase(o.getName()))
                .findFirst()
                .orElseGet(() -> {
                    OrganizationEntity o = new OrganizationEntity();
                    o.setName(name);
                    o.setCif("CIF-000001");
                    o.setAddress("Tech Park, Building B");
                    o.setEmail("contact@greenoffice.local");
                    o.setEndSubscription(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
                    o.setIsActive(Boolean.TRUE);
                    o.setCreatedAt(LocalDateTime.now());
                    OrganizationEntity saved = organizationRepository.save(o);
                    log.info("Seeded organization '{}'", saved.getName());
                    return saved;
                });
    }

    private void createAdminIfMissing(OrganizationEntity org) {
        final String adminEmail = "admin@ecotrack.local";
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user '{}' exists, skipping.", adminEmail);
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setEmail(adminEmail);
        admin.setPasswordHash("password");
        admin.setFirstName("Admin");
        admin.setLastName("Ecotrack");
        admin.setRole(Role.ADMIN);
        admin.setOrganization(org);
        admin.setIsActive(Boolean.TRUE);
        admin.setConsentGiven(Boolean.TRUE);
        admin.setPreferencesJson("{}");
        admin.setCreatedAt(LocalDateTime.now());

        userRepository.save(admin);
        log.info("Seeded admin user '{}'", adminEmail);
    }

    private void createTechAndEmployeeIfMissing(OrganizationEntity org) {
        final String techEmail = "tech@ecotrack.local";
        final String empEmail = "employee@ecotrack.local";

        if (!userRepository.existsByEmail(techEmail)) {
            UserEntity tech = new UserEntity();
            tech.setEmail(techEmail);
            tech.setPasswordHash("password");
            tech.setFirstName("Tech");
            tech.setLastName("User");
            tech.setRole(Role.TECHNICIAN);
            tech.setOrganization(org);
            tech.setIsActive(Boolean.TRUE);
            tech.setConsentGiven(Boolean.TRUE);
            tech.setPreferencesJson("{}");
            tech.setCreatedAt(LocalDateTime.now());
            userRepository.save(tech);
            log.info("Seeded technician '{}'", techEmail);
        } else {
            log.info("Technician '{}' exists, skipping.", techEmail);
        }

        if (!userRepository.existsByEmail(empEmail)) {
            UserEntity emp = new UserEntity();
            emp.setEmail(empEmail);
            emp.setPasswordHash("password");
            emp.setFirstName("Employee");
            emp.setLastName("User");
            emp.setRole(Role.EMPLOYEE);
            emp.setOrganization(org);
            emp.setIsActive(Boolean.TRUE);
            emp.setConsentGiven(Boolean.TRUE);
            emp.setPreferencesJson("{}");
            emp.setCreatedAt(LocalDateTime.now());
            userRepository.save(emp);
            log.info("Seeded employee '{}'", empEmail);
        } else {
            log.info("Employee '{}' exists, skipping.", empEmail);
        }
    }

    private void createFloorsIfMissing(OrganizationEntity org) {
        boolean hasLevel0 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .anyMatch(f -> Objects.equals(f.getLevel(), 0));
        if (!hasLevel0) {
            FloorEntity f0 = new FloorEntity(0, true, org);
            floorRepository.save(f0);
            log.info("Seeded floor level 0 for organization {}", org.getName());
        }

        boolean hasLevel1 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .anyMatch(f -> Objects.equals(f.getLevel(), 1));
        if (!hasLevel1) {
            FloorEntity f1 = new FloorEntity(1, true, org);
            floorRepository.save(f1);
            log.info("Seeded floor level 1 for organization {}", org.getName());
        }
    }

    private void createRoomsAndDesksIfMissing(OrganizationEntity org) {
        // pick floor 0 (preferentially) to attach rooms
        FloorEntity floor0 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .filter(f -> Objects.equals(f.getLevel(), 0))
                .findFirst()
                .orElseGet(() -> floorRepository.findByOrganizationId(org.getId()).stream().findFirst().orElse(null));

        if (floor0 == null) {
            log.warn("No floor found for organization {}, skipping room/desk seeding.", org.getName());
            return;
        }

        // Create desk_area rooms on floor 0
        createDeskAreaRoomsWithDesks(floor0, "Desk Area A", 50.0);
        createDeskAreaRoomsWithDesks(floor0, "Desk Area B", 40.0);
        createDeskAreaRoomsWithDesks(floor0, "Desk Area C", 45.0);

        // Create meeting rooms on floor 0
        createMeetingRoom(floor0, "Meeting Room 1", 25.0);
        createMeetingRoom(floor0, "Meeting Room 2", 30.0);

        // Get floor level 1 and add rooms there if exists
        FloorEntity floor1 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .filter(f -> Objects.equals(f.getLevel(), 1))
                .findFirst()
                .orElse(null);

        if (floor1 != null) {
            // Create desk_area rooms on floor 1
            createDeskAreaRoomsWithDesks(floor1, "Desk Area D", 35.0);
            createDeskAreaRoomsWithDesks(floor1, "Desk Area E", 38.0);

            // Create meeting rooms on floor 1
            createMeetingRoom(floor1, "Meeting Room 3", 28.0);
            createMeetingRoom(floor1, "Meeting Room 4", 32.0);
        }
    }

    /**
     * Helper method to create a desk_area room with 10 desks if it doesn't exist
     */
    private void createDeskAreaRoomsWithDesks(FloorEntity floor, String roomName, Double area) {
        RoomEntity room = roomRepository.findByFloor_Id(floor.getId())
                .stream()
                .filter(r -> roomName.equalsIgnoreCase(r.getName()))
                .findFirst()
                .orElseGet(() -> {
                    RoomEntity r = new RoomEntity(
                            roomName,
                            ResourceStatus.AVAILABLE,
                            "basic-equipment",
                            RoomType.DESK_AREA,
                            area,
                            floor,
                            10
                    );
                    RoomEntity saved = roomRepository.save(r);
                    log.info("Seeded desk_area room '{}'", saved.getName());
                    return saved;
                });

        // Create 10 desks for this desk_area room
        for (int i = 1; i <= 10; i++) {
            final String deskName = room.getName() + " - Desk " + i;
            boolean exists = deskRepository.findByRoom_Id(room.getId())
                    .stream()
                    .anyMatch(d -> deskName.equalsIgnoreCase(d.getName()));
            if (!exists) {
                DeskEntity desk = new DeskEntity(deskName, ResourceStatus.AVAILABLE, "chair,monitor", room);
                deskRepository.save(desk);
            }
        }
        log.info("Seeded 10 desks in room '{}'", room.getName());
    }

    /**
     * Helper method to create a meeting room if it doesn't exist
     */
    private void createMeetingRoom(FloorEntity floor, String roomName, Double area) {
        boolean exists = roomRepository.findByFloor_Id(floor.getId())
                .stream()
                .anyMatch(r -> roomName.equalsIgnoreCase(r.getName()));
        if (!exists) {
            RoomEntity r = new RoomEntity(
                    roomName,
                    ResourceStatus.AVAILABLE,
                    "video-conference,whiteboard",
                    RoomType.MEETING_ROOM,
                    area,
                    floor,
                    20
            );
            RoomEntity saved = roomRepository.save(r);
            log.info("Seeded meeting_room '{}'", saved.getName());
        }
    }

    /**
     * Helper method to create reservations for specific desks on given dates
     */
    private void createReservationsIfMissing(OrganizationEntity org) {
        // Get employee user
        UserEntity employee = userRepository.findByEmail("employee@ecotrack.local")
                .orElse(null);

        if (employee == null) {
            log.warn("Employee user not found, skipping reservation seeding.");
            return;
        }

        // Create reservations for 14/07/2026: desks 2, 5, 6
        LocalDate date1 = LocalDate.of(2026, 7, 14);
        createReservationForDesks(org, employee, date1, new int[]{2, 5, 6});

        // Create reservations for 15/07/2026: desks 2, 7, 9
        LocalDate date2 = LocalDate.of(2026, 7, 15);
        createReservationForDesks(org, employee, date2, new int[]{2, 7, 9});
    }

    /**
     * Helper method to create reservations for specific desk numbers on a given date
     */
    private void createReservationForDesks(OrganizationEntity org, UserEntity user, LocalDate date, int[] deskNumbers) {
        for (int deskNum : deskNumbers) {
            // Find the desk by name pattern
            String deskNamePattern = "Desk " + deskNum;
            DeskEntity desk = deskRepository.findAll()
                    .stream()
                    .filter(d -> d.getName().contains(deskNamePattern))
                    .findFirst()
                    .orElse(null);

            if (desk != null) {
                // Check if reservation already exists
                boolean reservationExists = reservationRepository.findByResourceId(desk.getId())
                        .stream()
                        .anyMatch(r -> r.getDate().equals(date));

                if (!reservationExists) {
                    ReservationEntity reservation = new ReservationEntity(
                            date,
                            ReservationStatus.CONFIRMED,
                            LocalDateTime.now(),
                            user,
                            desk
                    );
                    reservationRepository.save(reservation);
                    log.info("Seeded reservation for desk '{}' on date {}", desk.getName(), date);
                } else {
                    log.info("Reservation for desk '{}' on date {} already exists, skipping.", desk.getName(), date);
                }
            } else {
                log.warn("Desk with pattern '{}' not found, skipping reservation.", deskNamePattern);
            }
        }
        log.info("Seeded 10 desks in room '{}'", room.getName());
    }

    /**
     * Helper method to create a meeting room if it doesn't exist
     */
    private void createMeetingRoom(FloorEntity floor, String roomName, Double area) {
        boolean exists = roomRepository.findByFloor_Id(floor.getId())
                .stream()
                .anyMatch(r -> roomName.equalsIgnoreCase(r.getName()));
        if (!exists) {
            RoomEntity r = new RoomEntity(
                    roomName,
                    ResourceStatus.AVAILABLE,
                    "video-conference,whiteboard",
                    RoomType.MEETING_ROOM,
                    area,
                    floor,
                    20
            );
            RoomEntity saved = roomRepository.save(r);
            log.info("Seeded meeting_room '{}'", saved.getName());
        }
    }
}
