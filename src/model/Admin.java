package model;

public class Admin {
    private int adminId ;
    private String adminName ;
    private String adminEmail;
    private String password ;

    public Admin(int id, String name, String email, String pass) {
        this.adminId = id ;
        this.adminName = name ;
        this.adminEmail = email ;
        this.password = pass ;
    }
    public int getAdminId() {
        return adminId;
    }
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    public String getAdminName() {
        return adminName;
    }
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
    public String getAdminEmail() {
        return adminEmail;
    }
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    

}
