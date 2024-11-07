DROP TABLE IF EXISTS `like`;

CREATE TABLE `like` (
	`like_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`entity_type`	VARCHAR(10)	NOT NULL,
	`entity_id`	INT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`delete_whether`	CHAR(9)	NOT NULL
);

DROP TABLE IF EXISTS `board`;

CREATE TABLE `board` (
	`board_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`category_id`	INT	NOT NULL,
	`title`	VARCHAR(150)	NOT NULL,
	`content`	VARCHAR(4000)	NOT NULL,
	`like_count`	INT	NOT NULL,
	`view_count`	INT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL,
	`address`	VARCHAR(255)	NULL
);

DROP TABLE IF EXISTS `board_category`;

CREATE TABLE `board_category` (
	`board_category_id`	INT	NOT NULL,
	`name`	VARCHAR(20)	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL
);

DROP TABLE IF EXISTS `board_img`;

CREATE TABLE `board_img` (
	`board_img_id`	INT	NOT NULL,
	`board_id`	INT	NOT NULL,
	`path`	VARCHAR(30)	NOT NULL,
	`system_name`	VARCHAR(300)	NOT NULL,
	`origin_name`	VARCHAR(255)	NOT NULL,
	`main_whether`	CHAR(1)	NOT NULL
);

DROP TABLE IF EXISTS `reply`;

CREATE TABLE `reply` (
	`reply_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`board_id`	INT	NOT NULL,
	`parent_reply_id`	INT	NOT NULL,
	`content`	VARCHAR(1000)	NULL,
	`depth`	INT	NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL
);

DROP TABLE IF EXISTS `meal_food`;

CREATE TABLE `meal_food` (
	`meal_food_id`	INT	NOT NULL,
	`meal_id`	INT	NOT NULL,
	`food_id`	INT	NOT NULL,
	`quantity`	INT	NOT NULL
);

DROP TABLE IF EXISTS `food`;

CREATE TABLE `food` (
	`food_id`	INT	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL,
	`kcal`	INT	NOT NULL,
	`protein`	INT	NULL,
	`fat`	INT	NULL,
	`carbohydrates`	INT	NULL
);

DROP TABLE IF EXISTS `meal`;

CREATE TABLE `meal` (
	`meal_id`	INT	NOT NULL,
	`meal_plan_id`	INT	NOT NULL,
	`date`	DATE	NOT NULL,
	`type`	CHAR(10)	NOT NULL
);

DROP TABLE IF EXISTS `meal_plan`;

CREATE TABLE `meal_plan` (
	`meal_plan_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`name`	VARCHAR(50)	NOT NULL,
	`start_date`	TIMESTAMP	NOT NULL,
	`end_date`	TIMESTAMP	NOT NULL,
	`goal`	VARCHAR(50)	NOT NULL
);

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
	`user_id`	INT	NOT NULL,
	`email`	VARCHAR(320)	NOT NULL,
	`password`	VARCHAR(20)	NOT NULL,
	`name`	VARCHAR(15)	NOT NULL,
	`nickname`	VARCHAR(20)	NOT NULL,
	`profile_url`	VARCHAR(330)	NULL,
	`phone`	VARCHAR(13)	NULL,
	`post_code`	CHAR(5)	NOT NULL,
	`parcel_address`	VARCHAR(50)	NOT NULL,
	`street_address`	VARCHAR(100)	NOT NULL,
	`detail_address`	VARCHAR(100)	NULL,
	`refresh_token`	VARCHAR(600)	NOT NULL,
	`latitude`	DECIMAL(9,6)	NOT NULL,
	`longitude`	DECIMAL(9,6)	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL,
	`status`	TINYINT(1)	NOT NULL,
	`role`	VARCHAR(10)	NOT NULL
);

DROP TABLE IF EXISTS `body_record`;

CREATE TABLE `body_record` (
	`body_record_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`date`	DATE	NOT NULL,
	`weight`	DECIMAL(5,2)	NULL,
	`skeletal_muscle`	INT	NULL,
	`body_fat`	INT	NULL,
	`img`	VARCHAR(330)	NULL
);

DROP TABLE IF EXISTS `meal_record`;

CREATE TABLE `meal_record` (
	`meal_record_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`date`	DATE	NOT NULL,
	`type`	CHAR(6)	NOT NULL,
	`img`	VARCHAR(330)	NULL,
	`meal_time`	VARCHAR(20)	NULL,
	`content`	VARCHAR(500)	NOT NULL
);

DROP TABLE IF EXISTS `chat_room`;

CREATE TABLE `chat_room` (
	`chat_room_id`	INT	NOT NULL,
	`title`	VARCHAR(100)	NOT NULL,
	`last_at`	TIMESTAMP	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL
);

DROP TABLE IF EXISTS `chat_history`;

CREATE TABLE `chat_history` (
	`chat_history_id`	INT	NOT NULL,
	`chat_room_id`	INT	NOT NULL,
	`send_user_id`	INT	NOT NULL,
	`message`	VARCHAR(1000)	NOT NULL,
	`send_date`	TIMESTAMP	NOT NULL
);

DROP TABLE IF EXISTS `chat_room_user`;

CREATE TABLE `chat_room_user` (
	`chat_room_user_id`	INT	NOT NULL,
	`user_id2`	INT	NOT NULL,
	`chat_room_id2`	INT	NOT NULL,
	`joined_at`	TIMESTAMP	NOT NULL
);

ALTER TABLE `like` ADD CONSTRAINT `PK_LIKE` PRIMARY KEY (
	`like_id`
);

ALTER TABLE `board` ADD CONSTRAINT `PK_BOARD` PRIMARY KEY (
	`board_id`
);

ALTER TABLE `board_category` ADD CONSTRAINT `PK_BOARD_CATEGORY` PRIMARY KEY (
	`board_category_id`
);

ALTER TABLE `board_img` ADD CONSTRAINT `PK_BOARD_IMG` PRIMARY KEY (
	`board_img_id`
);

ALTER TABLE `reply` ADD CONSTRAINT `PK_REPLY` PRIMARY KEY (
	`reply_id`
);

ALTER TABLE `meal_food` ADD CONSTRAINT `PK_MEAL_FOOD` PRIMARY KEY (
	`meal_food_id`
);

ALTER TABLE `food` ADD CONSTRAINT `PK_FOOD` PRIMARY KEY (
	`food_id`
);

ALTER TABLE `meal` ADD CONSTRAINT `PK_MEAL` PRIMARY KEY (
	`meal_id`
);

ALTER TABLE `meal_plan` ADD CONSTRAINT `PK_MEAL_PLAN` PRIMARY KEY (
	`meal_plan_id`
);

ALTER TABLE `user` ADD CONSTRAINT `PK_USER` PRIMARY KEY (
	`user_id`
);

ALTER TABLE `body_record` ADD CONSTRAINT `PK_BODY_RECORD` PRIMARY KEY (
	`body_record_id`
);

ALTER TABLE `meal_record` ADD CONSTRAINT `PK_MEAL_RECORD` PRIMARY KEY (
	`meal_record_id`
);

ALTER TABLE `chat_room` ADD CONSTRAINT `PK_CHAT_ROOM` PRIMARY KEY (
	`chat_room_id`
);

ALTER TABLE `chat_history` ADD CONSTRAINT `PK_CHAT_HISTORY` PRIMARY KEY (
	`chat_history_id`
);

ALTER TABLE `chat_room_user` ADD CONSTRAINT `PK_CHAT_ROOM_USER` PRIMARY KEY (
	`chat_room_user_id`
);

ALTER TABLE `user` 
MODIFY COLUMN `user_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `like`
MODIFY COLUMN `like_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `board`
MODIFY COLUMN `board_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `board_img`
MODIFY COLUMN `board_img_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `reply`
MODIFY COLUMN `reply_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `board_category`
MODIFY COLUMN `board_category_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `food`
MODIFY COLUMN `food_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `meal_food`
MODIFY COLUMN `meal_food_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `meal_plan`
MODIFY COLUMN `meal_plan_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `meal`
MODIFY COLUMN `meal_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `meal_record`
MODIFY COLUMN `meal_record_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `body_record`
MODIFY COLUMN `body_record_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `chat_room_user`
MODIFY COLUMN `chat_room_user_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `chat_room`
MODIFY COLUMN `chat_room_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `chat_history`
MODIFY COLUMN `chat_history_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `like` ADD CONSTRAINT `FK_user_TO_like_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `board` ADD CONSTRAINT `FK_user_TO_board_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `board` ADD CONSTRAINT `FK_board_category_TO_board_1` FOREIGN KEY (
	`category_id`
)
REFERENCES `board_category` (
	`board_category_id`
);

ALTER TABLE `board_img` ADD CONSTRAINT `FK_board_TO_board_img_1` FOREIGN KEY (
	`board_id`
)
REFERENCES `board` (
	`board_id`
);

ALTER TABLE `reply` ADD CONSTRAINT `FK_user_TO_reply_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `reply` ADD CONSTRAINT `FK_board_TO_reply_1` FOREIGN KEY (
	`board_id`
)
REFERENCES `board` (
	`board_id`
);

ALTER TABLE `reply` ADD CONSTRAINT `FK_reply_TO_reply_1` FOREIGN KEY (
	`parent_reply_id`
)
REFERENCES `reply` (
	`reply_id`
);

ALTER TABLE `meal_food` ADD CONSTRAINT `FK_meal_TO_meal_food_1` FOREIGN KEY (
	`meal_id`
)
REFERENCES `meal` (
	`meal_id`
);

ALTER TABLE `meal_food` ADD CONSTRAINT `FK_food_TO_meal_food_1` FOREIGN KEY (
	`food_id`
)
REFERENCES `food` (
	`food_id`
);

ALTER TABLE `meal` ADD CONSTRAINT `FK_meal_plan_TO_meal_1` FOREIGN KEY (
	`meal_plan_id`
)
REFERENCES `meal_plan` (
	`meal_plan_id`
);

ALTER TABLE `meal_plan` ADD CONSTRAINT `FK_user_TO_meal_plan_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `body_record` ADD CONSTRAINT `FK_user_TO_body_record_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `meal_record` ADD CONSTRAINT `FK_user_TO_meal_record_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `chat_history` ADD CONSTRAINT `FK_chat_room_TO_chat_history_1` FOREIGN KEY (
	`chat_room_id`
)
REFERENCES `chat_room` (
	`chat_room_id`
);

ALTER TABLE `chat_history` ADD CONSTRAINT `FK_user_TO_chat_history_1` FOREIGN KEY (
	`send_user_id`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `chat_room_user` ADD CONSTRAINT `FK_user_TO_chat_room_user_1` FOREIGN KEY (
	`user_id2`
)
REFERENCES `user` (
	`user_id`
);

ALTER TABLE `chat_room_user` ADD CONSTRAINT `FK_chat_room_TO_chat_room_user_1` FOREIGN KEY (
	`chat_room_id2`
)
REFERENCES `chat_room` (
	`chat_room_id`
);
