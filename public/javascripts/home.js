$(document).ready(function() {
    $(document.body).on("click", "#id-card2-shuffle-btn", function(e) {
        e.preventDefault();
        card2ShufflePost($(this).data("user-book-id"));
    });

    $(document.body).on("click", ".cls-favorite-btn", function(e) {
        e.preventDefault();
        favoritePost($(this).attr("id"), $(this).data("book-id"));
    });

    // Consider moving this to a generic class later
    $(document.body).on("click", ".cls-more-options-modal-btn", function(e) {
        e.preventDefault();
        let moreOptionsModal = new bootstrap.Modal($(".more-options-modal"));
        $("#id-good-reads-element").attr("href", $(this).data("good-reads-url"));
        $("#id-amazon-element").attr("href", $(this).data("amazon-url"));
        $("#id-google-books-element").attr("href", $(this).data("google-books-url"));
        $("#id-youtube-element").attr("href", $(this).data("youtube-url"));
        moreOptionsModal.show();
    });

    $(document.body).on("click", ".cls-book-details-btn", function(e) {
        e.preventDefault();
        bookDetailsGet($(this).data("book-id"));
    });

    $(document.body).on("click", "#id-favorite-list-btn", function(e) {
        e.preventDefault();
        favoriteListsGet();
    });

    $(document.body).on("click", "#id-recent-list-btn", function(e) {
        e.preventDefault();
        recentListsGet();
    });

    $(document.body).on("click", ".cls-subcategory-list-btn", function(e) {
        e.preventDefault();
        subcategoryListsGet($(this).data("subcategory-title"));
    });
});