import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest
{
    @Test
    public void testUsernameCorrectlyFormatted()
    {
        Login login = new Login();
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testUsernameIncorrectlyFormatted()
    {
        Login login = new Login();
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testPasswordMeetsComplexity()
    {
        Login login = new Login();
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity()
    {
        Login login = new Login();
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCellPhoneCorrectlyFormatted()
    {
        Login login = new Login();
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted()
    {
        Login login = new Login();
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testRegisterUserUsernameMessage()
    {
        Login login = new Login();
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                login.registerUser("Kyle", "Brooks", "kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    public void testRegisterUserPasswordMessage()
    {
        Login login = new Login();
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                login.registerUser("Kyle", "Brooks", "kyl_1", "password", "+27838968976"));
    }

    @Test
    public void testRegisterUserCellPhoneMessage()
    {
        Login login = new Login();
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                login.registerUser("Kyle", "Brooks", "kyl_1", "Ch&&sec@ke99!", "08966553"));
    }

    @Test
    public void testRegisterUserSuccess()
    {
        Login login = new Login();
        assertEquals("Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.\nRegistration successful, you can now log in.",
                login.registerUser("Kyle", "Brooks", "kyl_1", "Ch&&sec@ke99!", "+27838968976"));
        assertTrue(login.registerStatus("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    public void testLoginSuccessful()
    {
        Login login = new Login();
        login.registerUser("Kyle", "Brooks", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed()
    {
        Login login = new Login();
        login.registerUser("Kyle", "Brooks", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongpassword"));
    }

    @Test
    public void testReturnLoginStatusSuccess()
    {
        Login login = new Login();
        login.registerUser("Kyle", "Brooks", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Welcome Kyle, Brooks it is great to see you.", login.returnLoginStatus(true));
    }

    @Test
    public void testReturnLoginStatusFailure()
    {
        Login login = new Login();
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(false));
    }
}
