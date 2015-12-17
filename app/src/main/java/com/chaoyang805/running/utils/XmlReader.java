package com.chaoyang805.running.utils;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by chaoyang805 on 2015/12/17.
 */
public class XmlReader {

    private XmlPullParser mParser;


    public XmlReader() throws XmlPullParserException {
        mParser = XmlPullParserFactory.newInstance().newPullParser();
    }

    public void parse(final Context context, final String fileName, final Callback callback) {
        new Thread() {

            @Override
            public void run() {
                try {
                    int position = 0;
                    double lat = 0;
                    double lng = 0;
                    FileInputStream fis = context.openFileInput(fileName);
                    mParser.setInput(fis, "UTF-8");
                    while (mParser.next() != XmlPullParser.END_DOCUMENT) {
                        switch (mParser.getEventType()) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.START_TAG:
                                if (mParser.getName() != null) {
                                    if (mParser.getName().equals("info")) {
                                        String account = "";
                                        String date = "";
                                        account = mParser.getAttributeValue(0);
                                        date = mParser.getAttributeValue(1);
                                        if (callback != null) {
                                            callback.start(account, date);
                                        }
                                    } else if (mParser.getName().equals("path")) {
                                        position = Integer.valueOf(mParser.getAttributeValue(0));
                                    } else if (mParser.getName().equals("lat")) {
                                        lat = Double.valueOf(mParser.nextText());
                                    } else if (mParser.getName().equals("lng")) {
                                        lng = Double.valueOf(mParser.nextText());
                                    }
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (mParser.getName() != null && mParser.getName().equals("path")) {
                                    if (callback != null) {
                                        callback.findNext(position, lat, lng);
                                    }
                                }
                                break;
                        }
                    }
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public interface Callback {

        void start(String name, String date);

        void findNext(int position, double lat, double lng);
    }
}
