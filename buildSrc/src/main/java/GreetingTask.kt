import com.google.common.io.Files
import extensions.android
import extensions.variants
import modules.loc.LocCounter
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.charset.Charset

abstract class GreetingTask : DefaultTask() {
    @get:Input
    abstract val greeting: Property<String>

    private val locCounter = LocCounter()

    init {
        greeting.convention("hello from GreetingTask")
    }

    @TaskAction
    fun greet() {
        project.android().variants().forEach { variant ->
            println("Variant: ${variant.name}")
            variant.sourceSets.forEach { sourceSet ->
                println("\t${sourceSet.name} ${sourceSet.javaDirectories}")
                sourceSet.javaDirectories.forEach { javaDirectory ->
                    val tree = project.fileTree(javaDirectory)
                    tree.include("**/*.kt")
                    tree.files.forEach { file ->
                        println(file.path)
                        val lines = Files.readLines(file, Charset.defaultCharset())
                        println("Lines: ${locCounter.count(lines)}")
                    }
                }
            }
        }
        println(greeting.get())
    }

//    private fun getFilesRecursively(path : File) : List<File>{
//        val files = listOf<File>()
//    }
}
