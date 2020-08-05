import java.io.IOException;
import java.util.*;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import java.util.regex.*;


public class Mail {
    private String host = "";
    private String protocol = "";
    private String user = "";
    private String password = "";
    private String port = "";
    private String message = "";
    private String subject = "";
    private String sender = "";


    public String getMail() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getSender() {
        return sender;
    }

    public Mail(String host, String protocol, String port, String user, String password) {
        this.host = host;
        this.protocol = protocol;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    private Properties getServerProperties() {

        Properties properties = new Properties();

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

        return properties;
    }

    public String fetch(String findSubject, String findSender, String orderNumber) {
        String ret = "";
        try {

            // create properties field
            Properties properties = getServerProperties();
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            // create the IMAP store object and connect with the pop server
            Store store = emailSession.getStore("imap");
            store.connect(host, user, password);

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            ret = ret + "messages.length---" + messages.length + "\n";
            ret = ret + "\n\n\n\n\n\n";

            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                if ((message.getSubject().contains(findSubject))&&((message.getFrom()) != null)) {
                    Address[] a = message.getFrom();
                    Boolean z = false;
                    for (int j = 0; j < a.length; j++)
                        if (a[j].toString().substring(a[j].toString().indexOf("<")+1,a[j].toString().indexOf(">")).equals(findSender)){
                            this.sender = a[j].toString().substring(a[j].toString().indexOf("<")+1,a[j].toString().indexOf(">"));
                            this.subject = message.getSubject();
                            z = true;
                        }
                    if(z){
                        ret = ret + "---------------------------------\n";
                        String temp = writePart(message);
                        if (temp.contains(orderNumber)){
                            this.message = temp;
                            ret = ret + this.message + "\n";
                            emailFolder.close(false);
                            store.close();
                            return ret;
                        }
                    }
                }
            }

            // close the store and folder objects
            try {
                emailFolder.close(false);
                store.close();
            } catch (Exception e){

            }


        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    /*
     * This method checks for content-type
     * based on which, it processes and
     * fetches the content of the message
     */
    private static String writePart(Part p) throws Exception {
        String ret = "";
        if (p instanceof Message) {
            ret = writeEnvelope((Message) p);
            ret = ret + "\n";

            ret = ret + "----------------------------\n";
            ret = ret + "CONTENT-TYPE: " + p.getContentType() + "\n";

            //check if the content is plain text
            if (p.isMimeType("text/plain")) {
                ret = ret + "This is plain text\n";
                ret = ret + "---------------------------\n";
                ret = ret + (String) p.getContent() + "\n";
            }
            //check if the content has attachment
            else if (p.isMimeType("multipart/*")) {
                ret = ret + "This is a Multipart\n";
                ret = ret + "---------------------------\n";
                Multipart mp = (Multipart) p.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++)
                    ret = ret + writePart(mp.getBodyPart(i));
            }
            //check if the content is a nested message
            else if (p.isMimeType("message/rfc822")) {
                System.out.println("This is a Nested Message");
                System.out.println("---------------------------");
                ret = ret + writePart((Part) p.getContent());
            }
            else {
                Object o = p.getContent();
                if (o instanceof String) {
                    ret = ret + "This is a string\n";
                    ret = ret + "---------------------------\n";
                    ret = ret + (String) o + "\n";

                }
                else {
                    ret = ret + "This is an unknown type\n";
                    ret = ret + "---------------------------\n";
                    ret = ret + o.toString() + "\n";
                }
            }
        }
        return ret;

    }
    /*
     * This method would print FROM,TO and SUBJECT of the message
     */
    private static String writeEnvelope(Message m) throws Exception {
        String ret = "";
        ret = ret + "This is the message envelope\n";
        ret = ret + "---------------------------\n";
        Address[] a;

        // FROM
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++)
                ret = ret + "FROM: " + a[j].toString() + "\n";
        }

        // TO
        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < a.length; j++)
                ret = ret + "TO: " + a[j].toString()+ "\n";
        }

        // SUBJECT
        if (m.getSubject() != null)
            ret = ret + "SUBJECT: " + m.getSubject() + "\n";

        return ret;
    }

    public ArrayList getLinks(String baseUrl) {
        return getLinks(baseUrl, getMail());
    }

    private static ArrayList getLinks(String baseUrl,String body) {
        ArrayList links = new ArrayList();

        String regex = "\\(?\\b("+baseUrl+")[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(body);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }

    public static void main(String[] args) {
        // Arguments to pass in
        String host = "imap.gmail.com";
        String protocol = "imap";
        String port = "993";
        String username = "";
        String password = "";

        // Create object
        Mail object = new Mail(host, protocol, port, username, password);

        // .fetch("subject", "sender", "orderNo.") will search for an email in which Argument 1 CONTAINS the subject, Argument 2 IS the sender, and the body CONTAINS the order number which is Argument 3.
        // All arguments are case sensitive
        // The function returns the full mail as a String
        // .fetch() needs to be called to initialize .getSender(), .getSubject(), .getMail(), .getLinks(), etc
        // Everytime .fetch() is called all the other information will be reinitialized according to the search parameters given
        // Fetch starts searching from the most recent email's in the inbox and once it finds an email that meets all of the search characteristics it stop the search
        String email = object.fetch("pickup", "donotreply@nextuple.com","NXTUPLE-123456789-21");

        // .getSender() returns the sender as a String
        System.out.println("Sender: " + object.getSender());

        // .getSubject() returns the subject as a String
        System.out.println("Subject: " + object.getSubject());

        // .getMail returns the full mail as a String
        System.out.println("Email: " + object.getMail());

        // .getLinks() will return ONLY get all the bit.ly links in the mail as an ArrayList of Strings
        // If an email has two links the first one is for modifying appointment and the second one is for checking in. If an email only has one link then it is for checking in
        ArrayList<String> links = object.getLinks("https://bit.ly");
        System.out.println("Links Below:");
        for(String link: links)
            System.out.println(link);
    }
}