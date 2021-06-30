function getArchivedColumns() {

    $.ajax({
        type: "GET",
        url: "/api/getArchivedColumns",
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
                    createCard(deck, tile, column.status);
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
    let archiveColumnButton = document.createElement("button");
    // card.className = " text-white";
    archiveColumnButton.className = "btn btn-success mr-2 mb-2";
    archiveColumnButton.textContent = "Unarchive column";
    archiveColumnButton.onclick = () => changeColumnStatus(title);
    cardBody.appendChild(archiveColumnButton);
    let br = document.createElement("br");
    deck.appendChild(br);
}

function createCard(deck, tile, column_status) {

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
    let br = document.createElement("br");
    deck.appendChild(br);
}

function changeColumnStatus(title) {

    $.ajax({
        type: "PATCH",
        url: "/api/changeColumnStatus",
        data: "title=" + title,
        dataType: "html",
        success: function() {
            window.location.href = "/archivedcolumns";
        },
        error: function(err) {
            showErrorModal(err.responseText);
        }
    });
}