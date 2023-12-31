insert into user (version, id, username, name, hashed_password, avatar_image, avatar_image_name, email,
                              enabled, is_account_non_expired, is_account_non_locked, is_credentials_non_expired)
values (1, 1, 'admin', 'admin',
        '{argon2}$argon2id$v=19$m=16384,t=2,p=1$S9swkCEbXj5O8cwbZdyrtQ$4RkxTGl3xkEfm51P/gc3FORCysPStozAsm6smOnQR18',
        null, null, 'admin@example.com', true, true, true, true);
/*
insert into user (version, id, username, name, hashed_password, avatar_image, avatar_image_name, email,
                  enabled, is_account_non_expired, is_account_non_locked, is_credentials_non_expired)
values (1, 2, 'shit', 'admin',
        '{argon2}$argon2id$v=19$m=16384,t=2,p=1$S9swkCEbXj5O8cwbZdyrtQ$4RkxTGl3xkEfm51P/gc3FORCysPStozAsm6smOnQR18',
        null, null, 'weichun@example.com', true, true, true, true);
*/
insert into user_roles (user_id, roles)
values (1, 'USER');
/*
insert into user_roles (user_id, roles)
values (2, 'USER');
*/
insert into user_roles (user_id, roles)
values (1, 'ADMIN');
/*
insert into product (version,id,product_name,product_type,is_fixed_price,product_description,sellerid,seller_name,upset_price,product_amount,current_price,update_time,finish_time,bid_increment,product_image,visible)
values (1,1,'IPhone13','3C產品',true,'ggggg',1,'admin',null,3,1500,"2023-11-04 19:45:00",null,null,null,true);


insert into product (version,id,product_name,product_type,is_fixed_price,product_description,sellerid,seller_name,upset_price,product_amount,current_price,update_time,finish_time,bid_increment,product_image,visible)
values (1,2,'IPhone18','3C產品',true,'ggggg',1,'admin',null,3,1500,"2023-11-04 19:45:00",null,null,null,true);


insert into product (version,id,product_name,product_type,is_fixed_price,product_description,sellerid,upset_price,product_amount,current_price,update_time,finish_time,bid_increment,product_image,visible)
values (1,3,'IPhone13','3C產品',false,'ggggg',1,1000,1,1500,"2023-11-04 19:45:00","2023-11-04 20:45:00",1000,null,true);

insert into product (version,id,product_name,product_type,is_fixed_price,product_description,sellerid,upset_price,product_amount,current_price,update_time,finish_time,bid_increment,product_image,visible)
values (1,4,'IPhone13','3C產品',false,'ggggg',1,1000,1,1500,"2023-11-04 19:45:00","2023-11-30 18:45:00",1000,null,true);
*/