package com.github.houbie.gradle.lesscss

import com.github.houbie.lesscss.Options
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class LesscTaskSpec extends Specification {
    File projectDir = new File('build/tmp/testproject')
    String projectRelativelessDir = '../../../src/test/resources/less'
    File lessDir = new File('src/test/resources/less')
    Project project

    def setup() {
        projectDir.delete()
        projectDir.mkdirs()
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        project.apply plugin: 'lesscss'
    }

    def 'lessc task defaults'() {
        def lesscTask = project.tasks.findByName('lessc')

        expect:
        lesscTask != null
        lesscTask.description == 'Compile LESS to CSS'
        lesscTask.options == new Options()
        lesscTask.engine == null
        lesscTask.lesscExecutable == null
        lesscTask.customJavaScript == null
        lesscTask.encoding == null
        lesscTask.sourceLocations == []
        lesscTask.includePaths == []
    }

    def 'configured lessc task'() {
        project.lessc {
            options.rootpath = 'myRootpath'
            engine = 'myEngine'
            lesscExecutable = 'myLesscExecutable'
            customJavaScript = 'myCustomJs'
            encoding = 'myEncoding'
            sourceLocations = ['mySourceLocation']
            includePaths = ['.', projectRelativelessDir]
        }

        def lesscTask = project.tasks.findByName('lessc')

        expect:
        lesscTask.options == new Options(rootpath: 'myRootpath')
        lesscTask.engine == 'myEngine'
        lesscTask.lesscExecutable == 'myLesscExecutable'
        lesscTask.customJavaScript == 'myCustomJs'
        lesscTask.encoding == 'myEncoding'
        lesscTask.sourceLocations == ['mySourceLocation']
        lesscTask.includePaths == [projectDir, lessDir]*.absoluteFile
    }

    def 'compile less files'() {
        project.lessc {
            dest = 'out'
            sourceLocations = ['basic.less']
            includePaths = [projectRelativelessDir]
            source = projectRelativelessDir
            include 'import.less'
        }

        project.tasks.findByName('lessc').run()

        expect:
        new File(projectDir, 'out/basic.css').text == new File(lessDir, 'basic.css').text
        new File(projectDir, 'out/import.css').text == new File(lessDir, 'import.css').text

    }
}
