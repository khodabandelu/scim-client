INSERT INTO users (id, email, first_name, last_name, organization_id, password,is_active)
VALUES ('10001', 'ma.khodabandelu@gmail.com', 'mahdi', 'khodabandelu', '1',
        '$2a$10$VCS12HWq5J69L92mJnJE8OnNkAZg27.S5I1t2A/7ckc4cKNR6zpB.',1);

INSERT INTO user_roles (user_id, role)
VALUES ('10001', 'ROLE_USER');

INSERT INTO user_roles (user_id, role)
VALUES ('10001', 'ROLE_ADMIN');

INSERT INTO user_roles (user_id, role)
VALUES ('10001', 'ROLE_ROOT');

INSERT INTO  PROVISIONER (id,name,provisioner_type)
VALUES ('10001', 'Okta','Okta');

