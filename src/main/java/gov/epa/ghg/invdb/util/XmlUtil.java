package gov.epa.ghg.invdb.util;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

public class XmlUtil {
    public static XMLReader createSecureXMLReader() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return factory.newSAXParser().getXMLReader();
    }
}
