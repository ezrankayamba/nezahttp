package tz.co.nezatech.dev.nezahttp;

import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
	private int statusCode;
	private String raw, statusLine, body;
	private Map<String, String> headers = new LinkedHashMap<String, String>();

	public Response(String raw) {
		super();
		this.raw = raw;
		build();
	}

	private void build() {
		String[] lines = this.raw.split("\n");
		this.statusLine = lines[0];
		this.statusCode = Integer.parseInt(this.statusLine.split(" ")[1]);

		int bodyIndex = -1;

		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			if (line.isEmpty()) {
				bodyIndex = i + 2;
				break;
			}
			String[] hkv = line.split(": ");
			headers.put(hkv[0], hkv[1]);
		}

		if (bodyIndex != -1 && bodyIndex < lines.length) {
			body = lines[bodyIndex];
		}
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
