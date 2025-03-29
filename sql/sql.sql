CREATE TABLE _user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    birth_date TIMESTAMP,
    hired_at TIMESTAMP,
    terminated_at TIMESTAMP,
    status VARCHAR(255),
    note TEXT,
    efficiency DOUBLE,
    user_role VARCHAR(255),
    address VARCHAR(255),
    phone_number VARCHAR(255) UNIQUE,
    allow_notifications BOOLEAN,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    leader_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (leader_id) REFERENCES _user(id)
);

CREATE TABLE team_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP,
    UNIQUE(team_id, user_id),
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_progress VARCHAR(255),
    name VARCHAR(255),
    description TEXT,
    note TEXT,
    priority VARCHAR(255),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_archived BOOLEAN DEFAULT FALSE,
    team_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE task_resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT,
    resource_id BIGINT,
    task_resource_type VARCHAR(255),
    quantity INTEGER,
    created_at TIMESTAMP,
    UNIQUE(task_id, resource_id),
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);

CREATE TABLE user_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP,
    UNIQUE(task_id, user_id),
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    quantity INTEGER,
    resource_type VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description TEXT,
    resource_id BIGINT,
    quantity INTEGER,
    cost INTEGER,
    created_at TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);

CREATE TABLE token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255),
    email VARCHAR(255),
    expired_at TIMESTAMP,
    created_at TIMESTAMP
);
