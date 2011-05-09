import org.springframework.core.io.Resource
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

includeTargets << grailsScript("_GrailsInit")

target ('default': "Installs the artifact and scaffolding templates") {
    depends(checkVersion, parseArguments)
    generatorsType = argsMap['params'][0]
    //regeneratorsName = generatorsType?.replaceAll("\\.", "/")
    moveFromDir = "${regenPluginDir}/grails-app/generators/${generatorsType ?: ''}"
    moveToDir   = "${basedir}/grails-app${generatorsType?'/generators':''}"
    targetDir   = "${basedir}/grails-app/generators/${generatorsType ?:''}"

    overwrite = false

    //only if template dir already exists in, ask to overwrite templates
    if (new File(targetDir).exists()) {
      if (!isInteractive || confirmInput("Overwrite existing generators? [y/n]","overwrite.generators"))
        overwrite = true
    }
    else {
      ant.mkdir(dir: moveToDir)
    }
    ant.move(file:moveFromDir, todir:moveToDir, overwrite:overwrite)
    //copyGrailsResources("${targetDir}/", "src/grails/templates/scaffolding/${templatesName}", overwrite)
    event("StatusUpdate", [ "Generators installed successfully"])
}
