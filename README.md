# FitLab

FitLab es un sistema de escritorio para la administración de un gimnasio, desarrollado en Java con JavaFX y SQLite. El proyecto permite gestionar socios, entrenadores, membresías, comprobantes de pago e informes desde una interfaz gráfica.

La versión **1.0.0 para Windows de 64 bits** está disponible en [GitHub Releases](https://github.com/dev-mae18/fitlab_final/releases/tag/v1.0.0). Puedes descargar el [instalador FitLab-1.0.0.exe](https://github.com/dev-mae18/fitlab_final/releases/download/v1.0.0/FitLab-1.0.0.exe); incluye Java 21.

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

### Creación y reutilización

Si la carpeta o el archivo de la base no existen, FitLab crea la carpeta del usuario y SQLite genera `fitlab.db`. El método `initDatabase()` prepara las tablas y los datos iniciales.

Si la base ya existe, FitLab utiliza sus registros y credenciales. Las bases existentes deben tener el esquema actual definido en `sqlite_comando.txt`. Los datos iniciales se insertan únicamente cuando las tablas de usuarios o planes están vacías.

Las membresías vencidas dejan de contar como activas y de ocupar cupo. Las que vencen en la fecha actual mantienen su vigencia durante ese día.

### Desinstalación

El desinstalador elimina el programa, el Java incluido y sus accesos directos. La base de datos de `%USERPROFILE%\FitLab\fitlab.db` se conserva, por lo que una reinstalación permite continuar con los datos anteriores.

### Ruta alternativa para pruebas

La propiedad Java opcional `-Dfitlab.db.path=RUTA_DEL_ARCHIVO` permite usar otra base, por ejemplo en pruebas aisladas. Al indicarla, se prepara únicamente el archivo elegido. `sqlite_comando.txt` se conserva como referencia del esquema.

## Credenciales de acceso

```text
Usuario: admin
Contraseña: admin123
```

Estas credenciales corresponden a una base nueva y vacía. Si ya existe una base, se mantienen sus credenciales, incluida cualquier contraseña que ya se hubiera cambiado.

## Requisitos

Para utilizar el instalador se necesita Windows de 64 bits. El paquete incluye Java 21; no requiere instalar Java, Maven, WiX ni SQLite por separado.

Para ejecutar el código fuente:

- JDK 21 instalado.
- Maven Wrapper incluido en `.mvn/wrapper`, utilizado por `iniciar.bat`, o una instalación de Maven.
- Acceso a Internet la primera vez para descargar las dependencias, o una caché de Maven ya preparada.
- IntelliJ IDEA es opcional.

## Ejecución

### Con el instalador

1. Descargar [FitLab-1.0.0.exe desde Releases](https://github.com/dev-mae18/fitlab_final/releases/download/v1.0.0/FitLab-1.0.0.exe).
2. Ejecutar el instalador.
3. Completar la instalación. La carpeta predeterminada es `C:\Program Files\FitLab`.
4. Abrir FitLab desde el escritorio o el menú Inicio.

La base de datos se prepara automáticamente al abrir el programa. Las credenciales `admin / admin123` corresponden a una base nueva; una base existente conserva sus credenciales.

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

## Empaquetado para Windows

El `pom.xml` configura Java 21 y copia las dependencias de ejecución a `target/libs` durante `mvn package`. La clase `Launcher` es el punto de entrada para el empaquetado modular con `jpackage`. El icono de la ventana está en `img/logo.png` y el del instalador en `img/icono.ico`.

Para generar el instalador se necesita JDK 21, Maven y WiX Toolset 3.14. `JAVA_HOME` debe apuntar al JDK 21, y Maven y los ejecutables `candle.exe` y `light.exe` de WiX deben estar disponibles en el `PATH`.

Desde la raíz del proyecto, ejecutar en PowerShell:

```powershell
mvn clean package

& "$env:JAVA_HOME\bin\jpackage.exe" `
    --type exe `
    --name "FitLab" `
    --app-version "1.0.0" `
    --vendor "FitLab" `
    --icon "src/main/resources/pe/edu/upeu/fitlab_final/img/icono.ico" `
    --module-path "target/fitlab_final-1.0-SNAPSHOT.jar;target/libs" `
    --module "pe.edu.upeu.fitlab_final/pe.edu.upeu.fitlab_final.Launcher" `
    --win-shortcut `
    --win-menu `
    --win-menu-group "FitLab" `
    --dest target/dist
```

El resultado es `target/dist/FitLab-1.0.0.exe`, con Java 21 incluido y accesos directos para el escritorio y el menú Inicio. La carpeta `target` contiene archivos generados y está excluida de Git. Para distribuir la aplicación basta con entregar el instalador.

## Estructura del código

```text
fitlab_final/
├── src/main/java/pe/edu/upeu/fitlab_final/
│   ├── controller/       Controladores de las vistas
│   ├── implementacion/   Implementaciones de los DAO
│   ├── interfaces/       Interfaces de acceso a datos
│   ├── modelo/           Clases del modelo
│   ├── test/             Pruebas manuales de los DAO y la conexión
│   └── utils/            Conexión y utilidades
├── src/main/resources/pe/edu/upeu/fitlab_final/
│   ├── img/              Iconos de la aplicación y del instalador
│   └── views/            Archivos FXML y estilos CSS
├── iniciar.bat
├── sqlite_comando.txt    Referencia del esquema de la base de datos
└── pom.xml
```
