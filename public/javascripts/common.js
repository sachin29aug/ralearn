$(document).ready(function() {
    // Feedback related

    $(document.body).on("input", "#id-feedback-text-area", function(e) {
        if($(this).val().trim() === '') {
            $("#id-submit-feedback-btn").addClass("disabled");
        } else {
            $("#id-submit-feedback-btn").removeClass("disabled");
        }
    });

    $(document.body).on("click", "#id-submit-feedback-btn", function(e) {
        e.preventDefault();
        feedbackPost($('#id-feedback-text-area').val().trim());
    });

    // Discover related

    $(document.body).on("click", ".discover--category-block", function(e) {
        e.preventDefault();
        subcategoriesDivId = $(this).data("subcategories-div-id");
        $(".discover--category-block").hide();
        $("#" + subcategoriesDivId).show();
    });
});