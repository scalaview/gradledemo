package task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs


class IncrementalReverseTask extends DefaultTask {

    @Input
    def inputRoot

    @InputDirectory
    def FileCollection inputDir

    @OutputDirectory
    def File outputDir

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        println inputs.incremental ? "CHANGED inputs considered out of date"
                : "ALL inputs considered out of date"
        if (!inputs.incremental)
            project.delete(outputDir.listFiles())

        inputs.outOfDate { change ->
            println "out of date: ${change.file.path}"
            def targetFileName = change.file.path.replace("${inputRoot}/", "")
            def targetFile = new File(outputDir, targetFileName)
            println "copy to ${targetFile.path}"
            (!targetFile.exists()) && targetFile.getParentFile().mkdirs() && targetFile.createNewFile()

            def dstStream = targetFile.newDataOutputStream()
            def srcStream = change.file.newDataInputStream()
            dstStream << srcStream
            srcStream.close()
            dstStream.close()
        }

        inputs.removed { change ->
            println "removed: ${change.file.path}"
            def targetFileName = change.file.path.replace("${inputRoot}/", "")
            def targetFile = new File(outputDir, targetFileName)
            if(targetFile.exists()) targetFile.delete()
        }
    }
}