package com.freshhome.ccavenue;

import android.support.v7.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;


public class ResponseHandler extends AppCompatActivity {

  String tag;


    public ResponseHandler() throws XmlPullParserException, IOException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( "<head><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">{\"success\":{\"msg\":\"Transaction completed\"},\"code\":200}</pre></body></head>" ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if(eventType == XmlPullParser.START_TAG) {
                tag=xpp.getName();

                System.out.println("Start tag "+xpp.getName());
            } else if(eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag "+xpp.getName());
            } else if(eventType == XmlPullParser.TEXT) {
                if(tag.equalsIgnoreCase("pre")) {
                    tag = xpp.getText();
                }
                System.out.println("Text "+xpp.getText());
            }
            eventType = xpp.next();
        }
    }
}
