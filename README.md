# Generación Impacto - Backend

Backend de la plataforma Generación Impacto construido con Spring Boot 3.2.0.

## Requisitos

- Java 17 o superior
- Maven 3.6+
- Base de datos H2 (incluida) o PostgreSQL

## Configuración

1. El archivo `application.properties` está configurado para usar H2 en memoria.
2. Para producción, cambiar a PostgreSQL modificando las propiedades de base de datos.

## Ejecución

1. Compilar el proyecto:
```bash
mvn clean install
```

2. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

3. La aplicación estará disponible en:
```
http://localhost:8080
```

4. Consola H2 (solo desarrollo):
```
http://localhost:8080/h2-console
```

## API Endpoints

### Autenticación
- POST `/api/auth/register` - Registro de usuarios
- POST `/api/auth/login` - Inicio de sesión

### Tutor
- POST `/api/tutor/tutorship-requests` - Crear solicitud de tutoría
- GET `/api/tutor/announcements` - Obtener mis anuncios
- GET `/api/tutor/notifications` - Obtener notificaciones
- GET `/api/tutor/finance/total` - Obtener ganancias totales
- GET `/api/tutor/finance/payments` - Obtener pagos
- GET `/api/tutor/students` - Obtener mis estudiantes
- GET `/api/tutor/schedule/reservations` - Obtener reservaciones
- GET `/api/tutor/schedule/availability` - Obtener horarios disponibles
- POST `/api/tutor/schedule/availability` - Agregar horario disponible

### Estudiante
- GET `/api/student/announcements` - Obtener todos los anuncios
- POST `/api/student/reservations` - Reservar tutoría
- POST `/api/student/scholarships` - Solicitar beca
- GET `/api/student/notifications` - Obtener notificaciones
- GET `/api/student/reservations` - Obtener mis reservaciones
- GET `/api/student/tutors` - Obtener mis tutores

### Administrador
- GET `/api/admin/statistics` - Obtener estadísticas
- GET `/api/admin/tutors` - Obtener todos los tutores
- GET `/api/admin/tutors/{id}/announcements` - Obtener anuncios de tutor
- GET `/api/admin/students` - Obtener todos los estudiantes
- GET `/api/admin/notifications/tutorship-requests` - Obtener solicitudes pendientes
- GET `/api/admin/notifications/scholarships` - Obtener becas pendientes
- POST `/api/admin/tutorship-requests/{id}/approve` - Aprobar solicitud
- POST `/api/admin/tutorship-requests/{id}/reject` - Rechazar solicitud
- POST `/api/admin/scholarships/{id}/approve` - Aprobar beca
- POST `/api/admin/scholarships/{id}/reject` - Rechazar beca
- GET `/api/admin/finance` - Obtener resumen financiero

## Seguridad

- JWT tokens para autenticación
- CORS configurado para `http://localhost:4200`
- Contraseñas encriptadas con BCrypt
- Código secreto para admins: `GENIMPACTO2025`

## Modelo de Negocio

- Cada tutoría cuesta 25 soles
- El tutor recibe 20 soles
- La empresa gana 5 soles por tutoría pagada
- Las becas permiten que el estudiante no pague, pero el tutor recibe 20 soles de los fondos de la empresa




