package cezam;

import java.util.Random;

/**
 * La classe <code>MyRandom</code> encapsule un générateur de nombres aléatoires basé sur la classe
 * <code>Random</code> de Java. Elle fournit une méthode pour générer un entier aléatoire dans une plage donnée.
 */
public class MyRandom {

    /**
     * L'instance de la classe <code>Random</code> utilisée pour générer des nombres aléatoires.
     */
    Random random;

    /**
     * Génère un entier aléatoire dans la plage de 0 (inclus) à <code>bound</code> (exclus).
     *
     * @param bound La borne supérieure (exclu) pour le nombre aléatoire généré.
     * @return Un entier aléatoire compris entre 0 (inclus) et <code>bound</code> (exclus).
     * @throws IllegalArgumentException Si <code>bound</code> est inférieur ou égal à 0.
     */
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }
}
