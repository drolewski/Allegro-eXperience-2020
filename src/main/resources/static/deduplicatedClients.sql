CREATE TABLE IF NOT EXISTS `allegro`.`allegro_client_deduplicated` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name_surname` VARCHAR(60) DEFAULT NULL,
  `nip` VARCHAR(45) NULL DEFAULT NULL,
  `company_name` VARCHAR(45) DEFAULT NULL,
  `email` VARCHAR(45) NOT NULL,
  `phone_number1` VARCHAR(45) NOT NULL,
  `phone_number2` VARCHAR(45) DEFAULT NULL,
  `login` VARCHAR(45) NOT NULL,
  `address` VARCHAR(45) DEFAULT NULL,
  `company_parent` INT DEFAULT NULL,
  `individual_parent` INT DEFAULT NULL,
  `allegro_id` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_company_idx` (`company_parent` ASC) VISIBLE,
  INDEX `fk_individual_idx` (`individual_parent` ASC) VISIBLE,
  CONSTRAINT `fk_company`
    FOREIGN KEY (`company_parent`)
    REFERENCES `allegro`.`allegro_client_deduplicated` (`id`),
  CONSTRAINT `fk_individual`
    FOREIGN KEY (`individual_parent`)
    REFERENCES `allegro`.`allegro_client_deduplicated` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 57
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci