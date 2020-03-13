import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.Log;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    static private Map<String, Image> mapImages = new HashMap<>();
    static private Map<String, UnicodeFont> mapFonts = new HashMap<>();

    static Image getImage(String s) {
        try {
            if (mapImages.containsKey(s)) {
                return mapImages.get(s);
            } else {
                InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream(s);
                Image im = new Image(is, s, false);
                Log.info("Loaded image: " + s);
                mapImages.put(s, im);
                return im;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static UnicodeFont getFont(String s, int size) {
        try {
            String fid = s + size;  // Add size to name
            if (mapFonts.containsKey(fid)) {
                return mapFonts.get(fid);
            } else {
                InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream(s);
                java.awt.Font aux = Font.createFont(Font.TRUETYPE_FONT, is);
                UnicodeFont ufont = new UnicodeFont(aux, size, true, false);
                ufont.addAsciiGlyphs();
                ufont.getEffects().add(new ColorEffect());
                ufont.loadGlyphs();
                Log.info("Loaded font: " + fid);
                mapFonts.put(fid, ufont);
                return ufont;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void clearImages() {
        mapImages.clear();
    }

    static void clearFonts() {
        mapFonts.clear();
    }

    static void clearImage(String s) {
        if (mapImages.containsKey(s)) {
            mapImages.remove(s);
        }
    }

    static void clearFont(String s, int size) {
        String k = s + size;
        if (mapFonts.containsKey(k)) {
            mapFonts.remove(k);
        }
    }


}
