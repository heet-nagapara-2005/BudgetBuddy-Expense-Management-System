package model;

import java.sql.Timestamp;
public class Category {
    private int id ; 
    private int u_id;
    private String name;
    private Timestamp createdDate;

      public Category(int id, int u_id, String name,Timestamp createdDate){
        this.id = id;
        this.u_id = u_id;
        this.name = name;
        this.createdDate = createdDate;
      }

      public void setId(int id){
        this.id = id;
      }
      public void setName(String name){
        this.name = name;
      }
      public int getId(){
        return id;
      }
      public String getName(){
        return name;

      }
      public int getU_id(){
        return u_id;
      }
      public void setU_id(int u_id){
        this.u_id = u_id;
      }
      public Timestamp getCreatedDate(){
        return createdDate;
      }
      public void setCreatedDate(Timestamp createdDate){
        this.createdDate = createdDate;
      }
    }