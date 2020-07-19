create table na_user
(
     na_user_id   number(9)    primary key,
     password_hash varchar(64), 
     last_name    varchar(32),
     first_name  varchar(16),
     user_name     varchar(16),
     date_of_birth date,
     phone_number  number(16),
     email        varchar(128)
    );
   

create table upload_file
(
       updload_file_id   number(9)   not null primary key,
       na_user_id_upload number(9)   not null references na_user,
       file_name         varchar(80) not null unique,
       file_type         varchar(4),
       bytes             number(12)
); 
