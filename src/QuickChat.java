// Runs the registration, login, and message menus.
import java.util.Scanner;

public class QuickChat
{
    Scanner scan = new Scanner(System.in);
    Login login = new Login();
    Message messages = new Message();
    int number;
    int entered;
    boolean totalDisplayed;

    public QuickChat()
    {
        register();
    }

    public QuickChat(String fileName)
    {
        messages.filename = fileName;
        register();
    }

    public void register()
    {
        System.out.println("Register");
        System.out.print("Enter first name: ");
        String name = scan.nextLine();
        System.out.print("Enter last name: ");
        String surname = scan.nextLine();
        System.out.print("Enter username: ");
        String username = scan.nextLine();
        System.out.print("Enter password: ");
        String password = scan.nextLine();
        System.out.print("Enter cellphone: ");
        String cellphone = scan.nextLine();
        System.out.println(login.registerUser(name, surname, username, password, cellphone));
        if (login.registerStatus(username, password, cellphone))
        {
            logIn();
        }
    }

    public void logIn()
    {
        System.out.println("Login");
        System.out.print("Enter username: ");
        String username = scan.nextLine();
        System.out.print("Enter password: ");
        String password = scan.nextLine();
        boolean status = login.loginUser(username, password);
        System.out.println(login.returnLoginStatus(status));
        if (status)
        {
            messages.loadStoredMessages();
            System.out.println("Welcome to QuickChat.");
            System.out.print("How many messages do you want to enter: ");
            number = Integer.parseInt(scan.nextLine());
            menu();
        }
    }

    public void menu()
    {
        String choice = "";
        while (!choice.equals("3"))
        {
            System.out.println("1 Send Messages");
            System.out.println("2 Show recently sent messages");
            System.out.println("3 Quit");
            System.out.println("4 Stored Messages");
            System.out.print("Enter option: ");
            choice = scan.nextLine();
            if (choice.equals("1"))
            {
                sendMessages();
            }
            else if (choice.equals("2"))
            {
                System.out.println("Coming Soon.");
            }
            else if (choice.equals("4"))
            {
                storedMessagesMenu();
            }
        }
    }

    public void storedMessagesMenu()
    {
        String choice = "";
        while (!choice.equals("7"))
        {
            System.out.println("1 Display stored message senders and recipients");
            System.out.println("2 Display the longest stored message");
            System.out.println("3 Search for a message ID");
            System.out.println("4 Search messages for a recipient");
            System.out.println("5 Delete a message using its hash");
            System.out.println("6 Display message reports");
            System.out.println("7 Back");
            System.out.print("Enter option: ");
            choice = scan.nextLine();

            if (choice.equals("1"))
            {
                System.out.println(messages.printStoredSendersAndRecipients());
            }
            else if (choice.equals("2"))
            {
                System.out.println(messages.findLongestStoredMessage());
            }
            else if (choice.equals("3"))
            {
                System.out.print("Enter message ID: ");
                System.out.println(messages.searchForMessageID(scan.nextLine()));
            }
            else if (choice.equals("4"))
            {
                System.out.print("Enter recipient number: ");
                System.out.println(messages.searchForRecipient(scan.nextLine()));
            }
            else if (choice.equals("5"))
            {
                System.out.print("Enter message hash: ");
                System.out.println(messages.deleteMessageHash(scan.nextLine()));
            }
            else if (choice.equals("6"))
            {
                System.out.println("Sent Messages");
                System.out.println(messages.printMessageReport());
                System.out.println("Stored Messages");
                System.out.println(messages.printStoredMessageReport());
            }
        }
    }

    public void sendMessages()
    {
        for (int i = entered; i < number; i++)
        {
            System.out.print("Enter recipient number: ");
            messages.recipient = scan.nextLine();
            while (!messages.checkRecipientCellNumber())
            {
                System.out.println(messages.checkRecipientCell());
                System.out.print("Enter recipient number: ");
                messages.recipient = scan.nextLine();
            }
            System.out.println(messages.checkRecipientCell());
            messages.sender = login.FirstName + " " + login.LastName;
            System.out.print("Enter message: ");
            messages.message = scan.nextLine();
            while (!messages.checkMessageLength())
            {
                System.out.println(messages.messageStatus());
                System.out.print("Enter message: ");
                messages.message = scan.nextLine();
            }
            System.out.println(messages.messageStatus());
            messages.createMessageID();
            System.out.println("Message ID generated: " + messages.messageID);
            System.out.println("1 Send Message");
            System.out.println("2 Disregard Message");
            System.out.println("3 Store Message");
            System.out.print("Enter option: ");
            String choice = scan.nextLine();
            while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3"))
            {
                System.out.print("Enter option: ");
                choice = scan.nextLine();
            }
            System.out.println(messages.SentMessage(choice));
            if (choice.equals("1"))
            {
                System.out.println(messages.messageDetails());
            }
            else if (choice.equals("2"))
            {
                String delete = scan.nextLine();
                while (!delete.equals("0"))
                {
                    System.out.print("Press 0 to delete the message: ");
                    delete = scan.nextLine();
                }
            }
            entered++;
        }
        if (entered == number && !totalDisplayed)
        {
            System.out.println("Total messages sent: " + messages.returnTotalMessages());
            totalDisplayed = true;
        }
    }
}
