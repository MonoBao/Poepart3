// Checks login details and returns the login message.
public class Login extends Registration
{
    public boolean loginUser(String username, String password)
    {
        if (Username != null && Password != null && Username.equals(username) && Password.equals(password))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String returnLoginStatus(boolean LoginStatus)
    {
        if (LoginStatus)
        {
            return "Welcome " + FirstName + ", " + LastName + " it is great to see you.";
        }
        else
        {
            return "Username or password incorrect, please try again.";
        }
    }
}
