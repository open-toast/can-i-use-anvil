import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.isRelease() = !project.version.toString().endsWith("-SNAPSHOT")
val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

object Pgp {
    val key by lazy {
        System.getenv("PGP_KEY")?.replace('$', '\n')
    }

    val password by lazy {
        System.getenv("PGP_PASSWORD")
    }
}

object Remote {
    val username by lazy {
        System.getenv("OSSRH_USERNAME")
    }

    val password by lazy {
        System.getenv("OSSRH_PASSWORD")
    }

    val url = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
}

object ProjectInfo {
    const val name = "Can I use Anvil?"
    const val url = "https://github.com/open-toast/can-i-use-anvil"
    const val description = "Helps migrate from pure Dagger to Anvil"
}
