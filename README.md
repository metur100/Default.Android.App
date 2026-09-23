# Android WebView App Template

This project is a reusable Android Studio template for packaging a deployed website as an Android application using `WebView`.

You can copy this project for a new website, change a few values, and build a separate APK without replacing an existing app.

## What you change for every new app

For each new WebView app, normally change these values:

1. Website URL in `MainActivity.kt`
2. `applicationId` in `app/build.gradle.kts`
3. `namespace` in `app/build.gradle.kts`
4. Kotlin package name in `MainActivity.kt` and project folders
5. App name in `strings.xml`
6. App icon using Android Studio Image Asset Studio
7. Optional: package name in the project configuration and launcher configuration

The most important value for installing multiple apps on the same phone is `applicationId`.

---

## Project structure

Important files:

```text
ProjectName/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/defaultandroidapp/MainActivity.kt
│           └── res/
│               ├── mipmap-*/
│               └── values/strings.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/libs.versions.toml
```

Open the project root in Android Studio. The project root is the folder containing `settings.gradle.kts`.

Do not open only the `app` folder.

---

# 1. Change the website URL

Open:

```text
app/src/main/java/com/example/defaultandroidapp/MainActivity.kt
```

Find:

```kotlin
webView.loadUrl("https://logistic-management-ui.pages.dev/")
```

Replace it with the deployed URL of the new website:

```kotlin
webView.loadUrl("https://your-new-website.com/")
```

Example:

```kotlin
webView.loadUrl("https://my-company-dashboard.pages.dev/")
```

Use one complete URL. Do not combine two URLs like this:

```kotlin
webView.loadUrl("https://logistic-management-ui.pages.dev/https://github.com/example/project.git")
```

A GitHub repository URL is normally not the URL that should be loaded in the WebView. The WebView should load the deployed website URL, such as a Cloudflare Pages, Vercel, Netlify, or company-hosted URL.

## Website requirements

The website must be publicly reachable by the phone or emulator and should use HTTPS.

For image upload, the website should contain an HTML file input such as:

```html
<input type="file" accept="image/*">
```

For multiple image selection:

```html
<input type="file" accept="image/*" multiple>
```

The Android code already contains a `WebChromeClient` and file-picker handling for these inputs.

---

# 2. Change the app ID so the new app does not overwrite the old app

Open:

```text
app/build.gradle.kts
```

Find the `defaultConfig` block:

```kotlin
defaultConfig {
    applicationId = "com.example.logisticsystem"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
}
```

Change `applicationId` to a new unique value:

```kotlin
defaultConfig {
    applicationId = "com.example.newwebapp"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
}
```

Good examples:

```text
com.example.coloringbook
com.example.logisticsystem
com.metur.companydashboard
com.mycompany.customerportal
```

Rules for an application ID:

- Use lowercase letters.
- Use numbers only where appropriate.
- Separate parts with dots.
- Do not use spaces.
- Do not use hyphens.
- Do not use special characters.
- Use a different ID for every separately installable app.

For example, these are different applications:

```text
com.example.logisticsystem
com.example.coloringbook
com.example.customerportal
```

Android uses `applicationId` to identify an installed app. If two projects use the same application ID, installing one will update or replace the other.

## Important distinction

Changing only this line is not enough:

```kotlin
package com.example.defaultandroidapp
```

The setting that prevents overwriting is:

```kotlin
applicationId = "com.example.newwebapp"
```

---

# 3. Change the namespace and Kotlin package

This step is recommended for a clean project, although changing the `applicationId` is the part that controls installation identity.

## Change the namespace

In `app/build.gradle.kts`, change:

```kotlin
android {
    namespace = "com.example.defaultandroidapp"
```

to:

```kotlin
android {
    namespace = "com.example.newwebapp"
```

The relevant section should look like this:

```kotlin
android {
    namespace = "com.example.newwebapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.newwebapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}
```

## Change the package declaration

Open:

```text
app/src/main/java/com/example/defaultandroidapp/MainActivity.kt
```

Change:

```kotlin
package com.example.defaultandroidapp
```

to:

```kotlin
package com.example.newwebapp
```

## Move the Kotlin file

The folder should match the package name:

```text
app/src/main/java/com/example/newwebapp/MainActivity.kt
```

The safest way to do this in Android Studio is:

1. Open the **Project** panel.
2. Select the **Android** view.
3. Right-click the existing package.
4. Select **Refactor → Rename**.
5. Rename the package.
6. Confirm the refactoring.

Do not manually rename only the text if Android Studio can perform the refactoring for you.

---

# 4. Change the app name

Open:

```text
app/src/main/res/values/strings.xml
```

Change:

```xml
<resources>
    <string name="app_name">Logistic System</string>
</resources>
```

to the name of the new app:

```xml
<resources>
    <string name="app_name">Coloring Book</string>
</resources>
```

The manifest uses this value:

```xml
android:label="@string/app_name"
```

This name is displayed below the app icon and in Android settings.

Avoid putting the name directly into the manifest. Keep the name in `strings.xml` so it can be changed or translated easily.

---

# 5. Change the app icon

Use Android Studio's Image Asset Studio.

1. In the Project panel, right-click the `app` folder.
2. Select **New → Image Asset**.
3. For **Icon Type**, select **Launcher Icons (Adaptive and Legacy)**.
4. Keep the icon name as:

   ```text
   ic_launcher
   ```

5. Select the logo image for the foreground layer.
6. Choose a background color or background image.
7. Adjust the scale so the logo fits inside the safe area.
8. Click **Next**.
9. Confirm the resource locations.
10. Click **Finish**.

The launcher icon resources are generated in the `mipmap` folders.

The manifest should contain:

```xml
android:icon="@mipmap/ic_launcher"
android:roundIcon="@mipmap/ic_launcher_round"
```

A good source image is:

- Square
- At least 512 × 512 pixels
- PNG with transparency for the foreground logo, or SVG where supported
- Designed with space around the edges

If the old icon still appears on the phone, uninstall the old installation of the new package, then rebuild and install again. Android launchers can cache icons.

---

# 6. AndroidManifest.xml

The WebView application manifest should include Internet permission:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.DefaultAndroidApp">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.DefaultAndroidApp"
            android:windowSoftInputMode="adjustResize">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>
    </application>

</manifest>
```

For HTTPS websites, Internet permission is normally all that is required.

For a local HTTP development server, you may temporarily add this to the `<application>` element:

```xml
android:usesCleartextTraffic="true"
```

Do not use cleartext traffic for a production website unless it is genuinely required.

---

# 7. WebView features included in this template

The template includes:

- JavaScript support
- DOM storage support
- In-app navigation
- Android back-button navigation
- Image file selection through `<input type="file">`
- Single and multiple file-selection support when requested by the website
- WebView state restoration after configuration changes
- Cleanup when the activity is destroyed

The main settings are:

```kotlin
webView.settings.apply {
    javaScriptEnabled = true
    domStorageEnabled = true
    allowFileAccess = true
    allowContentAccess = true
}
```

The file picker is handled by:

```kotlin
webView.webChromeClient = object : WebChromeClient() {
    override fun onShowFileChooser(
        view: WebView?,
        callback: ValueCallback<Array<Uri>>?,
        params: FileChooserParams?
    ): Boolean {
        // Opens Android's file picker.
        return true
    }
}
```

No storage permission is normally needed to select an image through Android's document picker.

---

# 8. Build configuration

The current dependencies require Android API 37 for compilation. Keep this in `app/build.gradle.kts`:

```kotlin
compileSdk = 37
```

`compileSdk` controls which Android API is available while compiling. It does not determine the oldest Android version that can install the app.

The oldest supported Android version is controlled by:

```kotlin
minSdk = 24
```

The runtime behavior selected for newer Android versions is controlled by:

```kotlin
targetSdk = 36
```

Before building, install Android API 37 from:

**Tools → SDK Manager → SDK Platforms → Android API 37**

If API 37 is not installed, Gradle will not be able to compile the project.

---

# 9. Complete per-app change checklist

Use this checklist every time you create a new WebView app.

## Website

- [ ] Website is deployed and reachable from a phone.
- [ ] Website uses an HTTPS URL.
- [ ] The URL in `MainActivity.kt` is correct.
- [ ] The website has `<input type="file" accept="image/*">` if image upload is required.
- [ ] The website is tested in a normal mobile browser.

## Identity

- [ ] `applicationId` is unique.
- [ ] `namespace` is updated.
- [ ] Kotlin package declaration is updated.
- [ ] Kotlin package folders are updated if the package was renamed.
- [ ] The app name in `strings.xml` is updated.

## Branding

- [ ] `ic_launcher` was regenerated with Image Asset Studio.
- [ ] `ic_launcher_round` was regenerated if needed.
- [ ] The launcher icon is visible at the correct scale.

## Build

- [ ] Android API 37 is installed.
- [ ] Gradle sync completes successfully.
- [ ] The `app` module is selected in the run configuration.
- [ ] The emulator or phone is connected.
- [ ] The app builds successfully.

---

# 10. Sync, clean, rebuild, and run

After changing the project:

1. Click **File → Sync Project with Gradle Files**.
2. Click **Build → Clean Project**.
3. Click **Build → Rebuild Project**.
4. Select the `app` run configuration.
5. Start one emulator or connect a phone.
6. Click **Run**.

If the run configuration says **Module not specified**:

1. Open **Run → Edit Configurations**.
2. Click **+**.
3. Select **Android App**.
4. Set the module to `app`.
5. Click **Apply → OK**.

If no `app` module appears, verify that `settings.gradle.kts` contains:

```kotlin
include(":app")
```

Also make sure Android Studio opened the project root, not the `app` subfolder.

---

# 11. Export an APK

## Debug APK for testing

In Android Studio:

1. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. Wait for the build to finish.
3. Click the link in the notification to locate the APK.

The debug APK is usually created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

You can install this APK on a test phone.

## Signed release APK

For a production release:

1. Select **Build → Generate Signed Bundle / APK**.
2. Choose **APK** or **Android App Bundle**.
3. Create or select a keystore.
4. Select the `release` build type.
5. Complete the signing process.

Keep the keystore and passwords safe. You need the same signing key to update the app in the future.

Each separate app should have its own unique `applicationId`. If you are updating an existing app, keep its existing `applicationId` and signing key.

---

# 12. How to use this project as a permanent template

There are two useful approaches.

## Approach A: Copy the project folder

1. Close Android Studio.
2. Copy the complete project folder.
3. Rename the copied folder.
4. Open the copied folder in Android Studio.
5. Change the URL, app ID, app name, package, and icon.
6. Sync and build.

Do not copy only the `app` folder. Copy the complete project, including:

```text
settings.gradle.kts
build.gradle.kts
gradle/
gradlew
gradlew.bat
app/
```

## Approach B: Create an Android Studio project template

For a reusable template, keep one clean project named something like:

```text
AndroidWebViewTemplate
```

Keep these features in the template:

- A working `app` module
- `compileSdk = 37`
- WebView configuration
- File upload support
- Internet permission
- A placeholder URL
- Placeholder app name
- Placeholder launcher icon
- No website-specific business logic

For every new app, copy the template and change only the values listed in the checklist.

## Recommended template values

Template `app/build.gradle.kts`:

```kotlin
android {
    namespace = "com.example.webviewtemplate"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.webviewtemplate"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}
```

Template `strings.xml`:

```xml
<resources>
    <string name="app_name">WebView App</string>
</resources>
```

Template URL:

```kotlin
webView.loadUrl("https://example.com/")
```

Before building a real app, replace all three placeholder values.

---

# 13. Common problems

## The new app replaces the old app

Cause: both apps have the same `applicationId`.

Fix:

```kotlin
applicationId = "com.example.differentapp"
```

## The app opens a blank page

Check:

- The URL is correct.
- The website is deployed.
- The phone has Internet access.
- The manifest contains Internet permission.
- The website uses HTTPS.

## Image upload does not open

Check:

- The website uses `<input type="file">`.
- `webView.webChromeClient` is configured.
- The file-picker callback is always completed, including when the user cancels.
- The phone or emulator has an image available.

## Old icon is still visible

Uninstall the app, clean and rebuild the project, then install again. The launcher may have cached the old icon.

## `compileSdk` error

Install Android API 37 and make sure the module file contains:

```kotlin
compileSdk = 37
```

## Module not specified

Open the project root, sync Gradle, and create an Android App run configuration with module `app`.

## Emulator is already running

Do not start the same emulator twice. Stop the existing emulator process, then start it once from Device Manager.

---

# Quick reference: files to edit for a new app

| Purpose | File | Value to change |
|---|---|---|
| Website URL | `app/src/main/java/.../MainActivity.kt` | `webView.loadUrl(...)` |
| App identity | `app/build.gradle.kts` | `applicationId` |
| Code namespace | `app/build.gradle.kts` | `namespace` |
| Display name | `app/src/main/res/values/strings.xml` | `app_name` |
| Launcher icon | `app/src/main/res/mipmap-*` | Generate with Image Asset Studio |
| Internet access | `app/src/main/AndroidManifest.xml` | `INTERNET` permission |
| Included module | `settings.gradle.kts` | `include(":app")` |

After these changes, sync, rebuild, and export the APK.
