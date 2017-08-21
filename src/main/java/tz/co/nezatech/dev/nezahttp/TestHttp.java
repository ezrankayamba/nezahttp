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
			//testGet();
			//testPost();
			testPostParts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testGet() throws UnknownHostException, IOException {
		HttpClient client = new HttpClient("http://371309a9.ngrok.io/survey/test");
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
		HttpClient client = new HttpClient("http://371309a9.ngrok.io/survey/form");
		client.setPostProgressListener(new HttpPostProgressListener() {
			
			@Override
			public void progressChanged(int bytesAdded, int currentProgress, int totalSize) {
				System.out.println(String.format("%s - %d | %d -> %d", getCurrentTimeStamp(), totalSize,bytesAdded, currentProgress));
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
		parts.add(new HttpPart("name2", "text/plain", "Test Form2"));
		File binaryFile2 = new File("/home/nkayamba/Desktop/test.jpg");
		parts.add(new HttpFilePart("testfile2", "image/jpeg", binaryFile2, "test2.jpg"));
		File binaryFile3 = new File("/home/nkayamba/Desktop/test3.png");
		parts.add(new HttpFilePart("testfile33", "image/png", binaryFile3, "test3.png"));
		parts.add(new HttpPart("form_name", "text/plain", 21 + "gggggdggg"));
		parts.add(new HttpPart("form_id", "text/plain", 1 + ""));
		parts.add(new HttpPart("name2a", "text/plain", "Test Form2 AlA"));
		parts.add(new HttpPart("form_id", "text/plain", 1 + ""));
		parts.add(new HttpPart("form_name", "text/plain", 21 + "ssswsssssd"));
		parts.add(new HttpPart("repository_id", "text/plain", 1 + "ssssssssssssssssss"));
		parts.add(new HttpPart("repository_id2", "text/plain", 1 + ""));
		parts.add(new HttpPart("repository_id3", "text/plain", 1 + "wwww"));
		parts.add(new HttpPart("repository_id4", "text/plain", 1 + "wwww"));
		parts.add(new HttpPart("repository_id", "text/plain", 1 + "ssssssssssssssssss"));
		parts.add(new HttpPart("repository_id5", "text/plain", 1 + "wwww"));
		parts.add(new HttpPart("repository_id6", "text/plain", 1 + "wwww"));
		parts.add(new HttpPart("repository_id7", "text/plain", 1 + "wwdddssww"));
		
		parts.add(new HttpFilePart("testfile4", "image/png", new File("/home/nkayamba/Desktop/test3.png"), "test3.png"));
		parts.add(new HttpFilePart("testfile5", "image/png", new File("/home/nkayamba/Desktop/test3.png"), "test3.png"));
		parts.add(new HttpFilePart("testfile6", "image/png", new File("/home/nkayamba/Desktop/test3.png"), "test3.png"));
		parts.add(new HttpFilePart("testfile7", "image/png", new File("/home/nkayamba/Desktop/test3.png"), "test3.png"));
		parts.add(new HttpFilePart("testfile8", "image/png", new File("/home/nkayamba/Desktop/test3.png"), "test3.png"));
		parts.add(new HttpFilePart("testfile9", "image/png", new File("/home/nkayamba/Desktop/test3.png"), "test3.png"));
		return parts;
	}

	private static void println(String text) {
		System.out.println(String.format("%s - %s", getCurrentTimeStamp(), text));
	}
}
