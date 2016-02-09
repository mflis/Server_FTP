create table files (
  file_id                       integer auto_increment not null,
  filename                      varchar(255) not null,
  owner_user_id                 integer,
  group_group_id                integer,
  user_read                     tinyint(1) default 0,
  user_write                    tinyint(1) default 0,
  group_read                    tinyint(1) default 0,
  group_write                   tinyint(1) default 0,
  constraint uq_files_filename unique (filename),
  constraint pk_files primary key (file_id)
);

create table groups (
  group_id                      integer auto_increment not null,
  group_name                    varchar(10) not null,
  constraint uq_groups_group_name unique (group_name),
  constraint pk_groups primary key (group_id)
);

create table users (
  user_id                       integer auto_increment not null,
  username                      varchar(10) not null,
  password                      varchar(255) not null,
  constraint uq_users_username unique (username),
  constraint pk_users primary key (user_id)
);

create table user_group (
  user_id                       integer not null,
  group_id                      integer not null,
  constraint pk_user_group primary key (user_id,group_id)
);

alter table files add constraint fk_files_owner_user_id foreign key (owner_user_id) references users (user_id) on delete restrict on update restrict;
create index ix_files_owner_user_id on files (owner_user_id);

alter table files add constraint fk_files_group_group_id foreign key (group_group_id) references groups (group_id) on delete restrict on update restrict;
create index ix_files_group_group_id on files (group_group_id);

alter table user_group add constraint fk_user_group_users foreign key (user_id) references users (user_id) on delete restrict on update restrict;
create index ix_user_group_users on user_group (user_id);

alter table user_group add constraint fk_user_group_groups foreign key (group_id) references groups (group_id) on delete restrict on update restrict;
create index ix_user_group_groups on user_group (group_id);

