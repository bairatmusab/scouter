package org.atos.scouter.TikaTools;

import com.google.common.base.Throwables;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.atos.scouter.util.nlp.OpenNLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class PdfExtracter{

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfExtracter.class);

    private final OpenNLP openNLP = OpenNLP.getOpenNLP(Thread.currentThread());

    public void extractText(String path)
            throws IOException
    {
        WriteOutContentHandler wrapped = new WriteOutContentHandler();
        ContentHandler handler = new BodyContentHandler(wrapped);
        try
        {
            TikaInputStream inputStream = TikaInputStream.get(new File(path));

            Metadata meta = new Metadata();
            Parser parser = new AutoDetectParser(new TikaConfig(getClass().getClassLoader()));
            parser.parse(inputStream, handler, meta, new ParseContext());

            String out = handler.toString().trim().replaceAll(" +", " ");
            //.replace("\n", "").replace("\r", "")

            findNames("Bonjour Monsieur Ahmad");
        }
        catch( Exception t )
        {
            if( wrapped.isWriteLimitReached(t) )
            {
                // keep going
                LOGGER.debug("PDF size limit reached.  Indexing truncated text");
                return;
            }
            throw Throwables.propagate(t);
        }
    }

    private void findNames (String input){

       // String [] tokens = openNLP.learnableTokenize(input);

        List<String> persons =  openNLP.applyNLPner(input,OpenNLP.nerOptions.PERSON);
        for (String s: persons) {
            System.out.println(s);
        }
    }

}