package me.fishydarwin.fractalexplorer.view.clipboard.owner;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public class Owner implements ClipboardOwner {

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
        System.out.println("Lost Clipboard Ownership");
    }

}
