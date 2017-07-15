package import_SDS_metamodels;

// TODO feature 1
// improved

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author p00371475
 *
 */
public class MetaModelClass {

	public MetaModelClass() {
		mmClsAttrib = new HashMap<String, String>();
		mmClsFields = new HashSet<String>();
		mmRels = new ArrayList<MetaModelRel>();
	}

	private String fileName;
	private String className;
	private String path;
	private String xmlID;
	private Map<String, String> mmClsAttrib;
	private Set<String> mmClsFields;

	private List<MetaModelRel> mmRels;

	public List<MetaModelRel> getMmRels() {
		return mmRels;
	}

	public void setMmRels(List<MetaModelRel> mmRels) {
		this.mmRels = mmRels;
	}

	public Set<String> getMmClsFields() {
		return mmClsFields;
	}

	public void setMmClsFields(Set<String> mmFields) {
		this.mmClsFields = mmFields;
	}

	public Map<String, String> getMmClsAttrib() {
		return mmClsAttrib;
	}

	public void setMmClsAttrib(Map<String, String> mmClassAttributes) {
		this.mmClsAttrib = mmClassAttributes;
	}

	public String getXmlID() {
		return xmlID;
	}

	public void setXmlID(String xmlID) {
		this.xmlID = xmlID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
