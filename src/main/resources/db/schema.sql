CREATE DATABASE IF NOT EXISTS kadai2_db; 
ALTER DATABASE kadai2_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `kadai2_db`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(50) NOT NULL,
  `password` VARCHAR(60) NOT NULL,
  `email` VARCHAR(255) NULL,
  `birthday` DATE NOT NULL,
  `memo` TEXT NULL,
  `role` ENUM('user', 'admin') NOT NULL DEFAULT 'user',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;
