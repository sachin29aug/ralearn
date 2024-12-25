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
        $("#gb-preview").show();
        let viewer = new google.books.DefaultViewer(document.getElementById("gb-viewer"));
        $(".more-options-modal").modal('hide');
        $(window).scrollTop(0);
        viewer.load($(this).attr("href"));
    });

    $(document.body).on("click", "#close-preview", function () {
        $("#gb-preview").hide();
    });
});

function hideBSBackDrop() {
    $('.modal-backdrop').addClass('d-none');
    $('.modal').hide();
    $('body').css('overflow', 'auto');
}

function displayErrorMessage(msg) {
    $(".error").text(msg);
    $(".error").show();
}

function displaySuccessMessage(msg) {
    $(".success-message").text(msg);
    $(".success").show();
}

function closeSuccessMessage(msg) {
    $(".success-message").text('');
    $(".success").hide();
}

function isValidEmail(email) {
    let emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
}