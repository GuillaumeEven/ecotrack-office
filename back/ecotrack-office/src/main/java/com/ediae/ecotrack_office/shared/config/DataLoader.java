package com.ediae.ecotrack_office.shared.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ediae.ecotrack_office.analyticsreport.entity.AnalyticsReportEntity;
import com.ediae.ecotrack_office.analyticsreport.repository.AnalyticsReportRepository;
import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.incident.entity.IncidentEntity;
import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import com.ediae.ecotrack_office.incident.repository.IncidentRepository;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.users.repository.UserRepository;

@Component
@Profile("prod")
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final DeskRepository deskRepository;
    private final ReservationRepository reservationRepository;
    private final IncidentRepository incidentRepository;
    private final AnalyticsReportRepository analyticsReportRepository; // Atributo añadido
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(OrganizationRepository organizationRepository,
            UserRepository userRepository,
            FloorRepository floorRepository,
            RoomRepository roomRepository,
            DeskRepository deskRepository,
            ReservationRepository reservationRepository,
            IncidentRepository incidentRepository,
            AnalyticsReportRepository analyticsReportRepository, // Repositorio inyectado
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.deskRepository = deskRepository;
        this.reservationRepository = reservationRepository;
        this.incidentRepository = incidentRepository;
        this.analyticsReportRepository = analyticsReportRepository; // Asignación
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
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
        createIncidentsIfMissing();
        createHistoricalAnalyticsIfMissing(org); // Llamada al método
    }

    /**
     * Borra todos los datos de desarrollo y reinicia los contadores para que los
     * IDs generados sean siempre predecibles.
     * Lo he hecho así para que los datos de desarrollo sean consistentes y
     * predecibles en cada ejecución.
     */
    private void resetDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.execute("TRUNCATE TABLE analytics_report");
        jdbcTemplate.execute("TRUNCATE TABLE incidents");
        jdbcTemplate.execute("TRUNCATE TABLE reservation");
        jdbcTemplate.execute("TRUNCATE TABLE ast_desks");
        jdbcTemplate.execute("TRUNCATE TABLE ast_rooms");
        jdbcTemplate.execute("TRUNCATE TABLE ast_resources");
        jdbcTemplate.execute("TRUNCATE TABLE ast_floors");
        jdbcTemplate.execute("TRUNCATE TABLE usr_users");
        jdbcTemplate.execute("TRUNCATE TABLE organizations");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        log.info("Base de datos MySQL de desarrollo reiniciada: todas las tablas truncadas con éxito.");
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
                    o.setAddress("Dirección desconocida");
                    o.setEmail("contact@ecotrack.local");
                    o.setEndSubscription(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
                    o.setIsActive(Boolean.TRUE);
                    o.setCreatedAt(LocalDateTime.now());
                    OrganizationEntity saved = organizationRepository.save(o);
                    log.info("Organización sembrada '{}'", saved.getName());
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
                    o.setAddress("Parque Tecnológico, Edificio B");
                    o.setEmail("contact@greenoffice.local");
                    o.setEndSubscription(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
                    o.setIsActive(Boolean.TRUE);
                    o.setCreatedAt(LocalDateTime.now());
                    OrganizationEntity saved = organizationRepository.save(o);
                    log.info("Organización sembrada '{}'", saved.getName());
                    return saved;
                });
    }

    private void createAdminIfMissing(OrganizationEntity org) {
        final String adminEmail = "admin@ecotrack.local";
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Usuario administrador '{}' existe, omitiendo.", adminEmail);
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setFirstName("Admin");
        admin.setLastName("Ecotrack");
        admin.setRole(Role.ADMIN);
        admin.setOrganization(org);
        admin.setIsActive(Boolean.TRUE);
        admin.setConsentGiven(Boolean.TRUE);
        admin.setPreferencesJson("{}");
        admin.setCreatedAt(LocalDateTime.now());

        userRepository.save(admin);
        log.info("Usuario administrador sembrado '{}'", adminEmail);
    }

    private void createTechAndEmployeeIfMissing(OrganizationEntity org) {
        final String techEmail = "tech@ecotrack.local";
        final String empEmail = "employee@ecotrack.local";

        if (!userRepository.existsByEmail(techEmail)) {
            UserEntity tech = new UserEntity();
            tech.setEmail(techEmail);
            tech.setPasswordHash(passwordEncoder.encode("password"));
            tech.setFirstName("Tech");
            tech.setLastName("Usuario");
            tech.setRole(Role.TECHNICIAN);
            tech.setOrganization(org);
            tech.setIsActive(Boolean.TRUE);
            tech.setConsentGiven(Boolean.TRUE);
            tech.setPreferencesJson("{}");
            tech.setCreatedAt(LocalDateTime.now());
            userRepository.save(tech);
            log.info("Técnico sembrado '{}'", techEmail);
        } else {
            log.info("Técnico '{}' existe, omitiendo.", techEmail);
        }

        if (!userRepository.existsByEmail(empEmail)) {
            UserEntity emp = new UserEntity();
            emp.setEmail(empEmail);
            emp.setPasswordHash(passwordEncoder.encode("password"));
            emp.setFirstName("Empleado");
            emp.setLastName("Usuario");
            emp.setRole(Role.EMPLOYEE);
            emp.setOrganization(org);
            emp.setIsActive(Boolean.TRUE);
            emp.setConsentGiven(Boolean.TRUE);
            emp.setPreferencesJson("{}");
            emp.setCreatedAt(LocalDateTime.now());
            userRepository.save(emp);
            log.info("Empleado sembrado '{}'", empEmail);
        } else {
            log.info("Empleado '{}' existe, omitiendo.", empEmail);
        }
    }

    private void createFloorsIfMissing(OrganizationEntity org) {
        boolean hasLevel0 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .anyMatch(f -> Objects.equals(f.getLevel(), 0));
        if (!hasLevel0) {
            FloorEntity f0 = new FloorEntity(0, true, org);
            f0.setName("Planta 1");
            floorRepository.save(f0);
            log.info("Piso nivel 0 sembrado para la organización {}", org.getName());
        }

        boolean hasLevel1 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .anyMatch(f -> Objects.equals(f.getLevel(), 1));
        if (!hasLevel1) {
            FloorEntity f1 = new FloorEntity(1, true, org);
            f1.setName("Planta 2");
            floorRepository.save(f1);
            log.info("Piso nivel 1 sembrado para la organización {}", org.getName());
        }
    }

    private void createRoomsAndDesksIfMissing(OrganizationEntity org) {
        FloorEntity floor0 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .filter(f -> Objects.equals(f.getLevel(), 0))
                .findFirst()
                .orElseGet(() -> floorRepository.findByOrganizationId(org.getId()).stream().findFirst().orElse(null));

        if (floor0 == null) {
            log.warn("No se encontró piso para la organización {}, omitiendo siembra de salas/escritorios.",
                    org.getName());
            return;
        }

        createDeskAreaRoomsWithDesks(floor0, "Sala de Trabajo A", 50.0);
        createDeskAreaRoomsWithDesks(floor0, "Sala de Trabajo B", 40.0);
        createDeskAreaRoomsWithDesks(floor0, "Sala de Trabajo C", 45.0);

        createMeetingRoom(floor0, "Sala de Reunión 1", 25.0);
        createMeetingRoom(floor0, "Sala de Reunión 2", 30.0);

        FloorEntity floor1 = floorRepository.findByOrganizationId(org.getId())
                .stream()
                .filter(f -> Objects.equals(f.getLevel(), 1))
                .findFirst()
                .orElse(null);

        if (floor1 != null) {
            createDeskAreaRoomsWithDesks(floor1, "Sala de Trabajo D", 35.0);
            createDeskAreaRoomsWithDesks(floor1, "Sala de Trabajo E", 38.0);

            createMeetingRoom(floor1, "Sala de Reunión 3", 28.0);
            createMeetingRoom(floor1, "Sala de Reunión 4", 32.0);
        }
    }

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
                            10);
                    r.setIsActive(true);
                    RoomEntity saved = roomRepository.save(r);
                    log.info("Sala de trabajo sembrada '{}'", saved.getName());
                    return saved;
                });

        for (int i = 1; i <= 10; i++) {
            final String deskName = "D" + i;
            boolean exists = deskRepository.findByRoom_Id(room.getId())
                    .stream()
                    .anyMatch(d -> deskName.equalsIgnoreCase(d.getName()));
            if (!exists) {
                DeskEntity desk = new DeskEntity(deskName, ResourceStatus.AVAILABLE, "silla,monitor", room);
                desk.setIsActive(true);
                deskRepository.save(desk);
            }
        }
        log.info("10 escritorios sembrados en la sala '{}'", room.getName());
    }

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
                    20);
            r.setIsActive(true);
            RoomEntity saved = roomRepository.save(r);
            log.info("Sala de reunión sembrada '{}'", saved.getName());
        }
    }

    private void createReservationsIfMissing(OrganizationEntity org) {
        UserEntity employee = userRepository.findByEmail("employee@ecotrack.local")
                .orElse(null);

        if (employee == null) {
            log.warn("Usuario empleado no encontrado, omitiendo siembra de reservaciones.");
            return;
        }

        LocalDate date1 = LocalDate.of(2026, 7, 14);
        createReservationForDesks(org, employee, date1, new int[] { 2, 5, 6 });

        LocalDate date2 = LocalDate.of(2026, 7, 15);
        createReservationForDesks(org, employee, date2, new int[] { 2, 7, 9 });

        UserEntity user1 = userRepository.findById(1L).orElse(null);
        if (user1 != null) {
            DeskEntity desk4 = deskRepository.findById(4L).orElse(null);
            if (desk4 != null) {
                for (int day = 1; day <= 5; day++) {
                    LocalDate dateForDesk4 = LocalDate.of(2026, 5, day);
                    boolean reservationExists = reservationRepository.findByResourceId(desk4.getId())
                            .stream()
                            .anyMatch(r -> r.getDate().equals(dateForDesk4));

                    if (!reservationExists) {
                        ReservationEntity reservation = new ReservationEntity(
                                dateForDesk4,
                                ReservationStatus.CONFIRMED,
                                user1,
                                desk4);
                        reservationRepository.save(reservation);
                        log.info("Reservación sembrada para el desk '{}' (ID: 4) en la fecha {}", desk4.getName(),
                                dateForDesk4);
                    } else {
                        log.info("Reservación para el desk '{}' (ID: 4) en la fecha {} ya existe, omitiendo.",
                                desk4.getName(), dateForDesk4);
                    }
                }
            } else {
                log.warn("Desk con ID 4 no encontrado, omitiendo siembra de reservaciones.");
            }
        } else {
            log.warn("Usuario con ID 1 no encontrado, omitiendo siembra de reservaciones.");
        }
    }

    private void createReservationForDesks(OrganizationEntity org, UserEntity user, LocalDate date, int[] deskNumbers) {
        for (int deskNum : deskNumbers) {
            String deskNamePattern = "D" + deskNum;
            DeskEntity desk = deskRepository.findAll()
                    .stream()
                    .filter(d -> d.getName().contains(deskNamePattern))
                    .findFirst()
                    .orElse(null);

            if (desk != null) {
                boolean reservationExists = reservationRepository.findByResourceId(desk.getId())
                        .stream()
                        .anyMatch(r -> r.getDate().equals(date));

                if (!reservationExists) {
                    ReservationEntity reservation = new ReservationEntity(
                            date,
                            ReservationStatus.CONFIRMED,
                            user,
                            desk);
                    reservationRepository.save(reservation);
                    log.info("Reservación sembrada para el escritorio '{}' en la fecha {}", desk.getName(), date);
                } else {
                    log.info("Reservación para el escritorio '{}' en la fecha {} ya existe, omitiendo.", desk.getName(),
                            date);
                }
            } else {
                log.warn("Escritorio con patrón '{}' no encontrado, omitiendo reservación.", deskNamePattern);
            }
        }
    }

    private void createIncidentsIfMissing() {
        UserEntity tech = userRepository.findByEmail("tech@ecotrack.local")
                .orElse(null);

        if (tech == null) {
            log.warn("Usuario técnico no encontrado, omitiendo siembra de incidentes.");
            return;
        }

        DeskEntity desk = deskRepository.findById(3L)
                .orElse(null);

        if (desk == null) {
            log.warn("Desk con ID 3 no encontrado, omitiendo siembra de incidente.");
            return;
        }

        boolean incidentExists = incidentRepository.findByResourceId(desk.getId())
                .stream()
                .anyMatch(i -> i.getStatus() == IncidentStatus.IN_PROGRESS);

        if (!incidentExists) {
            IncidentEntity incident = new IncidentEntity(
                    "La silla está rota",
                    IncidentStatus.IN_PROGRESS,
                    LocalDateTime.now(),
                    tech,
                    desk);
            incidentRepository.save(incident);
            log.info("Incidente sembrado para el desk '{}' (ID: 3) con descripción: 'La silla está rota'",
                    desk.getName());
        } else {
            log.info("Incidente para el desk con ID 3 ya existe, omitiendo.");
        }
    }

    /**
     * Generador automático de historial analítico (Escenario B).
     * Inyecta en MySQL los últimos 60 días correlativos con datos realistas
     * basados en la capacidad real del mapa de plantas y salas de la oficina.
     * Esto permite que el frontend tenga datos consistentes para mostrar en las
     * tarjetas de métricas y en la tabla de historial.
     */
    private void createHistoricalAnalyticsIfMissing(OrganizationEntity org) {
        java.util.Random random = new java.util.Random();

        log.info("Generando 60 días de historial analítico en la base de datos...");

        for (int i = 60; i >= 1; i--) {
            // Resto 'i' días a la fecha actual del sistema para ir hacia atrás en el
            // tiempo
            LocalDateTime reportDate = LocalDateTime.now().minusDays(i+1);

            // 1. Simulación del algoritmo: salas vacías de 1 a 3 (eliminamos el 0 para la
            // demo)
            int emptyRooms = random.nextInt(3) + 1; // Genera 1, 2 o 3

            // 2. Escala de costes fijos según el tipo de sala cerrada
            double co2 = 0.0;
            double energy = 0.0;

            if (emptyRooms == 1) {
                co2 = 1.50;
                energy = 5.00;
            } else if (emptyRooms == 2) {
                co2 = 2.30; // 1 sala trabajo + 1 reunión
                energy = 8.00;
            } else if (emptyRooms == 3) {
                co2 = 3.80; // 2 salas trabajo + 1 reunión
                energy = 13.00;
            }

            // 3. Ocupación de la oficina (escala de 50 puestos basada en la capacidad real
            // de las salas de trabajo y reuniones)
            int reservations = random.nextInt(9) + 28; // Entre 28 y 36 reservas diarias (64% media, media realista)
            int checkins = random.nextInt(7) + 24; // Entre 24 y 30 check-ins confirmados

            // 4. Guardo el registro diario en la entidad
            AnalyticsReportEntity dailyReport = new AnalyticsReportEntity(
                    co2,
                    energy,
                    reservations,
                    checkins,
                    emptyRooms,
                    reportDate,
                    org);

            analyticsReportRepository.save(dailyReport);
        }

        log.info("¡Historial de 60 días inyectado con éxito en MySQL!");
    }
}