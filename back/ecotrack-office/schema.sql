set foreign_key_checks = 0;

drop table if exists analitics_report;
drop table if exists ast_desks;
drop table if exists ast_rooms;
drop table if exists ast_floors;
drop table if exists ast_resources;
drop table if exists incidents;
drop table if exists reservation;
drop table if exists usr_users;
drop table if exists organizations;

set foreign_key_checks = 1;

create table analitics_report (
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
    add constraint uk_organizations_cif unique (cif);

alter table organizations
    add constraint uk_organizations_name unique (name);

alter table usr_users
    add constraint uk_usr_users_email unique (email);

alter table analitics_report
    add constraint fk_analitics_report_organization
    foreign key (organization_id)
    references organizations (id);

alter table ast_desks
    add constraint fk_ast_desks_room
    foreign key (room_id)
    references ast_rooms (id);

alter table ast_desks
    add constraint fk_ast_desks_resource
    foreign key (id)
    references ast_resources (id);

alter table ast_floors
    add constraint fk_ast_floors_organization
    foreign key (organization_id)
    references organizations (id);

alter table ast_rooms
    add constraint fk_ast_rooms_floor
    foreign key (floor_id)
    references ast_floors (id);

alter table ast_rooms
    add constraint fk_ast_rooms_resource
    foreign key (id)
    references ast_resources (id);

alter table incidents
    add constraint fk_incidents_resource
    foreign key (resource_id)
    references ast_resources (id);

alter table incidents
    add constraint fk_incidents_user
    foreign key (user_id)
    references usr_users (id);

alter table reservation
    add constraint fk_reservation_resource
    foreign key (resource_id)
    references ast_resources (id);

alter table reservation
    add constraint fk_reservation_user
    foreign key (user_id)
    references usr_users (id);

alter table usr_users
    add constraint fk_usr_users_organization
    foreign key (organization_id)
    references organizations (id);
