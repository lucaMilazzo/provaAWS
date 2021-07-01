function getOngoingColumns() {

    $.ajax({
        type: "GET",
        url: "/api/getOngoingColumns",
        success: function(data) {
            let cards = $("#cards");
            let row = document.createElement("div");
            row.className = "row";
            cards.append(row);
            for (let column of data) {
                let col = document.createElement("div")
                col.className = "col-md";
                row.appendChild(col);
                let div = document.createElement("div");
                div.style = "padding-top: 10px";
                col.appendChild(div);
                let deck = document.createElement("div");
                deck.className = "container";
                col.appendChild(deck);
                createHeader(deck, column.title);
                for (let tile of column.tiles) {
                    createCard(deck, tile);
                }
                let option = document.createElement("option");
                option.innerText = column.title;
                $("#moveTileColumn").append(option);
            }
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}

function showErrorModal(text) {

    $("#errorModalBody").text(text);
    $("#errorModal").modal("show")
}

function createHeader(deck, title) {

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
    editColumnButton.textContent = "Edit column";
    editColumnButton.onclick = () => {
        $("#editColumnOldTitle").val(title);
        $("#editColumnNewTitle").val(title);
        $("#editColumnModal").modal("show");
    }
    cardBody.appendChild(editColumnButton);
    let archiveColumnButton = document.createElement("button");
    archiveColumnButton.className = "btn btn-secondary mr-2 mb-2";
    archiveColumnButton.textContent = "Archive column";
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

function createCard(deck, tile) {

    let card = document.createElement("div");
    card.className = "card text-white";
    deck.appendChild(card);
    let header = document.createElement("div");
    header.className = "card-header";
    if (tile.content_type === 'I') {
        card.className += " bg-warning";
        header.innerText = "Information";
    } else if (tile.content_type === 'O') {
        card.className += " bg-success";
        header.innerText = "Organization";
    }
    card.appendChild(header);
    let cardBody = document.createElement("div");
    cardBody.className = "card-body";
    card.appendChild(cardBody);
    let cardTitle = document.createElement("h5");
    cardTitle.innerText = tile.title;
    cardTitle.className = "card-title";
    cardBody.appendChild(cardTitle);
    let cardAuthor = document.createElement("p")
    cardAuthor.className = "card-text";
    cardAuthor.innerText = "Author: " + tile.author.username;
    cardBody.appendChild(cardAuthor);
    if (tile.tile_type === 'T') {
        let cardContent = document.createElement("p");
        cardContent.innerText = tile.content;
        cardContent.className = "card-text";
        cardBody.appendChild(cardContent);
    } else if (tile.tile_type === 'I') {
        let cardContent = document.createElement("img");
        cardContent.src = tile.content;
        cardContent.className = "mb-2";
        cardBody.appendChild(cardContent);
        let br = document.createElement("br");
        cardBody.appendChild(br);
    }
    let moveTileButton = document.createElement("button");
    moveTileButton.className = "btn btn-primary mr-2 mb-2";
    moveTileButton.textContent = "Move tile";
    moveTileButton.onclick = () => {
        $("#moveTileTitle").val(tile.title);
        $("#moveTileId").val(tile.id);
        $.ajax({
            type: "GET",
            url: "/api/getTile/" + tile.id,
            success: function(data) {
                for (let option of $("#moveTileColumn").children()) {
                    if (option.innerText === data.column.title)
                        option.selected = true;
                }
                $("#moveTileModal").modal("show");
            },
            error: function(err) {
                showErrorModal(err.responseText);
            }
        });
    }
    cardBody.appendChild(moveTileButton);
    let editTileButton = document.createElement("button");
    editTileButton.className = "btn btn-info mr-2 mb-2";
    editTileButton.textContent = "Edit tile";
    editTileButton.onclick = () => {
        $("#editTileId").val(tile.id);
        $("#editTileOldTitle").val(tile.title);
        $("#editTileNewTitle").val(tile.title);
        if (tile.tile_type === 'T') {
            $("#editTileText").val(tile.content);
        }
        $.ajax({
            type: "GET",
            url: "/api/getTile/" + tile.id,
            success: function(data) {
                $("#editTileContent").val(data.content);
                if (data.content_type === 'O')
                    $("#editTileOrganizational").click()
                else if (data.content_type === 'I')
                    $("#editTileInformational").click()
                $("#editTileModal").modal("show");
            },
            error: function(err) {
                showErrorModal(err.responseText);
            }
        });
    }
    cardBody.appendChild(editTileButton);
    let deleteTileButton = document.createElement("button");
    deleteTileButton.className = "btn btn-danger mr-2 mb-2";
    deleteTileButton.textContent = "Delete tile";
    deleteTileButton.onclick = () => deleteTile(tile.id);
    cardBody.appendChild(deleteTileButton);
    let br = document.createElement("br");
    deck.appendChild(br);
}

function signout() {

    document.cookie = "username = ; expires = Thu, 01 Jan 1970 00:00:00 GMT";
    window.location.href = "/login";
}

function toggleTileContent(radio, text, image) {
    if ($(radio).val() === 'T') {
        $(text).prop("readonly", false);
        $(image).prop("disabled", true);
    } else {
        $(text).prop("readonly", true);
        $(image).prop("disabled", false);
    }
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
    let data;
    let tileTitle = $("#addTileTitle").val();
    let tileType = $("#addTileContentRadio input:radio:checked").val();
    let tileContentType = $("#addTileContentType input:radio:checked").val();
    let tileColumn = $("#addTileColumn").val();
    let tileAuthor = $("#addTileAuthor").val();
    let tileContent, url = "/api/", contentType, processData;
    if (tileType === 'T') {
        tileContent = $("#addTileText").val();
        url += "addTextTile";
        contentType = "application/x-www-form-urlencoded";
        processData = true;
        data = "title=" + tileTitle + "&content=" + tileContent + "&content_type=" + tileContentType
            + "&column_title=" + tileColumn + "&author=" + tileAuthor + "&tile_type=" + tileType;
    }
    else {
        tileContent = $("#addTileImage").prop("files")[0];
        url += "addImageTile";
        contentType = false;
        processData = false;
        data = new FormData()
        data.append("title", tileTitle);
        data.append("content", tileContent);
        data.append("content_type", tileContentType);
        data.append("column_title", tileColumn);
        data.append("author", tileAuthor);
        data.append("tile_type", tileType);
    }
    if (!tileTitle || !tileContent) {
        showErrorModal("Please fill out the form.");
        return;
    }
    if (tileType === 'I') {
        let extension = tileContent.name.split(".")[1];
        if (extension !== "png" && extension  !== "jpg" && extension  !== "jpeg" && extension !== "bmp"
            && extension !== "gif" && extension !== "tif") {
            showErrorModal("Unsupported file format.");
            return;
        }
    }
    $.ajax({
        type: "POST",
        url: url,
        contentType: contentType,
        data: data,
        dataType: "html",
        processData: processData,
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
    let tileId = $("#moveTileId").val();
    let tileColumnTitle = $("#moveTileColumn").val();
    $.ajax({
        type: "PATCH",
        url: "/api/moveTile",
        data: "tile_id=" + tileId + "&column_title=" + tileColumnTitle,
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
    let data;
    let tileId = $("#editTileId").val();
    let tileNewTitle = $("#editTileNewTitle").val();
    let tileType = $("#editTileContentRadio input:radio:checked").val();
    let tileContent, url = "/api/", contentType, processData;
    if (tileType === 'T') {
        tileContent = $("#editTileText").val();
        url += "editTextTile";
        contentType = "application/x-www-form-urlencoded";
        processData = true;
        data = "tile_id=" + tileId + "&new_title=" + tileNewTitle + "&content=" + tileContent;
    }
    else {
        tileContent = $("#editTileImage").prop("files")[0];
        url += "editImageTile";
        contentType = false;
        processData = false;
        data = new FormData()
        data.append("tile_id", tileId);
        data.append("new_title", tileNewTitle);
        data.append("content", tileContent);
    }
    if (tileType === 'I' && tileContent) {
        let extension = tileContent.name.split(".")[1];
        if (extension !== "png" && extension  !== "jpg" && extension  !== "jpeg" && extension !== "bmp"
            && extension !== "gif" && extension !== "tif") {
            showErrorModal("Unsupported file format.");
            return;
        }
    }
    $.ajax({
        type: "PATCH",
        url: url,
        contentType: contentType,
        data: data,
        dataType: "html",
        processData: processData,
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
        data: "tile_id=" + title,
        dataType: "html",
        success: function() {
            window.location.href = "/";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}