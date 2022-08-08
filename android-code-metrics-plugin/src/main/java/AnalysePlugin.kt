//import extensions.android
//import extensions.variants
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AnalysePlugin : Plugin<Project> {

    companion object {
        // All clients of this plugin will need to use this specific file name
        private const val COLOR_FILE_NAME = "my_colors.txt"
    }

    override fun apply(project: Project) {
        println("TEST!!!")
        project.extensions.create("codeMetrics", CodeMetricsPluginExtension::class.java)
        val task = project.tasks.register("greeting", AnalyseTask::class.java)
        task.get().group = "MyPluginTasks"
//        { greetingTask ->
//
//            greetingTask.group = "MyPluginTasks"
//        }
//        println(project.properties)
    }
}

open class CodeMetricsPluginExtension {
    var projectId : String? = null
    var token : String? = null
}