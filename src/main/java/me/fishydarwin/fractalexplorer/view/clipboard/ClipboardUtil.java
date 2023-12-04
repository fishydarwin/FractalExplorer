package me.fishydarwin.fractalexplorer.view.clipboard;

import me.fishydarwin.fractalexplorer.view.clipboard.image.TransferableImage;
import me.fishydarwin.fractalexplorer.view.clipboard.owner.Owner;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;

// https://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard
public class ClipboardUtil {
    private static final Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    private static final ClipboardOwner owner = new Owner();

    public static void copy(BufferedImage img) {
        copy(new TransferableImage(img));
    }
    public static void copy(Transferable trans) {
        c.setContents(trans, owner);
    }

}

