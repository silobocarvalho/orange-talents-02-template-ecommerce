INSERT INTO USER(email, password) VALUES('user1@email.com', '$2a$10$WY2ZL3oL4aRxl0l9xtk9B.vS8NAx2qcRfeG9Tl96g/Vw3EeyTFth.');
INSERT INTO USER(email, password) VALUES('moderator@email.com', '$2a$10$WY2ZL3oL4aRxl0l9xtk9B.vS8NAx2qcRfeG9Tl96g/Vw3EeyTFth.');

INSERT INTO PROFILE(id, name) VALUES(1, 'ROLE_USER');
INSERT INTO PROFILE(id, name) VALUES(2, 'ROLE_MODERATOR');

INSERT INTO USER_PROFILES(user_id, profiles_id) VALUES  (1, 1);
INSERT INTO USER_PROFILES(user_id, profiles_id) VALUES  (2, 2);

INSERT INTO CATEGORY(name) VALUES ('Category 1');
INSERT INTO CATEGORY(name) VALUES ('Category 2');

INSERT INTO PRODUCT(description, name, price, quantity, category_id, owner_id) VALUES ('This is my product test.', 'Product Test', 100.50, 2, 1, 1);

INSERT INTO QUESTION(title, product_id, user_id) VALUES ('is this a title example for a question?', 1, 1);
INSERT INTO QUESTION(title, product_id, user_id) VALUES ('This is a second title for a question example.', 1, 1);

INSERT INTO PRODUCT_EVALUATION(title, rating, description, product_id, user_id) VALUES ('This is a rating from some user.', 5, 'this is the description of my rating.', 1, 1);
INSERT INTO PRODUCT_EVALUATION(title, rating, description, product_id, user_id) VALUES ('This is my second rating for same product.', 4, 'this is the description of second rating.', 1, 1);
INSERT INTO PRODUCT_EVALUATION(title, rating, description, product_id, user_id) VALUES ('This is my rating from user 2.', 5, 'this is my description.', 1, 2);

INSERT INTO IMAGE (owner_id, product_id, link) VALUES (1, 1, 'http://www.link1.com.br/23.png');
INSERT INTO IMAGE (owner_id, product_id, link) VALUES (1, 1, 'http://www.link2.com.br/image.png');
INSERT INTO IMAGE (owner_id, product_id, link) VALUES (2, 1, 'http://www.new.com.br/product.png');
INSERT INTO IMAGE (owner_id, product_id, link) VALUES (2, 1, 'http://www.new2.com.br/product2.png');

INSERT INTO ORDER_ENTITY(transaction_id, product_id, quantity_to_buy, payment_type, buyer_id) VALUES ('transaction-id-123', 1, 1, 1, 1);
INSERT INTO ORDER_ENTITY(transaction_id, product_id, quantity_to_buy, payment_type, buyer_id) VALUES ('transaction-989898', 1, 1, 0, 1);

INSERT INTO PAYMENT(payment_status, gateway_transaction_id, order_id, operation_time) VALUES (0, 'transaction-123456789', 1, '2021-03-03T05:47:08.644');




