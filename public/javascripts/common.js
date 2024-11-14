$(document).ready(function() {
    $(document.body).on("click", ".cls-share-btn", function(e) {
        e.preventDefault();
        let shareTitle = $(this).data("share-title");
        let shareUrl = $(this).data("share-url");
        if (navigator.share) {
            navigator.share({
                title: shareTitle,
                text: "RaLearn helps you discover books that you might have not have discovered yourself. Let's learn: ",
                url: shareUrl,
            })
        }
    });

    $(document.body).on("click", ".cls-gb-preview-btn", function(e) {
        e.preventDefault();
        let isbn = $(this).data('book-isbn');
    });
});