// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.dataprepschemaanasysis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.fork.ForkParser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.talend.dataprep.common.EasyFiles;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * created by stef on Dec 30, 2014 Detailled comment
 *
 */
public class TestingTika {

    private static void parseResource(File file) throws TransformerConfigurationException {
        System.out.println("Parsing resource : " + file.getName());
        BufferedInputStream inputStream = null;

        try {
            long start = System.currentTimeMillis();
            inputStream = new BufferedInputStream(new FileInputStream(file));
            inputStream.mark(10000);
            System.out.println(inputStream.markSupported());

            Parser parser = new ForkParser();
            ContentHandler contentHandler = new BodyContentHandler(1000000);

            // ==================================================================================
            // Build transformer to XML:
            // ==================================================================================
            boolean prettyPrint = true;
            String encoding = null;
            SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            TransformerHandler handler = factory.newTransformerHandler();
            handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
            handler.getTransformer().setOutputProperty(OutputKeys.INDENT, prettyPrint ? "yes" : "no");
            if (encoding != null) {
                handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, encoding);
            }
            handler.setResult(new StreamResult(System.out));
            // ==================================================================================

            Metadata metadata = new Metadata();

            parser.parse(inputStream, handler, metadata, new ParseContext());

            long end = System.currentTimeMillis();

            for (String name : metadata.names()) {
                String value = metadata.get(name);
                System.out.println("Metadata Name: " + name);
                System.out.println("Metadata Value: " + value);
            }

            System.out.println("Type: " + new Tika().detect(file));
            System.out.println("Content: " + contentHandler.toString());
            System.out.println("Done in " + (end - start) + " ms");
            System.out.println("\n");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, SAXException, TikaException, TransformerConfigurationException {
        // parseResource(new File("/home/stef/talend/test_files/customers_10k.csv"));
        parseResource(new File("/home/stef/talend/test_files/users.xls"));
        parseResource(EasyFiles.getFile("tagada.csv"));
        parseResource(EasyFiles.getFile("tagada.json"));
    }
}
