create table analytics_report (
        co_2_savings_kg float(53) not null,
        confirmed_check_ins integer not null,
        empty_rooms integer not null,
        energy_savings_euros float(53) not null,
        total_reservations integer not null,
        generated_at datetime(6) not null,
        id bigint not null auto_increment,
        organization_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table ast_desks (
        id bigint not null,
        room_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table ast_floors (
        is_active bit not null,
        level integer not null,
        id bigint not null auto_increment,
        organization_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table ast_resources (
        is_active bit not null,
        id bigint not null auto_increment,
        equipment_list varchar(255),
        name varchar(255),
        status enum ('AVAILABLE','RESERVED','UNAVAILABLE') not null,
        primary key (id)
    ) engine=InnoDB;

    create table ast_rooms (
        capacity integer not null,
        surface_area float(53) not null,
        floor_id bigint not null,
        id bigint not null,
        type enum ('DESK_AREA','MEETING_ROOM') not null,
        primary key (id)
    ) engine=InnoDB;

    create table incidents (
        created_at datetime(6) not null,
        id bigint not null auto_increment,
        resolved_at datetime(6),
        resource_id bigint not null,
        user_id bigint not null,
        description varchar(255) not null,
        status enum ('IN_PROGRESS','RESOLVED') not null,
        primary key (id)
    ) engine=InnoDB;

    create table organizations (
        is_active bit not null,
        created_at datetime(6) not null,
        end_subscription datetime(6) not null,
        id bigint not null auto_increment,
        address varchar(255) not null,
        cif varchar(255) not null,
        email varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table reservation (
        date date not null,
        created_at datetime(6) not null,
        id bigint not null auto_increment,
        resource_id bigint not null,
        user_id bigint not null,
        status enum ('CANCELLED','CHECKED_IN','CONFIRMED','RELEASED') not null,
        primary key (id)
    ) engine=InnoDB;

    create table usr_users (
        consent_given bit not null,
        is_active bit not null,
        created_at datetime(6) not null,
        id bigint not null auto_increment,
        organization_id bigint not null,
        email varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        password_hash varchar(255) not null,
        preferences_json varchar(255),
        role enum ('ADMIN','EMPLOYEE','TECHNICIAN') not null,
        primary key (id)
    ) engine=InnoDB;

    alter table organizations
       add constraint UKjp9bf801x0g3g9q7dbdo3blen unique (cif);

    alter table organizations
       add constraint UKp9pbw3flq9hkay8hdx3ypsldy unique (name);

    alter table usr_users
       add constraint UKg0jloiasku8a7gat4lu7866r6 unique (email);

    alter table analytics_report
       add constraint FK8mmh578bxyo81iqeddjg3m4ag
       foreign key (organization_id)
       references organizations (id);

    alter table ast_desks
       add constraint FK5rnvc5i5isiaaydbnjttunrpt
       foreign key (room_id)
       references ast_rooms (id);

    alter table ast_desks
       add constraint FK3wko10md6ac0wwjjplbyyrnnn
       foreign key (id)
       references ast_resources (id);

    alter table ast_floors
       add constraint FKk5k00t07vd7cghnugk84u33ic
       foreign key (organization_id)
       references organizations (id);

    alter table ast_rooms
       add constraint FKtn98oxecaifladp6e4pq8ddjk
       foreign key (floor_id)
       references ast_floors (id);

    alter table ast_rooms
       add constraint FKf2j73g5cnd60p5qx46gudsjmj
       foreign key (id)
       references ast_resources (id);

    alter table incidents
       add constraint FK211qe7n4hy678gy9dab2kq5iy
       foreign key (resource_id)
       references ast_resources (id);

    alter table incidents
       add constraint FKt8nwq9oplqondjh2s3d4pyydd
       foreign key (user_id)
       references usr_users (id);

    alter table reservation
       add constraint FKtbje575kj6qied4yj034orlmy
       foreign key (resource_id)
       references ast_resources (id);

    alter table reservation
       add constraint FKadal8qoat9q8r3pd2h3gks2h
       foreign key (user_id)
       references usr_users (id);

    alter table usr_users
       add constraint FKsuhy9uavk2vilm8uaxsmy53x5
       foreign key (organization_id)
       references organizations (id);
