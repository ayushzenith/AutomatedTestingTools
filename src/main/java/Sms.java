import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sms {
    private String ACCOUNT_SID = "";
    private String AUTH_TOKEN = "";
    private String body = "";
    private String sender = "";
    private String message = "";

    public String getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Sms(String ACCOUNT_SID, String AUTH_TOKEN) {
        this.ACCOUNT_SID = ACCOUNT_SID;
        this.AUTH_TOKEN = AUTH_TOKEN;
    }

    public String fetch(String findSender, String orderNumber, int numberOfTexts) {
        Twilio.init(this.ACCOUNT_SID, this.AUTH_TOKEN);
        ResourceSet<Message> messages = Message.reader().limit(numberOfTexts).read();
        for(Message record : messages) {
            if (record.getFrom().toString().contains(findSender) && record.getBody().contains(orderNumber)) {
                this.body = record.getBody().toString();
                this.sender = record.getFrom().toString();
                this.message = "From: " + record.getFrom().toString() + "\nTo: " + record.getTo().toString() + "\nDate Sent: " + record.getDateSent().toString() + "\nBody: " + record.getBody().toString();
                return message;
            }
        }
        return "";
    }
    public ArrayList getLinks(String baseUrl) {
        return getLinks(baseUrl, getBody());
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
        // Get ACCOUNT_SID and AUTH_TOKEN from Twilio account
        String ACCOUNT_SID = "";
        String AUTH_TOKEN = "";

        // Create object
        Sms object = new Sms(ACCOUNT_SID, AUTH_TOKEN);

        // .fetch("sender", "orderNo", 12) will search for an email in which the sender number CONTAINS Argument 1, the sender IS Argument 2, and Argument 3 determines how many texts should be searched starting at the latest text.
        // Arguments are case sensitive
        // The function returns the full message as a String
        // .fetch() needs to be called to initialize .getSender(), .getBody(), .getMessage(), .getLinks(), etc.
        // Everytime .fetch() is called all the other information will be reinitialized according to the search parameters given
        // Fetch starts searching from the most recent message in the twilio message inbox and outbox and once it finds an email that meets all of the search criterion it stop the search
        String text = object.fetch("+10123456789", "NXTUPLE-123456789-21",1000000000);

        // .getSender() returns the sender phone number as a String
        System.out.println("Sender: " + object.getSender() + "\n");

        // .getBody() returns the body as a String
        System.out.println("Body: " + object.getBody()+ "\n");

        // .getMessage() returns the full text message with receiver, sender, body, and DateTime information as a String
        System.out.println("Message: " + object.getMessage()+ "\n");

        // .getLinks() will return ONLY get all the bit.ly links in the message body as an ArrayList of Strings
        ArrayList<String> links = object.getLinks("https://bit.ly");
        System.out.println("Links Below:");
        for(String link: links)
            System.out.println(link);
    }

}
