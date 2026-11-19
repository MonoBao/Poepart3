import java.awt.GraphicsEnvironment;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class QuickChat
{
    Scanner scan = new Scanner(System.in);
    Login login = new Login();
    Message messages = new Message();
    int number;
    int entered;
    boolean totalDisplayed;
    boolean cancelled;
    boolean useDialogs = !GraphicsEnvironment.isHeadless();

    public QuickChat()
    {
        register();
    }

    public QuickChat(String fileName)
    {
        messages.filename = fileName;
        register();
    }

    private String getInput(String prompt)
    {
        String answer;
        if (useDialogs)
        {
            answer = JOptionPane.showInputDialog(prompt);
        }
        else
        {
            System.out.print(prompt);
            answer = scan.nextLine();
        }
        if (answer == null)
        {
            cancelled = true;
            return "";
        }
        return answer;
    }

    private void showMessage(String text)
    {
        if (useDialogs)
        {
            JOptionPane.showMessageDialog(null, text);
        }
        else
        {
            System.out.println(text);
        }
    }

    public void register()
    {
        showMessage("Register");
        String name = getInput("Enter first name: ");
        if (cancelled)
        {
            return;
        }
        String surname = getInput("Enter last name: ");
        if (cancelled)
        {
            return;
        }
        String username = getInput("Enter username: ");
        if (cancelled)
        {
            return;
        }
        String password = getInput("Enter password: ");
        if (cancelled)
        {
            return;
        }
        String cellphone = getInput("Enter cellphone: ");
        if (cancelled)
        {
            return;
        }
        showMessage(login.registerUser(name, surname, username, password, cellphone));
        if (login.registerStatus(username, password, cellphone))
        {
            logIn();
        }
    }

    public void logIn()
    {
        showMessage("Login");
        String username = getInput("Enter username: ");
        if (cancelled)
        {
            return;
        }
        String password = getInput("Enter password: ");
        if (cancelled)
        {
            return;
        }
        boolean status = login.loginUser(username, password);
        showMessage(login.returnLoginStatus(status));
        if (status)
        {
            messages.loadStoredMessages();
            showMessage("Welcome to QuickChat.");
            String messageCount = getInput("How many messages do you want to enter: ");
            if (cancelled)
            {
                return;
            }
            number = Integer.parseInt(messageCount);
            menu();
        }
    }

    public void menu()
    {
        String choice = "";
        while (!choice.equals("3") && !cancelled)
        {
            choice = getInput("1 Send Messages\n2 Show recently sent messages\n3 Quit\n4 Stored Messages\nEnter option: ");
            if (cancelled)
            {
                return;
            }
            if (choice.equals("1"))
            {
                sendMessages();
            }
            else if (choice.equals("2"))
            {
                showMessage("Coming Soon.");
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
        while (!choice.equals("7") && !cancelled)
        {
            choice = getInput("1 Display stored message senders and recipients\n2 Display the longest stored message\n3 Search for a message ID\n4 Search messages for a recipient\n5 Delete a message using its hash\n6 Display message reports\n7 Back\nEnter option: ");
            if (cancelled)
            {
                return;
            }

            if (choice.equals("1"))
            {
                showMessage(messages.printStoredSendersAndRecipients());
            }
            else if (choice.equals("2"))
            {
                showMessage(messages.findLongestStoredMessage());
            }
            else if (choice.equals("3"))
            {
                String messageID = getInput("Enter message ID: ");
                if (cancelled)
                {
                    return;
                }
                showMessage(messages.searchForMessageID(messageID));
            }
            else if (choice.equals("4"))
            {
                String recipient = getInput("Enter recipient number: ");
                if (cancelled)
                {
                    return;
                }
                showMessage(messages.searchForRecipient(recipient));
            }
            else if (choice.equals("5"))
            {
                String hash = getInput("Enter message hash: ");
                if (cancelled)
                {
                    return;
                }
                showMessage(messages.deleteMessageHash(hash));
            }
            else if (choice.equals("6"))
            {
                showMessage("Sent Messages\n" + messages.printMessageReport()
                        + "\nStored Messages\n" + messages.printStoredMessageReport());
            }
        }
    }

    public void sendMessages()
    {
        for (int i = entered; i < number; i++)
        {
            messages.recipient = getInput("Enter recipient number: ");
            if (cancelled)
            {
                return;
            }
            while (!messages.checkRecipientCellNumber())
            {
                showMessage(messages.checkRecipientCell());
                messages.recipient = getInput("Enter recipient number: ");
                if (cancelled)
                {
                    return;
                }
            }
            showMessage(messages.checkRecipientCell());
            messages.sender = login.FirstName + " " + login.LastName;
            messages.message = getInput("Enter message: ");
            if (cancelled)
            {
                return;
            }
            while (!messages.checkMessageLength())
            {
                showMessage(messages.messageStatus());
                messages.message = getInput("Enter message: ");
                if (cancelled)
                {
                    return;
                }
            }
            showMessage(messages.messageStatus());
            messages.createMessageID();
            showMessage("Message ID generated: " + messages.messageID);
            String choice = getInput("1 Send Message\n2 Disregard Message\n3 Store Message\nEnter option: ");
            if (cancelled)
            {
                return;
            }
            while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3"))
            {
                choice = getInput("Enter option: ");
                if (cancelled)
                {
                    return;
                }
            }
            showMessage(messages.SentMessage(choice));
            if (choice.equals("1"))
            {
                showMessage(messages.messageDetails());
            }
            else if (choice.equals("2"))
            {
                String delete = getInput("Press 0 to delete the message: ");
                if (cancelled)
                {
                    return;
                }
                while (!delete.equals("0"))
                {
                    delete = getInput("Press 0 to delete the message: ");
                    if (cancelled)
                    {
                        return;
                    }
                }
            }
            entered++;
        }
        if (entered == number && !totalDisplayed)
        {
            showMessage("Total messages sent: " + messages.returnTotalMessages());
            totalDisplayed = true;
        }
    }
}
