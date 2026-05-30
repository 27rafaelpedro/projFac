package leiphotos.domain.core;

import java.io.File;
/* 
 * This class is a Mock for type File, for testing purposes.
 * It allow us to write unit tests for Photo without depending
 * on the existence of actual files in the file system. 
 * Since files are external resources, they are not under our 
 * control and might not be available when we run the tests.
 * 
 * Note that only properties of the file that are relevant for 
 * testing the Photo class were considered in this Mock. 
 */

public class MockFile extends File{

	private static final long serialVersionUID = 1L;
	private long size;

	public MockFile(String pathname, long size) {
		super(pathname);
		this.size = size;
	}

	@Override
	public long length() {
		return size;
	}
}
