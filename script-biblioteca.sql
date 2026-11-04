-- Bases de datos del sistema de biblioteca.
-- IF NOT EXISTS conserva las bases ya creadas y sus datos.
CREATE DATABASE IF NOT EXISTS bd_libro
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_lector
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_prestamo
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Las tablas se crean/actualizan al iniciar cada servicio gracias a
-- spring.jpa.hibernate.ddl-auto=update.
