package org.codehaus.groovy.grails.plugins.regen.util

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory

class HookMerge {
  static final Log LOG = LogFactory.getLog(HookMerge.class);

  def DefaultMarkers = [
   groovy:[beginCustom:"//<<"     , endCustom:"//>>"       , beginComment:"//"  ],
   java  :[beginCustom:"//<<"     , endCustom:"//>>"       , beginComment:"//"  ],
   js    :[beginCustom:"//<<"     , endCustom:"//>>"       , beginComment:"//"  ],
   gsp   :[beginCustom:"<g:custom", endCustom:"</g:custom>", beginComment:"%{--", endComment:"--}%", endOfLine:">" ],
   html  :[beginCustom:"<!--<<", endCustom:"<!-->>", beginComment:"<!--", endComment:"-->", endOfLine:">" ],
   jsp   :[beginCustom:"<%--<<", endCustom:"<%-->>", beginComment:"<%--", endComment:"-->", endOfLine:">" ]
  ]

  def getCustomRegions = { markers, String[] lines ->
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
         //println " custom start Line " + currentLine
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
         //println " custom end regionName " + regionName
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


String comment(Map markers, String text) {
  markers.beginComment + text + (markers.endComment ?:"")
}

 def void mergeTemplateFileWithCustomFile(Map params) {
   def templateFilePath = params.templateFilePath
   def customFilePath   = params.customFilePath ?: params.customFile?.getPath()
   def mergedFilePath   = params.mergedFilePath ?: customFilePath ?: params.customFile?.getPath()

   def extensionIndex = customFilePath.lastIndexOf(".")
   def fileExtension = (extensionIndex < 0)?"groovy":customFilePath.substring(extensionIndex + 1)

   def templateFile = params.templateFile ?: new java.io.File(templateFilePath)
   def customFile   = params.  customFile ?: new java.io.File(customFilePath)

   println "processing " + customFile
   def originalText =  templateFile.getText();
   def customText = customFile.getText();
   String mergedText = mergeTemplateWithCustom(originalText, customText, fileExtension);

   def mergedFile
   mergedFile = new java.io.File(mergedFilePath)
   if (mergedFile.exists()) {
    mergedFile.delete()
   }
   mergedFile << mergedText
 }

   def String mergeTemplateWithCustom(String originalText, String customText, String fileExtension) {
     mergeTemplateWithCustom( originalText, customText, fileExtension, false);
   }

   def String mergeTemplateWithCustom(String originalText, String customText, String fileExtension, boolean includeOrphans) {

   def templateFileLines = originalText.toString().split('\n')
   def customFileLines   = customText.split('\n')
   def mergedFileLines = []
   def markers = DefaultMarkers[fileExtension]

   def logLines = ["LogLines"]
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
      } else if (inCustomRegion) {
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
   def mergedLines = []
   def orphanRegions = customRegions.findAll { customRegion -> !templateRegionsNames.contains(customRegion.key) }
   //Orphan regions are appended at the end of the merged file
   if (orphanRegions != null)
   if (includeOrphans) {
     int closingBracketPosition = 0;
     for (int p=mergedFileLines.size()-1 ; p>1 && closingBracketPosition <=0; p-- ) {
       if (mergedFileLines[p]?.trim() == '}') {
         closingBracketPosition = p;
       }
     }
     if (!closingBracketPosition) closingBracketPosition = mergedFileLines.size() -1
     def mergeTail = mergedFileLines[closingBracketPosition..-1];
     mergedLines.addAll ( mergedFileLines[0..closingBracketPosition-1] )
     orphanRegions.each { orphanRegion ->
       orphanRegion.value.each { orphanRegionLine -> mergedLines << orphanRegionLine }
     }
     mergeTail?.each{mt -> mergedLines << mt}
   } else {
     mergedLines = mergedFileLines
     orphanRegions.each { orphanRegion ->
       //mergedFileLines << markers.beginComment + " Orphan Region : No custom region of name " + orphanRegion.key + " was not found " + (markers.endComment ?:"")
       mergedLines << comment (markers, " Orphan Region1 : No custom region of name ${orphanRegion.key} was not found ")
       orphanRegion.value.each { orphanRegionLine -> mergedFileLines << orphanRegionLine }
     }
   }

   StringBuffer mergedText = new StringBuffer();
   mergedLines.each{ line ->
     mergedText.append(line);
     mergedText.append("\n");
   }
   return mergedText.toString();
 }
}
