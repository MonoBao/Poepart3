// Creates, stores, searches, and displays messages.
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Message
{
    // Gson JSON reading and writing follows the Gson User Guide: https://github.com/google/gson/blob/main/UserGuide.md
    String messageID = "";
    String messageHash = "";
    String recipient = "";
    String message = "";
    String sender = "";
    String filename = "messages.json";
    String[] sentMessages = new String[0];
    String[] disregardedMessages = new String[0];
    String[] storedMessages = new String[0];
    String[] messageIDs = new String[0];
    String[] messageHashes = new String[0];
    String[] messageRecipients = new String[0];
    String[] messageContents = new String[0];
    String[] messageSenders = new String[0];
    String[] messageStatuses = new String[0];
    int number = -1;
    int total;

    public void createMessageID()
    {
        Random random = new Random();
        do
        {
            String randomNumber = "" + (1000000000L + random.nextLong(9000000000L));
            messageID = randomNumber.substring(0, 10);
        }
        while (contains(messageIDs, messageID));

        number++;
    }

    public boolean checkMessageID()
    {
        return messageID.matches("[0-9]{1,10}");
    }

    public String checkRecipientCell()
    {
        if (checkRecipientCellNumber())
        {
            return "Cell phone number successfully captured.";
        }
        else
        {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public boolean checkRecipientCellNumber()
    {
        Registration registration = new Registration();
        return registration.checkCellPhoneNumber(recipient) || recipient.matches("0[0-9]{9,10}");
    }

    public boolean checkMessageLength()
    {
        return message.length() <= 250;
    }

    public String messageStatus()
    {
        if (checkMessageLength())
        {
            return "Message ready to send.";
        }
        else
        {
            return "Message exceeds 250 characters by " + (message.length() - 250) + "; please reduce the size.";
        }
    }

    public String createMessageHash()
    {
        String[] words = message.trim().split("\\s+");
        String first = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");
        messageHash = (messageID.substring(0, 2) + ":" + number + ":" + first + last).toUpperCase();
        return messageHash;
    }

    public String SentMessage(String choice)
    {
        if (choice.equals("1"))
        {
            addMessage("Sent");
            sentMessages = addValue(sentMessages, message);
            total++;
            return "Message successfully sent.";
        }
        else if (choice.equals("2"))
        {
            addMessage("Disregarded");
            disregardedMessages = addValue(disregardedMessages, message);
            return "Press 0 to delete the message.";
        }
        else if (choice.equals("3"))
        {
            return storeMessage();
        }
        else
        {
            return "Invalid option";
        }
    }

    public String messageDetails()
    {
        return "Message ID: " + messageID + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient + "\nMessage: " + message;
    }

    public String printMessages()
    {
        String details = "";
        for (int i = 0; i < messageStatuses.length; i++)
        {
            if (messageStatuses[i].equals("Sent"))
            {
                details = details + messageDetails(i) + "\n";
            }
        }
        return details;
    }

    public int returnTotalMessages()
    {
        return total;
    }

    public String storeMessage()
    {
        try
        {
            JsonArray stored = readJsonFile();
            messageHash = createMessageHash();
            JsonObject item = new JsonObject();
            item.addProperty("messageID", messageID);
            item.addProperty("messageHash", messageHash);
            item.addProperty("sender", sender);
            item.addProperty("recipient", recipient);
            item.addProperty("message", message);
            stored.add(item);

            File file = new File(filename);
            try (FileWriter writer = new FileWriter(file))
            {
                new GsonBuilder().setPrettyPrinting().create().toJson(stored, writer);
            }

            addMessage("Stored");
            storedMessages = addValue(storedMessages, message);
            return "Message successfully stored.";
        }
        catch (IOException error)
        {
            return "Message could not be stored.";
        }
    }

    public void loadStoredMessages()
    {
        try
        {
            JsonArray stored = readJsonFile();
            for (int i = 0; i < stored.size(); i++)
            {
                JsonObject item = stored.get(i).getAsJsonObject();
                String id = getValue(item, "messageID");
                if (!contains(messageIDs, id))
                {
                    messageID = id;
                    messageHash = getValue(item, "messageHash");
                    sender = getValue(item, "sender");
                    recipient = getValue(item, "recipient");
                    message = getValue(item, "message");
                    messageIDs = addValue(messageIDs, messageID);
                    messageHashes = addValue(messageHashes, messageHash);
                    messageRecipients = addValue(messageRecipients, recipient);
                    messageContents = addValue(messageContents, message);
                    messageSenders = addValue(messageSenders, sender);
                    messageStatuses = addValue(messageStatuses, "Stored");
                    storedMessages = addValue(storedMessages, message);
                }
            }
        }
        catch (IOException error)
        {
            storedMessages = new String[0];
        }
    }

    public String printStoredSendersAndRecipients()
    {
        String details = "";
        for (int i = 0; i < messageStatuses.length; i++)
        {
            if (messageStatuses[i].equals("Stored"))
            {
                details = details + "Sender: " + messageSenders[i]
                        + "\nRecipient: " + messageRecipients[i] + "\n";
            }
        }
        if (details.equals(""))
        {
            return "No stored messages.";
        }
        return details;
    }

    public String findLongestStoredMessage()
    {
        if (storedMessages.length == 0)
        {
            return "No stored messages.";
        }

        String longest = storedMessages[0];
        for (int i = 1; i < storedMessages.length; i++)
        {
            if (storedMessages[i].length() > longest.length())
            {
                longest = storedMessages[i];
            }
        }
        return longest;
    }

    public String searchForMessageID(String searchID)
    {
        for (int i = 0; i < messageIDs.length; i++)
        {
            if (messageIDs[i].equals(searchID) && !messageStatuses[i].equals("Disregarded"))
            {
                return "Recipient: " + messageRecipients[i] + "\nMessage: " + messageContents[i];
            }
        }
        return "Message ID not found.";
    }

    public String searchForRecipient(String searchRecipient)
    {
        String results = "";
        for (int i = 0; i < messageRecipients.length; i++)
        {
            if (messageRecipients[i].equals(searchRecipient)
                    && !messageStatuses[i].equals("Disregarded"))
            {
                results = results + "Message ID: " + messageIDs[i]
                        + "\nMessage Hash: " + messageHashes[i]
                        + "\nMessage: " + messageContents[i] + "\n";
            }
        }

        if (results.equals(""))
        {
            return "No messages found for this recipient.";
        }
        return results;
    }

    public String deleteMessageHash(String searchHash)
    {
        try
        {
            JsonArray stored = readJsonFile();
            int jsonIndex = -1;
            String deletedMessage = "";

            for (int i = 0; i < stored.size(); i++)
            {
                JsonObject item = stored.get(i).getAsJsonObject();
                if (getValue(item, "messageHash").equals(searchHash))
                {
                    jsonIndex = i;
                    deletedMessage = getValue(item, "message");
                }
            }

            if (jsonIndex == -1)
            {
                return "Message hash not found.";
            }

            stored.remove(jsonIndex);
            File file = new File(filename);
            try (FileWriter writer = new FileWriter(file))
            {
                new GsonBuilder().setPrettyPrinting().create().toJson(stored, writer);
            }

            int storedIndex = 0;
            for (int i = 0; i < messageStatuses.length; i++)
            {
                if (messageStatuses[i].equals("Stored"))
                {
                    if (messageHashes[i].equals(searchHash))
                    {
                        removeMessage(i);
                        storedMessages = removeValue(storedMessages, storedIndex);
                        break;
                    }
                    storedIndex++;
                }
            }
            return "Message: \"" + deletedMessage + "\" successfully deleted.";
        }
        catch (IOException error)
        {
            return "Message could not be deleted.";
        }
    }

    public String printStoredMessageReport()
    {
        String report = "";
        for (int i = 0; i < messageStatuses.length; i++)
        {
            if (messageStatuses[i].equals("Stored"))
            {
                report = report + "Message Hash: " + messageHashes[i]
                        + "\nRecipient: " + messageRecipients[i]
                        + "\nMessage: " + messageContents[i] + "\n";
            }
        }
        if (report.equals(""))
        {
            return "No stored messages.";
        }
        return report;
    }

    public String printMessageReport()
    {
        String report = "";
        for (int i = 0; i < messageStatuses.length; i++)
        {
            if (messageStatuses[i].equals("Sent"))
            {
                report = report + "Message Hash: " + messageHashes[i]
                        + "\nRecipient: " + messageRecipients[i]
                        + "\nMessage: " + messageContents[i] + "\n";
            }
        }
        if (report.equals(""))
        {
            return "No sent messages.";
        }
        return report;
    }

    private void addMessage(String status)
    {
        messageHash = createMessageHash();
        messageIDs = addValue(messageIDs, messageID);
        messageHashes = addValue(messageHashes, messageHash);
        messageRecipients = addValue(messageRecipients, recipient);
        messageContents = addValue(messageContents, message);
        messageSenders = addValue(messageSenders, sender);
        messageStatuses = addValue(messageStatuses, status);
    }

    private void removeMessage(int index)
    {
        messageIDs = removeValue(messageIDs, index);
        messageHashes = removeValue(messageHashes, index);
        messageRecipients = removeValue(messageRecipients, index);
        messageContents = removeValue(messageContents, index);
        messageSenders = removeValue(messageSenders, index);
        messageStatuses = removeValue(messageStatuses, index);
    }

    private String messageDetails(int index)
    {
        return "Message ID: " + messageIDs[index] + "\nMessage Hash: " + messageHashes[index]
                + "\nRecipient: " + messageRecipients[index] + "\nMessage: " + messageContents[index];
    }

    private JsonArray readJsonFile() throws IOException
    {
        File file = new File(filename);
        if (!file.exists())
        {
            return new JsonArray();
        }

        try (FileReader reader = new FileReader(file))
        {
            JsonElement result = JsonParser.parseReader(reader);
            if (result == null || result.isJsonNull())
            {
                return new JsonArray();
            }
            return result.getAsJsonArray();
        }
    }

    private String getValue(JsonObject item, String name)
    {
        if (item.has(name) && !item.get(name).isJsonNull())
        {
            return item.get(name).getAsString();
        }
        return "";
    }

    private String[] addValue(String[] values, String value)
    {
        String[] newValues = new String[values.length + 1];
        for (int i = 0; i < values.length; i++)
        {
            newValues[i] = values[i];
        }
        newValues[values.length] = value;
        return newValues;
    }

    private String[] removeValue(String[] values, int index)
    {
        String[] newValues = new String[values.length - 1];
        int newIndex = 0;
        for (int i = 0; i < values.length; i++)
        {
            if (i != index)
            {
                newValues[newIndex] = values[i];
                newIndex++;
            }
        }
        return newValues;
    }

    private boolean contains(String[] values, String value)
    {
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equals(value))
            {
                return true;
            }
        }
        return false;
    }
}
