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
    private String concept; // Key themes and takeaways
    private String audience;
    private String toneStyle;
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

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getToneStyle() {
        return toneStyle;
    }

    public void setToneStyle(String toneStyle) {
        this.toneStyle = toneStyle;
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