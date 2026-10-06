# Vegas 50k - VIP Bankroll & Casino Betting Dashboard

Aplicación nativa para Android construida con **Kotlin**, **Jetpack Compose**, **Room Database** y **Firebase Auth**, diseñada bajo una estética **Dark Luxury** con analíticas avanzadas, control de riesgo y soporte multidivisa.

---

## 🚀 Cómo compilar la aplicación en GitHub

### Método 1: Compilación Automática en la nube con GitHub Actions (Recomendado)

El repositorio ya cuenta con el flujo de trabajo automatizado configurado en `.github/workflows/build.yml`.

1. **Subir el código a tu repositorio de GitHub:**
   ```bash
   git add .
   git commit -m "Compilar Vegas 50k"
   git push origin main
   ```
2. **Ejecutar o monitorear la compilación:**
   - Ve a la pestaña **Actions** en tu repositorio de GitHub.
   - Verás el flujo **"Build Android APK"** ejecutándose automáticamente.
   - También puedes iniciarlo manualmente haciendo clic en **"Run workflow"** (`workflow_dispatch`).
3. **Descargar tu archivo APK:**
   - Una vez finalizada la tarea (suele tardar 1–2 minutos), entra en la ejecución terminada.
   - En la sección inferior **Artifacts**, haz clic en **`Vegas-50k-Debug-APK`**.
   - Descargarás un archivo ZIP con el APK listo para instalar en cualquier teléfono o emulador Android.

---

### Método 2: Compilación Local desde la Terminal

Si clonas el repositorio en tu computadora:

1. **Requisitos previos:**
   - **Java JDK 17** instalado (`java -version`).
   - Android SDK (puedes instalarlo mediante Android Studio).

2. **Comando de compilación:**
   ```bash
   # En Linux / macOS:
   ./gradlew assembleDebug

   # En Windows:
   gradlew.bat assembleDebug
   ```

3. **Ubicación del APK generado:**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

### Método 3: Compilación con Android Studio

1. Abre **Android Studio**.
2. Selecciona **File > Open** y elige la carpeta raíz del proyecto.
3. Espera a que Gradle sincronice las dependencias automáticas.
4. En el menú superior, selecciona **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Haz clic en **locate** en la notificación inferior para abrir la carpeta con el APK.
