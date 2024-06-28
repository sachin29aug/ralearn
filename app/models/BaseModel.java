package models;

import io.ebean.Model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.util.Date;

@MappedSuperclass
public class BaseModel extends Model {
   @Id
   private Long id;

   public Date created;

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
