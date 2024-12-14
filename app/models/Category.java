package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Entity
public class Category extends BaseModel {
    public String title;

    public String url;

    public String faIconClass;

    @ManyToOne
    public Category parent;

    public static Finder<Long, Category> find = new Finder<>(Category.class);

    public static Category find(Long id) {
        return find.byId(id);
    }

    public static Category findByUrl(String url) {
        return find.query().where().eq("url", url).findOne();
    }

    public static List<Category> findCategories() {
        return find.query().where().isNotNull("parent").findList();
    }

    public static List<Category> findParentCategories() {
        return find.query().where().isNull("parent").findList();
    }

    public static List<Category> findCategoriesByParent(Long categoryId) {
        return find.query().where().eq("parent.id", categoryId).findList();
    }

    public String title() {
        return this.title;
    }

    public static String getDisplayTitle(String title) {
        if(!title.contains("-")) {
            return StringUtils.capitalize(title);
        } else {
            String[] words = title.split("-");
            return StringUtils.capitalize(words[0]) + " " + StringUtils.capitalize(words[1]);
        }
    }

    // Getters Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFaIconClass() {
        return faIconClass;
    }

    public void setFaIconClass(String faIconClass) {
        this.faIconClass = faIconClass;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
}
