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

La ruta se calcula a partir de `user.home`. No hace falta instalar SQLite, crear carpetas ni ejecutar SQL manualmente: la aplicación crea la carpeta y las seis tablas `usuarios`, `clientes`, `entrenadores`, `planes`, `membresias` y `comprobantes` cuando se conecta.

### Datos iniciales

Una base nueva y vacía recibe un administrador y estos planes de 30 días:

| Plan | Precio |
| --- | ---: |
| BASICO | S/ 50.00 |
| PREMIUM | S/ 80.00 |
| VIP | S/ 120.00 |

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

Para utilizar el instalador de Windows no se necesita instalar JDK, Maven ni SQLite: el paquete incluye el runtime de Java y las dependencias, y FitLab prepara la base de datos automáticamente.

Para desarrollar o ejecutar el código fuente:

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

## Empaquetado para Windows

El comando `empaquetar.bat` invoca la implementación `empaquetar.ps1` con JDK 21 y `jpackage`. El lanzador permite ejecutar el script aunque PowerShell bloquee archivos `.ps1`: aplica la opción de ejecución únicamente a ese proceso, sin cambiar la política global. Para generar el instalador `.exe` también se necesita WiX Toolset 3.14, con las herramientas `candle.exe` y `light.exe` de la serie 3.x. La opción `app-image` no requiere WiX.

El script usa `JAVA_HOME` o busca un JDK 21 dentro de `C:\Program Files\Java`. Busca WiX en su ubicación estándar o en el `PATH` del proceso, sin modificar el `PATH` global. Se pueden indicar rutas manuales mediante `-JavaHome` y `-WixBin`.

### Generar el instalador

Desde la carpeta `fitlab_final`, ejecutar:

```powershell
.\empaquetar.bat
```

El flujo ejecuta `clean package`, copia las dependencias de ejecución a `target/libs` y genera el instalador con `jpackage`. Con la versión predeterminada `1.0.0`, el archivo de salida es:

```text
target\dist\FitLab-1.0.0.exe
```

El proyecto conserva su estructura modular. El punto de entrada para empaquetar es `Launcher`, que delega en la aplicación JavaFX. El plugin `maven-dependency-plugin` 3.6.1 prepara las bibliotecas en `target/libs` durante `package`.

### Generar una aplicación sin instalador

```powershell
.\empaquetar.bat -Tipo app-image
```

La aplicación con su runtime queda en `target\app-image\FitLab`; se inicia mediante `target\app-image\FitLab\FitLab.exe`. Para distribuir esta opción se debe copiar la carpeta completa, no solamente el ejecutable.

### Opciones del script

| Opción | Uso |
| --- | --- |
| `-SinCompilar` | Reutiliza el JAR y `target/libs` ya construidos, y vuelve a ejecutar `jpackage` sin recompilar. |
| `-SinConexion` | Ejecuta Maven en modo offline; requiere todas las dependencias y plugins en la caché local. |
| `-JavaHome "C:\ruta\al\jdk-21"` | Selecciona manualmente el JDK 21. |
| `-WixBin "C:\ruta\a\WiX\bin"` | Selecciona la carpeta de `candle.exe` y `light.exe` 3.x. |
| `-Version 1.0.0` | Define la versión del paquete; `1.0.0` es el valor predeterminado. |

Por ejemplo, después de construir la aplicación, se puede reutilizar su compilación para generar el instalador:

```powershell
.\empaquetar.bat -SinCompilar
```

El script evita sobrescribir una salida que ya existe. Para reconstruir todo se puede ejecutar sin `-SinCompilar`; para conservar la compilación y generar otro instalador, se puede indicar una `-Version` diferente.

La preparación sigue la guía local de empaquetado utilizada para el proyecto, cuyo PDF no se incluye en el repositorio. Como referencias se utilizan el [proyecto del profesor y su configuración Maven](https://github.com/arlysanchez/poo_2026_2_final/blob/master/pom.xml) y la [documentación de jpackage para JDK 21](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html).

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
├── empaquetar.bat        Lanzador del empaquetado para Windows
├── empaquetar.ps1        Generación de app-image e instalador de Windows
├── sqlite_comando.txt    Referencia del esquema de la base de datos
└── pom.xml
```
