# Kanban Board

Progetto di Sistemi Distribuiti, A.A. 2020/2021. 

## Introduzione

Lo scopo di questo progetto è di sviluppare una *web-application* che consenta di organizzare il lavoro di un team di sviluppo tenendo traccia dei progressi svolti. Il progetto
è strutturato attraverso una base dati che deve essere memorizzata sul server tramite una delle seguenti possibilità:
* File su memoria secondaria in formato JSON, XML, TOML o YAML
* Un database come ad esempio PostgreSQL, SQLite, Redis, CockroachDB, etc... 

Per descrivere i dati contenuti nella board è necessario definire la struttura di un **tile** e la struttura di una **colonna**. Un *tile* è la più piccola unità che costituisce la kanban board. È formato da:
* Titolo
* Autore
* Contenuto
* Tipo di messaggio (*organizzativo* o *informativo*) 

La board deve consentire di trattare tile con due tipi di contenuto alternativi: *testuale* e *multimediale*. I tile testuali contengono un paragrafo di testo indefinitamente
lungo, mentre i tile multimediali contengono un file immagine. Il file immagine deve essere memorizzato in formato ridimensionato: se il file immagine caricato dall'utente in
fase di creazione del tile ha una risoluzione superiore a 900 × 900, il server deve ridimensionare l'immagine. È necessario che i tile risultino visivamente distinti a seconda
del tipo di messaggio (organizzativo o informativo). Il tipo di messaggio viene selezionato soltanto in fase di creazione del tile.  
Ogni *tile* deve essere contenuto in una colonna. Alla colonna sono associati un titolo e uno stato (*in corso* o *archiviato*). È necessario rappresentare la relazione di 
appartenenza che associa un insieme di tile ad una specifica colonna. La scelta della gestione della relazione all'interno della base dati è lasciata allo studente.  
Si deve quindi implementare un'interfaccia web che permetta di visualizzare le varie colonne con i tile in esse contenuti e uno o più form per la creazione di un nuovo tile o di 
una nuova colonna. Durante la creazione di un tile deve essere indicata una colonna di appartenenza. Il titolo di ogni colonna deve essere univoco mentre più tile possono 
condividere lo stesso titolo. L'interfaccia web deve consentire lo spostamento di un tile da una colonna ad un'altra. Non è richiesto che questo avvenga tramite trascinamento: è 
possibile implementare questa logica attraverso un form di modifica del tile. Un tile può essere aggiornato in una o tutte le sue parti. Una colonna può subire la modifica al 
titolo, a patto che non esistano altre colonne con lo stesso titolo. L'applicazione deve consentire anche l'eliminazione di tile e colonne con stato *in corso*. L'eliminazione di 
una colonna provoca l'eliminazione di tutti i tile associati ad essa. Inoltre, in ogni momento, è possibile cambiare lo stato di ogni colonna. Le colonne archiviate sono 
visualizzate in una pagina diversa da quella in cui sono presenti le colonne il cui stato è *in corso*. Alle colonne archiviate non possono essere aggiunti nuovi tile. Una colonna 
nello stato *archiviato* non può essere cancellata.
