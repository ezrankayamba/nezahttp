package tz.co.nezatech.dev.nezahttp;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class TestHttp {

	public static void main(String[] args) {
		try {
			testGet();
			testPost();
			testPostParts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testGet() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://localhost:9090/survey/test");
		client.connect();
		Response response = client.get();// working
		println(response.getBody());
	}
	private static void testPost() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://localhost:9090/survey/test");
		client.connect();
		Response response = client.post("Data123,Data123", "text/plain");
		println(response.getBody());
	}
	private static void testPostParts() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://localhost:9090/survey/form");
		client.connect();
		Response response = client.postParts(dummyParts());
		println(response.getBody());
	}

	private static List<HttpPart> dummyParts() {
		List<HttpPart> parts = new LinkedList<HttpPart>();
		parts.add(new HttpPart("name1", "text/plain", "Test Form1"));
		File binaryFile = new File("/home/nkayamba/Desktop/test.jpg");
		parts.add(new HttpFilePart("testfile1", "image/jpeg", binaryFile, "test.jpg"));
		parts.add(new HttpPart("name2", "text/plain", "Test Form2"));
		File binaryFile2 = new File("/home/nkayamba/Desktop/test.jpg");
		parts.add(new HttpFilePart("testfile2", "image/jpeg", binaryFile2, "test2.jpg"));
		File binaryFile3 = new File("/home/nkayamba/Desktop/test3.png");
		parts.add(new HttpFilePart("testfile33", "image/png", binaryFile3, "test3.png"));
		parts.add(new HttpPart("name2a", "text/plain", "Test Form2 AlA"));
		return parts;
	}

	private static void println(String text) {
		System.out.println(text);
	}
}
