package serialization;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ColorAdapter extends TypeAdapter<Color> {
    public Color read(JsonReader reader) throws IOException {
      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }
		
		  String colorInfo = reader.nextString(); 
		  String[] parts = colorInfo.split(","); 
		  int rgb = Integer.parseInt(parts[0]);
		  int alpha = Integer.parseInt(parts[1]);
		  Color col = new Color(rgb);
		  return new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
		 
    }
    public void write(JsonWriter writer, Color value) throws IOException {
      if (value == null) {
        writer.nullValue();
        return;
      }
      String colorInfo = value.getRGB() + "," + value.getAlpha();
      writer.value(colorInfo);
    }
  }