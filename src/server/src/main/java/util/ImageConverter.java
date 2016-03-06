package util;

import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public final class ImageConverter {

	private static final String directory = "server/scripts/database/";

	private static final int cache_size = 10;
	private static List<BufferedImage> image_cache = new ArrayList<>();
	private static List<String> paths_cache = new ArrayList<>();


	private static BufferedInputStream getImageStream(String filepath) throws IOException{
		return new BufferedInputStream(Files.newInputStream(Paths.get(directory + filepath)));
	}

	private static void addDataToCaches(String filepath, BufferedImage img){
		image_cache.add(img);
		paths_cache.add(filepath);
	}

	private static BufferedImage getBufferedImageFromPath(String filepath){
		BufferedImage img = null;
		try{
			BufferedInputStream stream = getImageStream(filepath);
			
			img = ImageIO.read(stream);
			addDataToCaches(filepath, img);
		} catch (IOException e){
			System.err.println(e.getMessage());
		}
		return img;
	}

	private static boolean cachesFull(){
		return image_cache.size() == cache_size;
	}

	private static void emptyCaches(){
		image_cache = new ArrayList<>();
		paths_cache = new ArrayList<>();
	}


	public static BufferedImage getBufferedImageFromFile(String filepath){
		int index = paths_cache.indexOf(filepath);
		if (index != -1){
			return image_cache.get(index);
		}
		if (cachesFull()){
			emptyCaches();
		}
		BufferedImage img = getBufferedImageFromPath(filepath);
		return img;
	}
}