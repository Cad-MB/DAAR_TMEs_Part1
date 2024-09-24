import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

// Classe représentant un Automate Fini Non Déterministe (AFN)
public class AutomateFiniNonDeterministe {
    // 256 caractères ASCII (pour les transitions)
    private final static int SYMBOLES_ASCII = 256;

    // Etat root (de départ) de l'automate
    private final Etat etatInitial;

    // Etat d'acceptation (fin) de l'automate
    private final Etat etatAcceptant;

    // Constructeur de l'AFN avec état de départ et état d'acceptation
    public AutomateFiniNonDeterministe(Etat etatInitial, Etat etatAcceptant) {
        this.etatInitial = etatInitial;
        this.etatAcceptant = etatAcceptant;
    }

    // Classe interne représentant un état dans l'AFN
    public static class Etat {
        // Compteur statique pour donner un ID unique à chaque état
        public static int compteur = 0;

        // ID unique pour cet état
        private final int id;

        // Les transitions avec des symboles d'entrée vers d'autres états
        private final Map<Integer, Set<Etat>> transitions;

        // Les transitions epsilon (sans symbole d'entrée)
        private final Set<Etat> transitionsEpsilon;

        // Constructeur d'un état
        public Etat() {
            id = compteur++; // Attribue un identifiant unique à l'état
            transitions = new HashMap<>();
            transitionsEpsilon = new HashSet<>();
        }

        // Ajouter une transition avec un symbole d'entrée
        public void ajouterTransition(int symbole, Etat suivant) {
            Set<Etat> etats = transitions.computeIfAbsent(symbole, k -> new HashSet<>());
            etats.add(suivant);
        }

        // Ajouter une transition epsilon (sans symbole d'entrée)
        public void ajouterTransition(Etat suivant) {
            transitionsEpsilon.add(suivant);
        }

        // Obtenir les états suivants pour un symbole d'entrée donné
        public Set<Etat> obtenirTransition(int symbole) {
            return transitions.get(symbole);
        }

        // Méthode pour imprimer les transitions de l'état (récursive pour suivre toutes
        // les transitions)
        public String imprimer(HashSet<Etat> visites) {
            if (!visites.add(this)) // Si l'état a déjà été visité, arrêter la récursion
                return null;

            StringBuilder sb = new StringBuilder();
            // Imprimer les transitions avec des symboles
            for (Map.Entry<Integer, Set<Etat>> entree : transitions.entrySet()) {
                for (Etat etat : entree.getValue()) {
                    sb.append(id).append(" -- ").append((char) entree.getKey().intValue()).append(" --> ")
                            .append(etat.id).append("\n");
                    String seq = etat.imprimer(visites);
                    if (seq != null)
                        sb.append(seq);
                }
            }
            // Imprimer les transitions epsilon
            for (Etat etat : transitionsEpsilon) {
                sb.append(id).append(" -- EPSILON --> ").append(etat.id).append("\n");
                String seq = etat.imprimer(visites);
                if (seq != null)
                    sb.append(seq);
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return imprimer(new HashSet<>());
        }
    }

    // Méthode statique pour créer un AFN à partir d'un arbre d'expression régulière
    // (RegExTree)
    public static AutomateFiniNonDeterministe depuisArbreRegExVersAFN(RegExTree arbreRegEx) {
        if (arbreRegEx.subTrees.isEmpty()) {
            // Si l'arbre n'a pas de sous-arbres (cas d'un simple caractère ou d'un point)
            Etat etatDebut = new Etat();
            Etat etatFin = new Etat();
            if (arbreRegEx.root != RegEx.DOT) {
                etatDebut.ajouterTransition(arbreRegEx.root, etatFin); // Transition avec un seul caractère
            } else {
                for (int i = 0; i < SYMBOLES_ASCII; i++) {
                    etatDebut.ajouterTransition(i, etatFin); // Transition pour tous les caractères ASCII
                }
            }
            return new AutomateFiniNonDeterministe(etatDebut, etatFin);
        }

        // Cas de concaténation (CONCAT)
        if (arbreRegEx.root == RegEx.CONCAT) {
            AutomateFiniNonDeterministe gauche = depuisArbreRegExVersAFN(arbreRegEx.subTrees.get(0));
            AutomateFiniNonDeterministe droite = depuisArbreRegExVersAFN(arbreRegEx.subTrees.get(1));
            gauche.etatAcceptant.ajouterTransition(droite.etatInitial); // L'état d'acceptation du gauche pointe vers la
                                                                        // root du droit
            return new AutomateFiniNonDeterministe(gauche.etatInitial, droite.etatAcceptant);
        }

        // Cas d'alternance (ALTERN)
        if (arbreRegEx.root == RegEx.ALTERN) {
            Etat etatDebut = new Etat();
            AutomateFiniNonDeterministe gauche = depuisArbreRegExVersAFN(arbreRegEx.subTrees.get(0));
            AutomateFiniNonDeterministe droite = depuisArbreRegExVersAFN(arbreRegEx.subTrees.get(1));
            Etat etatFin = new Etat();
            etatDebut.ajouterTransition(gauche.etatInitial);
            etatDebut.ajouterTransition(droite.etatInitial);
            gauche.etatAcceptant.ajouterTransition(etatFin);
            droite.etatAcceptant.ajouterTransition(etatFin);
            return new AutomateFiniNonDeterministe(etatDebut, etatFin);
        }

        // Cas d'étoile (ETOILE)
        if (arbreRegEx.root == RegEx.ETOILE) {
            Etat etatDebut = new Etat();
            AutomateFiniNonDeterministe gauche = depuisArbreRegExVersAFN(arbreRegEx.subTrees.get(0));
            Etat etatFin = new Etat();
            etatDebut.ajouterTransition(gauche.etatInitial);
            etatDebut.ajouterTransition(etatFin);
            gauche.etatAcceptant.ajouterTransition(gauche.etatInitial);
            gauche.etatAcceptant.ajouterTransition(etatFin);
            return new AutomateFiniNonDeterministe(etatDebut, etatFin);
        }

        // Si aucun des cas ci-dessus, retourner un automate avec deux états par défaut
        return new AutomateFiniNonDeterministe(new Etat(), new Etat());
    }

    // Méthode pour afficher l'automate (en imprimant toutes ses transitions)
    @Override
    public String toString() {
        return etatInitial.toString();
    }

    // ! Dot for tests only
    public void toDotFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("digraph NDFA {\n");
            writer.write("  rankdir=LR;\n"); // Left to Right orientation
            writer.write("  node [shape = circle];\n");

            // Add all states and transitions
            Set<Etat> visites = new HashSet<>();
            writeEtatToDot(etatInitial, writer, visites);

            // Mark initial and accepting states
            writer.write("  \"" + etatInitial.id + "\" [shape = doublecircle, label=\"" + etatInitial.id + "\"];\n");
            writer.write("  \"" + etatAcceptant.id + "\" [shape = doublecircle, style=bold, label=\"" + etatAcceptant.id
                    + "\"];\n");

            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier Dot: " + e.getMessage());
        }
    }

    // Helper function to recursively write states and transitions
    // Proposée par ChatGPT
    private void writeEtatToDot(Etat etat, FileWriter writer, Set<Etat> visites) throws IOException {
        if (!visites.add(etat))
            return; // Skip already visited states

        // Write all transitions for this state
        for (Map.Entry<Integer, Set<Etat>> entry : etat.transitions.entrySet()) {
            int symbol = entry.getKey();
            for (Etat suivant : entry.getValue()) {
                writer.write("  \"" + etat.id + "\" -> \"" + suivant.id + "\" [label=\"" + (char) symbol + "\"];\n");
                writeEtatToDot(suivant, writer, visites); // Recursively process the next state
            }
        }

        // Write epsilon transitions
        for (Etat suivant : etat.transitionsEpsilon) {
            writer.write("  \"" + etat.id + "\" -> \"" + suivant.id + "\" [label=\"EPSILON\"];\n");
            writeEtatToDot(suivant, writer, visites); // Recursively process the next state
        }
    }
}
