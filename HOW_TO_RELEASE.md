How to release
========

 1. Change the version in `plugin/build.gradle` to a non-SNAPSHOT verson.
 2. `git commit -am "Prepare X.Y.Z version"` (where X.Y.Z is the new version)
 3. `./gradlew clean publishPlugins`.
 4. `git tag X.Y.Z"` (where X.Y.Z is the new version)
 5. Change the version in `plugin/build.gradle` to next SNAPSHOT verson.
 6. `git commit -am "Prepare next iteration"`
 7. `git push origin master --tags`
 8. Promote new release on [releases page](https://github.com/brainly/onesky-gradle-plugin/releases)
