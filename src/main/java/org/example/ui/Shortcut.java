package org.example.ui;

import java.awt.event.KeyEvent;

public class Shortcut {
    public Shortcut() {}

    public void Detect(Interface parent, KeyEvent e){
        if(saveFile(e)){
            parent.getToolBar().saveFile(parent);
        }
        if(newFile(e)){
            parent.getToolBar().newFile(parent);
        }
        if(openFile(e)){
            parent.getToolBar().openFile(parent);
        }
    }

    public boolean saveFile(KeyEvent e){
        if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S){
            return true;
        }
        return false;
    }

    public boolean openFile(KeyEvent e){
        if(e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_A){
            return true;
        }
        return false;
    }

    public boolean newFile(KeyEvent e){
        if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N){
            return true;
        }
        return false;
    }
}
