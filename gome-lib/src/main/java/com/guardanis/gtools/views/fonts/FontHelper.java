package com.guardanis.gtools.views.fonts;

import java.awt.Font;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FontHelper {
	
	private static FontHelper instance;
	public static FontHelper getInstance(){
		if(instance == null)
			instance = new FontHelper();
		
		return instance;
	}
	
	public static final String FONT_ROBOTO = "roboto";
	
	private static final String FONTS_DIR = "assets/fonts/";
	private static final int BASE_FONT = 1;

	private Map<String, Map<Integer, Font>> fonts = new HashMap<String, Map<Integer, Font>>();
	
	protected FontHelper(){
		checkBaseFontExists();
		
		try {
			fonts.put(FONT_ROBOTO, buildDefaultMap("roboto.ttf"));
		} 
		catch (Exception e) { e.printStackTrace(); }		
	}
	
	private Map<Integer, Font> buildDefaultMap(String fontFile) throws Exception {
		Map<Integer, Font> map = new HashMap<Integer, Font>();
		map.put(BASE_FONT, build(fontFile));
		
		return map;
	}
	
	private Font build(String fileName) throws Exception {
		InputStream stream = getClass()
				.getClassLoader()
				.getResourceAsStream(FONTS_DIR + fileName);
		
		return Font.createFont(Font.TRUETYPE_FONT, stream);
	}
	
	public Font get(String key, int size){
		if(fonts.get(key).get(size) == null)
			fonts.get(key)
				.put(size, new Font(fonts.get(key).get(BASE_FONT).getName(), Font.PLAIN, size));
		
		return fonts.get(key).get(size);
	}
	
	public boolean checkBaseFontExists(){
		URL url = getClass()
				.getClassLoader()
				.getResource(FONTS_DIR + "roboto.ttf");
		
		if(url == null)
			throw new RuntimeException(FONTS_DIR + "roboto.ttf" + " does no exists!");
		
		return true;
	}

}