package com.toasttab.canv.processor

class DeclaredAnnotation private constructor(
    val name: String
) {
    companion object {
        val JAVAX_INJECT = DeclaredAnnotation("javax.inject.Inject")
        val DAGGER_COMPONENT = DeclaredAnnotation("dagger.Component")
        val DAGGER_SUBCOMPONENT = DeclaredAnnotation("dagger.Subcomponent")
        val DAGGER_MODULE = DeclaredAnnotation("dagger.Module")
        val DAGGER_ANDROID_INJECTOR = DeclaredAnnotation("dagger.android.ContributesAndroidInjector")
        val JAVAX_SINGLETON = DeclaredAnnotation("javax.inject.Singleton")
        val KOTLIN_METADATA = DeclaredAnnotation("kotlin.Metadata")
    }
}
