package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private Timestamp createdDate;

   public User(int i, String n, String e, String p,Timestamp d) {
        id = i;
        name = n;
        email = e;
        password = p;
        createdDate = d;
      }

    public void setId(int i)
    {
        this.id = i;
    }
    public int getId()
    {
        return id;
    }
    public void setName(String s)
    {
        this.name = s;
    }       

    public String getName() {
        return name;
    }
   public String getEmail() {
        return email;
    }
    public void setEmail(String e) {
        this.email = e;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String p) {
        this.password = p ;   
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Timestamp date) {
        this.createdDate = date;
    }
    

}
