# Can I use Anvil?

This tool assists with partial replacement of Kapt+Dagger with Anvil in multi-module projects.

## Background

Kapt+Dagger build performance in large projects is miserable.

Anvil is a Kotlin compiler plugin more performant than the Kapt+Dagger combo.

A subset of Kapt+Dagger functionality can be directly replaced with Anvil's factory generation without changing the ABI of generated code.

## So can I use Anvil?

This is a Kapt annotation processor which will tell you if you can replace Dagger+Kapt with Anvil is the current project. Specifically, it will call out

* Java (non-Kotlin) `@Module`s.
* Java (non-Kotlin) `@Singleton`s.
* Java classes with `@Inject` members.
* Kotlin classes with `@Inject` members inherited from a Java superclass.
* All `@Components`; components require Dagger anyway.

## How to use it

Add the processor to the `kapt` configuration:

```
dependencies {
    kapt("com.toasttab.anvil:can-i-use-anvil-processor:<< version >>")
}
```

or, if you have many subprojects without centralized configuration, apply the gradle plugin at the root project level

```
plugins {
    id("com.toasttab.can-i-use-anvil") version "<< version >>"
}
```

## References

* https://speakerdeck.com/zacsweers/improve-build-times-in-less-time?slide=67
* https://github.com/square/anvil#dagger-factory-generation
