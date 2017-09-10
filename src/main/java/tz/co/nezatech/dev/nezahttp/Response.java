package tz.co.nezatech.dev.nezahttp;

import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
	private int statusCode;
	private String raw, statusLine, body;
	private Map<String, String> headers = new LinkedHashMap<String, String>();
	private byte[] bytes;

	public Response(String raw) {
		super();
		this.raw = raw;
		build();
	}

	private void build() {
		String[] parts = this.raw.split("\r\n\r\n");
		String[] lines = parts[0].split("\n");
		this.statusLine = lines[0];

		this.statusCode = Integer.parseInt(this.statusLine.split(" ")[1]);

		for (int i = 1; i < lines.length; i++) {
			String[] hkv = lines[i].split(": ");
			headers.put(hkv[0], hkv[1]);
		}
		this.body = parts[1];
		try {
			String cd = getHeaders().get("Content-Disposition");
			if (cd != null && cd.contains("attachment")) {
				String fileInfo[] = cd.split(";")[1].split("=");
				headers.put("Filename", fileInfo[1]);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public String getBody() {
		return body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

}
