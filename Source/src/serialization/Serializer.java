package serialization;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import light.Source;
import light.SunSource;
import shape.Shape;
import window.AppFrame;

public class Serializer {
	
	public static void SaveFrame(AppFrame frame)
	{
		FileDialog dialog = new FileDialog((Frame) frame, "Select File to Open");
	    dialog.setMode(FileDialog.SAVE);
	    dialog.setVisible(true);
	    dialog.setFilenameFilter((File dir, String name) -> name.endsWith(".json"));
	    if(dialog.getFile()==null) return;
	    String fileName = dialog.getDirectory() + dialog.getFile();
	    if(!fileName.endsWith(".json"))
	    	fileName+=".json";
	    frame.isMousePosValid = false;
	    
	    String json = Serialize(frame);
	    
	    PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		    writer.print(json);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void LoadFrame(AppFrame frame)
	{
		FileDialog dialog = new FileDialog((Frame) frame, "Select File to Open");
	    dialog.setFile("*.json");
	    dialog.setMode(FileDialog.LOAD);
	    dialog.setVisible(true);
	    if(dialog.getFile()==null) return;
	    String fileName = dialog.getDirectory() + dialog.getFile();
	    frame.isMousePosValid = false;
	    
	    String json="";
	    FileInputStream inputStream;
	    try {
	    	inputStream = new FileInputStream(fileName);
	    	json = IOUtils.toString(inputStream);
	        inputStream.close();
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    AppStateModel model = Deserialize(json);
	    
	    model.InsertToFrame(frame);
	    
	    frame.fileOpen = true;
	     
//	    mFrame.walls.add((Shape)new Arc(2140,1800,70,350,120,new Material("",1,1,CColor.gray(255))));
	    
	    frame.menu.bar.remove(1);
	    frame.menu.bar.remove(0);
	    frame.repaint();
	    frame.menu.bar.add(frame.menu.newButton("SETTINGS","openSettings","",Color.cyan, frame.menu.settingsDialog));
	    frame.menu.bar.add(frame.menu.newButton("SAVE","SAVE","",Color.cyan, frame.menu));
		
	    frame.menu.mesurementReadout=new JButton("");
	    frame.menu.mesurementReadout.setBackground(Color.white);
	    frame.menu.mesurementReadout.setEnabled(false);
	    frame.menu.bar.add(frame.menu.mesurementReadout);
	}
	
	static String Serialize(AppFrame frame)
	{
		AppStateModel model = new AppStateModel(frame);
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Color.class, new ColorAdapter());
		Gson gson = builder.create();
		String json = gson.toJson(model);
		
		return json;
	}
	
	static AppStateModel Deserialize(String json)
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Color.class, new ColorAdapter());
		Gson gson = builder.create();
		AppStateModel recoveredModel=gson.fromJson(json, AppStateModel.class);
		
		return recoveredModel;
	}
	
	
}
