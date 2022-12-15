package util;
import java.awt.Color;

public class Material {
	public double difusion,reflect;    //rozpraszanie światła przy odbiciu; ilość światła oddawana po odbiciu
	public Color d_color;    //kolor wyświetlnia
	public String name;    //nazwa warstwy skojażonej z kolorem
	public boolean isMesuring=false;
	  
	public  Material(String name,double difusion, double reflect, Color col)
	  {
	    this.name=name;
	    this.difusion=difusion;
	    this.reflect=reflect;
	    this.d_color=col;
	  }
	public Material(Color col)
	{
		this.d_color=col;
	}
}
