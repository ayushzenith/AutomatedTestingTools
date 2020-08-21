# Automated Email and SMS Testing using JavaMail and Twilio for Nextuple

## Gmail end set up

### Less secure app access
In order to login into a Google account from a non Google based domain the user will need to allow for less secure app access. Under most cases this is turned off for the safety of the user but in order for JavaMail to gain access to the inbox it will need this to be turned on. The user can turn on "Less secure app access" [here](https://myaccount.google.com/lesssecureapps).
###### Note: Nextuple domain emails dont have Less secure app access enabled and requires adminstrator to enable them

### Two factor authentication
If the user has two factor authentication activated they will need to generate an "App specific password" so that JavaMail can log in without having to go through the two factor authentication. The user can generate an app specific password [here](https://myaccount.google.com/u/0/apppasswords). After the user generates an app specific password when inputing password in the program make sure not to use the google account password but instead to use the newly generated app specific password. 

## Twilio end set up

Create an account and create a virtual number. Take note of `ACCOUNT_SID` and `AUTH_TOKEN` and keep safe.

## Usage

### Dependencies

Have been mentioned in the `build.gradle` file

```
    compile group: 'javax.activation', name: 'javax.activation-api', version: '1.2.0'
    compile group: 'javax.mail', name: 'mail', version: '1.4.1'
    compile group: "com.twilio.sdk", name: "twilio", version: "7.45.+"
```


### Functions and usage

#### Mail

```
public static void main(String[] args) {
        // Arguments to pass in
        String host = "imap.gmail.com";
        String protocol = "imap";
        String port = "993";
        String username = "email@gmail.com";
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
```


#### SMS

```
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
```


