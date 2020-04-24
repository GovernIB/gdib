package es.caib.gdib.rm;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.Exporter;
import org.alfresco.service.cmr.view.ExporterContext;
import org.alfresco.service.cmr.view.ExporterException;
import org.alfresco.service.cmr.view.ReferenceType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.commons.lang3.ArrayUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ViewXMLExporter
  implements Exporter
{
  private static final String VIEW_LOCALNAME = "view";
  private static final String VALUES_LOCALNAME = "values";
  private static final String VALUE_LOCALNAME = "value";
  private static final String CHILDNAME_LOCALNAME = "childName";
  private static final String ASPECTS_LOCALNAME = "aspects";
  private static final String PROPERTIES_LOCALNAME = "properties";
  private static final String ASSOCIATIONS_LOCALNAME = "associations";
  private static final String DATATYPE_LOCALNAME = "datatype";
  private static final String ISNULL_LOCALNAME = "isNull";
  private static final String METADATA_LOCALNAME = "metadata";
  private static final String EXPORTEDBY_LOCALNAME = "exportBy";
  private static final String EXPORTEDDATE_LOCALNAME = "exportDate";
  private static final String EXPORTERVERSION_LOCALNAME = "exporterVersion";
  private static final String EXPORTOF_LOCALNAME = "exportOf";
  private static final String MLVALUE_LOCALNAME = "mlvalue";
  private static final String LOCALE_LOCALNAME = "locale";
  private static final String ACL_LOCALNAME = "acl";
  private static final String ACE_LOCALNAME = "ace";
  private static final String ACCESS_LOCALNAME = "access";
  private static final String AUTHORITY_LOCALNAME = "authority";
  private static final String PERMISSION_LOCALNAME = "permission";
  private static final String INHERITPERMISSIONS_LOCALNAME = "inherit";
  private static final String REFERENCE_LOCALNAME = "reference";
  private static final String PATHREF_LOCALNAME = "pathref";
  private static final String NODEREF_LOCALNAME = "noderef";
  private static QName VIEW_QNAME;
  private static QName VALUES_QNAME;
  private static QName VALUE_QNAME;
  private static QName PROPERTIES_QNAME;
  private static QName ASPECTS_QNAME;
  private static QName ASSOCIATIONS_QNAME;
  private static QName CHILDNAME_QNAME;
  private static QName DATATYPE_QNAME;
  private static QName ISNULL_QNAME;
  private static QName METADATA_QNAME;
  private static QName EXPORTEDBY_QNAME;
  private static QName EXPORTEDDATE_QNAME;
  private static QName EXPORTERVERSION_QNAME;
  private static QName EXPORTOF_QNAME;
  private static QName ACL_QNAME;
  private static QName ACE_QNAME;
  private static QName ACCESS_QNAME;
  private static QName AUTHORITY_QNAME;
  private static QName PERMISSION_QNAME;
  private static QName INHERITPERMISSIONS_QNAME;
  private static QName REFERENCE_QNAME;
  private static QName PATHREF_QNAME;
  private static QName NODEREF_QNAME;
  private static QName LOCALE_QNAME;
  private static QName MLVALUE_QNAME;
  private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
  private NamespaceService namespaceService;
  private NodeService nodeService;
  private SearchService searchService;
  private DictionaryService dictionaryService;
  private PermissionService permissionService;
  private ContentHandler contentHandler;
  private ExporterContext context;
  private ReferenceType referenceType;
  
  ViewXMLExporter(NamespaceService namespaceService, NodeService nodeService, SearchService searchService, DictionaryService dictionaryService, PermissionService permissionService, ContentHandler contentHandler)
  {
    this.namespaceService = namespaceService;
    this.nodeService = nodeService;
    this.searchService = searchService;
    this.dictionaryService = dictionaryService;
    this.permissionService = permissionService;
    this.contentHandler = contentHandler;
    
    VIEW_QNAME = QName.createQName("view", "view", namespaceService);
    VALUE_QNAME = QName.createQName("view", "value", namespaceService);
    VALUES_QNAME = QName.createQName("view", "values", namespaceService);
    CHILDNAME_QNAME = QName.createQName("view", "childName", namespaceService);
    ASPECTS_QNAME = QName.createQName("view", "aspects", namespaceService);
    PROPERTIES_QNAME = QName.createQName("view", "properties", namespaceService);
    ASSOCIATIONS_QNAME = QName.createQName("view", "associations", namespaceService);
    DATATYPE_QNAME = QName.createQName("view", "datatype", namespaceService);
    ISNULL_QNAME = QName.createQName("view", "isNull", namespaceService);
    METADATA_QNAME = QName.createQName("view", "metadata", namespaceService);
    EXPORTEDBY_QNAME = QName.createQName("view", "exportBy", namespaceService);
    EXPORTEDDATE_QNAME = QName.createQName("view", "exportDate", namespaceService);
    EXPORTERVERSION_QNAME = QName.createQName("view", "exporterVersion", namespaceService);
    EXPORTOF_QNAME = QName.createQName("view", "exportOf", namespaceService);
    ACL_QNAME = QName.createQName("view", "acl", namespaceService);
    ACE_QNAME = QName.createQName("view", "ace", namespaceService);
    ACCESS_QNAME = QName.createQName("view", "access", namespaceService);
    AUTHORITY_QNAME = QName.createQName("view", "authority", namespaceService);
    PERMISSION_QNAME = QName.createQName("view", "permission", namespaceService);
    INHERITPERMISSIONS_QNAME = QName.createQName("view", "inherit", namespaceService);
    REFERENCE_QNAME = QName.createQName("view", "reference", namespaceService);
    PATHREF_QNAME = QName.createQName("view", "pathref", namespaceService);
    NODEREF_QNAME = QName.createQName("view", "noderef", namespaceService);
    LOCALE_QNAME = QName.createQName("view", "locale", namespaceService);
    MLVALUE_QNAME = QName.createQName("view", "mlvalue", namespaceService);
  }
  
  public void setReferenceType(ReferenceType referenceType)
  {
    this.referenceType = referenceType;
  }
  
  public void start(ExporterContext context)
  {
    try
    {
      this.context = context;
      this.contentHandler.startDocument();
      this.contentHandler.startPrefixMapping("view", "http://www.alfresco.org/view/repository/1.0");
      this.contentHandler.startElement("view", "view", VIEW_QNAME.toPrefixString(), EMPTY_ATTRIBUTES);
      



      this.contentHandler.startElement("view", "metadata", METADATA_QNAME.toPrefixString(), EMPTY_ATTRIBUTES);
      

      this.contentHandler.startElement("view", "exportBy", EXPORTEDBY_QNAME.toPrefixString(), EMPTY_ATTRIBUTES);
      this.contentHandler.characters(context.getExportedBy().toCharArray(), 0, context.getExportedBy().length());
      this.contentHandler.endElement("view", "exportBy", EXPORTEDBY_QNAME.toPrefixString());
      

      this.contentHandler.startElement("view", "exportDate", EXPORTEDDATE_QNAME.toPrefixString(), EMPTY_ATTRIBUTES);
      String date = (String)DefaultTypeConverter.INSTANCE.convert(String.class, context.getExportedDate());
      this.contentHandler.characters(date.toCharArray(), 0, date.length());
      this.contentHandler.endElement("view", "exportDate", EXPORTEDDATE_QNAME.toPrefixString());
      

      this.contentHandler.startElement("view", "exporterVersion", EXPORTERVERSION_QNAME.toPrefixString(), EMPTY_ATTRIBUTES);
      this.contentHandler.characters(context.getExporterVersion().toCharArray(), 0, context.getExporterVersion().length());
      this.contentHandler.endElement("view", "exporterVersion", EXPORTERVERSION_QNAME.toPrefixString());
      

      this.contentHandler.startElement("view", "exportOf", EXPORTOF_QNAME.toPrefixString(), EMPTY_ATTRIBUTES);
      NodeRef[] exportList = context.getExportList();
      int comma = 1;
      for (int i = 0; i < exportList.length; i++)
      {
        NodeRef nodeRef = exportList[i];
        String path = this.nodeService.getPath(nodeRef).toPrefixString(this.namespaceService);
        if (i == exportList.length - 1) {
          comma = 0;
        }
        this.contentHandler.characters(ArrayUtils.addAll(path.toCharArray(), ",".toCharArray()), 0, path.length() + comma);
      }
      this.contentHandler.endElement("view", "exportOf", EXPORTOF_QNAME.toPrefixString());
      this.contentHandler.endElement("view", "metadata", METADATA_QNAME.toPrefixString());
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process export start event", e);
    }
  }
  
  public void startNamespace(String prefix, String uri)
  {
    try
    {
      this.contentHandler.startPrefixMapping(prefix, uri);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start namespace event - prefix " + prefix + " uri " + uri, e);
    }
  }
  
  public void endNamespace(String prefix)
  {
    try
    {
      this.contentHandler.endPrefixMapping(prefix);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end namespace event - prefix " + prefix, e);
    }
  }
  
  public void startNode(NodeRef nodeRef)
  {
    try
    {
      AttributesImpl attrs = new AttributesImpl();
      
      Path path = this.nodeService.getPath(nodeRef);
      if (path.size() > 1)
      {
        Path.ChildAssocElement pathElement = (Path.ChildAssocElement)path.last();
        QName childQName = pathElement.getRef().getQName();
        attrs.addAttribute("http://www.alfresco.org/view/repository/1.0", "childName", CHILDNAME_QNAME.toPrefixString(), null, toPrefixString(childQName));
      }
      QName type = this.nodeService.getType(nodeRef);
      this.contentHandler.startElement(type.getNamespaceURI(), type.getLocalName(), toPrefixString(type), attrs);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start node event - node ref " + nodeRef.toString(), e);
    }
  }
  
  public void endNode(NodeRef nodeRef)
  {
    try
    {
      QName type = this.nodeService.getType(nodeRef);
      this.contentHandler.endElement(type.getNamespaceURI(), type.getLocalName(), toPrefixString(type));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end node event - node ref " + nodeRef.toString(), e);
    }
  }
  
  public void startAspects(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.startElement(ASPECTS_QNAME.getNamespaceURI(), "aspects", toPrefixString(ASPECTS_QNAME), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start aspects", e);
    }
  }
  
  public void endAspects(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.endElement(ASPECTS_QNAME.getNamespaceURI(), "aspects", toPrefixString(ASPECTS_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end aspects", e);
    }
  }
  
  public void startAspect(NodeRef nodeRef, QName aspect)
  {
    try
    {
      this.contentHandler.startElement(aspect.getNamespaceURI(), aspect.getLocalName(), toPrefixString(aspect), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start aspect event - node ref " + nodeRef.toString() + "; aspect " + toPrefixString(aspect), e);
    }
  }
  
  public void endAspect(NodeRef nodeRef, QName aspect)
  {
    try
    {
      this.contentHandler.endElement(aspect.getNamespaceURI(), aspect.getLocalName(), toPrefixString(aspect));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end aspect event - node ref " + nodeRef.toString() + "; aspect " + toPrefixString(aspect), e);
    }
  }
  
  public void startACL(NodeRef nodeRef)
  {
    try
    {
      AttributesImpl attrs = new AttributesImpl();
      boolean inherit = this.permissionService.getInheritParentPermissions(nodeRef);
      if (!inherit) {
        attrs.addAttribute("http://www.alfresco.org/view/repository/1.0", "inherit", INHERITPERMISSIONS_QNAME.toPrefixString(), null, "false");
      }
      this.contentHandler.startElement(ACL_QNAME.getNamespaceURI(), ACL_QNAME.getLocalName(), toPrefixString(ACL_QNAME), attrs);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start ACL event - node ref " + nodeRef.toString());
    }
  }
  
  public void permission(NodeRef nodeRef, AccessPermission permission)
  {
    try
    {
      AttributesImpl attrs = new AttributesImpl();
      attrs.addAttribute("http://www.alfresco.org/view/repository/1.0", "access", ACCESS_QNAME.toPrefixString(), null, permission.getAccessStatus().toString());
      this.contentHandler.startElement(ACE_QNAME.getNamespaceURI(), ACE_QNAME.getLocalName(), toPrefixString(ACE_QNAME), attrs);
      

      this.contentHandler.startElement(AUTHORITY_QNAME.getNamespaceURI(), AUTHORITY_QNAME.getLocalName(), toPrefixString(AUTHORITY_QNAME), EMPTY_ATTRIBUTES);
      String authority = permission.getAuthority();
      this.contentHandler.characters(authority.toCharArray(), 0, authority.length());
      this.contentHandler.endElement(AUTHORITY_QNAME.getNamespaceURI(), AUTHORITY_QNAME.getLocalName(), toPrefixString(AUTHORITY_QNAME));
      

      this.contentHandler.startElement(PERMISSION_QNAME.getNamespaceURI(), PERMISSION_QNAME.getLocalName(), toPrefixString(PERMISSION_QNAME), EMPTY_ATTRIBUTES);
      String strPermission = permission.getPermission();
      this.contentHandler.characters(strPermission.toCharArray(), 0, strPermission.length());
      this.contentHandler.endElement(PERMISSION_QNAME.getNamespaceURI(), PERMISSION_QNAME.getLocalName(), toPrefixString(PERMISSION_QNAME));
      

      this.contentHandler.endElement(ACE_QNAME.getNamespaceURI(), ACE_QNAME.getLocalName(), toPrefixString(ACE_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process permission event - node ref " + nodeRef.toString() + "; permission " + permission);
    }
  }
  
  public void endACL(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.endElement(ACL_QNAME.getNamespaceURI(), ACL_QNAME.getLocalName(), toPrefixString(ACL_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end ACL event - node ref " + nodeRef.toString());
    }
  }
  
  public void startProperties(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.startElement(PROPERTIES_QNAME.getNamespaceURI(), "properties", toPrefixString(PROPERTIES_QNAME), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start properties", e);
    }
  }
  
  public void endProperties(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.endElement(PROPERTIES_QNAME.getNamespaceURI(), "properties", toPrefixString(PROPERTIES_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start properties", e);
    }
  }
  
  public void startProperty(NodeRef nodeRef, QName property)
  {
    try
    {
      QName encodedProperty = QName.createQName(property.getNamespaceURI(), ISO9075.encode(property.getLocalName()));
      this.contentHandler.startElement(encodedProperty.getNamespaceURI(), encodedProperty.getLocalName(), toPrefixString(encodedProperty), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start property event - nodeRef " + nodeRef + "; property " + toPrefixString(property), e);
    }
  }
  
  public void endProperty(NodeRef nodeRef, QName property)
  {
    try
    {
      QName encodedProperty = QName.createQName(property.getNamespaceURI(), ISO9075.encode(property.getLocalName()));
      this.contentHandler.endElement(encodedProperty.getNamespaceURI(), encodedProperty.getLocalName(), toPrefixString(encodedProperty));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end property event - nodeRef " + nodeRef + "; property " + toPrefixString(property), e);
    }
  }
  
  public void startValueCollection(NodeRef nodeRef, QName property)
  {
    try
    {
      this.contentHandler.startElement("view", "values", toPrefixString(VALUES_QNAME), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start collection event - nodeRef " + nodeRef + "; property " + toPrefixString(property), e);
    }
  }
  
  public void endValueCollection(NodeRef nodeRef, QName property)
  {
    try
    {
      this.contentHandler.endElement("view", "values", toPrefixString(VALUES_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end collection event - nodeRef " + nodeRef + "; property " + toPrefixString(property), e);
    }
  }
  
  public void value(NodeRef nodeRef, QName property, Object value, int index)
  {
    try
    {
      QName valueDataType = null;
      PropertyDefinition propDef = this.dictionaryService.getProperty(property);
      DataTypeDefinition dataTypeDef = propDef == null ? null : propDef.getDataType();
      if ((dataTypeDef == null) || (dataTypeDef.getName().equals(DataTypeDefinition.ANY)))
      {
        dataTypeDef = value == null ? null : this.dictionaryService.getDataType(value.getClass());
        if (dataTypeDef != null) {
          valueDataType = dataTypeDef.getName();
        }
      }
      boolean isMLText = (dataTypeDef != null) && (dataTypeDef.getName().equals(DataTypeDefinition.MLTEXT));
      if (((value instanceof NodeRef)) && (this.referenceType.equals(ReferenceType.PATHREF)))
      {
        NodeRef valueNodeRef = (NodeRef)value;
        if (nodeRef.getStoreRef().equals(valueNodeRef.getStoreRef()))
        {
          Path nodeRefPath = null;
          if (property.equals(ContentModel.PROP_CATEGORIES)) {
            nodeRefPath = this.nodeService.getPath(valueNodeRef);
          } else {
            nodeRefPath = createPath(this.context.getExportParent(), nodeRef, valueNodeRef);
          }
          value = nodeRefPath == null ? null : nodeRefPath.toPrefixString(this.namespaceService);
        }
      }
      if ((value == null) || (valueDataType != null) || (index != -1))
      {
        AttributesImpl attrs = new AttributesImpl();
        if (value == null) {
          attrs.addAttribute("view", "isNull", ISNULL_QNAME.toPrefixString(), null, "true");
        }
        if (valueDataType != null) {
          attrs.addAttribute("view", "datatype", DATATYPE_QNAME.toPrefixString(), null, toPrefixString(valueDataType));
        }
        if (!isMLText) {
          this.contentHandler.startElement("view", "value", toPrefixString(VALUE_QNAME), attrs);
        }
      }
      String strValue = (String)DefaultTypeConverter.INSTANCE.convert(String.class, value);
      if (strValue != null) {
        for (int i = 0; i < strValue.length(); i++)
        {
          char[] temp = { strValue.charAt(i) };
          this.contentHandler.characters(temp, 0, 1);
        }
      }
      if (((value == null) || (valueDataType != null) || (index != -1)) && (!isMLText)) {
        this.contentHandler.endElement("view", "value", toPrefixString(VALUE_QNAME));
      }
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process value event - nodeRef " + nodeRef + "; property " + toPrefixString(property) + "; value " + value, e);
    }
  }
  
  public void content(NodeRef nodeRef, QName property, InputStream content, ContentData contentData, int index) {}
  
  public void startAssoc(NodeRef nodeRef, QName assoc)
  {
    try
    {
      this.contentHandler.startElement(assoc.getNamespaceURI(), assoc.getLocalName(), toPrefixString(assoc), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start assoc event - nodeRef " + nodeRef + "; association " + toPrefixString(assoc), e);
    }
  }
  
  public void endAssoc(NodeRef nodeRef, QName assoc)
  {
    try
    {
      this.contentHandler.endElement(assoc.getNamespaceURI(), assoc.getLocalName(), toPrefixString(assoc));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end assoc event - nodeRef " + nodeRef + "; association " + toPrefixString(assoc), e);
    }
  }
  
  public void startAssocs(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.startElement(ASSOCIATIONS_QNAME.getNamespaceURI(), "associations", toPrefixString(ASSOCIATIONS_QNAME), EMPTY_ATTRIBUTES);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start associations", e);
    }
  }
  
  public void endAssocs(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.endElement(ASSOCIATIONS_QNAME.getNamespaceURI(), "associations", toPrefixString(ASSOCIATIONS_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end associations", e);
    }
  }
  
  public void startReference(NodeRef nodeRef, QName childName)
  {
    try
    {
      ReferenceType referenceFormat = this.referenceType;
      if (nodeRef.equals(this.nodeService.getRootNode(nodeRef.getStoreRef()))) {
        referenceFormat = ReferenceType.PATHREF;
      }
      AttributesImpl attrs = new AttributesImpl();
      if (referenceFormat.equals(ReferenceType.PATHREF))
      {
        Path path = createPath(this.context.getExportParent(), this.context.getExportParent(), nodeRef);
        attrs.addAttribute("http://www.alfresco.org/view/repository/1.0", "pathref", PATHREF_QNAME.toPrefixString(), null, path.toPrefixString(this.namespaceService));
      }
      else
      {
        attrs.addAttribute("http://www.alfresco.org/view/repository/1.0", "noderef", NODEREF_QNAME.toPrefixString(), null, nodeRef.toString());
      }
      if (childName != null) {
        attrs.addAttribute("http://www.alfresco.org/view/repository/1.0", "childName", CHILDNAME_QNAME.toPrefixString(), null, childName.toPrefixString(this.namespaceService));
      }
      this.contentHandler.startElement(REFERENCE_QNAME.getNamespaceURI(), "reference", toPrefixString(REFERENCE_QNAME), attrs);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start reference", e);
    }
  }
  
  public void endReference(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.endElement(REFERENCE_QNAME.getNamespaceURI(), "reference", toPrefixString(REFERENCE_QNAME));
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end reference", e);
    }
  }
  
  public void startValueMLText(NodeRef nodeRef, Locale locale, boolean isNull)
  {
    AttributesImpl attrs = new AttributesImpl();
    attrs.addAttribute("view", "locale", LOCALE_QNAME.toPrefixString(), null, locale.toString());
    if (isNull) {
      attrs.addAttribute("view", "isNull", ISNULL_QNAME.toPrefixString(), null, "true");
    }
    try
    {
      this.contentHandler.startElement("view", "mlvalue", MLVALUE_QNAME.toPrefixString(), attrs);
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process start mlvalue", e);
    }
  }
  
  public void endValueMLText(NodeRef nodeRef)
  {
    try
    {
      this.contentHandler.endElement("view", "mlvalue", MLVALUE_QNAME.toPrefixString());
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end mltext", e);
    }
  }
  
  public void warning(String warning) {}
  
  public void end()
  {
    try
    {
      this.contentHandler.endElement("view", "view", VIEW_QNAME.toPrefixString());
      this.contentHandler.endPrefixMapping("view");
      this.contentHandler.endDocument();
    }
    catch (SAXException e)
    {
      throw new ExporterException("Failed to process end export event", e);
    }
  }
  
  private String toPrefixString(QName qname)
  {
    return qname.toPrefixString(this.namespaceService);
  }
  
  private Path createPath(NodeRef rootRef, NodeRef fromRef, NodeRef toRef)
  {
    if (!this.nodeService.exists(toRef)) {
      return null;
    }
    if (toRef.equals(this.nodeService.getRootNode(toRef.getStoreRef()))) {
      return this.nodeService.getPath(toRef);
    }
    Path rootPath = createIndexedPath(rootRef, this.nodeService.getPath(rootRef));
    Path fromPath = createIndexedPath(fromRef, this.nodeService.getPath(fromRef));
    Path toPath = createIndexedPath(toRef, this.nodeService.getPath(toRef));
    Path relativePath = null;
    try
    {
      for (int i = 0; i < toPath.size(); i++)
      {
        Path.Element pathElement = toPath.get(i);
        if (pathElement.getPrefixedString(this.namespaceService).equals("cm:categoryRoot"))
        {
          Path.ChildAssocElement childPath = (Path.ChildAssocElement)pathElement;
          relativePath = new Path();
          relativePath.append(new Path.ChildAssocElement(new ChildAssociationRef(null, null, null, childPath.getRef().getParentRef())));
          relativePath.append(toPath.subPath(i + 1, toPath.size() - 1));
          break;
        }
      }
      if (relativePath == null)
      {
        int i = 0;
        while ((i < rootPath.size()) && (i < fromPath.size()) && (rootPath.get(i).equals(fromPath.get(i)))) {
          i++;
        }
        if (i == rootPath.size()) {
          for (NodeRef nodeRef : this.context.getExportParentList())
          {
            int j = 0;
            Path tryPath = createIndexedPath(nodeRef, this.nodeService.getPath(nodeRef));
            while ((j < tryPath.size()) && (j < toPath.size()) && (tryPath.get(j).equals(toPath.get(j)))) {
              j++;
            }
            if (j == tryPath.size())
            {
              relativePath = new Path();
              for (int p = 0; p < fromPath.size() - i; p++) {
                relativePath.append(new Path.ParentElement());
              }
              if (j >= toPath.size()) {
                break;
              }
              relativePath.append(toPath.subPath(j, toPath.size() - 1)); break;
            }
          }
        }
      }
      if (relativePath == null) {
        relativePath = toPath;
      }
    }
    catch (Throwable e)
    {
      String msg = "Failed to determine relative path: root path=" + rootPath + "; from path=" + fromPath + "; to path=" + toPath;
      throw new ExporterException(msg, e);
    }
    return relativePath;
  }
  
  private Path createIndexedPath(NodeRef nodeRef, Path path)
  {
    int index;
    ChildAssociationRef childAssoc;
    NodeRef childRef;
    for (int i = path.size() - 1; i >= 0; i--)
    {
      Path.Element pathElement = path.get(i);
      if ((i > 0) && ((pathElement instanceof Path.ChildAssocElement)))
      {
        index = 1;
        String searchPath = path.subPath(i).toPrefixString(this.namespaceService);
        List<NodeRef> siblings = this.searchService.selectNodes(nodeRef, searchPath, null, this.namespaceService, false);
        if (siblings.size() > 1)
        {
          childAssoc = ((Path.ChildAssocElement)pathElement).getRef();
          childRef = childAssoc.getChildRef();
          for (NodeRef sibling : siblings)
          {
            if (sibling.equals(childRef))
            {
              childAssoc.setNthSibling(index);
              break;
            }
            index++;
          }
        }
      }
    }
    return path;
  }
}
