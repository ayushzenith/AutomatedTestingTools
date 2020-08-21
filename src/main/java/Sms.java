import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import org.joda.time.DateTime;

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

    public String fetch(String findSender, String keyword, int numberOfTexts, DateTime date) {
        this.body = "";
        this.sender = "";
        this.message = "";
        Twilio.init(this.ACCOUNT_SID, this.AUTH_TOKEN);
        ResourceSet<Message> messages = Message.reader().limit(numberOfTexts).read();
        for(Message record : messages) {
            if (record.getFrom().toString().contains(findSender) && record.getBody().contains(keyword)) {
                if (record.getDateSent().isAfter(date) || record.getDateSent().isEqual(date)) {
                    this.body = record.getBody().toString();
                    this.sender = record.getFrom().toString();
                    this.message = "From: " + record.getFrom().toString() + "\nTo: " + record.getTo().toString() + "\nDate Sent: " + record.getDateSent().toString() + "\nBody: " + record.getBody().toString();
                } else {
                    return message;
                }
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
        Sms sms = new Sms(ACCOUNT_SID, AUTH_TOKEN);
        
        //20-8-2020 00:00:00
        DateTime date = new DateTime(2020, 8, 20, 0, 0, 0);

        // .fetch("senderNo.", "Keyword", NumberOfTextsToSearch, DateTime)
        // Arguments are case sensitive
        // The function returns the full message as a String
        // .fetch() needs to be called to initialize .getSender(), .getBody(), .getMessage(), .getLinks(), etc
        // Everytime .fetch() is called all the other information will be reinitialized according to the search parameters given
        // Fetch starts searching from the most recent message in the twilio message inbox and outbox and once it finds a text that meets all of the search criterion it stop the search
        // In this it will find a text that came from the number "+10123456789" and the text must have the word "pickup" in it. It will search up to 1000000000 texts before terminating and if it finds multiple texts that fit the critereon then it will find the one that was sent right after 20-8-2020 00:00:00. It doesnt take any text before that date into consideration at all.

        String text = sms.fetch("+10123456789", "pickup",1000000000, date);

        // .getSender() returns the sender phone number as a String
        System.out.println("Sender: " + sms.getSender() + "\n");

        // .getBody() returns the body as a String
        System.out.println("Body: " + sms.getBody()+ "\n");

        // .getMessage() returns the full text message with receiver, sender, body, and DateTime information as a String
        System.out.println("Message: " + sms.getMessage()+ "\n");

        // .getLinks() will return ONLY get all the bit.ly links in the message body as an ArrayList of Strings
        ArrayList<String> smsLinks = sms.getLinks("https://bit.ly");
        System.out.println("Links Below:");
        for(String link: smsLinks)
            System.out.println(link);
    }

}
