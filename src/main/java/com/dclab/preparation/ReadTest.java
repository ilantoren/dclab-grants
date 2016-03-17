/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dclab.preparation;

//import com.google.common.base.Charsets;
//import com.google.common.io.Files;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author richardthorne
 */
public class ReadTest {

    public static void main(String[] args) {
        try {
            ReadTest app;
            app = new ReadTest("/Users/richardthorne/Downloads/forward-flow");
        } catch (IOException ex) {
            Logger.getLogger(ReadTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Pattern pat = Pattern.compile("<ce:acknowledgment[^>]+>(.+)</ce:ack", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    //Pattern pat2 = Pattern.compile("<section[^>]+>\\s+<title[^>]+>Acknowledgements</title>\\s?(.+)\\s+</section>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    Pattern pat3 = Pattern.compile("<section[^>]+>\\s?<title[^>]+>Acknowledg[^>]+</title>\\s?(.+)\\s?</section>");
    static final String path = "/Users/richardthorne/Downloads/forward-flow";
    private final int MAX_CAPACITY = 200000000;
    private final BufferedWriter bfw;

    public ReadTest(String path) throws IOException {
        bfw = new BufferedWriter( new FileWriter( "grants.txt"));
        File folder = new File(path);
        dig(folder);
        bfw.close();
    }

    public void dig(File folder) {
        File[] files = folder.listFiles();
        Logger.getAnonymousLogger().info("OPENING folder " + folder.getName());
        Map<Boolean, List<File>> isZip = Arrays.stream(files).collect(Collectors.partitioningBy((f -> f.getName().endsWith("zip"))));
        Map<Boolean, List<File>> isFolder = isZip.get(false).stream().collect(Collectors.partitioningBy(f -> f.isDirectory()));
        isFolder.get(false).stream().filter(y -> y.getName().endsWith("xml")).forEach(this::scanFile);
        isFolder.get(true).stream().forEach(this::dig);
        isZip.get(true).stream().forEach(z -> {
            try {
                ZipFile zipFile = new ZipFile(z);
                zipFile.stream().forEach(ze -> {
                    try {
                        String s = handleZip(zipFile, ze);
                        if ( s!= null )  {
                             printLine(s);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ReadTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(ReadTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    private void printLine(String s) throws IOException {
        bfw.write("-START\n");
        bfw.write( s );
        bfw.write("\n-END\n");
    }

    public String handleZip(final ZipFile zf, final ZipEntry ze) throws IOException {

        if (ze.isDirectory()) {
            System.out.println(ze.getName() + " is folder ");
        } else if (ze.getName().endsWith(".xml")) {
            Logger.getAnonymousLogger().info("process file " + ze.getName());

            String s = ze.toString();

           // ByteBuffer bb = ByteBuffer.allocate(MAX_CAPACITY);
            InputStream is = zf.getInputStream(ze);

            byte[] bytes = IOUtils.toByteArray(is);

            return extract( new String(bytes) );

            //scan( sr );
        }
        return null;
    }

    public void scanFile(File f) {
        String s = null;
        try {
            s = Files.toString(f, Charsets.UTF_8);
         String res = extract(s);
         if ( res != null) {
            printLine(res);
         }
        } catch (IOException ex) {
            Logger.getLogger(ReadTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String scan(Reader r) {
        try {
            CharBuffer cb = CharBuffer.allocate(MAX_CAPACITY);
            int i = r.read(cb);
            Logger.getAnonymousLogger().log(Level.INFO, "read {0} bytes", i);

            String content = cb.toString();
            return extract(content);
        } catch (IOException ex) {
            Logger.getLogger(ReadTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String extract(String content) throws IOException {
        Matcher mat = pat.matcher(content);
        if (mat.find()) {
            System.out.println(mat.group(1));
            return mat.group(1);
        } else {
            mat = pat3.matcher(content);
            if (mat.find()) {
                System.out.println(mat.group(1));
                return (mat.group(1));
            }
        }
        return null;
    }

}
