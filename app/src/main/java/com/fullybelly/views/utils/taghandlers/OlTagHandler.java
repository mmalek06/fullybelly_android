package com.fullybelly.views.utils.taghandlers;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

public final class OlTagHandler implements Html.TagHandler{
    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        if(tag.equals("ol") && !opening) {
            output.append("\n");
        }
        if(tag.equals("li") && opening) {
            output.append("\n\t•");
        }
    }
}
