//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//
//Ant.property(environment:"env")
//grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

  //ant.move(todir:"${basedir}/grails-app") {
  //  fileset(dir:"${regenPluginDir}/grails-app/generators")
  //}

//   ant.move(file:"${regenPluginDir}/scripts/generators", todir:"$basedir/grails-app")