/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Gant script that installs artifact and scaffolding templates
 *
 * @author Marcel Overdijk
 *
 * @since 0.4
 */

includeTargets << grailsScript("_GrailsInit")

target ('default': "Installs the artifact and scaffolding templates") {
    depends(checkVersion, parseArguments)
    templatesName = argsMap['params'][0]
    //templatesName = templatesName.replaceAll("\\.", "/")
  	templatesDir = 'src/templates/scaffolding/' + (templatesName ? "${templateName}/":'')
    targetDir = basedir + '/' + (templatesDir ?:'')
    overwrite = false

    // only if template dir already exists in, ask to overwrite templates
    if (new File(targetDir).exists()) {
        if (!isInteractive || confirmInput("Overwrite existing templates? [y/n]","overwrite.templates"))
            overwrite = true
    }
    else {
        ant.mkdir(dir: targetDir)
    }
    println "targetDir $targetDir"
    println "templatesDir $templatesDir"
	println  "basedir $basedir"
	copyGrailsResources("${targetDir}", "${templatesDir}*", overwrite)
    //ant.mkdir(dir:"${targetDir}/war")
    //copyGrailsResource("${targetDir}/war/web.xml", grailsResource("src/war/WEB-INF/web${servletVersion}.template.xml"), overwrite)

    event("StatusUpdate", [ "Templates installed successfully"])
}
