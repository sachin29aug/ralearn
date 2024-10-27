package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Entity
public class Category extends BaseModel {
    public String title;

    public String faIconClass;

    @ManyToOne
    public Category parent;

    @OneToMany(mappedBy = "parent")
    public List<Category> subCategories;

    public Category(String title, String faIconClass, Category parent) {
        this.title = title;
        this.faIconClass = faIconClass;
        this.parent = parent;
    }

    public static Finder<Long, Category> find = new Finder(Category.class);

    public static Category find(String id) {
        return find.byId(Long.valueOf(id));
    }

    public static List<Category> findParentCategories() {
        return find.query().where().isNull("parent").findList();
    }

    public static List<Category> findSubCategoriesByParentCategory(Long parentCategoryId) {
        return find.query().where().eq("parent.id", parentCategoryId).findList();
    }

    public static Map<String, List<String>> getCategoriesMap() {
        Map<String, List<String>> categoriesMap = new LinkedHashMap<>();
        categoriesMap.put("Personal Development", Arrays.asList("self-help", "productivity", "communication-skills", "creativity", "education", "biography", "philosophy"));
        categoriesMap.put("Mind & Spirit", Arrays.asList("psychology", "spirituality", "mindfulness"));
        categoriesMap.put("Business & Economics", Arrays.asList("business", "economics", "leadership", "entrepreneurship", "marketing"));
        categoriesMap.put("Family & Lifestyle", Arrays.asList("childrens", "parenting", "travel"));
        categoriesMap.put("Science & Environment", Arrays.asList("science", "environment", "gardening"));
        categoriesMap.put("Arts & Humanities", Arrays.asList("art", "design", "architecture", "folklore", "history", "politics", "law"));
        return categoriesMap;
    }

    public String title() {
        return this.title;
    }

    public String getDisplayTitle() {
        if(!this.title.contains("-")) {
            return StringUtils.capitalize(this.title);
        } else {
            String[] words = this.title.split("-");
            return StringUtils.capitalize(words[0]) + " " + StringUtils.capitalize(words[1]);
        }
    }
}
