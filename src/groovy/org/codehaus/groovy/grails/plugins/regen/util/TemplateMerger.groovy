package org.codehaus.groovy.grails.plugins.regen.util

import groovy.text.*;
//import org.springframework.beans.factory.InitializingBean
//import serviceinterfaces.MergeTemplateServiceInterface
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.cyberneko.html.parsers.SAXParser

class TemplateMerger { // implements InitializingBean {
  static final Log LOG = LogFactory.getLog(TemplateMerger.class);
  def grailsApplication
  def setting
  def engine = new SimpleTemplateEngine()

  void afterPropertiesSet()
  {
    this.setting = grailsApplication.config.setting
  }

  boolean transactional = false

  def DefaultMarkers = [
   groovy:[beginCustom:"//<<"     , endCustom:"//>>"       , beginComment:"//"  ],
   java  :[beginCustom:"//<<"     , endCustom:"//>>"       , beginComment:"//"  ],
   js    :[beginCustom:"//<<"     , endCustom:"//>>"       , beginComment:"//"  ],
   gsp   :[beginCustom:"<g:custom", endCustom:"</g:custom>", beginComment:"%{--", endComment:"--}%", endOfLine:">" ],
   html  :[beginCustom:"<!--<<", endCustom:"<!-->>", beginComment:"<!--", endComment:"-->", endOfLine:">" ],
   jsp   :[beginCustom:"<%--<<", endCustom:"<%-->>", beginComment:"<%--", endComment:"-->", endOfLine:">" ]
  ]

  def getCustomRegions = { markers, List lines ->
   def regions = [:]
   def currentRegion = []
   def inRegion = false
   def regionName = ""
   lines.each { currentLine ->
     if (currentLine.replaceAll("\\s+", "").startsWith(markers.beginCustom)) {
       if (!inRegion) {
         inRegion = true
         regionName =  getRegionName(markers, currentLine)
         currentRegion = [currentLine]
         println " custom start Line " + currentLine
       }
       else
       {
         def embeddedRegionName = getRegionName(markers, currentLine)
         LOG.error " custom region " + embeddedRegionName + " is embedded in region " + regionName
         currentRegion << currentLine
       }
     }
     else if (currentLine.replaceAll("\\s+", "").startsWith(markers.endCustom)) {
       if (inRegion) {
         inRegion = false
         currentRegion << currentLine
         println " custom end regionName " + regionName
         if (!regions.containsKey(regionName)) {
           regions.put(regionName, currentRegion)
         } else {
           LOG.error "Duplicate custom region named: " + regionName + " " + lines[6]
         }
       } else {
         LOG.error "region named: " + regionName + " " + lines[6]
       }
     }
     else if (inRegion)
     {
       currentRegion << currentLine
     }
   }
   regions
 }

 def getRegionName = { markers, String line ->
   def regionName = ""
   if (markers.endOfLine == ">") // assume it is an XML file
   {
     def matcher1 = line =~ "\\s" + markers.beginCustom + "\\sid=([\"\'])?(.*)\\1"
     def matcher2 = line =~ "\\s" + markers.beginCustom + "\\sid=(.*)[\\s" + markers.endOfLine + "]"
     if (matcher1.size() > 0) {
       regionName = matcher1[0][2]
     }
     else if (matcher2.size() > 0) {
       regionName = matcher2[0][1]
     }
   }
   else
   {
    if (line.trim().startsWith(markers.beginCustom)) {
      regionName = line.trim().substring(markers.beginCustom.length()).trim()
      if (markers.eolMarker) {
        regionName = regionName.substring(0, regionName.length() - markers.eolMarker.length() ).trim()
      }
    }
   }
   regionName
 }

 def void mergeTemplateFileWithCustomFile(Map params) {
   def logLines = []
   logLines << "LogLines"
   def templateFilePath = params.templateFilePath
   def customFilePath   = params.customFilePath ?: params.customFile?.getPath()
   def mergedFilePath   = params.mergedFilePath ?: customFilePath ?: params.customFile?.getPath()
   logLines << "template " + templateFilePath
   logLines << "custom   " + customFilePath
   logLines << "merged   " + mergedFilePath
   def extensionIndex = customFilePath.lastIndexOf(".")
   def fileExtension = (extensionIndex < 0)?"groovy":customFilePath.substring(extensionIndex + 1)
   def markers = DefaultMarkers[fileExtension]

   def templateFile = params.templateFile ?: new java.io.File(templateFilePath)
   def customFile   = params.  customFile ?: new java.io.File(customFilePath)
   logLines << "templaFile " + templateFile.exists()
   logLines << "customFile " + customFile.exists()
   println "processing " + customFile
   def t = engine.createTemplate(templateFile.getText())

   def originalText =  new StringWriter()
   t.make(params.binding).writeTo(originalText)
   def templateFileLines = originalText.toString().tokenize('\n')
   def customFileLines = customFile.readLines()

   def mergedFileLines = []

   def customRegions = getCustomRegions(markers, customFileLines)
   logLines << "customRegions " + customRegions

   def templateRegionsNames = []
   def inCustomRegion = false
   def inOriginRegion = false

   templateFileLines.each({currentLine ->
     if (currentLine.trim().startsWith(markers.beginCustom)) {
       def regionName = getRegionName(markers, currentLine)
       templateRegionsNames << regionName
      //println "=>starts >$regionName< -> " + currentLine
      if (customRegions.containsKey(regionName)) {
         mergedFileLines.addAll(customRegions[regionName])
         inCustomRegion = true
       }
       else
       {
         inOriginRegion = true
         mergedFileLines << currentLine
       }
     }
     else if (currentLine.trim().startsWith(markers.endCustom)) {
       //println("=>ends " + currentLine)
       if (inOriginRegion) {
         inOriginRegion = false
         mergedFileLines << currentLine
      }        else if (inCustomRegion) {
         inCustomRegion = false
       }
       else {
         mergedFileLines << currentLine + "  Warning: Unmatched end of Region in template."
       }
     }
     else if (!inCustomRegion) {
       mergedFileLines << currentLine
     }
     else {
        // Custom region code does not use the template.
     }
   })

   // Build orphan regions from orphan custom regions
   def orphanRegions = customRegions.findAll { customRegion -> !templateRegionsNames.contains(customRegion.key) }
   //Orphan regions are appended at the end of the merged file
   if (orphanRegions != null)
   {
     orphanRegions.each { orphanRegion ->
       //mergedFileLines << markers.beginComment + " Orphan Region : No custom region of name " + orphanRegion.key + " was not found " + (markers.endComment ?:"")
       mergedFileLines << comment (" Orphan Region : No custom region of name " + orphanRegion.key + " was not found ")
       orphanRegion.value.each { orphanRegionLine -> mergedFileLines << orphanRegionLine }
     }
   }

   def mergedFile
   mergedFile = new java.io.File(mergedFilePath)
   if (mergedFile.exists()) {
    mergedFile.delete()
   }

   // Write down the results in the merged file
   mergedFile.withWriter { merged ->
     mergedFileLines.each { line -> merged.writeLine(line) }
     //logLines.each { line ->  merged.writeLine(line) }
    }
  }

   // Incomplete first step for release 1.2
   // The merge will use the full xml tree.
   def void mergeTemplateAndCustomViews(Map params) {
   def logLines = []
   logLines << "LogLines"
   def templaFilePath = params.templateFilePath
   def customFilePath = params.customFilePath ?: params.customFile?.getPath()
   def mergedFilePath = params.mergedFilePath ?: customFilePath ?: params.customFile?.getPath()
   logLines << "template " + templaFilePath
   logLines << "custom   " + customFilePath
   logLines << "merged   " + mergedFilePath
   def fileExtension = "gsp"
   def markers = DefaultMarkers[fileExtension]

   def templaFile = new java.io.File(templaFilePath)
   def customFile = new java.io.File(customFilePath)
   logLines << "templaFile " + templaFile.exists()
   logLines << "customFile " + customFile.exists()

   def templaFileLines = templaFile.readLines()
   def customFileLines = customFile.readLines()

   //def parser = new org.cyberneko.html.parsers.SAXParser()
   //parser.setFeature('http://xml.org/sax/features/namespaces', false)
   //def page = new XmlParser(parser).parse(templaFilePath)
   //def allNodes = page.depthFirst().collect{it}

   //allNodes.collect({it.name().startsWith("G:CUSTOM")}).each{println it.name()}
   //allNodes.each({println "id " + it?.attribute('id')})
   def allNodes = new XmlParser().parse(templaFilePath);
   allNodes.findAll({it?.name().trim() == "G:CUSTOM"}).each({println it})
   println "------------"
   allNodes.findAll({it?.name().trim() == "G:CUSTOM"}).each({println "id " + it.attribute('id')})
   println "------------"
   allNodes.findAll({it?.name().trim() == "G:CUSTOM"}).each({println "c " + it.text()})
   //def cs = page.depthFirst().findAll{it?.name() == "G.CUSTOM"}
   //cs.each({println "cs " + it })
   //println "allNodes[0] " + allNodes[0]
   def data = page.depthFirst().A.'@href'.grep{ it != null && it.endsWith('.html') }
   data.each { println it }
   }

   String comment(Map markers, String text) {
     markers.beginComment + text + (markers.endComment ?:"")
   }
}
