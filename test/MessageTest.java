import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class MessageTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    Message messages;

    @Before
    public void setUp()
    {
        messages = new Message();
        messages.messageID = "0012345678";
        messages.number = 0;
        messages.recipient = "+27718693002";
        messages.message = "Hi Mike, can you join us for dinner tonight?";
        messages.filename = new File(folder.getRoot(), "messages.json").getPath();
    }

    @Test
    public void testMessageLengthSuccess()
    {
        messages.message = "a".repeat(250);
        assertTrue(messages.checkMessageLength());
        assertEquals("Message ready to send.", messages.messageStatus());
    }

    @Test
    public void testMessageLengthFailure()
    {
        messages.message = "a".repeat(251);
        assertFalse(messages.checkMessageLength());
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.", messages.messageStatus());
        messages.message = "a".repeat(300);
        assertEquals("Message exceeds 250 characters by 50; please reduce the size.", messages.messageStatus());
    }

    @Test
    public void testRecipientSuccess()
    {
        assertEquals("Cell phone number successfully captured.", messages.checkRecipientCell());
        messages.recipient = "08575975889";
        assertEquals("Cell phone number successfully captured.", messages.checkRecipientCell());
    }

    @Test
    public void testRecipientFailure()
    {
        messages.recipient = "08966553";
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", messages.checkRecipientCell());
    }

    @Test
    public void testMessageHashes()
    {
        String[] text = {"Hi Mike, can you join us for dinner tonight?", "Hi Keegan, did you receive the payment?"};
        String[] hashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};
        for (int i = 0; i < text.length; i++)
        {
            messages.message = text[i];
            messages.number = i;
            assertEquals(hashes[i], messages.createMessageHash());
        }
    }

    @Test
    public void testMessageIDValidation()
    {
        assertTrue(messages.checkMessageID());
        messages.messageID = "12345678901";
        assertFalse(messages.checkMessageID());
        messages.messageID = "123456789";
        assertTrue(messages.checkMessageID());
        messages.messageID = "";
        assertFalse(messages.checkMessageID());
        messages.messageID = "abcdefghij";
        assertFalse(messages.checkMessageID());
    }

    @Test
    public void testGeneratedMessageIDs()
    {
        Message generated = new Message();
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            generated.createMessageID();
            assertTrue(generated.checkMessageID());
            assertFalse(ids.contains(generated.messageID));
            assertEquals(i, generated.number);
            ids.add(generated.messageID);
        }
    }

    @Test
    public void testSendMessage()
    {
        assertEquals("Message successfully sent.", messages.SentMessage("1"));
        assertEquals(1, messages.returnTotalMessages());
        assertEquals("Message ID: 0012345678\nMessage Hash: 00:0:HITONIGHT\nRecipient: +27718693002\nMessage: Hi Mike, can you join us for dinner tonight?\n", messages.printMessages());
    }

    @Test
    public void testDisregardMessage()
    {
        assertEquals("Press 0 to delete the message.", messages.SentMessage("2"));
        assertEquals(0, messages.returnTotalMessages());
        assertEquals("", messages.printMessages());
        assertFalse(new File(messages.filename).exists());
    }

    @Test
    public void testStoreMessage() throws Exception
    {
        assertEquals("Message successfully stored.", messages.SentMessage("3"));
        assertEquals(0, messages.returnTotalMessages());
        assertEquals("", messages.printMessages());
        try (FileReader reader = new FileReader(messages.filename))
        {
            JsonArray stored = JsonParser.parseReader(reader).getAsJsonArray();
            assertEquals(1, stored.size());
            assertEquals("0012345678", stored.get(0).getAsJsonObject().get("messageID").getAsString());
            assertEquals("00:0:HITONIGHT", stored.get(0).getAsJsonObject().get("messageHash").getAsString());
            assertEquals(messages.recipient, stored.get(0).getAsJsonObject().get("recipient").getAsString());
            assertEquals(messages.message, stored.get(0).getAsJsonObject().get("message").getAsString());
        }
    }

    @Test
    public void testStorageKeepsEarlierMessages() throws Exception
    {
        assertEquals("Message successfully stored.", messages.storeMessage());
        Message next = new Message();
        next.filename = messages.filename;
        next.createMessageID();
        next.recipient = "+27718693002";
        next.message = "Text with \"quotes\", a slash \\ and a newline\nGoodbye";
        assertEquals("Message successfully stored.", next.storeMessage());
        try (FileReader reader = new FileReader(messages.filename))
        {
            JsonArray stored = JsonParser.parseReader(reader).getAsJsonArray();
            assertEquals(2, stored.size());
            assertEquals(messages.message, stored.get(0).getAsJsonObject().get("message").getAsString());
            assertEquals(next.message, stored.get(1).getAsJsonObject().get("message").getAsString());
        }
    }

    @Test
    public void testTotalOnlyCountsSentMessages()
    {
        messages.SentMessage("1");
        messages.SentMessage("2");
        messages.SentMessage("3");
        assertEquals(1, messages.returnTotalMessages());
        messages.SentMessage("1");
        assertEquals(2, messages.returnTotalMessages());
    }

    @Test
    public void testPartThreeArraysUseTheAssignmentData()
    {
        addTestMessage(messages, 0, "1111111111", "+27834557896", "Did you get the cake?", "1");
        addTestMessage(messages, 1, "2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "3");
        addTestMessage(messages, 2, "3333333333", "+27834484567", "Yohoooo, I am at your gate.", "2");
        addTestMessage(messages, 3, "0838884567", "+27834557896", "It is dinner time !", "1");
        addTestMessage(messages, 4, "5555555555", "+27838884567", "Ok, I am leaving without you.", "3");

        assertArrayEquals(new String[] {"Did you get the cake?", "It is dinner time !"}, messages.sentMessages);
        assertArrayEquals(new String[] {"Yohoooo, I am at your gate."}, messages.disregardedMessages);
        assertArrayEquals(new String[] {"Where are you? You are late! I have asked you to be on time.", "Ok, I am leaving without you."}, messages.storedMessages);
        assertEquals(5, messages.messageIDs.length);
        assertEquals(5, messages.messageHashes.length);
    }

    @Test
    public void testStoredMessagesAreReadFromJson() throws Exception
    {
        messages.sender = "Katso Mosime";
        messages.SentMessage("3");

        Message loaded = new Message();
        loaded.filename = messages.filename;
        loaded.loadStoredMessages();

        assertArrayEquals(new String[] {messages.message}, loaded.storedMessages);
        assertEquals(messages.messageID, loaded.messageIDs[0]);
        assertEquals(messages.createMessageHash(), loaded.messageHashes[0]);
        assertTrue(loaded.printStoredSendersAndRecipients().contains("Sender: Katso Mosime"));
        assertTrue(loaded.printStoredSendersAndRecipients().contains("Recipient: +27718693002"));
    }

    @Test
    public void testLongestStoredMessage()
    {
        addTestMessage(messages, 1, "2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "3");
        addTestMessage(messages, 4, "5555555555", "+27838884567", "Ok, I am leaving without you.", "3");

        assertEquals("Where are you? You are late! I have asked you to be on time.", messages.findLongestStoredMessage());
    }

    @Test
    public void testSearchMessageID()
    {
        addTestMessage(messages, 3, "0838884567", "+27834557896", "It is dinner time !", "1");

        String result = messages.searchForMessageID("0838884567");
        assertTrue(result.contains("Recipient: +27834557896"));
        assertTrue(result.contains("Message: It is dinner time !"));
        assertEquals("Message ID not found.", messages.searchForMessageID("9999999999"));
    }

    @Test
    public void testSearchMessagesByRecipient()
    {
        addTestMessage(messages, 1, "2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "3");
        addTestMessage(messages, 3, "4444444444", "+27834557896", "It is dinner time !", "1");
        addTestMessage(messages, 4, "5555555555", "+27838884567", "Ok, I am leaving without you.", "3");

        String result = messages.searchForRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
        assertFalse(result.contains("It is dinner time !"));
    }

    @Test
    public void testDeleteStoredMessageByHash() throws Exception
    {
        addTestMessage(messages, 1, "2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "3");
        String hash = messages.messageHashes[0];

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.", messages.deleteMessageHash(hash));
        assertEquals(0, messages.storedMessages.length);
        assertEquals(0, messages.messageHashes.length);
        assertEquals("No stored messages.", messages.printStoredMessageReport());
        try (FileReader reader = new FileReader(messages.filename))
        {
            assertEquals(0, JsonParser.parseReader(reader).getAsJsonArray().size());
        }
    }

    @Test
    public void testSentAndStoredReports()
    {
        addTestMessage(messages, 0, "1111111111", "+27834557896", "Did you get the cake?", "1");
        addTestMessage(messages, 1, "2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "3");
        addTestMessage(messages, 3, "0838884567", "+27834557896", "It is dinner time !", "1");
        addTestMessage(messages, 4, "5555555555", "+27838884567", "Ok, I am leaving without you.", "3");

        String sentReport = messages.printMessageReport();
        String storedReport = messages.printStoredMessageReport();
        assertTrue(sentReport.contains("Did you get the cake?"));
        assertTrue(sentReport.contains("It is dinner time !"));
        assertFalse(sentReport.contains("Where are you?"));
        assertTrue(storedReport.contains("Where are you?"));
        assertTrue(storedReport.contains("Ok, I am leaving without you."));
        assertTrue(storedReport.contains("Message Hash:"));
        assertTrue(storedReport.contains("Recipient:"));
    }

    public void addTestMessage(Message target, int number, String id, String recipient, String text, String choice)
    {
        target.number = number;
        target.messageID = id;
        target.sender = "Katso Mosime";
        target.recipient = recipient;
        target.message = text;
        target.SentMessage(choice);
    }
}
