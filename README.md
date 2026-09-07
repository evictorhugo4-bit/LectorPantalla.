# Lector de Pantalla

App Android que lee en voz alta (texto a voz) el contenido que ves en pantalla
en cualquier app, pensada especialmente para usarla mientras leés versículos
en **Logos Bible Software** (o cualquier otra app bíblica).

## Cómo funciona

1. Un **servicio de accesibilidad** puede leer el árbol de texto de la
   ventana activa (esto es distinto a "sacar una foto" — lee el texto real,
   no una imagen).
2. Un **botón flotante** aparece sobre cualquier app. Lo tocás una vez y lee
   en voz alta todo el texto visible en pantalla en ese momento. Lo tocás de
   nuevo y se detiene.
3. No lee nada automáticamente ni en segundo plano: solo actúa cuando vos
   tocás el botón.

## Opción A (recomendada): que GitHub te compile el APK gratis, sin instalar nada

Este proyecto ya incluye un archivo (`.github/workflows/build-apk.yml`) que le
dice a GitHub que compile el APK automáticamente en sus propios servidores.
Vos no instalás nada en tu PC.

1. Entrá a github.com, creá una cuenta si no tenés, y creá un repositorio
   nuevo (puede ser privado). Botón verde **"New"**.
2. Dentro del repo vacío, click en **"uploading an existing file"** (o
   **Add file → Upload files**).
3. Arrastrá **toda la carpeta** `LectorPantalla` descomprimida (con
   subcarpetas y todo) a esa pantalla. Confirmá el commit ("Commit changes").
4. Andá a la pestaña **Actions** del repositorio. Debería aparecer solo un
   workflow corriendo llamado "Compilar APK" (tarda 2–4 minutos). Si no
   arrancó solo, click en **"Run workflow"**.
5. Cuando termine (tilde verde ✅), entrá a esa ejecución y bajá del final
   de la página, en **Artifacts**, el archivo `LectorPantalla-debug-apk`
   (es un .zip que contiene el .apk adentro).
6. Pasá ese .apk a tu celular e instalalo (Android te va a pedir permitir
   "instalar apps de orígenes desconocidos" la primera vez).

## Opción B: compilarlo vos con Android Studio

1. Descomprimí este proyecto en tu PC.
2. Abrí Android Studio → **Open** → seleccioná la carpeta `LectorPantalla`.
3. Esperá a que termine el "Gradle Sync" (puede tardar unos minutos la
   primera vez, descarga dependencias de internet).
4. Menú **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Si más adelante querés instalarla en varios celulares o subirla a algún
lado, avisame y armamos también la versión firmada (release).

## Uso en el celular

1. Abrí la app "Lector de Pantalla".
2. Botón 1: te lleva a Configuración → Accesibilidad. Buscá "Lector de
   Pantalla" en la lista y activalo.
3. Botón 2: te pide el permiso de "Mostrar sobre otras apps" (superposición).
   Concedelo.
4. Botón 3: aparece un botón flotante naranja en la pantalla.
5. Abrí Logos, andá al versículo que quieras, tocá el botón flotante: te lo
   lee en voz alta. Tocalo otra vez para parar.

## Limitación conocida

Este método lee el **texto real** de la pantalla (accesibilidad), que es la
forma más confiable y liviana. Funciona con la enorme mayoría de las apps,
incluidas apps bíblicas, porque necesitan que el texto sea seleccionable
para poder copiar versículos.

Si alguna vez tocás "Leer pantalla" dentro de Logos y te dice "no encontré
texto legible", puede ser que esa pantalla puntual esté dibujada como imagen
en vez de texto. En ese caso, el siguiente paso sería agregar reconocimiento
óptico de caracteres (OCR) sobre una captura de pantalla — es más pesado
(necesita cámara/captura + librería de OCR) pero también se puede armar.
Avisame si te pasa eso y lo sumamos.

## Estructura del proyecto

```
LectorPantalla/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/lectorpantalla/app/
│       │   ├── MainActivity.kt                     (pantalla de configuración)
│       │   ├── ScreenReaderAccessibilityService.kt (lee el texto + TTS)
│       │   └── FloatingButtonService.kt            (botón flotante arrastrable)
│       └── res/                                    (layouts, strings, íconos)
├── build.gradle
└── settings.gradle
```
