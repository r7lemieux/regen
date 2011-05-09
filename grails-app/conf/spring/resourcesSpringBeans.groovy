 import org.codehaus.groovy.grails.plugins.regen.GenerationProcess
 import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator

 beans  {
   genProcess(GenerationProcess) {
   }

   regenerator(GrailsTemplateRegenerator){
     genProcess = ref('genProcess')
   }


 
}