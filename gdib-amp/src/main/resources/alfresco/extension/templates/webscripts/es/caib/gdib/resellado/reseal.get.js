function main(){		  

   var nodeRef = search.findNode("workspace://SpacesStore/"+args["nodeId"]).nodeRef;
   resealDocuments.resealDocument(nodeRef,"eni_documento");

}
main();