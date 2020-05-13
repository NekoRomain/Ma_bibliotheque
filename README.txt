Application de gestion d'une collection de livres.
Créateur : WANGON Romain

Fonction de l'application:
    L'application permet d'ajouter des livres dans une base de données interne et de modifier les livres existants dans cette dite base de données.
    Dans l'ajout du livre par ISBN ou SCAN, l'image est téléchargée sur le téléphone quand le livre est ajouté pour pouvoir consulter sa collection, même hors ligne avec les images.
    Ajouter un livre:
        Il y a 3 façons d'ajouter un livre (dont 2 façons utilisant l'API https://openlibrary.org/dev/docs/api/books)
        Champs ISBN + Rechercher
                Rentrer l'ISBN du livre. (10 ou 13) puis cliquer sur Rechercher pour envoyer une requête à l'API
        Scan:
            Scanner le code-barre d'un livre (pour en extraire l'ISBN) et envoyer une requête à l'API
        Si le livre recherché n'est pas dans la BDD de Openlibrary, on a 
        Ajouter un livre:
            On peut ajouter un livre à la main

    Ma collection:
        Dans la collection, on peut rechercher un livre avec son titre avec la barre de recherche du haut. 
        En cliquant sur un livre, on voit ses informations. 
        On peut modifier le livre (Modifier) ou alors on peut supprimer le livre (Supprimer).

    Une vidéo montrant l'application en fonctionnement est disponible ici: https://youtu.be/GOTwnzcH1Tc

    (Bugs)
        - Photo se tournant à 90° (ne se produit qu'avec des images personnelles)
        - Scan du code-barre qui ne fonctionne pas quelques fois (sûrement lié au fait que l'on scan tous les types de codes possibles (code-barre, QR code, etc.)).

    (Améliorations possibles)
        - Récupération d'une base de données déjà existante pour voir des collections d'autres personnes.
        - Recherche par Auteur, Editeur dans la base. (Impossible à faire avec l'ajout car l'API ne le permet pas)