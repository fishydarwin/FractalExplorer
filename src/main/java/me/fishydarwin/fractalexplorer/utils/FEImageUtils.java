package me.fishydarwin.fractalexplorer.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class FEImageUtils {

    // https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        int[] sourceData = ((DataBufferInt) source.getRaster().getDataBuffer()).getData();
        int[] biData = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);
        return bi;
    }

}
