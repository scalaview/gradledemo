package task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TestTask extends DefaultTask {
    String greeting = 'hello from Y Soft'

    @TaskAction
    def greet() {
        println greeting
    }
}