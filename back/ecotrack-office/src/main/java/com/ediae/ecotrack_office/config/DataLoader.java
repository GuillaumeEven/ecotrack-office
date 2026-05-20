// package com.ediae.ecotrack_office.config;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Profile;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.ediae.ecotrack_office.assets.repository.DeskRepository;
// import com.ediae.ecotrack_office.assets.repository.FloorRepository;
// import com.ediae.ecotrack_office.assets.repository.RoomRepository;
// import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
// import com.ediae.ecotrack_office.users.repository.UserRepository;

// @Component
// @Profile("dev")
// public class DataLoader implements CommandLineRunner {

//     private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

//     private final OrganizationRepository organizationRepository;
//     private final UserRepository userRepository;
//     private final FloorRepository floorRepository;
//     private final RoomRepository roomRepository;
//     private final DeskRepository deskRepository;
//     private final JdbcTemplate jdbcTemplate;

//     public DataLoader(OrganizationRepository organizationRepository,
//                       UserRepository userRepository,
//                       FloorRepository floorRepository,
//                       RoomRepository roomRepository,
//                       DeskRepository deskRepository,
//                       JdbcTemplate jdbcTemplate) {
//         this.organizationRepository = organizationRepository;
//         this.userRepository = userRepository;
//         this.floorRepository = floorRepository;
//         this.roomRepository = roomRepository;
//         this.deskRepository = deskRepository;
//         this.jdbcTemplate = jdbcTemplate;
//     }

//     @Override
//     @Transactional
//     public void run(String... args) {
//         // resetDatabase();
//         // OrganizationEntity org = createOrgIfMissing();
//         // createFloorsIfMissing(org);
//         // createAdminIfMissing(org);
//         // createTechAndEmployeeIfMissing(org);
//         // createRoomsAndDesksIfMissing(org);
//     }

//     // /**
//     //  * Wipes all dev data and resets AUTO_INCREMENT to 1 so seeded IDs are always predictable:
//     //  * org=1, admin=1, tech=2, employee=3, floor0=1, floor1=2, roomA=1, roomB=2, desk1-3=1-3.
//     //  * MySQL TRUNCATE resets AUTO_INCREMENT automatically; FK checks disabled during truncation.
//     //  */
//     // private void resetDatabase() {
//     //     jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
//     //     jdbcTemplate.execute("TRUNCATE TABLE analitics_report");
//     //     jdbcTemplate.execute("TRUNCATE TABLE incidents");
//     //     jdbcTemplate.execute("TRUNCATE TABLE reservation");
//     //     jdbcTemplate.execute("TRUNCATE TABLE ast_desks");
//     //     jdbcTemplate.execute("TRUNCATE TABLE ast_rooms");
//     //     jdbcTemplate.execute("TRUNCATE TABLE ast_resources");
//     //     jdbcTemplate.execute("TRUNCATE TABLE ast_floors");
//     //     jdbcTemplate.execute("TRUNCATE TABLE usr_users");
//     //     jdbcTemplate.execute("TRUNCATE TABLE organizations");
//     //     jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
//     //     log.info("Dev database reset: all tables truncated, AUTO_INCREMENT reset to 1.");
//     // }

//     // private OrganizationEntity createOrgIfMissing() {
//     //     final String name = "EcoTrack";
//     //     return organizationRepository.findAll()
//     //             .stream()
//     //             .filter(o -> name.equalsIgnoreCase(o.getName()))
//     //             .findFirst()
//     //             .orElseGet(() -> {
//     //                 OrganizationEntity o = new OrganizationEntity();
//     //                 o.setName(name);
//     //                 o.setCIF("CIF-000000");
//     //                 o.setAddress("Unknown address");
//     //                 o.setEmail("contact@ecotrack.local");
//     //                 o.setEndSubscription(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
//     //                 o.setIsActive(Boolean.TRUE);
//     //                 o.setCreatedAt(LocalDateTime.now());
//     //                 OrganizationEntity saved = organizationRepository.save(o);
//     //                 log.info("Seeded organization '{}'", saved.getName());
//     //                 return saved;
//     //             });
//     // }

//     // private void createAdminIfMissing(OrganizationEntity org) {
//     //     final String adminEmail = "admin@ecotrack.local";
//     //     if (userRepository.existsByEmail(adminEmail)) {
//     //         log.info("Admin user '{}' exists, skipping.", adminEmail);
//     //         return;
//     //     }

//     //     UserEntity admin = new UserEntity();
//     //     admin.setEmail(adminEmail);
//     //     admin.setPasswordHash("password");
//     //     admin.setFirstName("Admin");
//     //     admin.setLastName("Ecotrack");
//     //     admin.setRole(Role.ADMIN);
//     //     admin.setOrganization(org);
//     //     admin.setIsActive(Boolean.TRUE);
//     //     admin.setConsentGiven(Boolean.TRUE);
//     //     admin.setPreferencesJson("{}");
//     //     admin.setCreatedAt(LocalDateTime.now());

//     //     userRepository.save(admin);
//     //     log.info("Seeded admin user '{}'", adminEmail);
//     // }

//     // private void createTechAndEmployeeIfMissing(OrganizationEntity org) {
//     //     final String techEmail = "tech@ecotrack.local";
//     //     final String empEmail = "employee@ecotrack.local";

//     //     if (!userRepository.existsByEmail(techEmail)) {
//     //         UserEntity tech = new UserEntity();
//     //         tech.setEmail(techEmail);
//     //         tech.setPasswordHash("password");
//     //         tech.setFirstName("Tech");
//     //         tech.setLastName("User");
//     //         tech.setRole(Role.TECHNICIAN);
//     //         tech.setOrganization(org);
//     //         tech.setIsActive(Boolean.TRUE);
//     //         tech.setConsentGiven(Boolean.TRUE);
//     //         tech.setPreferencesJson("{}");
//     //         tech.setCreatedAt(LocalDateTime.now());
//     //         userRepository.save(tech);
//     //         log.info("Seeded technician '{}'", techEmail);
//     //     } else {
//     //         log.info("Technician '{}' exists, skipping.", techEmail);
//     //     }

//     //     if (!userRepository.existsByEmail(empEmail)) {
//     //         UserEntity emp = new UserEntity();
//     //         emp.setEmail(empEmail);
//     //         emp.setPasswordHash("password");
//     //         emp.setFirstName("Employee");
//     //         emp.setLastName("User");
//     //         emp.setRole(Role.EMPLOYEE);
//     //         emp.setOrganization(org);
//     //         emp.setIsActive(Boolean.TRUE);
//     //         emp.setConsentGiven(Boolean.TRUE);
//     //         emp.setPreferencesJson("{}");
//     //         emp.setCreatedAt(LocalDateTime.now());
//     //         userRepository.save(emp);
//     //         log.info("Seeded employee '{}'", empEmail);
//     //     } else {
//     //         log.info("Employee '{}' exists, skipping.", empEmail);
//     //     }
//     // }

//     // private void createFloorsIfMissing(OrganizationEntity org) {
//     //     boolean hasLevel0 = floorRepository.findByOrganizationId(org.getId())
//     //             .stream()
//     //             .anyMatch(f -> Objects.equals(f.getLevel(), 0));
//     //     if (!hasLevel0) {
//     //         FloorEntity f0 = new FloorEntity(0, true, org);
//     //         floorRepository.save(f0);
//     //         log.info("Seeded floor level 0 for organization {}", org.getName());
//     //     }

//     //     boolean hasLevel1 = floorRepository.findByOrganizationId(org.getId())
//     //             .stream()
//     //             .anyMatch(f -> Objects.equals(f.getLevel(), 1));
//     //     if (!hasLevel1) {
//     //         FloorEntity f1 = new FloorEntity(1, true, org);
//     //         floorRepository.save(f1);
//     //         log.info("Seeded floor level 1 for organization {}", org.getName());
//     //     }
//     // }

//     // private void createRoomsAndDesksIfMissing(OrganizationEntity org) {
//     //     // pick floor 0 (preferentially) to attach rooms
//     //     FloorEntity floor0 = floorRepository.findByOrganizationId(org.getId())
//     //             .stream()
//     //             .filter(f -> Objects.equals(f.getLevel(), 0))
//     //             .findFirst()
//     //             .orElseGet(() -> floorRepository.findByOrganizationId(org.getId()).stream().findFirst().orElse(null));

//     //     if (floor0 == null) {
//     //         log.warn("No floor found for organization {}, skipping room/desk seeding.", org.getName());
//     //         return;
//     //     }

//     //     // create two desk_area rooms if missing
//     //     String roomAName = "Desk Area A";
//     //     String roomBName = "Desk Area B";

//     //     RoomEntity roomA = roomRepository.findByFloorId(floor0.getId())
//     //             .stream()
//     //             .filter(r -> roomAName.equalsIgnoreCase(r.getName()))
//     //             .findFirst()
//     //             .orElseGet(() -> {
//     //                 RoomEntity r = new RoomEntity(
//     //                         roomAName,
//     //                         ResourceStatus.AVAILABLE,
//     //                         "basic-equipment",
//     //                         RoomType.DESK_AREA,
//     //                         50.0,
//     //                         floor0,
//     //                         10
//     //                 );
//     //                 RoomEntity saved = roomRepository.save(r);
//     //                 log.info("Seeded room '{}'", saved.getName());
//     //                 return saved;
//     //             });

//     //     RoomEntity roomB = roomRepository.findByFloorId(floor0.getId())
//     //             .stream()
//     //             .filter(r -> roomBName.equalsIgnoreCase(r.getName()))
//     //             .findFirst()
//     //             .orElseGet(() -> {
//     //                 RoomEntity r = new RoomEntity(
//     //                         roomBName,
//     //                         ResourceStatus.AVAILABLE,
//     //                         "basic-equipment",
//     //                         RoomType.DESK_AREA,
//     //                         40.0,
//     //                         floor0,
//     //                         8
//     //                 );
//     //                 RoomEntity saved = roomRepository.save(r);
//     //                 log.info("Seeded room '{}'", saved.getName());
//     //                 return saved;
//     //             });

//     //     // create three desks in roomA if missing
//     //     for (int i = 1; i <= 3; i++) {
//     //         final String deskName = "Desk " + i;
//     //         boolean exists = deskRepository.findByRoomId(roomA.getId())
//     //                 .stream()
//     //                 .anyMatch(d -> deskName.equalsIgnoreCase(d.getName()));
//     //         if (!exists) {
//     //             DeskEntity desk = new DeskEntity(deskName, ResourceStatus.AVAILABLE, "chair,monitor", roomA);
//     //             deskRepository.save(desk);
//     //             log.info("Seeded desk '{}' in room '{}'", deskName, roomA.getName());
//     //         } else {
//     //             log.info("Desk '{}' in room '{}' exists, skipping.", deskName, roomA.getName());
//     //         }
//     //     }
//     // }
// }
