package models;

import io.ebean.Finder;
import jakarta.persistence.*;

@Entity
@Table(name = "gb_book")
public class GBBook extends BaseModel {
    private String gbId;
    private String etag;
    private String contentVersion;

    // Main fields
    private String title;
    private String subTitle;
    private String authors;
    private String mainCategory;
    @Column(length = 500)
    private String categories;
    @Lob
    private String description;
    private double averageRating;
    private int ratingsCount;
    private String maturityRating;

    // Links
    @Column(length = 500)
    private String selfLink;
    @Column(length = 500)
    private String previewLink;
    @Column(length = 500)
    private String thumbnailUrl;
    @Column(length = 500)
    private String smallThumbnailUrl;
    @Column(length = 500)
    private String smallUrl;
    @Column(length = 500)
    private String mediumUrl;
    @Column(length = 500)
    private String largeUrl;
    @Column(length = 500)
    private String extraLargeUrl;
    @Column(length = 500)
    private String infoLink;
    @Column(length = 500)
    private String buyLink;
    @Column(length = 500)
    private String canonicalVolumeLink;

    // Other info
    private String isbn10;
    private String isbn13;
    private String publisher;
    private String publishedDate;
    private int pageCount;
    private int printedPageCount;

    // Format
    private String language;
    private boolean isEbook;
    private boolean pdfIsAvailable;
    private boolean epubIsAvailable;
    @Column(length = 500)
    private String epubAcsTokenLink;
    private boolean readingModeText;
    private boolean readingModeImage;
    private boolean containsEpubBubbles;
    private boolean containsImageBubbles;

    // Access related
    private String saleCountry;
    private String saleability;
    private String viewability;
    private boolean embeddable;
    private boolean publicDomain;
    private String accessViewStatus;

    @OneToOne(mappedBy = "gbBook")
    private Book book;

    public static Finder<Long, GBBook> find = new Finder<>(GBBook.class);

    // Static methods

    public static GBBook findByBookId(Long bookId) {
        return find.query().where().eq("book.id", bookId).findOne();
    }

    public String getGbId() {
        return gbId;
    }

    public void setGbId(String gbId) {
        this.gbId = gbId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getContentVersion() {
        return contentVersion;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getMaturityRating() {
        return maturityRating;
    }

    public void setMaturityRating(String maturityRating) {
        this.maturityRating = maturityRating;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getPreviewLink() {
        return "NO_PAGES".equals(viewability) ? null : previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSmallThumbnailUrl() {
        return smallThumbnailUrl;
    }

    public void setSmallThumbnailUrl(String smallThumbnailUrl) {
        this.smallThumbnailUrl = smallThumbnailUrl;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public String getExtraLargeUrl() {
        return extraLargeUrl;
    }

    public void setExtraLargeUrl(String extraLargeUrl) {
        this.extraLargeUrl = extraLargeUrl;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    public String getCanonicalVolumeLink() {
        return canonicalVolumeLink;
    }

    public void setCanonicalVolumeLink(String canonicalVolumeLink) {
        this.canonicalVolumeLink = canonicalVolumeLink;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPrintedPageCount() {
        return printedPageCount;
    }

    public void setPrintedPageCount(int printedPageCount) {
        this.printedPageCount = printedPageCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isEbook() {
        return isEbook;
    }

    public void setEbook(boolean ebook) {
        isEbook = ebook;
    }

    public boolean isPdfIsAvailable() {
        return pdfIsAvailable;
    }

    public void setPdfIsAvailable(boolean pdfIsAvailable) {
        this.pdfIsAvailable = pdfIsAvailable;
    }

    public boolean isEpubIsAvailable() {
        return epubIsAvailable;
    }

    public void setEpubIsAvailable(boolean epubIsAvailable) {
        this.epubIsAvailable = epubIsAvailable;
    }

    public String getEpubAcsTokenLink() {
        return epubAcsTokenLink;
    }

    public void setEpubAcsTokenLink(String epubAcsTokenLink) {
        this.epubAcsTokenLink = epubAcsTokenLink;
    }

    public boolean isReadingModeText() {
        return readingModeText;
    }

    public void setReadingModeText(boolean readingModeText) {
        this.readingModeText = readingModeText;
    }

    public boolean isReadingModeImage() {
        return readingModeImage;
    }

    public void setReadingModeImage(boolean readingModeImage) {
        this.readingModeImage = readingModeImage;
    }

    public boolean isContainsEpubBubbles() {
        return containsEpubBubbles;
    }

    public void setContainsEpubBubbles(boolean containsEpubBubbles) {
        this.containsEpubBubbles = containsEpubBubbles;
    }

    public boolean isContainsImageBubbles() {
        return containsImageBubbles;
    }

    public void setContainsImageBubbles(boolean containsImageBubbles) {
        this.containsImageBubbles = containsImageBubbles;
    }

    public String getSaleCountry() {
        return saleCountry;
    }

    public void setSaleCountry(String saleCountry) {
        this.saleCountry = saleCountry;
    }

    public String getSaleability() {
        return saleability;
    }

    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    public String getViewability() {
        return viewability;
    }

    public void setViewability(String viewability) {
        this.viewability = viewability;
    }

    public boolean isEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }

    public boolean isPublicDomain() {
        return publicDomain;
    }

    public void setPublicDomain(boolean publicDomain) {
        this.publicDomain = publicDomain;
    }

    public String getAccessViewStatus() {
        return accessViewStatus;
    }

    public void setAccessViewStatus(String accessViewStatus) {
        this.accessViewStatus = accessViewStatus;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
