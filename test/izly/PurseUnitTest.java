package izly;

import cezam.CodeSecret;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Classe de tests unitaires pour la classe <code>Purse</code>.
 * Cette classe teste les opérations de crédit, débit, ainsi que les différents cas d'erreur possibles
 * pour la gestion d'un portefeuille électronique.
 */
public class PurseUnitTest {

    private String codeJuste = "9876";  // Code secret valide pour les tests

    private CodeSecret mockPinCode;  // Mock pour le code secret

    /**
     * Initialisation avant chaque test. Crée un mock pour le code secret
     * et définit la vérification du code secret valide.
     */
    @BeforeEach
    public void setup() {
        mockPinCode = Mockito.mock(CodeSecret.class);
        Mockito.when(mockPinCode.verifierCode(codeJuste)).thenReturn(true);  // Code secret valide
    }

    /**
     * Test de la méthode de crédit. Vérifie que le solde du portefeuille est correctement mis à jour après un crédit.
     *
     * @throws MotifBlocageTransaction Si une exception liée au blocage de la transaction se produit.
     */
    @Test
    public void testCredit() throws MotifBlocageTransaction {
        Purse purse = new Purse(100, 100, mockPinCode);  // Créer un portefeuille avec un plafond de 100
        double solde = purse.getSolde();  // Récupérer le solde initial
        purse.credit(10);  // Effectuer un crédit de 10
        Assertions.assertEquals(solde + 10, purse.getSolde());  // Vérifier si le solde est bien mis à jour
    }

    /**
     * Test de la méthode de débit. Vérifie que le solde du portefeuille est correctement mis à jour après un débit.
     *
     * @throws Exception Si une exception liée au débit se produit.
     */
    @Test
    public void testDebit() throws Exception {
        Purse purse = new Purse(100, 100, mockPinCode);  // Créer un portefeuille avec un plafond de 100
        purse.credit(10);  // Ajouter 10 au portefeuille
        double solde = purse.getSolde();  // Récupérer le solde actuel
        purse.debit(10, codeJuste);  // Effectuer un débit de 10 avec le code secret valide
        Assertions.assertEquals(solde - 10, purse.getSolde());  // Vérifier si le solde est correctement mis à jour
    }

    /**
     * Test pour vérifier que le solde du portefeuille ne devient jamais négatif lors d'un débit.
     */
    @Test
    public void testLeSoldeNeDoitJamaisEtreNegatif() {
        Purse purse = new Purse(100, 100, mockPinCode);
        double solde = purse.getSolde();
        Assertions.assertThrows(SoldeNegatifInterdit.class, () -> purse.debit(solde + 1, codeJuste));  // Débit supérieur au solde
    }

    /**
     * Test pour vérifier que le solde du portefeuille ne dépasse jamais le plafond fixé lors de la création.
     */
    @Test
    public void testLeSoldeNeDoitJamaisDepasserLePlafondFixeALaCreationDuPurse() {
        Purse purse = new Purse(50, 100, mockPinCode);  // Créer un portefeuille avec un plafond de 50
        double solde = purse.getSolde();
        Assertions.assertThrows(DepassementPlafondInterdit.class, () -> purse.credit(50 - solde + 1));  // Crédit qui dépasse le plafond
    }

    /**
     * Test pour vérifier que les montants négatifs sont interdits lors des opérations de crédit ou de débit.
     */
    @Test
    public void testMontantsNegatifsInterdits() {
        Purse purse = new Purse(150, 100, mockPinCode);
        Assertions.assertThrows(MontantNegatif.class, () -> purse.credit(-200));  // Tentative de crédit avec un montant négatif
        Assertions.assertThrows(MontantNegatif.class, () -> purse.debit(-200, codeJuste));  // Tentative de débit avec un montant négatif
    }

    /**
     * Test de la gestion du nombre d'opérations autorisées. Vérifie que les opérations sont bloquées lorsque le nombre maximal d'opérations est atteint.
     *
     * @throws Exception Si une exception liée aux opérations se produit.
     */
    @Test
    public void testGestionDureeDeVie() throws Exception {
        Purse purse = new Purse(100, 2, mockPinCode);  // Créer un portefeuille avec 2 opérations maximales
        purse.credit(40);  // Effectuer une opération de crédit
        purse.debit(20, codeJuste);  // Effectuer une opération de débit
        Assertions.assertThrows(NbOperationsMaxAtteint.class, () -> purse.credit(20));  // Essayer de créditer après avoir atteint la limite
        Assertions.assertThrows(NbOperationsMaxAtteint.class, () -> purse.debit(20, codeJuste));  // Essayer de débiter après avoir atteint la limite
    }

    /**
     * Test pour vérifier que le débit est rejeté si le code secret est incorrect.
     *
     * @throws Exception Si une exception liée au débit se produit.
     */
    @Test
    public void testDebitRejeteSurCodeFaux() throws Exception {
        String codeFaux = "1234";  // Code secret incorrect
        Purse purse = new Purse(100, 50, mockPinCode);
        purse.credit(50);  // Créer un portefeuille et effectuer un crédit de 50
        Assertions.assertThrows(CodeFauxException.class, () -> purse.debit(20, codeFaux));  // Tentative de débit avec un code incorrect
    }

    /**
     * Test pour vérifier que le débit est rejeté si le code secret est bloqué.
     *
     * @throws MotifBlocageTransaction Si une exception liée au blocage du code secret se produit.
     */
    @Test
    public void testDebitRejeteSurCodeBloque() throws MotifBlocageTransaction {
        Mockito.when(mockPinCode.isBlocked()).thenReturn(false, true);  // Le code n'est pas bloqué au début, puis il est bloqué
        Purse purse = new Purse(100, 50, mockPinCode);
        purse.credit(50);  // Créer un portefeuille et effectuer un crédit de 50
        Assertions.assertThrows(CodeBloqueException.class, () -> purse.debit(20, codeJuste));  // Tentative de débit avec un code bloqué
    }

    /**
     * Test pour vérifier que le crédit est rejeté si le code secret est bloqué.
     *
     * @throws Exception Si une exception liée au crédit se produit.
     */
    @Test
    public void testCreditRejeteSurCodeBloque() throws Exception {
        Mockito.when(mockPinCode.isBlocked()).thenReturn(false, true);  // Le code n'est pas bloqué au début, puis il est bloqué
        Purse purse = new Purse(100, 50, mockPinCode);
        purse.credit(50);  // Créer un portefeuille et effectuer un crédit de 50
        Assertions.assertThrows(CodeBloqueException.class, () -> purse.credit(20));  // Tentative de crédit avec un code bloqué
    }
}
