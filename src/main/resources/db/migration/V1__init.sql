CREATE TABLE event_publication
(
    id                     UUID                        NOT NULL,
    completion_attempts    INTEGER                     NOT NULL,
    completion_date        TIMESTAMP(6) WITH TIME ZONE,
    last_resubmission_date TIMESTAMP(6) WITH TIME ZONE,
    publication_date       TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    event_type             TEXT                        NOT NULL,
    listener_id            TEXT                        NOT NULL,
    serialized_event       TEXT                        NOT NULL,
    status                 TEXT CHECK (status IN ('PUBLISHED', 'PROCESSING', 'COMPLETED', 'FAILED', 'RESUBMITTED')),
    PRIMARY KEY (id)
);
