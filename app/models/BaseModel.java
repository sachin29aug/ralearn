package models;

import io.ebean.Model;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class BaseModel extends Model {
   @Id
   public Long id;

   @WhenCreated
   public Date created;

   @WhenModified
   public Date updated;

   public void saveOrUpdate() {
      if(id == null) {
         save();
      } else {
         update();
      }
   }

   // Getters Setters

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getUpdated() {
      return updated;
   }

   public void setUpdated(Date updated) {
      this.updated = updated;
   }
}
