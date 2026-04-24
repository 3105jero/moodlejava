# 🎯 RIWI Talent Manager

Aplicación de escritorio para la gestión de **Coders / Empleados** de RIWI, desarrollada en Java 17 con arquitectura MVC, JDBC puro y soporte para vistas en consola o interfaz gráfica (Swing).

---

## 🗂️ Estructura del proyecto

```
riwi-talent/
├── src/main/java/com/riwi/talent/
│   ├── config/          # AppConfig — carga database.properties y app.properties
│   ├── controller/      # EmployedController — mediador MVC
│   ├── dao/             # Interfaces e implementaciones CRUD (GenericDAO, EmployedDAO)
│   │   └── impl/
│   ├── db/              # ConnectionManager — gestión de conexión JDBC
│   ├── model/entity/    # Employed (POJO mutable) · EmployedRecord (Java Record)
│   ├── view/            # View (interfaz) · ConsoleView · SwingView · BaseView
│   └── Main.java        # Punto de entrada
├── src/main/resources/
│   ├── database.properties         # ⚠️ NO subir a Git (ver .gitignore)
│   ├── database.properties.example # ✅ Plantilla segura — sí se sube
│   ├── app.properties              # Configuración de la app (nombre, vista)
│   └── schema.sql                  # DDL de la tabla `empleados`
└── pom.xml
```

---

## ✨ Características

- **Java 17 LTS** — `Records`, `Text Blocks`, `var`, `switch expressions`, `try-with-resources`
- **Arquitectura MVC** — separación clara entre modelo, vista y controlador
- **JDBC puro** — sin ORM; uso de `PreparedStatement` para prevenir SQL Injection
- **Doble vista** — consola o interfaz Swing, configurable sin recompilar
- **Dual DB** — compatible con **MySQL** y **PostgreSQL** (Supabase listo)
- **CRUD completo** — crear, listar, buscar, actualizar y eliminar empleados

---

## ⚙️ Requisitos previos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java JDK    | 17            |
| Maven       | 3.8+          |
| Base de datos | MySQL 8+ **ó** PostgreSQL 14+ |

---

## 🚀 Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/riwi-talent.git
cd riwi-talent
```

### 2. Configurar la base de datos

Copia la plantilla de configuración y completa tus credenciales:

```bash
cp src/main/resources/database.properties.example \
   src/main/resources/database.properties
```

Edita `database.properties` con tus datos reales (ver el archivo `.example` como guía).

### 3. Crear el esquema

**MySQL:**
```bash
mysql -u root -p < src/main/resources/schema.sql
```

**PostgreSQL:**
```bash
psql -U tu_usuario -d tu_base < src/main/resources/schema.sql
```

> Si usas Supabase, ejecuta el DDL directamente desde el SQL Editor del dashboard.

### 4. Seleccionar la vista (opcional)

En `src/main/resources/app.properties`:

```properties
view.type = swing    # "swing" para GUI | "console" para terminal
```

### 5. Compilar y ejecutar

```bash
mvn clean package
java -jar target/riwi-talent-1.0.0.jar
```

O directamente con Maven:

```bash
mvn compile exec:java -Dexec.mainClass="com.riwi.talent.Main"
```

---

## 🗄️ Modelo de datos

```sql
CREATE TABLE empleados (
    id            SERIAL / AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    apellido      VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    cohorte       VARCHAR(50)   NOT NULL,
    stack         VARCHAR(80)   NOT NULL,
    activo        BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_ingreso DATE          NOT NULL DEFAULT CURRENT_DATE
);
```

---

## 🔐 Seguridad y variables sensibles

El archivo `database.properties` contiene credenciales y **nunca debe subirse a Git**.

Asegúrate de que tu `.gitignore` incluya:

```
src/main/resources/database.properties
target/classes/database.properties
```

Usa `database.properties.example` como plantilla pública (sin valores reales).

---

## 🏗️ Arquitectura MVC

```
Main
 ├─► AppConfig           ← lee los .properties
 ├─► View (factory)      ← ConsoleView | SwingView
 ├─► EmployedDAO         ← interfaz del repositorio
 │     └─► EmployedDAOImpl
 │           └─► GenericDAOImpl ← lógica CRUD reutilizable
 │                 └─► ConnectionManager ← conexión JDBC singleton
 └─► EmployedController  ← orquesta vista ↔ DAO
```

---

## 📦 Dependencias principales

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| `org.postgresql:postgresql` | 42.7.3 | Driver JDBC PostgreSQL |
| `com.mysql:mysql-connector-j` | 8.3.0 | Driver JDBC MySQL (comentado en pom.xml) |
| `org.junit.jupiter:junit-jupiter` | 5.10.2 | Tests unitarios |

---

## Cambio base de datos

El proyecto actualmente funciona con postgresql, pero con facil cambio a mysql, en caso de hacerlo porfavor cambiar descometariar esta linea en el archivo `pom.xml` 
```
<!--
    <dependency>
      <groupId>com.mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <version>8.3.0</version>
    </dependency>
-->
```
Encontradas en las lineas 22 y 28 de este archivo

Ademas es importante que este descomentariado en el `database.properties` en el `database.properties.example` se muestra un ejemplo de como deberian quedar

## 👤 Autor

Desarrollado como proyecto académico en **RIWI** — Cohorte 2026.
