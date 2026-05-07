
    create table ast_desks (
        id bigint not null,
        room_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table ast_floors (
        is_active bit not null,
        level integer not null,
        id bigint not null auto_increment,
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

    alter table ast_desks 
       add constraint FK5rnvc5i5isiaaydbnjttunrpt 
       foreign key (room_id) 
       references ast_rooms (id);

    alter table ast_desks 
       add constraint FK3wko10md6ac0wwjjplbyyrnnn 
       foreign key (id) 
       references ast_resources (id);

    alter table ast_rooms 
       add constraint FKtn98oxecaifladp6e4pq8ddjk 
       foreign key (floor_id) 
       references ast_floors (id);

    alter table ast_rooms 
       add constraint FKf2j73g5cnd60p5qx46gudsjmj 
       foreign key (id) 
       references ast_resources (id);
