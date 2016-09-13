package com.guardanis.gtools.general;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;

public class ImageHelper {
		
	public static String convertToString(final RenderedImage image, final String format) {
	    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    write(image, format, stream);
	    
	    try {
		    return stream.toString(StandardCharsets.ISO_8859_1.name());
	    } 
	    catch (final IOException ioe) {
	        throw new UncheckedIOException(ioe);
	    }
	}
	
	public static void write(final RenderedImage image, final String format, ByteArrayOutputStream stream){	    
	    try {
	        ImageIO.write(image, 
	        		format, 
	        		Base64.getEncoder().wrap(stream));
	    } 
	    catch (final IOException ioe) {
	        throw new UncheckedIOException(ioe);
	    }
	}

	public static BufferedImage convertToImage(final String base64String) {
		return convert(Base64.getDecoder()
        		.decode(base64String));
	}
	
	public static BufferedImage convert(byte[] data){
		try {
	        return ImageIO.read(new ByteArrayInputStream(data));
	    } 
	    catch (final IOException ioe) {
	        throw new UncheckedIOException(ioe);
	    }
	}

}
