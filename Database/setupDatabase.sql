drop database db;
create database db;
create user 'springuser'@'%' identified by 'ThePassword';
grant all on db.* to 'springuser'@'%';
