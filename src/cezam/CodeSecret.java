package cezam;

/**
 * La classe <code>CodeSecret</code> représente un code secret utilisé dans un système de verrouillage.
 * Elle permet de gérer la création, la vérification et l'affichage du code secret, ainsi que le blocage
 * du code après un certain nombre d'essais incorrects.
 */
public class CodeSecret {

    /**
     * Indique si le code secret a été révélé ou non.
     */
    private boolean codeRevele;

    /**
     * Le code secret.
     */
    private String code;

    /**
     * Le nombre d'essais infructueux de saisie du code.
     */
    private int nbEssaisFaux;

    /**
     * Le nombre maximum d'essais incorrects avant le blocage.
     */
    private final int NB_ESSAIS_MAX = 3;

    /**
     * Crée un nouveau code secret en générant un code de 4 chiffres aléatoires.
     *
     * @param random Un générateur de nombres aléatoires utilisé pour générer chaque chiffre du code secret.
     * @return Un objet <code>CodeSecret</code> contenant un code généré aléatoirement.
     */
    public static CodeSecret createCode(MyRandom random) {
        StringBuilder pinCode = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pinCode.append(random.nextInt(10));
        }
        return new CodeSecret(pinCode.toString());
    }

    /**
     * Constructeur pour initialiser un <code>CodeSecret</code> avec un code donné.
     *
     * @param code Le code secret sous forme de chaîne de caractères.
     */
    public CodeSecret(String code) {
        this.code = code;
    }

    /**
     * Vérifie si le code secret a été bloqué en raison de trop nombreux essais incorrects.
     *
     * @return <code>true</code> si le code est bloqué (nombre d'essais incorrects supérieur ou égal à 3),
     *         <code>false</code> sinon.
     */
    public boolean isBlocked() {
        return nbEssaisFaux >= NB_ESSAIS_MAX;
    }

    /**
     * Révèle le code secret une seule fois. Si le code n'a pas encore été révélé, il est renvoyé,
     * sinon la méthode renvoie "xxxx" pour masquer le code.
     *
     * @return Le code secret si il n'a pas été révélé auparavant, sinon la chaîne "xxxx" pour masquer le code.
     */
    public String revelerCode() {
        if (!codeRevele) {
            codeRevele = true;
            return code;
        }
        return "xxxx";
    }

    /**
     * Vérifie si le code fourni correspond au code secret.
     * Si le code est correct, le compteur d'essais infructueux est réinitialisé.
     * Si le code est incorrect, le compteur d'essais infructueux est incrémenté.
     *
     * @param code Le code à vérifier.
     * @return <code>true</code> si le code fourni correspond au code secret,
     *         <code>false</code> si le code est incorrect ou si le code est bloqué.
     */
    public boolean verifierCode(String code) {
        if (isBlocked()) return false;

        boolean bonCode = this.code.equals(code);
        if (!bonCode) {
            nbEssaisFaux++;
        } else {
            nbEssaisFaux = 0;
        }
        return bonCode;
    }
}
