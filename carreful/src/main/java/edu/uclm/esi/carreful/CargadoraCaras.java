package edu.uclm.esi.carreful;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.FileOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CargadoraCaras {

	public static void main(String[] args) throws Exception {

		String outputFolder = System.getProperty("java.io.tmpdir");
		System.out.println(outputFolder);
		if (!outputFolder.endsWith("/"))
			outputFolder += "/";
		CloseableHttpClient client = HttpClients.createDefault();

		for (int i = 1; i <= 100; i++) {
			System.out.println("Bajando foto " + i + "/" + 100);
			HttpGet get = new HttpGet("https://thispersondoesnotexist.com/image");
			CloseableHttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			byte[] image = EntityUtils.toByteArray(entity);
			try (FileOutputStream fos = new FileOutputStream(outputFolder + "cara" + i + ".jpeg")) {
				fos.write(image);
			}

		}

		client.close();

	}

}