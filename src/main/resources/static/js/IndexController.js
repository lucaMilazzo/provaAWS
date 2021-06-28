function showErrorModal(text) {

    $("#errorModalBody").text(text);
    $("#errorModal").modal("show")
}

function createHeader(deck, title, column_status) {

    let card = document.createElement("div");
    card.className = "card border-0";
    deck.appendChild(card);
    let cardBody = document.createElement("div");
    cardBody.className = "card-body";
    card.appendChild(cardBody);
    let cardTitle = document.createElement("h5");
    cardTitle.innerText = title;
    cardTitle.className = "card-title";
    cardBody.appendChild(cardTitle);
    let addTileButton = document.createElement("button");
    addTileButton.className = "btn btn-primary mr-2 mb-2";
    addTileButton.textContent = "Add tile";
    addTileButton.onclick = () => {
        $("#addTileColumn").val(title);
        $("#addTileAuthor").val(document.cookie.split("=")[1]);
        $("#addTileModal").modal("show");
    }
    cardBody.appendChild(addTileButton);
    let editColumnButton = document.createElement("button");
    editColumnButton.className = "btn btn-info mr-2 mb-2";
    editColumnButton.textContent = "Edit tile";
    editColumnButton.onclick = () => {
        $("#editColumnOldTitle").val(title);
        $("#editColumnNewTitle").val(title);
        $("#editColumnModal").modal("show");
    }
    cardBody.appendChild(editColumnButton);
    let archiveColumnButton = document.createElement("button");
    if (column_status === 'A') {
        card.className = " text-white bg-secondary";
        archiveColumnButton.className = "btn btn-success mr-2 mb-2";
        archiveColumnButton.textContent = "Unarchive column";
    } else {
        archiveColumnButton.className = "btn btn-secondary mr-2 mb-2";
        archiveColumnButton.textContent = "Archive column";
    }
    archiveColumnButton.onclick = () => changeColumnStatus(title);
    cardBody.appendChild(archiveColumnButton);
    let deleteColumnButton = document.createElement("button");
    deleteColumnButton.className = "btn btn-danger mr-2 mb-2";
    deleteColumnButton.textContent = "Delete column";
    deleteColumnButton.onclick = () => deleteColumn(title);
    cardBody.appendChild(deleteColumnButton);
    let br = document.createElement("br");
    deck.appendChild(br);
}

function createCard(deck, title, content, column_status) {

    let card = document.createElement("div");
    card.className = "card";
    if (column_status === 'A')
        card.className = " text-white bg-secondary";
    deck.appendChild(card);
    let cardBody = document.createElement("div");
    cardBody.className = "card-body";
    card.appendChild(cardBody);
    let cardTitle = document.createElement("h5");
    cardTitle.innerText = title;
    cardTitle.className = "card-title";
    cardBody.appendChild(cardTitle);
    let cardText = document.createElement("p");
    cardText.innerText = content;
    cardText.className = "card-text";
    cardBody.appendChild(cardText);
    let moveTileButton = document.createElement("button");
    moveTileButton.className = "btn btn-primary mr-2 mb-2";
    moveTileButton.textContent = "Move tile";
    moveTileButton.onclick = () => {
        $("#moveTileTitle").val(title);
        $.ajax({
            type: "GET",
            url: "/api/getTile/" + title,
            success: function(data) {
                for (let option of $("#moveTileColumn").children()) {
                    if (option.innerText === data.column.title)
                        option.selected = true;
                }
                $("#moveTileModal").modal("show");
            },
            error: function(err) {
                showModal(err.responseText);
            }
        });
    }
    cardBody.appendChild(moveTileButton);
    let editTileButton = document.createElement("button");
    editTileButton.className = "btn btn-info mr-2 mb-2";
    editTileButton.textContent = "Edit tile";
    editTileButton.onclick = () => {
        $("#editTileOldTitle").val(title);
        $("#editTileNewTitle").val(title);
        $.ajax({
            type: "GET",
            url: "/api/getTile/" + title,
            success: function(data) {
                $("#editTileContent").val(data.content);
                if (data.content_type === 'O')
                    $("#editTileOrganizational").click()
                else if (data.content_type === 'I')
                    $("#editTileInformational").click()
                $("#editTileModal").modal("show");
            },
            error: function(err) {
                showModal(err.responseText);
            }
        });
    }
    cardBody.appendChild(editTileButton);
    let deleteTileButton = document.createElement("button");
    deleteTileButton.className = "btn btn-danger mr-2 mb-2";
    deleteTileButton.textContent = "Delete tile";
    deleteTileButton.onclick = () => deleteTile(title);
    cardBody.appendChild(deleteTileButton);
    let br = document.createElement("br");
    deck.appendChild(br);
}

function addColumn() {

    $("#addColumnModal").modal("hide");
    let columnTitle = $("#addColumnTitle").val();
    if (!columnTitle) {
        showErrorModal("Please insert a title.");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/api/addColumn",
        data: "title=" + columnTitle,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function changeColumnStatus(title) {

    $.ajax({
        type: "PATCH",
        url: "/api/changeColumnStatus",
        data: "title=" + title,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function editColumn() {

    $("#editColumnModal").modal("hide");
    let oldColumnTitle = $("#editColumnOldTitle").val();
    let newColumnTitle = $("#editColumnNewTitle").val();
    if (!newColumnTitle) {
        showErrorModal("Please insert a title.");
        return;
    }
    $.ajax({
        type: "PATCH",
        url: "/api/editColumn",
        data: "old_title=" + oldColumnTitle + "&new_title=" + newColumnTitle,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function deleteColumn(title) {

    $.ajax({
        type: "DELETE",
        url: "/api/deleteColumn",
        data: "title=" + title,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function addTile() {

    $("#addTileModal").modal("hide");
    let tileTitle = $("#addTileTitle").val();
    let tileContent = $("#addTileContent").val();
    let tileContentType = $("#addTileContentType input:radio:checked").val();
    let tileColumn = $("#addTileColumn").val();
    let tileAuthor = $("#addTileAuthor").val();
    $.ajax({
        type: "POST",
        url: "/api/addTile",
        data: "title=" + tileTitle + "&content=" + tileContent + "&content_type=" + tileContentType
            + "&column_title=" + tileColumn + "&author=" + tileAuthor,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function moveTile() {

    $("#moveTileModal").modal("hide");
    let tileTitle = $("#moveTileTitle").val();
    let tileColumnTitle = $("#moveTileColumn").val();
    $.ajax({
        type: "PATCH",
        url: "/api/moveTile",
        data: "tile_title=" + tileTitle + "&column_title=" + tileColumnTitle,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function editTile() {

    $("#editTileModal").modal("hide");
    let tileOldTitle = $("#editTileOldTitle").val();
    let tileNewTitle = $("#editTileNewTitle").val();
    let tileContent = $("#editTileContent").val();
    let tileContentType = $("#editTileContentType input:radio:checked").val();
    $.ajax({
        type: "PATCH",
        url: "/api/editTile",
        data: "old_title=" + tileOldTitle + "&new_title=" + tileNewTitle + "&content=" + tileContent
            + "&content_type=" + tileContentType,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function deleteTile(title) {

    $.ajax({
        type: "DELETE",
        url: "/api/deleteTile",
        data: "title=" + title,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}