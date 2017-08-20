package tz.co.nezatech.dev.nezahttp;

public class HttpPart {
	private String name, contentType;
	private Object data;
	private int contentLength;

	public HttpPart(String name, String contentType, Object data) {
		super();
		this.name = name;
		this.contentType = contentType;
		this.data = data;
		calculateLength();
	}

	public void calculateLength() {
		if (this.data instanceof String) {
			this.contentLength = ((String) data).getBytes().length;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

}
