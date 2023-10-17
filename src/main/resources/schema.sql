 CREATE DATABASE driveronboardingapp;
DROP DATABASE driveronboardingapp;
-- IF NOT EXISTS (SELECT * FROM sys.objects
-- WHERE object_id = OBJECT_ID(N'[dbo].[profile_status]') AND type in (N'U'))
-- BEGIN
-- CREATE TABLE  dbo.profile_status (
--     profile_status_cd TINYINT PRIMARY KEY IDENTITY(1,1),
--     profile_status_desc VARCHAR(20) NOT NULL
-- )

-- INSERT INTO dbo.profile_status (profile_status_desc)
-- VALUES ('Created'), ('Pending Verification'), ('Verified'), ('Verification Failed'), ('Complete'), ('Blocked');
-- END;


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



-- IF NOT EXISTS (SELECT * FROM sys.objects
-- WHERE object_id = OBJECT_ID(N'[dbo].[vehicle_type]') AND type in (N'U'))
-- BEGIN
-- CREATE TABLE dbo.vehicle_type (
--     vehicle_type_cd TINYINT PRIMARY KEY IDENTITY(1,1),
--     vehicle_type_desc VARCHAR(10) NOT NULL
-- )

-- INSERT INTO dbo.vehicle_type (vehicle_type_desc) VALUES
-- ('2-Wheeler'), ('3-Wheeler'), ('4-Wheeler')
-- END;



IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[vehicle]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.vehicle (
    vehicle_id BIGINT PRIMARY KEY IDENTITY(1,1),
    vehicle_model VARCHAR(10),
    vehicle_reg_no VARCHAR(10) NOT NULL,
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
    step_title VARCHAR(20) NOT NULL,
    step_desc VARCHAR(250) NOT NULL,
)
END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[onboarding_step_instance]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.onboarding_step_instance (
    step_instance_id BIGINT PRIMARY KEY IDENTITY(1,1),
    complete_ind BIT NOT NULL DEFAULT(0),
    step_id TINYINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY (step_id) REFERENCES dbo.onboarding_step(step_id),
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id)
)

CREATE UNIQUE NONCLUSTERED INDEX unique_driver_step
ON dbo.onboarding_step_instance(step_id, driver_id)
END;

--IF NOT EXISTS (SELECT * FROM sys.objects
--WHERE object_id = OBJECT_ID(N'[dbo].[document_type]') AND type in (N'U'))
--BEGIN
--CREATE TABLE dbo.document_type (
--    doc_type_cd TINYINT PRIMARY KEY IDENTITY(1,1),
--    doc_type VARCHAR(10) NOT NULL
--)
--END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[document]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.document (
    doc_id BIGINT PRIMARY KEY IDENTITY(1,1),
    doc_name VARCHAR(10) NOT NULL,
    doc_upload_time DATETIME2 NOT NULL,
    valid_till DATETIME2,
    step_instance_id BIGINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY(step_instance_id) REFERENCES dbo.onboarding_step_instance(step_instance_id),
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id)
)
CREATE UNIQUE NONCLUSTERED INDEX unique_document
ON dbo.document(step_instance_id, driver_id)
END;

-- IF NOT EXISTS (SELECT * FROM sys.objects
-- WHERE object_id = OBJECT_ID(N'[dbo].[order_status]') AND type in (N'U'))
-- BEGIN
-- CREATE TABLE dbo.order_status (
--     order_status_cd TINYINT PRIMARY KEY IDENTITY(1,1),
--     order_status_desc VARCHAR(20) NOT NULL
-- )

-- INSERT INTO dbo.order_status (order_status_desc) VALUES
-- ('ORDERED'), ('SHIPPED'), ('DELIVERED')
-- END;

--IF NOT EXISTS (SELECT * FROM sys.objects
--WHERE object_id = OBJECT_ID(N'[dbo].[product]') AND type in (N'U'))
--BEGIN
--CREATE TABLE dbo.product (
--    product_upc VARCHAR(10) PRIMARY KEY,
--    product_desc VARCHAR(20) NOT NULL
--)
--END;

IF NOT EXISTS (SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[shipment]') AND type in (N'U'))
BEGIN
CREATE TABLE dbo.shipment (
    shipment_id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id VARCHAR(15) NOT NULL UNIQUE,
    status_cd TINYINT NOT NULL DEFAULT(1),
    carrier VARCHAR(10),
    order_date DATETIME2 NOT NULL,
    last_update_time DATETIME2,
    step_instance_id BIGINT NOT NULL,
    driver_id VARCHAR(6) NOT NULL,
    FOREIGN KEY(step_instance_id) REFERENCES dbo.onboarding_step_instance(step_instance_id),
    FOREIGN KEY (driver_id) REFERENCES dbo.driver_profile(driver_id)
)
CREATE UNIQUE NONCLUSTERED INDEX unique_shipment
ON dbo.shipment(step_instance_id, driver_id)
END;