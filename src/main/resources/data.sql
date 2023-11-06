insert into user (version, id, username, name, hashed_password, avatar_image, avatar_image_name, email,
                              enabled, is_account_non_expired, is_account_non_locked, is_credentials_non_expired)
values (1, '1', 'admin', 'admin',
        '{argon2}$argon2id$v=19$m=16384,t=2,p=1$S9swkCEbXj5O8cwbZdyrtQ$4RkxTGl3xkEfm51P/gc3FORCysPStozAsm6smOnQR18',
        null, null, 'admin@example.com', true, true, true, true);
insert into user_roles (user_id, roles)
values ('1', 'USER');
insert into user_roles (user_id, roles)
values ('1', 'ADMIN');

insert into product (version,id,product_name, price,is_fixed_price,product_description,product_scale,product_image)
values (1,'1','Action-figure', 10000,true,'ggggg','kkkk',null);

insert into product (version,id,product_name, price,is_fixed_price,product_description,product_scale,product_image)
values (2,'2','Action-figure', 10000,true,'ggggg','kkkkk',null);
