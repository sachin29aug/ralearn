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

    $(document.body).on("click", ".cls-google-books-preview-btn", function(e) {
        e.preventDefault();
        let viewer = new google.books.DefaultViewer(document.getElementById("gb-preview"));
        $("#gb-preview").show();
        viewer.load($(this).attr("href"));
    });
});