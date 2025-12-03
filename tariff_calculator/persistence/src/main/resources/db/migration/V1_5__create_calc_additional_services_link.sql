CREATE TABLE calculation_additional_services (
    service_id INT NOT NULL,
    PRIMARY KEY (service_id),
    FOREIGN KEY (service_id) REFERENCES additional_services(id)
);