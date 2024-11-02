$(document).ready(function() {
    $(document.body).on("click", "#id-card2-shuffle-btn", function(e) {
        e.preventDefault();
        card2ShufflePost($(this).data("user-book-id"));
    });

    $(document.body).on("click", ".cls-favorite-btn", function(e) {
        e.preventDefault();
        favoritePost($(this).attr("id"), $(this).data("user-book-id"));
    });
});