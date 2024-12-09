package models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cpt_book")
public class CPTBook extends BaseModel {
    private String headline;
    private String teaser;
    @Lob
    private String description;
    @Lob
    private String authorBio;
    private String themeConcept;
    private String audience;
    private String styleTone;
    private String actionableIdeas;
    private String usp;
    private String topics;

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

    public String getAuthorBio() {
        return authorBio;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
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
}