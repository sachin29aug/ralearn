package models;

import jakarta.persistence.*;

@Entity
@Table(name = "cpt_book")
public class CPTBook extends BaseModel {
    private String headline;
    private String teaser;
    @Lob
    private String description;
    private String themeConcept;
    private String keyTakeaways;
    private String actionableIdeas;
    private String audience;
    private String styleTone;
    private String usp;
    private String topics;
    private String setting;

    @OneToOne(mappedBy = "cptBook")
    private Book book;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThemeConcept() {
        return themeConcept;
    }

    public void setThemeConcept(String themeConcept) {
        this.themeConcept = themeConcept;
    }

    public String getStyleTone() {
        return styleTone;
    }

    public void setStyleTone(String styleTone) {
        this.styleTone = styleTone;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getActionableIdeas() {
        return actionableIdeas;
    }

    public void setActionableIdeas(String actionableIdeas) {
        this.actionableIdeas = actionableIdeas;
    }

    public String getUsp() {
        return usp;
    }

    public void setUsp(String usp) {
        this.usp = usp;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getKeyTakeaways() {
        return keyTakeaways;
    }

    public void setKeyTakeaways(String keyTakeaways) {
        this.keyTakeaways = keyTakeaways;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}