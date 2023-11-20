insert into user (version, id, username, name, hashed_password, avatar_image, avatar_image_name, email,
                              enabled, is_account_non_expired, is_account_non_locked, is_credentials_non_expired)
values (1, 1, 'admin', 'admin',
        '{argon2}$argon2id$v=19$m=16384,t=2,p=1$S9swkCEbXj5O8cwbZdyrtQ$4RkxTGl3xkEfm51P/gc3FORCysPStozAsm6smOnQR18',
        null, null, 'admin@example.com', true, true, true, true);
insert into user_roles (user_id, roles)
values (1, 'USER');
insert into user_roles (user_id, roles)
values (1, 'ADMIN');

insert into product (version,id,product_name,product_type, price,is_fixed_price,product_description,sellerID,upset_price,current_price,product_image)
values (1,1,'IPhone13','electronic',10000,true,'ggggg',1057088,1000,1500,null);

/*
insert into product (version,id,product_name,product_type, price,is_fixed_price,product_description,sellerID,upset_price,current_price,product_image)
values (2,3,'衛生紙','Daily need', 10000,true,'ggggg',987987,1000,1500,null);

insert into product (version,id,product_name,product_type, price,is_fixed_price,product_description,sellerID,upset_price,current_price,product_image)
values (3,4,'action figure','Stationary',10000,true,'ggggg',456456,1000,1500,null);
*/