CREATE TABLE anl_audit_log (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    event_type  VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id   BIGINT      NOT NULL,
    actor_id    BIGINT      NOT NULL,
    created_at  DATETIME    NOT NULL,
    PRIMARY KEY (id)
);
