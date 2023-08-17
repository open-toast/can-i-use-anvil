# Can I use Anvil?

[![Github Actions](https://github.com/open-toast/can-i-use-anvil/actions/workflows/ci.yml/badge.svg)](https://github.com/open-toast/can-i-use-anvil/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.toasttab.canv/can-i-use-anvil-processor)](https://search.maven.org/artifact/com.toasttab.canv/can-i-use-anvil-processor)

This tool assists with partial replacement of Kapt+Dagger with Anvil in multi-module projects.

## Background

Kapt+Dagger build performance in large projects is miserable.

Anvil is a Kotlin compiler plugin more performant than the Kapt+Dagger combo.

A subset of Kapt+Dagger functionality can be directly replaced with Anvil's factory generation without changing the ABI of generated code.

## So can I use Anvil?

This Gradle plugin will tell you if you can replace Dagger+Kapt with Anvil in a mixed Java/Kotlin project.

Under the hood, it registers a Kapt annotation processor which detects the following patterns incompatible with Anvil.

* Java (non-Kotlin) `@Module`s.
* Java (non-Kotlin) `@Singleton`s.
* Java classes with `@Inject` members.
* Kotlin classes with `@Inject` members inherited from a Java superclass.
* All `@Components`; components require Dagger anyway.

## How to use it

Apply the plugin to the root project.

```
plugins {
    id("com.toasttab.can-i-use-anvil") version "<< version >>"
}
```

Run `./gradlew anvilMigrationReport` to generate an aggregated report for all subprojects under `build/anvil-aggregated-report.json`.

The plugin needs to be aware of the _main_ Kapt task in each subproject. This can be controlled via the `anvilMigration` extension.
The default is `kaptKotlin`. However, in Android projects, Kapt tasks are variant-specific, e.g. `kaptDebugKotlin`.

```
anvilMigration {
    // for mixed Android/Java projects
    kaptTasks = listOf("kaptKotlin", "kaptDebugKotlin") 
}
```

## References

* https://speakerdeck.com/zacsweers/improve-build-times-in-less-time?slide=67
* https://github.com/square/anvil#dagger-factory-generation
