// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// log4j configuration

log4j = {

  root {
      info 'regenSummary', stdout
      error()
      additivity = true
  }

  appenders {
      console name:'stdout', layout:pattern(conversionPattern: '%m%n')
      appender new org.apache.log4j.FileAppender (name:'regenLast', append:false, file:'regenLast1.log', layout:pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{2} %m%n') )
      appender new org.apache.log4j.DailyRollingFileAppender(name: "regenSummary", datePattern: "'.'yyyy-MM-dd", file: "regenSummary.log", layout: pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{2} %m%n') )

  }

  error  'org.codehaus.groovy.grails.web.servlet',  //  controllers       static Logger LOG = Logger.getLogger(GrailsTemplateRegenerator.class)
       'org.codehaus.groovy.grails.web.pages', //  GSP
       'org.codehaus.groovy.grails.web.sitemesh', //  layouts
       'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
       'org.codehaus.groovy.grails.web.mapping', // URL mapping
       'org.codehaus.groovy.grails.commons', // core / classloading
       'org.codehaus.groovy.grails.plugins', // plugins
       'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
       'org.springframework',
       'org.hibernate',
       'net.sf.ehcache.hibernate'

  warn  'org.mortbay.log'

  info 'org.codehaus.groovy.grails.plugins.regen'

  debug regenSummary: 'org.codehaus.groovy.grails.plugins.regen'

  //debug regenLast: 'org.codehaus.groovy.grails.plugins.regen'


}


     
