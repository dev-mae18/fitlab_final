# FitLab

FitLab es un sistema de escritorio para la administración de un gimnasio, desarrollado en Java con JavaFX y SQLite. El proyecto permite gestionar socios, entrenadores, membresías, comprobantes de pago e informes desde una interfaz gráfica.

## Tecnologías utilizadas

- Java 21
- JavaFX 21.0.6
- FXML y CSS
- SQLite mediante JDBC 3.42.0.0
- JDBC
- Maven
- IntelliJ IDEA

## Funcionalidades

- Inicio de sesión de administrador.
- Registro, edición, búsqueda y consulta detallada de socios.
- Registro y administración de entrenadores y sus horarios.
- Asignación de membresías con una duración de 1 a 12 meses.
- Configuración de nombres y precios de los planes, y creación de planes mensuales o packs promocionales.
- Selección de plan, horario, días de asistencia y entrenador.
- Renovación y reactivación de membresías.
- Migración de planes con cálculo proporcional según los días restantes.
- Restricción de renovación y cancelación para membresías de 2 meses o más.
- Generación y anulación de comprobantes de pago.
- Control del aforo por día y turno.
- Visualización de informes generales del gimnasio.

## Base de datos

FitLab prepara automáticamente su base de datos en la carpeta del usuario de Windows:

```text
%USERPROFILE%\FitLab\fitlab.db
```

La ruta se calcula a partir de `user.home`. La clase `ConexionDB` crea la carpeta y prepara las seis tablas `usuarios`, `clientes`, `entrenadores`, `planes`, `membresias` y `comprobantes` mediante su método `initDatabase()`.

### Datos iniciales

Una base nueva y vacía recibe un administrador y estos planes de 30 días:

| Plan | Precio |
| --- | ---: |
| BASICO | S/ 50.00 |
| PREMIUM | S/ 80.00 |
| VIP | S/ 120.00 |

### Configuración de planes

La opción **Configuración**, encima de **Mi cuenta**, permite crear y editar planes. Los tres planes iniciales se conservan; las promociones se crean desde esa pantalla.

En un plan mensual se registra el precio por mes. En una promoción se registra el precio total del pack y su duración, entre 2 y 12 meses. Al seleccionar una promoción en una membresía se fija automáticamente la duración del pack y se aplica el descuento de estudiante cuando corresponde.

Las membresías guardan su precio y duración contratados, de modo que editar el catálogo no cambia sus importes anteriores. Las nuevas contrataciones y renovaciones utilizan los precios vigentes. Un plan que ya tenga membresías permite editar su nombre y precio; para ofrecer otro tipo o duración se crea un plan nuevo.

### Migración de la ubicación anterior

Si la nueva base todavía no existe y se encuentra `C:\sqlite\db\fitlab.db`, FitLab copia automáticamente esa base a la nueva ubicación. La migración conserva sus registros y credenciales, sin sobrescribir ni borrar el archivo original.

La copia se realiza una sola vez: cuando la base de destino ya existe, FitLab utiliza esa base y no vuelve a importar la anterior. Los datos iniciales no sustituyen usuarios, contraseñas ni planes existentes.

### Ruta alternativa para pruebas

La propiedad Java opcional `-Dfitlab.db.path=RUTA_DEL_ARCHIVO` permite usar otra base, por ejemplo en pruebas aisladas. Al indicar esta propiedad no se importa automáticamente la base de `C:\sqlite\db`; se prepara únicamente el archivo elegido. `sqlite_comando.txt` se conserva como referencia del esquema.

## Credenciales de acceso

```text
Usuario: admin
Contraseña: admin123
```

Estas credenciales corresponden a una base nueva y vacía. Si se migró una base anterior, se mantienen sus credenciales, incluida cualquier contraseña que ya se hubiera cambiado.

## Requisitos

Para ejecutar el código fuente:

- JDK 21 instalado.
- Maven Wrapper incluido en `.mvn/wrapper`, utilizado por `iniciar.bat`, o una instalación de Maven.
- Acceso a Internet la primera vez para descargar las dependencias, o una caché de Maven ya preparada.
- IntelliJ IDEA es opcional.

## Ejecución

### Desde IntelliJ IDEA

1. Abrir la carpeta `fitlab_final`.
2. Esperar que Maven descargue las dependencias.
3. Ejecutar la clase `HelloApplication`.

### Desde Windows

Dentro de la carpeta `fitlab_final`, ejecutar en PowerShell:

```powershell
.\iniciar.bat
```

Este archivo utiliza el Maven Wrapper del proyecto para iniciar JavaFX con JDK 21.

### Con Maven

```bash
mvn javafx:run
```

Este comando requiere Maven instalado; `iniciar.bat` utiliza el wrapper incluido.

## Preparación para WiX

El `pom.xml` configura Java 21 y copia las dependencias de ejecución a `target/libs` durante `mvn package`. La clase `Launcher` es el punto de entrada para el empaquetado modular con `jpackage`.

Para crear posteriormente un instalador de Windows se necesita WiX Toolset 3.14, con `candle.exe` y `light.exe` disponibles en el `PATH`.

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
├── sqlite_comando.txt    Referencia del esquema de la base de datos
└── pom.xml
```
