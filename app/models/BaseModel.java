package models;

import io.ebean.Model;

import io.ebean.annotation.WhenModified;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.util.Date;

@MappedSuperclass
public class BaseModel extends Model {
   @Id
   public Long id;

   public Date created;

   //@WhenModified
   //private String lastModified;

   @PrePersist
   public void prePersist() throws IllegalAccessException {
      created = new Date();
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }
}
