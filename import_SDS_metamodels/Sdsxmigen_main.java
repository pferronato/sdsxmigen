package import_SDS_metamodels;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author p00371475
 *
 *         ****************************************************************************
 *         Transforms SDS metaschema XSD in XMI v2.1
 *         ****************************************************************************
 * 
 *         The XMI can be imported in Enterprise Architect, where you are free
 *         to generate class diagrams as you need.
 * 
 *         I assumed the following mappings: SDS – UML
 *         ----------------------------------------------- Name – Class Name
 *         isAbstract – isAbstract inhertisFrom – Generalization module –
 *         Package containment fields - Properties foreignKey - Dependency
 * 
 *         I could not figure out how to map namespace and containedIn in UML.
 *         It seems Module+Namespace makes up the file system path where files
 *         are.
 * 
 *         It generates a text file with some of the information
 *
 *****************************************************************************
 */
class Sdsxmigen_main {
	private static final String XMLNS_XMI = "xmlns:xmi";
	private static final String XMLNS_UML = "xmlns:uml";
	private static final String HTTP_SCHEMA_OMG_ORG_SPEC_XMI_2_1 = "http://schema.omg.org/spec/XMI/2.1";
	private static final String HTTP_WWW_OMG_ORG_SPEC_UML_20110701 = "http://www.omg.org/spec/UML/20110701";
	private final static String NS_PREFIX = "xs:";
	private final static String NSXMI_PREFIX = "xmi:";
	private final static String XMI_FILENAME_OUTPUT = "SDSSchema";
	private final static String TXT_FILENAME_OUTPUT = "listOfClasses";

	/**
	 * write a text file of the classes with details
	 * @param listOfClasses
	 * @param fileName
	 */
	static void getReport(ArrayList<MetaModelClass> listOfClasses, String fileName) {
		BufferedWriter writer = null;
		try {
			// create a temporary file
			String CR = System.getProperty("line.separator");
			File logFile = new File(fileName);

			// This will output the full path where the file will be written
			// to...
			System.out.println(logFile.getCanonicalPath());

			writer = new BufferedWriter(new FileWriter(logFile));

			for (MetaModelClass mmiter : listOfClasses) {
				writer.write(mmiter.getFileName() + CR);
				// get MetaModelClass attributes
				for (Map.Entry<String, String> iter : mmiter.getMmClsAttrib().entrySet()) {
					writer.write("\t" + iter.getKey() + ": " + iter.getValue() + CR);
				}

				// get fields
				if (!mmiter.getMmClsFields().isEmpty()) {
					writer.write("\tFields: " + CR);
					for (String iter : mmiter.getMmClsFields())
						writer.write("\t\t" + iter + CR);
				}

				// get relationships
				if (!mmiter.getMmRels().isEmpty()) {
					writer.write("\tRelationships: " + CR);
					for (MetaModelRel interRel : mmiter.getMmRels()) {
						writer.write("\t\t" + interRel.getName()+CR);
						writer.write("\t\t\tFrom: " + interRel.getFrom() + " - " 
								+ "Card: " + interRel.getCardinality() + " - " 
								+ "To: " + interRel.getTo() + " - "
								+ "ForeignKey: " + interRel.getForeignKey() + CR);
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @param pList
	 * @return
	 * @throws ParserConfigurationException
	 */
	static Document getXMI(ArrayList<MetaModelClass> pList) throws ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();

		Element schemaRoot = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, NSXMI_PREFIX + "XMI");
		schemaRoot.setAttribute("xmi:version", "2.1");
		schemaRoot.setAttribute(XMLNS_UML, HTTP_WWW_OMG_ORG_SPEC_UML_20110701);
		schemaRoot.setAttribute(XMLNS_XMI, HTTP_SCHEMA_OMG_ORG_SPEC_XMI_2_1);
		doc.appendChild(schemaRoot);

		// set root model
		DOMElementMaker elMaker = new DOMElementMaker(NSXMI_PREFIX, doc);
		Element modelEle = elMaker.createXMIModel("SDS_MetaMdel");
		schemaRoot.appendChild(modelEle);

		// set root package, not required though
		Element packageEle = elMaker.createXMIPackage("SDS_MetaModel");
		modelEle.appendChild(packageEle);

		// append element classes to the DOM model
		Element classEle = null, moduleEle = null, relEle = null;
		// scan all the classes
		for (MetaModelClass mmiter : pList) {
			try {
				// if class has a name create the element
				if (mmiter != null && !mmiter.getClassName().equals("")) {
					// create XMI node of the class
					classEle = elMaker.createXMIClass(mmiter.getMmClsAttrib().get("name"),
							mmiter.getMmClsAttrib().get("isAbstract"), mmiter.getMmClsAttrib().get("inheritsFrom"),
							mmiter.getMmClsFields());
				}
				// get the module from the XMI map it as UML package
				moduleEle = doc.getElementById(mmiter.getMmClsAttrib().get("module"));
				// if there is not, create it
				if (moduleEle == null) {
					moduleEle = elMaker.createXMIPackage(mmiter.getMmClsAttrib().get("module"));
				}
				// append class to module
				moduleEle.appendChild(classEle);
				// append module to to level folder
				packageEle.appendChild(moduleEle);

				// create relationships

				for (MetaModelRel relIter : mmiter.getMmRels()) {
					relEle = elMaker.createRelationship(relIter.getName(), relIter.getFrom(), relIter.getTo(), relIter.getForeignKey(), relIter.getCardinality());
					packageEle.appendChild(relEle);
				}

			} catch (java.lang.NullPointerException e) {
				e.printStackTrace();
				// do nothing, proceed
				// an element is invalid
				// it's bad practice I know
			}
		}

		return doc;
	}

	static int subsRel(ArrayList<MetaModelClass> listOfClasses) {
		int retVal = 0;
		for (MetaModelClass iterClass : listOfClasses)
			try {
				{ // loop classes
					System.out.println(iterClass.getClassName());
					for (MetaModelRel iterRel : iterClass.getMmRels()) { // loop rel for a class
						for (MetaModelClass iterClassSec : listOfClasses) { // loop classes
							System.out.println(iterClassSec.getClassName());
							for (MetaModelRel iterSecRel : iterClassSec.getMmRels()) // loop rel for second classes loop {
								if (iterSecRel.getTo().equals(iterClass.getClassName()) &&
										iterRel.getTo().equals(iterClassSec.getClassName()) &&
										!iterClass.getClassName().equals(iterClassSec.getClassName())) { // exclude auto loop relationships
									// m:m relationship found
									iterClassSec.getMmRels().remove(iterSecRel);
									iterRel.setCardinality(MetaModelRel.CardMMTypes.CARDMM);
									retVal++;
									// delete 1
									// change the other to M:M
								}
							}
						}
					}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.err.println("null pointer exception");
			}
		
		return retVal;
	}

	/**
	 * @param listOfClasses
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static void getXMLData(ArrayList<MetaModelClass> listOfClasses)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		File inputFile = null;
		Document doc = null;
		XPath xPath = null;
		String expression = null;
		
		for (MetaModelClass mmiter : listOfClasses) {
			inputFile = new File(mmiter.getFileName());
			// parse the file, create a doc model
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			// get the model class element
			xPath = XPathFactory.newInstance().newXPath();
			expression = "/MetaModelClass";
			Element eElement = null;
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					eElement = (Element) nNode;
					// get all the attributes
					for (int j = 0; j < eElement.getAttributes().getLength(); j++) {
						// *** add attributes
						mmiter.getMmClsAttrib().put(eElement.getAttributes().item(j).getNodeName(),
								eElement.getAttributes().item(j).getNodeValue());
					}
					// *** add id and name
					mmiter.setClassName(eElement.getAttribute("name"));
					mmiter.setXmlID("xmi:id");
				}
			}
			// add relationships and fields
			expression = "/MetaModelClass/fields/field";
			eElement = null;
			nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					eElement = (Element) nNode;
					// get all the attributes for field
					// scan all the attributes
					String name = null, foreignKey = null;
					// if it's a relationship manage it separately
					for (int j = 0; j < eElement.getAttributes().getLength(); j++) {
						if (eElement.getAttributes().item(j).getNodeName().equals("foreignKey")) {
							foreignKey = eElement.getAttributes().item(j).getNodeValue();
						}
						if (eElement.getAttributes().item(j).getNodeName().equals("name")) {
							name = eElement.getAttributes().item(j).getNodeValue();
						}
					}
					if (foreignKey != null) {
						// get field name and class name from the linked class
						// name
						String key = foreignKey.substring(foreignKey.lastIndexOf(".") + 1, foreignKey.length());
						String to = foreignKey.substring(0, foreignKey.lastIndexOf("."));
						// *** ADD RELATIONSHIP
						mmiter.getMmRels().add(new MetaModelRel(name, mmiter.getClassName(), to, key,
								MetaModelRel.CardMMTypes.CARDM1));
					}
					// if it is not a relationship, get the field name
					else {
						// *** ADD FIELD
						mmiter.getMmClsFields().add(name);
					}
				}
			}
		}
		// remove duplicate classes, it can happen
		int dupCount=0;
		List<MetaModelClass> dup = new ArrayList<MetaModelClass>();
		for (MetaModelClass iterClass : listOfClasses) {
			for (MetaModelClass iterClassSec : listOfClasses) {
				if (iterClass.getClassName().equals(iterClassSec.getClassName()) &&
						!iterClass.getFileName().equals(iterClassSec.getFileName())) {
				dup.add(iterClassSec);	// any of the two is deleted, it does not make a difference
				dupCount++;
				}
			}
		}
		dup.clear();
		System.out.println(dupCount);
	}

	/**
	 * @param args
	 *            arg[0] path from where to scan arg[1] -name arg[2] pattern of
	 *            files to filter in
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 */
	public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {

		if (args.length < 3 || !args[1].equals("-name")) {
			System.err.println("SDS Metamodel <path>" + " -name \"<glob_pattern>\"");
			System.exit(-1);
		}

		Path startingDir = Paths.get(args[0]);
		String pattern = args[2];

		System.out.println("Project: " + startingDir);
		System.out.println("------------------------");
		String projectName = startingDir.getParent().getParent().toString();
		projectName = projectName.substring(projectName.lastIndexOf("\\") + 1);
/*
		SDSmetamodel model = new SDSmetamodel();
		model.getXML( file name );
		model.getReport();
		model.getXMI();

*/		// ============================================
		// get list of XML files info from the file system
		// ============================================
		FileParser finder = new FileParser();
		finder.setPattern(pattern);
		Files.walkFileTree(startingDir, finder);
		ArrayList<MetaModelClass> listOfClasses = finder.getClasses();

		// ============================================
		// getting properties from SDS XML files
		// ============================================
		getXMLData(listOfClasses);

		// ==============================
		// get report	
		// ==============================
		getReport(listOfClasses, projectName + TXT_FILENAME_OUTPUT + "before mm.txt");

		
		// remove double 1:m relationship with one single m:m
		int howm = subsRel (listOfClasses);
		System.out.println("m:m: "+howm);

		// ==============================
		// get report	
		// ==============================
		getReport(listOfClasses, projectName + TXT_FILENAME_OUTPUT + ".txt");

		// ===============================
		// get XSD file
		// ===============================
		{
		}

		// ===================================
		// get XMI file
		// ===================================
		try {
			// create XMI
			Document doc = getXMI(listOfClasses);

			// save XMI to file
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(doc);
			// create XMI a file
			transformer.transform(domSource, new StreamResult(new File(projectName + XMI_FILENAME_OUTPUT + ".xmi")));
			// dump file to console
			transformer.transform(domSource, new StreamResult(System.out));
		} catch (FactoryConfigurationError | ParserConfigurationException | TransformerException e) {
			// handle exception
			e.printStackTrace();
		}

	}

}
