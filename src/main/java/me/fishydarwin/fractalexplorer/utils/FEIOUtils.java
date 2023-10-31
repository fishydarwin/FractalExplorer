package me.fishydarwin.fractalexplorer.utils;

import me.fishydarwin.fractalexplorer.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class FEIOUtils {

    private static Image iconImage;
    private static Image aboutIconImage;

    public static Image getIconImage() throws IOException {
        if (iconImage == null) {
            URL iconURL = Main.class.getResource("/icon.png");
            assert iconURL != null;
            iconImage = ImageIO.read(iconURL);
        }
        return iconImage;
    }

    public static Image getAboutIconImage() throws IOException {
        if (aboutIconImage == null) {
            aboutIconImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            Graphics gfx = ((BufferedImage) aboutIconImage).createGraphics();
            gfx.drawImage(getIconImage(), 0, 0, 48, 48, null);
            gfx.dispose();
        }
        return aboutIconImage;
    }

}
