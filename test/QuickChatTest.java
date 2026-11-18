import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class QuickChatTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    String registration = "Katso\nMosime\nkat_1\nPassword1!\n+27718693002\n";
    String login = "kat_1\nPassword1!\n";

    public String runChat(String input)
    {
        return runChat(input, null);
    }

    public String runChat(String input, String filename)
    {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output));
            if (filename == null)
            {
                new QuickChat();
            }
            else
            {
                new QuickChat(filename);
            }
        }
        finally
        {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void testFailedLoginCannotSend()
    {
        String output = runChat(registration + "kat_1\nwrong\n");
        assertTrue(output.contains("Username or password incorrect, please try again."));
        assertFalse(output.contains("Welcome to QuickChat"));
        assertFalse(output.contains("Enter recipient number"));
    }

    @Test
    public void testSendDiscardLimitAndMenu()
    {
        String output = runChat(registration + login + "2\n2\n1\n+27718693002\nHi Mike, can you join us for dinner tonight?\n1\n+27718693002\nHi Keegan, did you receive the payment?\n2\n0\n1\n3\n");
        assertTrue(output.contains("Welcome to QuickChat"));
        assertTrue(output.contains("Coming Soon."));
        assertTrue(output.contains("Message successfully sent."));
        assertTrue(output.matches("(?s).*Message ID generated: [0-9]{10}.*"));
        assertTrue(output.contains(":0:HITONIGHT"));
        assertTrue(output.contains("Press 0 to delete the message"));
        assertTrue(output.contains("Total messages sent: 1"));
        assertEquals(2, output.split("Enter recipient number:", -1).length - 1);
        assertEquals(1, output.split("Message successfully sent\\.", -1).length - 1);
    }

    @Test
    public void testInvalidRecipientAndLongMessageCanBeCorrected()
    {
        String output = runChat(registration + login + "1\n1\n08966553\n+27718693002\n" + "a".repeat(251) + "\nHi again\n1\n3\n");
        assertTrue(output.contains("Cell phone number is incorrectly formatted"));
        assertTrue(output.contains("Message exceeds 250 characters by 1; please reduce the size."));
        assertTrue(output.contains("Message successfully sent."));
        assertTrue(output.contains("Total messages sent: 1"));
    }

    @Test
    public void testStoredMessagesMenu() throws Exception
    {
        File file = folder.newFile("messages.json");
        Message stored = new Message();
        stored.filename = file.getPath();
        stored.messageID = "0012345678";
        stored.number = 0;
        stored.sender = "Developer";
        stored.recipient = "+27838884567";
        stored.message = "Where are you? You are late! I have asked you to be on time.";
        assertEquals("Message successfully stored.", stored.storeMessage());

        String input = registration + login + "0\n4\n1\n2\n3\n0012345678\n4\n+27838884567\n6\n7\n3\n";
        String output = runChat(input, file.getPath());

        assertTrue(output.contains("Sender: Developer"));
        assertTrue(output.contains("Recipient: +27838884567"));
        assertTrue(output.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(output.contains("Message Hash: 00:0:WHERETIME"));
    }
}
