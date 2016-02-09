ALTER TABLE files DROP FOREIGN KEY fk_files_owner_user_id;
DROP INDEX ix_files_owner_user_id ON files;

ALTER TABLE files DROP FOREIGN KEY fk_files_group_group_id;
DROP INDEX ix_files_group_group_id ON files;

ALTER TABLE user_group DROP FOREIGN KEY fk_user_group_users;
DROP INDEX ix_user_group_users ON user_group;

ALTER TABLE user_group DROP FOREIGN KEY fk_user_group_groups;
DROP INDEX ix_user_group_groups ON user_group;

DROP TABLE IF EXISTS files;

DROP TABLE IF EXISTS groups;

DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS user_group;

