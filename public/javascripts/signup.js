$(document).ready(function() {
    var selectedPillCount = 0;
    $(document.body).on("click", ".category-pill", function(e) {
        e.preventDefault();
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            selectedPillCount++;
        } else {
            $(this).addClass("selected");
            selectedPillCount--;
        }

        if(selectedPillCount == 0) {
            $(".signup--continue-btn").addClass("disabled");
        } else {
            $(".signup--continue-btn").removeClass("disabled");
        }
    });

    $(document.body).on("click", "#categories-page .signup--continue-btn", function(e) {
        e.preventDefault();
        $("#categories-page").hide();
        $("#subcategories-page").show();
        $(".category-pills .category-pill.selected").each(function() {
            let subcategoriesDivId = $(this).data("subcategories-div-id");
            $("#" + subcategoriesDivId).show();
        });
    });
});