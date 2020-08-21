import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        // MAIL EXAMPLE


        String host = "imap.gmail.com";
        String protocol = "imap";
        String port = "993";
        String username = "";
        String password = "";

        // Create object
        Mail mail = new Mail(host, protocol, port, username, password);

        // .fetch("subject", "sender", "orderNo.") will search for an email in which Argument 1 CONTAINS the subject, Argument 2 IS the sender, and the body CONTAINS the order number which is Argument 3.
        // All arguments are case sensitive
        // The function returns the full mail as a String
        // .fetch() needs to be called to initialize .getSender(), .getSubject(), .getMail(), .getLinks(), etc
        // Everytime .fetch() is called all the other information will be reinitialized according to the search parameters given
        // Fetch starts searching from the most recent email's in the inbox and once it finds an email that meets all of the search characteristics it stop the search
        String email = mail.fetch("pickup", "donotreply@nextuple.com","NXTUPLE-123456789-21");

        // .getSender() returns the sender as a String
        System.out.println("Sender: " + mail.getSender());

        // .getSubject() returns the subject as a String
        System.out.println("Subject: " + mail.getSubject());

        // .getMail returns the full mail as a String
        System.out.println("Email: " + mail.getMail());

        // .getLinks() will return ONLY get all the bit.ly links in the mail as an ArrayList of Strings
        // If an email has two links the first one is for modifying appointment and the second one is for checking in. If an email only has one link then it is for checking in
        ArrayList<String> mailLinks = mail.getLinks("https://bit.ly");
        System.out.println("Links Below:");
        for(String link: mailLinks)
            System.out.println(link);




        // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // SMS EXAMPLE


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
