package tz.co.nezatech.dev.nezahttp;

import java.io.File;

public class HttpFilePart extends HttpPart {
	String fileName;

	public HttpFilePart(String name, String contentType, Object data, String fileName) {
		super(name, contentType, data);
		this.fileName = fileName;
	}

	@Override
	public void calculateLength() {
		setContentLength(((int) ((File) getData()).length()));
	}

	public String getFileName() {
		return fileName;
	}
}
