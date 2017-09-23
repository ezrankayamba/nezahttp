package tz.co.nezatech.dev.nezahttp;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestHttp {

	public static void main(String[] args) {
		try {
			// testGet();
			// testPost();
			testPostParts();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testGet() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://pincomtz.net:9090/survey/repos");
		client.setBasicAuth("Authorization: Basic dGVzdC5hcGk6cHdkQDEyMw==");
		client.connect();
		Response response = client.get();// working
		println(response.getStatusLine());
		println(response.getHeaders().toString());
		println(response.getBody());
		// println(new String(response.getBytes()));

	}

	public static void testPost() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://localhost:9090/survey/test");
		client.connect();
		Response response = client.post("Data123,Data123", "text/plain");
		println(response.getBody());
	}

	public static void testPostParts() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://pincomtz.net:9090/survey/form/1");
		client.setBasicAuth("Authorization: Basic dGVzdC5hcGk6cHdkQDEyMw==");
		client.setPostProgressListener(new HttpPostProgressListener() {

			@Override
			public void progressChanged(long bytesAdded, long currentProgress, long totalSize) {
				System.out.println(String.format("%s - %d | %d -> %d | Pct: %s", getCurrentTimeStamp(), totalSize,
						bytesAdded, currentProgress, String.format("%2.2f", (100.0 * currentProgress / totalSize)) + "%"));

			}

			@Override
			public void postCompleted(long totalSize) {
				println("Upload completed, total upload: " + totalSize);
			}
		});
		client.connect();
		Response response = client.postParts(dummyParts());
		println(response.getBody());
	}

	private static String getCurrentTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
	}

	private static List<HttpPart> dummyParts() {
		List<HttpPart> parts = new LinkedList<HttpPart>();

		parts.add(new HttpPart("name1", "text/plain", "Test Form1"));
		File binaryFile = new File("/home/nkayamba/Desktop/test.jpg");
		parts.add(new HttpFilePart("testfile1", "image/jpeg", binaryFile, "test.jpg"));

		File binaryFile2 = new File("/home/nkayamba/Desktop/test.mp4");
		parts.add(new HttpFilePart("testfile2", "image/jpeg", binaryFile2, "test.mp4"));

		return parts;
	}

	private static void println(String text) {
		System.out.println(String.format("%s - %s", getCurrentTimeStamp(), text));
	}
}
