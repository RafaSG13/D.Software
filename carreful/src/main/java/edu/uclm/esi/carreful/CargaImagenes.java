package edu.uclm.esi.carreful;

import java.io.FileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CargaImagenes {

	public static int getImagenes(String nombreProducto) throws Exception {
		String outputFolder = System.getProperty("java.io.tmpdir");
		if (!outputFolder.endsWith("/"))
			outputFolder+="/";
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://www.google.com/search?q=" + nombreProducto + "&tbm=isch");
		CloseableHttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		String html = EntityUtils.toString(entity);
		
		client.close();
		
		Document doc = Jsoup.parse(html);
		Elements imgs = doc.getElementsByClass("t0fcAb");
		for (int i=0; i<imgs.size(); i++) {
			Element img = imgs.get(i);
			String src = img.attr("src");
			System.out.println(src);
			descargaFoto(nombreProducto, i, src);
		}
		return imgs.size();
	}

	private static void descargaFoto(String nombre, int index, String url) throws Exception {
		String outputFolder = System.getProperty("java.io.tmpdir");
		if (!outputFolder.endsWith("/"))
			outputFolder+="/";
		
		CloseableHttpClient client = HttpClients.createDefault();
		System.out.println("Bajando foto " + index);
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		byte[] image = EntityUtils.toByteArray(entity);
		try(FileOutputStream fos = new  FileOutputStream(outputFolder + nombre + index + ".jpeg")) {
			fos.write(image);
		}
		client.close();
	}
}
