CREATE TABLE perfiles_cultivo (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    humedad_min_optima INT NOT NULL,
    humedad_max_optima INT NOT NULL,
    duracion_riego_minutos INT NOT NULL,
    trato_recomendado TEXT,
    activo BIT,
    fecha_registro DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),
    email VARCHAR(255),
    password_hash VARCHAR(255),
    fecha_creacion DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE configuracion_riego (
    id INT NOT NULL,
    modo_operacion VARCHAR(20) NOT NULL,
    cultivo_activo_id INT,
    hora_riego_programada TIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_configuracion_riego_cultivo_activo
        FOREIGN KEY (cultivo_activo_id) REFERENCES perfiles_cultivo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eventos_riego (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cultivo_id INT NOT NULL,
    modo_riego VARCHAR(255) NOT NULL,
    usuario_id INT,
    fecha_inicio DATETIME(6) NOT NULL,
    fecha_fin DATETIME(6),
    humedad_suelo_inicial INT NOT NULL,
    humedad_suelo_final INT,
    estado VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_eventos_riego_cultivo
        FOREIGN KEY (cultivo_id) REFERENCES perfiles_cultivo (id),
    CONSTRAINT fk_eventos_riego_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lecturas_sensor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    humedad_suelo INT NOT NULL,
    distancia_agua DOUBLE NOT NULL,
    fecha_lectura DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
