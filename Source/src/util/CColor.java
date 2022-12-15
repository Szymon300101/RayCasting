package util;
import java.awt.Color;

public class CColor {
	public static Color gray(int bri)
	{
		return new Color(bri,bri,bri);
	}
	public static Color gray(int bri,int alpha)
	{
		return new Color(bri,bri,bri,alpha);
	}

	public static Color RGB(int r,int g,int b)
	{
		return new Color(r,g,b);
	}
	public static Color RGB(int r,int g,int b,int alpha)
	{
		return new Color(r,g,b,alpha);
	}
}
 