CREATE TABLE IF NOT EXISTS `allegro`.`allegro_client` (
  `id` INT NOT NULL,
  `name_surname` VARCHAR(90) DEFAULT NULL,
  `nip` VARCHAR(45) DEFAULT NULL,
  `company_Name` VARCHAR(45) DEFAULT NULL,
  `email` VARCHAR(45) DEFAULT NULL,
  `phone_Number1` VARCHAR(45) DEFAULT NULL,
  `phone_Number2` VARCHAR(45) DEFAULT NULL,
  `login` VARCHAR(45) DEFAULT NULL,
  `address` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  INDEX `fk_name` (`name_surname` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci