package edu.uclm.esi.carreful.serializables;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class lanzadoraSreializable {

	public static void viejomain(String[] args) throws Exception {
		Persona pepe = new Persona("5.000",23);
		Persona ana= new Persona("1.000",19);
		
		FileOutputStream fos = new FileOutputStream("E:/pepe.txt");
		ObjectOutputStream oos=new ObjectOutputStream(fos);
		oos.writeObject(pepe);
		fos.close();
		
		fos = new FileOutputStream("E:/ana.txt");
		oos=new ObjectOutputStream(fos);
		oos.writeObject(ana);
		fos.close();
	}
	
	public static void main(String[] args) throws Exception {
		
		FileInputStream fis = new FileInputStream("E:/pepe.txt");
		ObjectInputStream ois=new ObjectInputStream(fis);
		Persona pepe = (Persona) ois.readObject();
		fis.close();
		pepe.print();
		
	
	}

}
