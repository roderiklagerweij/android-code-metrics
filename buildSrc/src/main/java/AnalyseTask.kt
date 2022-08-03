//import com.google.common.io.Files
import data.api.AuthApi
import data.api.UploadApi
import domain.AnalysisResult
import extensions.android
import extensions.variants
import modules.loc.LocCounter
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.common.io.Files
import java.nio.charset.Charset

/*
* get config V
* post result call V
* project id V
* pass token V
* serialise analysis result V
* actual authentication V
* Test multi module
* Release
*
* Duplicate blocks
* Code complexity
* Retry network calls
*
 */

abstract class AnalyseTask : DefaultTask() {
//    @get:Input
//    abstract val greeting: Property<String>

    private val locCounter = LocCounter()

    init {
//        greeting.convention("hello from GreetingTask")
    }

    @TaskAction
    fun greet() {
        val codeMetricsPluginExtension =
            project.extensions.getByType(CodeMetricsPluginExtension::class.java)
                ?: return

        val projectId = codeMetricsPluginExtension.projectId ?: return
        val token = codeMetricsPluginExtension.token ?: return

        if (!AuthApi().validateProjectIdAndToken(projectId, token)) {
            println("Not authorized. Are projectId and token correct?")
            return
        }

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
//        println(greeting.get())
        UploadApi().postAnalysisResult(AnalysisResult(loc = 100))
    }
}
