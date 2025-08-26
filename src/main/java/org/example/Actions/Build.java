package org.example.Actions;

import org.example.ui.Interface;

public class Build {
    Interface parent;
    public Build() {
    }

    public void setParent(Interface parent) {
        this.parent = parent;
    }

    public void buildCode(){
        parent.getTextOutput().setText(parent.getTextInput().getText());
    }

}
