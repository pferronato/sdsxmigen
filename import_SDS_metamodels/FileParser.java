package import_SDS_metamodels;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 * @author p00371475
 *
 */
public class FileParser extends SimpleFileVisitor<Path> {

	private PathMatcher matcher = null;
	private ArrayList<MetaModelClass> listOfClasses = null;;
	private String pattern=null;
	private final static String EXCLUDE_FILE = "Submodule.xml";

	public void setPattern(String pPattern) {
		pattern = pPattern;
	}
	
	public FileParser() {
		listOfClasses = new ArrayList<MetaModelClass>();
	}

	ArrayList<MetaModelClass> getClasses() {
		return listOfClasses;
	}

	/**
	 * @param pattern
	 */
	public PathMatcher GetSDSClasses(String pattern) {
		return matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	}

	// Compares the glob pattern against
	// the file or directory name.
	/**
	 * @param file
	 */
	void find(Path file) {
		Path name = file.getFileName();
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		if (name != null && matcher.matches(name) && !name.toString().equals(EXCLUDE_FILE)) {
			MetaModelClass tmp = new MetaModelClass();
			tmp.setFileName(file.toString());
			tmp.setPath(file.getFileName().toString());
			listOfClasses.add(tmp);
		}
	}

	// Invoke the pattern matching
	// method on each file.
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		find(file);
		return CONTINUE;
	}

	// Invoke the pattern matching
	// method on each directory.
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		find(dir);
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return CONTINUE;
	}


}