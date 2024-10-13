package repository.model;

public class UserDao {

    private long userId;
    private String userName;
    private String phoneNumber;
    private String emailId;

    public UserDao(long userId, String userName, String phoneNumber, String emailId) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailId=emailId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getUserId() {
        return userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
