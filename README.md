# Clone de egrep avec support partiel des ERE.

## Exécuter le projet
Le jar remplace le Makefile!

Dans la racine du projet il y a l'exécutable `projet1.jar`, la commande d'exécution est la suivante:
> `java -jar projet1.jar [methode]  [pattern]  [file]`

- [methode] : String of ["automate" ou "kmp"]
- [pattern] : String
- [file] : String


### Exemple: 
Pour chercher avec la méthode d'ahu-ullman : 
> `java -jar projet1.jar "automate" "S(a|r|g)+on" Backend/resources/texts/56667-0.txt`

Pour chercher avec la méthode KMP : 
> `java -jar projet1.jar "kmp" "about" Backend/resources/texts/56667-0.txt`

## Lancer les tests de performance:

Pour lancer les tests, il faut exécuter la classe `EtudeExp.java` qui se trouve dans le chemin suivant: `Backend/etudeExp/EtudeExp.java`


## Visualiser les tests de performance:

Après avoir lancer les tests de la classe `EtudeExp.java` il faut exécuter le fichier `results.ipynb` qui se trouve dans le même dossier.


