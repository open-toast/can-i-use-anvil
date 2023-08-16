package com.toasttab.canv.processor

sealed interface OutputLocation {
    class AbsoluteLocation(val path: String) : OutputLocation

    class ClasspathLocation(val path: String) : OutputLocation

    companion object {
        fun from(path: String): OutputLocation {
            return if (path.startsWith("file://")) {
                AbsoluteLocation(path.removePrefix("file://"))
            } else if (path.startsWith("classpath://")) {
                ClasspathLocation(path.removePrefix("classpath://"))
            } else {
                throw IllegalArgumentException("$path is not a valid location")
            }
        }
    }
}
