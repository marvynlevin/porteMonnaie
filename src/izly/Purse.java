package izly;

import cezam.CodeSecret;


/**
 * La classe <code>Purse</code> représente un portefeuille électronique avec un solde, un plafond de dépôt,
 * un nombre d'opérations autorisées, et un code secret pour valider les transactions. Elle permet d'effectuer
 * des opérations de crédit et de débit avec des contrôles afin d'éviter les erreurs courantes (solde négatif,
 * dépassement du plafond, code incorrect, etc.).
 */
public class Purse {

    private double solde;
    private double plafond;
    private int nbOperations;
    private int nbOperationsDejaRealisees = 0;
    private CodeSecret codeSecret;

    /**
     * Retourne le solde actuel du portefeuille.
     *
     * @return Le solde actuel du portefeuille.
     */
    public double getSolde() {
        return solde;
    }

    /**
     * Constructeur pour créer un portefeuille avec un plafond de dépôt, un nombre d'opérations autorisées
     * et un code secret pour valider les opérations de débit.
     *
     * @param plafond Le montant maximal autorisé sur le portefeuille.
     * @param nbOperations Le nombre maximal d'opérations autorisées sur le portefeuille.
     * @param codeSecret Le code secret pour valider les opérations de débit.
     */
    public Purse(double plafond, int nbOperations, CodeSecret codeSecret) {
        this.plafond = plafond;
        this.nbOperations = nbOperations;
        this.codeSecret = codeSecret;
    }

    /**
     * Effectue un crédit sur le portefeuille. Si le montant dépasse le plafond ou si le nombre d'opérations
     * autorisées est atteint, une exception sera lancée.
     * Si le montant est négatif, une exception sera également déclenchée.
     *
     * @param montant Le montant à créditer.
     * @throws MotifBlocageTransaction Si le montant est négatif ou si le plafond est dépassé.
     * @throws NbOperationsMaxAtteint Si le nombre d'opérations a atteint sa limite.
     */
    public void credit(double montant) throws MotifBlocageTransaction {
        controlePreOp(montant);
        if (solde + montant > plafond) throw new DepassementPlafondInterdit();
        solde += montant;
        nbOperationsDejaRealisees++;
    }

    /**
     * Effectue un débit sur le portefeuille. Si le montant est supérieur au solde disponible ou si le code secret
     * est incorrect ou bloqué, une exception sera lancée.
     *
     * @param montant Le montant à débiter.
     * @param code Le code secret pour valider l'opération.
     * @throws MotifBlocageTransaction Si le montant est négatif, le code est incorrect, ou si le code est bloqué.
     * @throws CodeFauxException Si le code secret est incorrect.
     * @throws SoldeNegatifInterdit Si le montant à débiter dépasse le solde disponible.
     * @throws NbOperationsMaxAtteint Si le nombre d'opérations a atteint sa limite.
     */
    public void debit(double montant, String code) throws MotifBlocageTransaction {
        controlePreOp(montant);
        if (codeSecret.isBlocked()) throw new CodeBloqueException();
        if (!codeSecret.verifierCode(code)) throw new CodeFauxException();
        if (montant > solde) throw new SoldeNegatifInterdit();
        solde -= montant;
        nbOperationsDejaRealisees++;
    }

    /**
     * Effectue un contrôle préalable avant toute opération. Vérifie que le montant est valide (non négatif)
     * et que le nombre maximal d'opérations autorisées n'a pas été atteint. Vérifie également si le code secret est bloqué.
     *
     * @param montant Le montant de l'opération.
     * @throws MotifBlocageTransaction Si le montant est négatif, le nombre d'opérations est dépassé, ou le code est bloqué.
     * @throws NbOperationsMaxAtteint Si le nombre d'opérations a atteint sa limite.
     * @throws CodeBloqueException Si le code secret est bloqué.
     */
    private void controlePreOp(double montant) throws MotifBlocageTransaction {
        if (nbOperationsDejaRealisees >= nbOperations) throw new NbOperationsMaxAtteint();
        if (montant < 0) throw new MontantNegatif();
        if (codeSecret.isBlocked()) throw new CodeBloqueException();
    }
}
