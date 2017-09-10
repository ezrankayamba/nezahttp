package tz.co.nezatech.dev.nezahttp;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HttpClient {
	private int port;
	private String host;
	boolean autoflush = true;
	private String path = "/";
	private String httpVersion = "HTTP/1.1";
	private Socket socket;
	private String basicAuth = "Authorization: Basic dGVzdC5hcGk6MTIzNDU2";
	String boundary = Long.toHexString(System.currentTimeMillis());
	private final String CRLF = "\r\n";
	String charset = "UTF-8";
	final int MULTIPART_EXTA_LEN = 0;
	private InputStream is;
	private HttpPostProgressListener postProgressListener;

	public HttpClient(String url) throws MalformedURLException {
		URL theUrl = new URL(url);
		host = theUrl.getHost();
		path = theUrl.getPath();
		port = theUrl.getPort();
		if (port < 1) {
			port = theUrl.getDefaultPort();
		}
	}

	public HttpClient(String host, int port) {
		super();
		this.port = port;
		this.host = host;
	}

	public HttpClient(String host, int port, String path) {
		super();
		this.port = port;
		this.host = host;
		this.path = path;
	}

	public void connect() throws UnknownHostException, IOException {
		if (socket == null || socket.isClosed()) {
			socket = new Socket(host, port);
		}
	}

	public void close() throws UnknownHostException, IOException {
		if (socket != null) {
			socket.close();
		}

	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	public Response get() throws IOException {
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		wr.write("GET " + path + " " + httpVersion + CRLF);
		wr.write("Host: " + this.host + ":" + this.port + CRLF);
		wr.write(basicAuth + CRLF);
		wr.write("Connection: close" + CRLF);
		wr.write(CRLF);
		wr.flush();

		return response();
	}

	public Response post(String data, String contentType) throws IOException {
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		wr.write("POST " + path + " " + httpVersion + CRLF);
		wr.write("Host: " + this.host + ":" + this.port + CRLF);
		wr.write(basicAuth + CRLF);
		wr.write("Connection: close" + CRLF);
		wr.write("Content-Length: " + data.length() + CRLF);
		wr.write("Content-Type: " + contentType + CRLF);
		wr.write(CRLF);
		wr.write(data);
		wr.flush();

		return response();
	}

	public Response postParts(List<HttpPart> parts) throws IOException {
		String twoHyphens = "--";
		int maxBufferSize = 1 * 1024;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;

		int len = contentLengthMultiPart(parts, twoHyphens);

		OutputStream os = socket.getOutputStream();

		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
		wr.append("POST " + path + " " + httpVersion + CRLF);
		wr.append("Host: " + this.host + ":" + this.port + CRLF);
		wr.append(basicAuth + CRLF);
		wr.append("Connection: close" + CRLF);
		wr.append("Content-Length: " + (len + MULTIPART_EXTA_LEN) + CRLF);
		wr.append("Content-Type: multipart/form-data; boundary=" + boundary + CRLF);
		wr.append(CRLF);

		int progress = 0;
		if (postProgressListener != null) {// start progress
			postProgressListener.progressChanged(0, 0, len);
		}

		for (Iterator<HttpPart> iterator = parts.iterator(); iterator.hasNext();) {
			HttpPart part = (HttpPart) iterator.next();
			Object data = part.getData();

			int hdLen = contentLengthMultiPart(part, twoHyphens);

			wr.append(twoHyphens + boundary + CRLF);
			if (part instanceof HttpFilePart) {
				HttpFilePart filePart = (HttpFilePart) part;
				wr.append("Content-Disposition: form-data; name=\"" + part.getName() + "\"; filename=\""
						+ filePart.getFileName() + "\"" + CRLF);
				wr.append("Content-Type: " + part.getContentType() + CRLF);
				wr.append("Content-Length: " + part.getContentLength() + CRLF);
				wr.append("Content-Transfer-Encoding: binary" + CRLF);
				wr.append(CRLF);
				wr.flush();

				progress += hdLen;
				if (postProgressListener != null) {// start progress
					postProgressListener.progressChanged(hdLen, progress, len);
				}

				DataOutputStream dos = new DataOutputStream(os);
				File f = (File) data;

				is = new FileInputStream(f);
				bytesAvailable = is.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				bytesRead = is.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);

					progress += (bytesRead);
					if (postProgressListener != null) {// start progress
						postProgressListener.progressChanged(bufferSize, progress, len);
					}

					bytesAvailable = is.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = is.read(buffer, 0, bufferSize);
				}
				dos.flush();
				wr.append(CRLF);
				wr.flush();
			} else {
				wr.append("Content-Disposition: form-data; name=\"" + part.getName() + "\"" + CRLF);
				wr.append("Content-Type: " + part.getContentType() + "; charset=" + charset + CRLF);
				wr.append("Content-Length: " + part.getContentLength() + CRLF);
				wr.append(CRLF);
				wr.append(part.getData().toString());
				wr.append(CRLF);
				wr.flush();

				progress += hdLen + part.getContentLength();
				if (postProgressListener != null) {// start progress
					postProgressListener.progressChanged(hdLen + part.getContentLength(), progress, len);
				}
			}
		}
		wr.append(twoHyphens + boundary + twoHyphens);
		wr.flush();

		int end = contentLengthMultiPart(twoHyphens);
		progress += end;
		if (postProgressListener != null) {// start progress
			postProgressListener.progressChanged(end, progress, len);
			postProgressListener.postCompleted(len);
		}

		return response();
	}

	public boolean isBetween(int x, int lower, int upper) {
		return lower <= x && x <= upper;
	}

	private int contentLengthMultiPart(List<HttpPart> parts, String twoHyphens) {
		int len = 0;
		for (Iterator<HttpPart> iterator = parts.iterator(); iterator.hasNext();) {
			HttpPart part = (HttpPart) iterator.next();
			StringBuilder sb = new StringBuilder();

			sb.append(twoHyphens + boundary + CRLF);
			if (part instanceof HttpFilePart) {
				HttpFilePart filePart = (HttpFilePart) part;
				sb.append("Content-Disposition: form-data; name=\"" + part.getName() + "\"; filename=\""
						+ filePart.getFileName() + "\"" + CRLF);
				sb.append("Content-Type: " + part.getContentType() + CRLF);
				sb.append("Content-Length: " + part.getContentLength() + CRLF);
				sb.append("Content-Transfer-Encoding: binary" + CRLF);
				sb.append(CRLF);

				// data

				sb.append(CRLF);
			} else {
				sb.append("Content-Disposition: form-data; name=\"" + part.getName() + "\"" + CRLF);
				sb.append("Content-Type: " + part.getContentType() + "; charset=" + charset + CRLF);
				sb.append("Content-Length: " + part.getContentLength() + CRLF);
				sb.append(CRLF);

				// data

				sb.append(CRLF);
			}

			len += sb.toString().length() + part.getContentLength();
		}
		String end = twoHyphens + boundary + twoHyphens;
		len += end.length();

		return len;
	}

	private int contentLengthMultiPart(HttpPart part, String twoHyphens) {
		int len = 0;

		StringBuilder sb = new StringBuilder();

		sb.append(twoHyphens + boundary + CRLF);
		if (part instanceof HttpFilePart) {
			HttpFilePart filePart = (HttpFilePart) part;
			sb.append("Content-Disposition: form-data; name=\"" + part.getName() + "\"; filename=\""
					+ filePart.getFileName() + "\"" + CRLF);
			sb.append("Content-Type: " + part.getContentType() + CRLF);
			sb.append("Content-Length: " + part.getContentLength() + CRLF);
			sb.append("Content-Transfer-Encoding: binary" + CRLF);
			sb.append(CRLF);

			// data

			sb.append(CRLF);
		} else {
			sb.append("Content-Disposition: form-data; name=\"" + part.getName() + "\"" + CRLF);
			sb.append("Content-Type: " + part.getContentType() + "; charset=" + charset + CRLF);
			sb.append("Content-Length: " + part.getContentLength() + CRLF);
			sb.append(CRLF);

			// data

			sb.append(CRLF);
		}

		len += sb.toString().length();

		return len;
	}

	private int contentLengthMultiPart(String twoHyphens) {
		int len = 0;
		String end = twoHyphens + boundary + twoHyphens;
		len += end.length();

		return len;
	}

	private Response response() throws IOException {
		StringBuilder sb = new StringBuilder();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
		byte[] contents = new byte[1024];

		int bytesRead = 0;
		while ((bytesRead = in.read(contents)) != -1) {
			sb.append(new String(contents, 0, bytesRead));
			baos.write(contents, 0, bytesRead);
			baos.flush();
		}

		Response res = new Response(sb.toString());
		String len = res.getHeaders().get("Content-Length");

		byte[] bytes = baos.toByteArray();
		int to = bytes.length;
		int from = to - Integer.parseInt(len.trim());
		int size = to - from;
		System.out.println(String.format("Len: %s, From: %d, To: %d, Size: %d\n", len, from, to, size));
		res.setBytes(Arrays.copyOfRange(bytes, from, to));
		return res;
	}

	public HttpPostProgressListener getPostProgressListener() {
		return postProgressListener;
	}

	public void setPostProgressListener(HttpPostProgressListener postProgressListener) {
		this.postProgressListener = postProgressListener;
	}

	public void setBasicAuth(String basicAuth) {
		this.basicAuth = basicAuth;
	}

}
