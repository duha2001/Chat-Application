package com.example.d_five;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

import com.example.d_five.dao.implementDAO.ConversationDAO;
import com.example.d_five.dao.implementDAO.ParticipantsDAO;
import com.example.d_five.dao.implementDAO.UserDAO;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final String username = "dfive";
    private static final String password = "dfive";
    private static final String host = "";
    private static final String port = "5432";
    private static final String database = "postgres";
    private static String Classes = "org.postgresql.Driver";
    private static String url = "jdbc:postgresql://"+host+":"+port+"/"+database;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDB() throws Exception {

        Class.forName(Classes);
        Connection connection = null;
        connection = DriverManager.getConnection(url, username, password);

        ConversationDAO conversationDAO = new ConversationDAO(connection  );

        UserDAO userDAO = new UserDAO(connection);
        ParticipantsDAO participantsDAO  = new ParticipantsDAO(connection);

        //Participant participant = participantsDAO.deleteParticipant(3L, 263L);
        //System.out.println(participant);
        //Conversation conversation = userDAO.newConversation("hello", null,"hi", false, null);

        Boolean check = participantsDAO.checkUserInConversation(2L, 61L);
        System.out.println(check);
    }

    @Test
    public void testReadXML() {
        String body = "<?xml version=\"1.0\"?>\n" +
                "<reginfo xmlns=\"urn:ietf:params:xml:ns:reginfo\" version=\"5\" state=\"full\">\n" +
                "\t<registration aor=\"sip:du@dfive-ims.dek.vn\" id=\"0x7fd9cf850fc8\" state=\"terminated\">\n" +
                "\t\t<contact id=\"0x1\" state=\"terminated\" event=\"expired\" expires=\"3600\" q=\"0.000\">\n" +
                "\t\t\t<uri>sip:du@192.168.122.30:55386;transport=tcp;alias=192.168.122.30~55386~2</uri>\n" +
                " \t\t</contact>\n" +
                "\t</registration>\n" +
                "</reginfo>";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(body)));
            NodeList nodeList = document.getElementsByTagName("contact");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String ex = element.getAttribute("id");
                    String event = element.getAttribute("event");
                    String state = element.getAttribute("state");
                    String expires = element.getAttribute("expires");
                    System.out.println(element.getAttribute("id"));
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}