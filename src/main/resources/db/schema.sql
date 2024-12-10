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
ALTER TABLE vote_items MODIFY COLUMN created_by INT NULL;
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE users DROP COLUMN role;


CREATE TABLE vote_items (
    vote_item_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by INT,
    voting_start TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    voting_end TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)  -- 外部キー制約
);
ALTER TABLE vote_items DROP COLUMN updated_at;
ALTER TABLE vote_items ADD COLUMN agree_count INT DEFAULT 0, ADD COLUMN disagree_count INT DEFAULT 0;
ALTER TABLE vote_items ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL;


CREATE TABLE vote_results (
    vote_result_id INT AUTO_INCREMENT PRIMARY KEY,  -- 結果ID
    user_id INT NOT NULL,                           -- ユーザーID
    vote_item_id INT NOT NULL,                      -- 投票アイテムID
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 投票日時
    FOREIGN KEY (user_id) REFERENCES users(user_id),     -- 外部キー: ユーザーID
    FOREIGN KEY (vote_item_id) REFERENCES vote_items(vote_item_id),  -- 外部キー: 投票アイテムID
    CONSTRAINT unique_vote UNIQUE (user_id, vote_item_id)  -- ユーザーと投票アイテムの組み合わせを一意にする
);
ALTER TABLE vote_results ADD COLUMN vote_value TINYINT NOT NULL DEFAULT 0;
-- 外部キー制約の修正
ALTER TABLE vote_results DROP FOREIGN KEY vote_results_ibfk_1;
ALTER TABLE vote_results DROP FOREIGN KEY vote_results_ibfk_2;
ALTER TABLE vote_results ADD CONSTRAINT vote_results_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;
ALTER TABLE vote_results ADD CONSTRAINT vote_results_ibfk_2 FOREIGN KEY (vote_item_id) REFERENCES vote_items(vote_item_id) ON DELETE CASCADE;


