package import_SDS_metamodels;

import java.util.Set;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author p00371475
 *
 */
class DOMElementMaker {
	private static final String XMI_IDREF = "xmi:idref";
	private static final String XMI_VALUE = "xmi:value";
	private static final String ANY_ID = "12";
	private static final String UML_ASSOCIATION = "uml:Association";
	private String nsPrefix;	
	private Document doc;
	private final static String XMI_ID = "xmi:id";
	private final static String XMI_TYPE = "xmi:type";
	private final static String XMI_NAME = "xmi:name";
	private final static String UML_MODEL = "uml:Model";
	private final static String UML_CLASS = "uml:Class";
	private final static String UML_PACKAGE = "uml:Package";
	private final static String UML_GENERALIZATION = "uml:Generalization";
	private final static String UML_PROPERTY = "uml:Property";
	private final static String UML_DEPENDENCY = "uml:Dependency";
	
	// static String EA_REPOS = "D:\\workspace\\repos.eap";

	/**
	 * @param nsPrefix
	 * @param doc
	 */
	public DOMElementMaker(String nsPrefix, Document doc) {
		this.nsPrefix = nsPrefix;
		this.doc = doc;
	}

	/**
	 * @param elementName
	 * @param nameAttrVal
	 * @param typeAttrVal
	 * @return
	 */
	public Element createElement(String elementName, String nameAttrVal, String typeAttrVal) {
		Element element = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, nsPrefix + elementName);
		if (nameAttrVal != null)
			element.setAttribute("name", nameAttrVal);
		if (typeAttrVal != null)
			element.setAttribute("type", typeAttrVal);
		return element;
	}

	/**
	 * @param modelName
	 * @return
	 */
	public Element createXMIModel(String modelName) {
		Element element = doc.createElement(UML_MODEL);
		element.setAttribute(XMI_TYPE, UML_MODEL);
		element.setAttribute(XMI_NAME, modelName);
		return element;
	}

	/**
	 * @param packageName
	 * @return
	 */
	public Element createXMIPackage(String packageName) {
		Element element = doc.createElement("packagedElement");
		element.setAttribute(XMI_TYPE, UML_PACKAGE);
		element.setAttribute(XMI_ID, packageName);
		element.setAttribute("name", packageName);
		element.setIdAttribute(XMI_ID, true);
		return element;
	}

	public Element createXMIClass(String className, String isAbstract, String parentClassName) {
		return createXMIClass(className, isAbstract, parentClassName, null);
	}

	/**
	 * @param className
	 * @param isAbstract
	 * @param parentClassName
	 * @return
	 */
	public Element createXMIClass(String className, String isAbstract, String parentClassName, Set<String> fields) {
		Element element = doc.createElement("packagedElement");
		element.setAttribute(XMI_TYPE, UML_CLASS);
		element.setAttribute(XMI_ID, className);
		element.setAttribute("name", className);
		element.setIdAttribute(XMI_ID, true);
		element.setAttribute("isAbstract", isAbstract);
		if (parentClassName != null) {
			Element subElement = doc.createElement("generalization");
			subElement.setAttribute(XMI_TYPE, UML_GENERALIZATION);
			subElement.setAttribute(XMI_ID, className + parentClassName);
			subElement.setAttribute("general", parentClassName);
			subElement.setIdAttribute(XMI_ID, true);
			element.appendChild(subElement);
		}
		if (fields != null) {
			for (String iter : fields) {
				Element fieldEle = doc.createElement("ownedAttribute");
				fieldEle.setAttribute(XMI_TYPE, UML_PROPERTY);
				fieldEle.setAttribute(XMI_ID, className + "-" + iter);
				fieldEle.setAttribute("visibility", "public");
				fieldEle.setAttribute("name", iter);
				element.appendChild(fieldEle);
			}
		}
		return element;
	}

	/**
	 * @param relName
	 * @param clientID
	 * @param supplierID
	 * @return
	 */
	public Element createRelationship(String relName, String clientID, String supplierID, String key, MetaModelRel.CardMMTypes card) {
		Element packagedElement = doc.createElement("packagedElement");
		packagedElement.setAttribute(XMI_ID, clientID + "-" + supplierID);
		packagedElement.setAttribute(XMI_TYPE, UML_DEPENDENCY);
		packagedElement.setAttribute("name", relName + ":" + card);
		packagedElement.setAttribute("client", clientID);
		packagedElement.setAttribute("supplier", supplierID);
		return packagedElement;
	}

	public Element createRelationshipMM (String relName, String clientID, String supplierID, String key) {
		Element packagedElement = doc.createElement("packagedElement");
		packagedElement.setAttribute(XMI_ID, clientID + "-" + supplierID);
		packagedElement.setAttribute(XMI_TYPE, UML_ASSOCIATION);
		packagedElement.setAttribute("name", relName + ": MM");
		
			Element memberEnd = doc.createElement("memberEnd");
			memberEnd.setAttribute(XMI_IDREF, "memberEnd1");
			packagedElement.appendChild(memberEnd);
			
			Element ownedEnd = doc.createElement("ownedEnd");
			ownedEnd.setAttribute(XMI_ID, "memberEnd1");
			ownedEnd.setAttribute(XMI_TYPE, "uml:Property"); 
			ownedEnd.setAttribute("association", "memberEnd1"); // link with memberEnd ID
			packagedElement.appendChild(ownedEnd);
				Element ele =  doc.createElement("type"); 
				ele.setAttribute(XMI_IDREF, supplierID); // link to supplierID node
				ownedEnd.appendChild(ele);
				ele = doc.createElement("lowerValue");
				ele.setAttribute(XMI_ID, ANY_ID); 
				ele.setAttribute(XMI_TYPE, "uml:LiteralInteger");
				ele.setAttribute(XMI_VALUE, "0");
				ownedEnd.appendChild(ele);
				ele = doc.createElement("upperValue");
				ele.setAttribute(XMI_ID, ANY_ID);
				ele.setAttribute(XMI_TYPE, "uml:LiteralUnlimitedNatural");
				ele.setAttribute(XMI_VALUE, "*");
				ownedEnd.appendChild(ele);
			
			memberEnd = doc.createElement("memberEnd");
			memberEnd.setAttribute(XMI_IDREF, "memberEnd2");
			packagedElement.appendChild(memberEnd);
			
			ownedEnd = doc.createElement("ownedEnd");	
			ownedEnd.setAttribute(XMI_ID, "memberEnd2");
			ownedEnd.setAttribute(XMI_TYPE, "uml:Property"); 
			ownedEnd.setAttribute("association", "memberEnd2"); // link with memberEnd ID
			packagedElement.appendChild(ownedEnd);
				ele =  doc.createElement("type"); 
				ele.setAttribute(XMI_IDREF, clientID); // link to supplierID node
				ownedEnd.appendChild(ele);
				ele = doc.createElement("lowerValue");
				ele.setAttribute(XMI_ID, ANY_ID); 
				ele.setAttribute(XMI_TYPE, "uml:LiteralInteger");
				ele.setAttribute(XMI_VALUE, "0");
				ownedEnd.appendChild(ele);
				ele = doc.createElement("upperValue");
				ele.setAttribute(XMI_ID, ANY_ID);
				ele.setAttribute(XMI_TYPE, "uml:LiteralUnlimitedNatural");
				ele.setAttribute(XMI_VALUE, "*");
				ownedEnd.appendChild(ele);
		
		return packagedElement;
	}
	/**
	 * @param className
	 * @param isAbstract
	 * @return
	 */
	public Element createXMIClass(String className, String isAbstract) {
		return createXMIClass(className, isAbstract);
	}

	/**
	 * @param elementName
	 * @param nameAttrVal
	 * @return
	 */
	public Element createElement(String elementName, String nameAttrVal) {
		return createElement(elementName, nameAttrVal, null);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public Element createElement(String elementName) {
		return createElement(elementName, null, null);
	}

	/**
	 * @param elementName
	 * @param baseAttrVal
	 * @return
	 */
	public Element createElementBase(String elementName, String baseAttrVal) {
		Element element = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, nsPrefix + elementName);
		if (baseAttrVal != null)
			element.setAttribute("base", baseAttrVal);
		return element;
	}

}