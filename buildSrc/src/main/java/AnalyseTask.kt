//import org.apache.groovy.nio.extensions.NioExtensions.readLines
//import org.apache.tools.ant.types.resources.Files
import com.google.common.io.Files
import data.api.AuthApi
import data.api.UploadApi
import domain.AnalysisResult
import extensions.android
import extensions.variants
import modules.loc.LocCounter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.charset.Charset

//import java.nio.charset.Charset

/*
* get config V
* post result call V
* project id V
* pass token V
* serialise analysis result V
* actual authentication V
* Test multi module V
* Release
*
* Duplicate blocks
* Code complexity
* Retry network calls
*
 */

abstract class AnalyseTask : DefaultTask() {

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

        var loc = 0
        run {
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
                            loc += locCounter.count(lines)
                            println("Lines: ${locCounter.count(lines)}")
                        }
                    }
                }
                return@run
            }
        }

        UploadApi().postAnalysisResult(AnalysisResult(
            moduleName = project.name,
            loc = loc
        ))
    }
}
