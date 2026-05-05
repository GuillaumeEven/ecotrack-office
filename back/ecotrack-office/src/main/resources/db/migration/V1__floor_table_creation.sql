    create table ast_floors (
        `is-active` bit not null,
        level integer not null,
        id bigint not null auto_increment,
        primary key (id)
    ) engine=InnoDB;