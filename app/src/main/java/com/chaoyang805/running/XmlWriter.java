package com.chaoyang805.running;

import android.content.Context;
import android.util.Xml;

import com.baidu.mapapi.model.LatLng;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

/**
 * Created by chaoyang805 on 2015/12/17.
 */
public class XmlWriter {

    private StringWriter mStringWriter;
    private XmlSerializer mXmlSerializer;
    private static final String NAME_SPACE = "";
    private int index;
    public XmlWriter() throws IOException {
        index = 0;
        init();
    }

    private void init() throws IOException {
        mXmlSerializer = Xml.newSerializer();
        mStringWriter = new StringWriter();
        mXmlSerializer.setOutput(mStringWriter);
    }

    public void addPath(LatLng latLng) throws IOException {
        mXmlSerializer.startTag(NAME_SPACE,"path");
        mXmlSerializer.attribute(NAME_SPACE, "index", String.valueOf(index++));

        mXmlSerializer.startTag(NAME_SPACE, "lat");
        mXmlSerializer.text(String.valueOf(latLng.latitude));
        mXmlSerializer.endTag(NAME_SPACE, "lat");

        mXmlSerializer.startTag(NAME_SPACE, "lng");
        mXmlSerializer.text(String.valueOf(latLng.longitude));
        mXmlSerializer.endTag(NAME_SPACE, "lng");

        mXmlSerializer.endTag(NAME_SPACE, "path");
    }

    public void start(String name,String date) throws IOException {
        mXmlSerializer.startDocument("UTF-8", true);
        mXmlSerializer.startTag(NAME_SPACE, "message");
        mXmlSerializer.startTag(NAME_SPACE, "info");

        mXmlSerializer.attribute(NAME_SPACE, "account", name);

        mXmlSerializer.attribute(NAME_SPACE, "start_time", date);
        mXmlSerializer.endTag(NAME_SPACE, "info");
    }

    public void end() throws IOException {
        mXmlSerializer.endTag(NAME_SPACE, "message");
        mXmlSerializer.endDocument();

    }

    public boolean write(Context context,String fileName) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        osw.write(mStringWriter.toString());
        osw.flush();
        osw.close();
        fos.close();
        return true;
    }
}
