// Stores account details and checks registration information.
public class Registration
{
    String FirstName;
    String LastName;
    String Username;
    String Password;
    String CellPhoneNumber;

    public boolean checkUserName(String Username)
    {
        return Username.contains("_") && Username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String Password)
    {
        return Password.length() >= 8 &&
                Password.matches(".*[A-Z].*") &&
                Password.matches(".*[a-z].*") &&
                Password.matches(".*\\d.*") &&
                Password.matches(".*[^a-zA-Z0-9].*");
    }

    public boolean checkCellPhoneNumber(String CellPhoneNumber)
    {
        // Regex source: Oracle Java Pattern API https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html
        return CellPhoneNumber.matches("\\+27[0-9]{9}");
    }

    public String registerUser(String fname, String lname, String username, String password, String cellphone)
    {
        if (!checkUserName(username))
        {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        else if (!checkPasswordComplexity(password))
        {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        else if (!checkCellPhoneNumber(cellphone))
        {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }
        else
        {
            FirstName = fname;
            LastName = lname;
            Username = username;
            Password = password;
            CellPhoneNumber = cellphone;
            return "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.\nRegistration successful, you can now log in.";
        }
    }

    public boolean registerStatus(String username, String password, String cellphone)
    {
        return checkUserName(username) &&
                checkPasswordComplexity(password) &&
                checkCellPhoneNumber(cellphone);
    }
}
