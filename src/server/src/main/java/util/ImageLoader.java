package util;

import java.util.List;
import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.EdibleItemImage;



public class ImageLoader {

	public static void loadImages(List<? extends EdibleItem> items){
		for (EdibleItem item : items){
			String filename = item.getImagePath();
			EdibleItemImage img = new EdibleItemImage(ImageConverter.getBufferedImageFromFile(filename));
			item.setImagePic(img);
		}
	}
}