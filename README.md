<h1 align="center">OneSky Gradle Plugin</h1>
<p align="center">
Plugin for downloading and uploading translations from <a href="https://www.oneskyapp.com/">OneSky</a>
</p>

<p align="center">
    <img src="https://img.shields.io/badge/version-0.5.0-blue.svg">
</p>

## Installation

```kotlin
plugins {
    id("co.brainly.onesky") version "0.5.0"
}

// ...

configure<OneSkyPluginExtension> {
    apiKey = "your-api-key"
    apiSecret = "your-api-secret"
    projectId = <your_project_id>
    // list of files to sync, e.g.: listOf("strings.xml", "plurals.xml")
    sourceStringFiles = emptyList()
}
```

## Features

| Task                 | Description                                                              |
|----------------------|--------------------------------------------------------------------------|
| **translationsProgress** | Displays progress translations for all languages                      |
| **downloadTranslations** | Downloads all of available translations (including not finished ones) |
