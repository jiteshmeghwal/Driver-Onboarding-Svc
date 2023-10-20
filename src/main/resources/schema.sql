CREATE DATABASE driveronboardingapp;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[driver_profile]') AND type in (N'U'))
BEGIN
CREATE TABLE  dbo.driver_profile (
    driver_id VARCHAR(6) PRIMARY KEY,
    addr_line_1 VARCHAR(20) NOT NULL,
    addr_line_2 VARCHAR(20),
    city VARCHAR(10) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    available_ind BIT NOT NULL DEFAULT 0
)
END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[vehicle]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.vehicle (
    vehicle_id BIGINT PRIMARY KEY IDENTITY(1,1),
    vehicle_model VARCHAR(20),
    vehicle_reg_no VARCHAR(15) NOT NULL,
    vehicle_type_cd TINYINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id)
)
END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[onboarding_step]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.onboarding_step (
    step_id TINYINT PRIMARY KEY IDENTITY(1,1),
    step_type_cd TINYINT NOT NULL,
    step_title VARCHAR(100) NOT NULL,
    step_desc VARCHAR(250) NOT NULL,
)
END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[onboarding_step_instance]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.onboarding_step_instance (
    complete_ind BIT NOT NULL DEFAULT(0),
    step_id TINYINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY (step_id) REFERENCES dbo.onboarding_step(step_id),
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id),
    CONSTRAINT PK_onboarding_step PRIMARY KEY (driver_id, step_id)
)
END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[document]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.document (
    doc_id BIGINT PRIMARY KEY IDENTITY(1,1),
    doc_name VARCHAR(50) NOT NULL,
    doc_upload_time DATETIME2 NOT NULL,
    valid_till DATETIME2,
    step_id TINYINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id),
    FOREIGN KEY (driver_id, step_id) REFERENCES dbo.onboarding_step_instance(driver_id, step_id),
    CONSTRAINT CK_unique_driver_document UNIQUE CLUSTERED (step_id, driver_id)
)
END;


IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[shipment]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.shipment (
    shipment_id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id VARCHAR(40) NOT NULL UNIQUE,
    status_cd TINYINT NOT NULL DEFAULT(1),
    carrier VARCHAR(10),
    order_date DATETIME2 NOT NULL,
    last_update_time DATETIME2,
    step_id TINYINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id),
    FOREIGN KEY (driver_id, step_id) REFERENCES dbo.onboarding_step_instance(driver_id, step_id),
    CONSTRAINT CK_unique_driver_shipment UNIQUE CLUSTERED (step_id, driver_id)
)
END;