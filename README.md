<h1 align="center">OneSky Gradle Plugin</h1>
<p align="center">
Plugin for downloading and uploading translations from <a href="https://www.oneskyapp.com/">OneSky</a>
</p>

<p align="center">
    <img src="https://img.shields.io/badge/version-1.2.0-blue.svg">
</p>

## Installation

**app/build.gradle.kts**
```kotlin
plugins {
    id("co.brainly.onesky") version "1.2.0"
}

// ...

configure<OneSkyPluginExtension> {
    apiKey = "your-api-key"
    apiSecret = "your-api-secret"
    projectId = <your_project_id>
    // list of files to sync in your src/main/res/values directory, 
    // e.g.: listOf("strings.xml", "plurals.xml")
    sourceStringFiles = emptyList()
    sourcePath = "path-to-your-string-values-directory"
    // has src/main/res/ as a default value,
    // can be overriden with your custom path (optional)
}
```

## Features

| Task                 | Description                                                              |
|----------------------|--------------------------------------------------------------------------|
| **translationsProgress** | Displays translations progress for all languages                      |
| **downloadTranslations** | Downloads all of available translations (including not finished ones) |
| **uploadTranslations**  | Uploads base translation files |

Add `-Pdeprecate-strings` if you would like to deprecate strings on OneSky which are not present in `sourceStringFiles`.

```bash
# deprecates removed strings on OneSky
./gradlew sample:uploadTranslations -Pdeprecate-strings
```
## Releasing

See the release instructions [here](HOW_TO_RELEASE.md).

## Development

```bash
# publish locally
./gradlew clean plugin:build plugin:publishToMavenLocal

# run a sample
./gradlew sample:translationsProgress
```

## License

```
MIT License

Copyright (c) 2021 Brainly, Inc

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
