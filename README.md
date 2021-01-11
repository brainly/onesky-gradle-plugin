<h1 align="center">OneSky Gradle Plugin</h1>
<p align="center">
Plugin for downloading and uploading translations from <a href="https://www.oneskyapp.com/">OneSky</a>
</p>

<p align="center">
    <img src="https://img.shields.io/badge/version-1.0.0-blue.svg">
</p>

## Installation

**app/build.gradle.kts**
```kotlin
plugins {
    id("co.brainly.onesky") version "1.0.0"
}

// ...

configure<OneSkyPluginExtension> {
    apiKey = "your-api-key"
    apiSecret = "your-api-secret"
    projectId = <your_project_id>
    // list of files to sync in your src/main/res/values directory, 
    // e.g.: listOf("strings.xml", "plurals.xml")
    sourceStringFiles = emptyList()
}
```

## Features

| Task                 | Description                                                              |
|----------------------|--------------------------------------------------------------------------|
| **translationsProgress** | Displays translations progress for all languages                      |
| **downloadTranslations** | Downloads all of available translations (including not finished ones) |
| **uploadTranslations**  | Uploads base translation files |

## Releasing

See the release instructions [here](HOW_TO_RELEASE.md).

## Development

```bash
# publish locally
./gradlew clean plugin:build plugin:publishToMavenLocal

# run a sample
./gradlew sample:translationsProgress --refresh-dependencies
```
