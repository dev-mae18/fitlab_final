# FitLab

FitLab es un sistema de escritorio para la administración de un gimnasio, desarrollado en Java con JavaFX y SQLite. El proyecto permite gestionar socios, entrenadores, membresías, comprobantes de pago e informes desde una interfaz gráfica.

## Tecnologías utilizadas

- Java 21
- JavaFX 21
- FXML y CSS
- SQLite 3
- JDBC
- Maven
- IntelliJ IDEA

## Funcionalidades

- Inicio de sesión de administrador.
- Registro, edición, búsqueda y consulta detallada de socios.
- Registro y administración de entrenadores y sus horarios.
- Asignación de membresías con una duración de 1 a 12 meses.
- Selección de plan, horario, días de asistencia y entrenador.
- Renovación y reactivación de membresías.
- Migración de planes con cálculo proporcional según los días restantes.
- Restricción de renovación y cancelación para membresías de 2 meses o más.
- Generación y anulación de comprobantes de pago.
- Control del aforo por día y turno.
- Visualización de informes generales del gimnasio.

## Base de datos

El sistema se conecta directamente al siguiente archivo SQLite:

```text
C:\sqlite\db\fitlab.db
```

La base de datos almacena usuarios, socios, entrenadores, planes, membresías y comprobantes.

### Programa recomendado

Se recomienda instalar **DB Browser for SQLite** para crear y administrar la base de datos mediante una interfaz gráfica. También se puede utilizar la consola oficial de SQLite.

### Crear la base de datos con DB Browser for SQLite

1. Crear la carpeta `C:\sqlite\db` si todavía no existe.
2. Abrir DB Browser for SQLite.
3. Seleccionar **Nueva base de datos**.
4. Ingresar a la carpeta `C:\sqlite\db`.
5. Guardar la base con el nombre exacto `fitlab.db`.
6. Si aparece la ventana para crear una tabla manualmente, cerrarla sin crear ninguna tabla.
7. Abrir la pestaña **Ejecutar SQL**.
8. Abrir el archivo `fitlab_final/sqlite_comando.txt` y copiar todo su contenido.
9. Pegar el código en el editor SQL.
10. Presionar el botón **Ejecutar todas las sentencias SQL**.
11. Seleccionar **Escribir cambios** para guardar la estructura dentro de `fitlab.db`.
12. Revisar la pestaña **Estructura de la base de datos**. Deben aparecer las tablas `usuarios`, `clientes`, `entrenadores`, `planes`, `membresias` y `comprobantes`.

El archivo `sqlite_comando.txt` contiene solamente la estructura de la base de datos. No incluye registros de prueba ni instrucciones para abrir una ruta específica, por lo que debe ejecutarse después de crear y abrir `fitlab.db`.

### Crear la base de datos con la consola de SQLite

Desde una terminal ubicada en la carpeta `fitlab_final`, ejecutar:

```powershell
sqlite3 C:\sqlite\db\fitlab.db ".read sqlite_comando.txt"
```

La ruta y el nombre deben conservarse exactamente porque la clase `ConexionDB` utiliza `C:/sqlite/db/fitlab.db`.

## Credenciales de acceso

```text
Usuario: admin
Contraseña: 12345
```

Estas credenciales corresponden a la base de datos utilizada por el proyecto. Una base recién creada mediante `sqlite_comando.txt` queda vacía; por ello, el usuario administrador y los planes deben registrarse posteriormente antes de utilizar todas las funciones del sistema.

## Requisitos

- JDK 21 instalado.
- SQLite 3 o DB Browser for SQLite.
- Tener creado el archivo `C:\sqlite\db\fitlab.db` con la estructura de `sqlite_comando.txt`.
- IntelliJ IDEA o Maven para ejecutar el proyecto.

## Ejecución

### Desde IntelliJ IDEA

1. Abrir la carpeta `fitlab_final`.
2. Esperar que Maven descargue las dependencias.
3. Ejecutar la clase `HelloApplication`.

### Desde Windows

Dentro de la carpeta `fitlab_final`, ejecutar:

```text
iniciar.bat
```

### Con Maven

```bash
mvn javafx:run
```

## Estructura del código

```text
fitlab_final/
├── src/main/java/pe/edu/upeu/fitlab_final/
│   ├── controller/       Controladores de las vistas
│   ├── implementacion/   Implementaciones de los DAO
│   ├── interfaces/       Interfaces de acceso a datos
│   ├── modelo/           Clases del modelo
│   ├── test/             Pruebas con la base de datos real
│   └── utils/            Conexión y utilidades
├── src/main/resources/pe/edu/upeu/fitlab_final/
│   └── views/            Archivos FXML y estilos CSS
├── iniciar.bat
└── pom.xml
```

## Documentación

La explicación del funcionamiento y de las partes principales del código se encuentra en `Manual_FitLab.pdf`.

## Autoría

Proyecto grupal desarrollado para la Unidad 2 del curso de Programación Orientada a Objetos.
