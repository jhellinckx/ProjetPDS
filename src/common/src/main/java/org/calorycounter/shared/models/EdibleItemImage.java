package org.calorycounter.shared.models;


import android.org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.*;

import static org.calorycounter.shared.Constants.network.*;

import org.json.simple.JSONObject;
import java.io.UnsupportedEncodingException;
public class EdibleItemImage implements JSONSerializable{
	private BufferedImage img;
	private int img_width;
	private int img_height;
	private byte[] imgBytes;
	private int[] pixels;

	public EdibleItemImage(){
		img = null;
		img_width = -1;
		img_height = 1;
	}

	public EdibleItemImage(BufferedImage image){
		img = image;
		img_width = image.getWidth();
		img_height = image.getHeight();

		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(img, "jpeg", out);
			imgBytes = out.toByteArray();
		} catch (IOException e){
			System.err.println(e.getMessage());
		}
		
	}

	private void convertByteArrayToIntArray(){
		int[] pix = new int[img_width*img_height];
		for (int i = 0; i < pix.length; i++){
			int byteIndex = i;
			pix[i] = ((imgBytes[byteIndex] & 0xFF) << 24) | ((imgBytes[byteIndex + 3] & 0xFF) << 16)		// convert a byte (8bits) to an int (32 bits).
						| ((imgBytes[byteIndex + 2] & 0xFF) << 8) | (imgBytes[byteIndex + 1] & 0xFF);
		}

		pixels = pix;
	}

	private String getEncodedStringOfImageBytes(){
		byte[] encoded = Base64.encodeBase64(imgBytes);
		return new String(encoded);
	}

	private void initImageBytesFromEncodedString(String encodedString){
		imgBytes = Base64.decodeBase64(encodedString);

	}

	public byte[] getImageBytesArray(){
		return imgBytes;
	}

	public int[] getImagesPixels(){
		return pixels;
	}

	public int getImageHeight(){
		return img_height;
	}

	public int getImageWidth(){
		return img_width;
	}

	@Override
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		obj.put(IMAGE_WIDTH, img_width);
		obj.put(IMAGE_HEIGHT, img_height);
		obj.put(IMAGE_PIC, getEncodedStringOfImageBytes());
		return obj;
	}

	@Override
	public JSONObject toJSON(boolean noImage){
		return toJSON();
	}

	@Override
	public void initFromJSON(JSONObject obj){
		this.img_width = ((Long) obj.get(IMAGE_WIDTH)).intValue();
		this.img_height = ((Long) obj.get(IMAGE_HEIGHT)).intValue();
		String encodedString = (String) obj.get(IMAGE_PIC);
		initImageBytesFromEncodedString(encodedString);
		//convertByteArrayToIntArray();
	}
}