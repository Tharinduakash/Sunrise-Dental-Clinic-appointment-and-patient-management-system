-- Sunrise Dental Clinic - initial schema
-- Staff accounts (Task: User Authentication / Login)
CREATE TABLE staff_users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'STAFF',
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE dentists (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    specialization  VARCHAR(100),
    contact_number  VARCHAR(20)
);

CREATE TABLE patients (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    address        VARCHAR(255),
    contact_number VARCHAR(20)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE treatment_types (
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(100) UNIQUE NOT NULL,
    base_cost         NUMERIC(10,2) NOT NULL,
    consultation_fee  NUMERIC(10,2) NOT NULL DEFAULT 1500.00
);

-- Appointment number is the human-facing unique identifier from the scenario (e.g. APT-000123)
CREATE TABLE appointments (
    id                  BIGSERIAL PRIMARY KEY,
    appointment_number  VARCHAR(20) UNIQUE NOT NULL,
    patient_id          BIGINT NOT NULL REFERENCES patients(id),
    dentist_id          BIGINT NOT NULL REFERENCES dentists(id),
    treatment_type_id   BIGINT NOT NULL REFERENCES treatment_types(id),
    appointment_date    DATE NOT NULL,
    appointment_time    TIME NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    -- Enforces the clinic's core pain point from the brief: no double-booking a dentist for the same slot
    CONSTRAINT uq_dentist_slot UNIQUE (dentist_id, appointment_date, appointment_time)
);

CREATE TABLE bills (
    id                BIGSERIAL PRIMARY KEY,
    appointment_id    BIGINT UNIQUE NOT NULL REFERENCES appointments(id),
    consultation_fee  NUMERIC(10,2) NOT NULL,
    treatment_cost    NUMERIC(10,2) NOT NULL,
    total_amount      NUMERIC(10,2) NOT NULL,
    generated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Business-rule enforcement at the database level (advanced DB feature: trigger + function)
CREATE OR REPLACE FUNCTION validate_bill_total() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.total_amount <> (NEW.consultation_fee + NEW.treatment_cost) THEN
        RAISE EXCEPTION 'total_amount (%) must equal consultation_fee + treatment_cost (%)',
            NEW.total_amount, (NEW.consultation_fee + NEW.treatment_cost);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_bill_total
    BEFORE INSERT OR UPDATE ON bills
    FOR EACH ROW EXECUTE FUNCTION validate_bill_total();

-- Reference data
INSERT INTO dentists (name, specialization, contact_number) VALUES
    ('Dr. Nimal Perera', 'General Dentistry', '0771234567'),
    ('Dr. Shalini Fernando', 'Orthodontics', '0779876543'),
    ('Dr. Ruwan Jayasuriya', 'Oral Surgery', '0712345678');

INSERT INTO treatment_types (name, base_cost, consultation_fee) VALUES
    ('General Checkup', 0.00, 1500.00),
    ('Scaling and Polishing', 3500.00, 1500.00),
    ('Tooth Extraction', 5000.00, 1500.00),
    ('Root Canal Treatment', 15000.00, 2000.00),
    ('Dental Filling', 4500.00, 1500.00),
    ('Braces Consultation', 2500.00, 2500.00);
