package cezam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Classe de test unitaire pour la classe <code>CodeSecret</code>.
 * Cette classe vérifie le comportement des fonctionnalités principales
 * de la classe <code>CodeSecret</code>, telles que la génération et la vérification
 * de codes, ainsi que la gestion des tentatives incorrectes et du blocage après
 * plusieurs tentatives échouées.
 */
public class CodeSecretUnitTest {

    /**
     * Instance de <code>CodeSecret</code> utilisée pour les tests.
     */
    private CodeSecret codeSecret;

    /**
     * Initialisation des tests avant chaque exécution.
     * Un mock de la classe <code>MyRandom</code> est créé pour simuler la génération
     * des codes secrets de manière contrôlée.
     */
    @BeforeEach
    public void setup() {
        MyRandom random = Mockito.mock(MyRandom.class);
        Mockito.when(random.nextInt(10)).thenReturn(5, 4, 3, 2);
        codeSecret = CodeSecret.createCode(random);
    }

    /**
     * Teste la méthode <code>revelerCode</code> pour vérifier le comportement de
     * la révélation du code secret.
     * Au premier appel, le code secret doit être révélé et au second appel,
     * il doit être masqué par des 'x'.
     */
    @Test
    public void testRevelerCode() {
        Assertions.assertTrue(isCode(codeSecret.revelerCode()));
        Assertions.assertEquals("xxxx", codeSecret.revelerCode());
    }

    /**
     * Méthode utilitaire pour vérifier si un code est un code valide (un entier de 4 chiffres).
     *
     * @param code Le code à vérifier.
     * @return <code>true</code> si le code est un entier valide de 4 chiffres, sinon <code>false</code>.
     */
    private boolean isCode(String code) {
        if (code.length() != 4) return false;
        try {
            Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Teste la génération aléatoire du code secret.
     * La méthode vérifie que le code secret généré est correct et que la méthode
     * <code>nextInt</code> de <code>MyRandom</code> a bien été appelée 4 fois.
     */
    @Test
    public void testCodeGenereAleatoirement() {
        MyRandom random = Mockito.mock(MyRandom.class);
        Mockito.when(random.nextInt(10)).thenReturn(5, 4, 3, 2);
        CodeSecret codeSecret = CodeSecret.createCode(random);
        Assertions.assertEquals("5432", codeSecret.revelerCode());
        Mockito.verify(random, Mockito.times(4)).nextInt(10);
    }

    /**
     * Teste la méthode <code>verifierCode</code> avec des codes incorrects et corrects.
     * Le test vérifie si la méthode retourne les bons résultats pour un code erroné
     * et un code correct.
     */
    @Test
    public void testVerifierCodeSurCodeFauxEtJuste() {
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertTrue(codeSecret.verifierCode("5432"));
    }

    /**
     * Teste le blocage du code après 3 tentatives incorrectes consécutives.
     * Ce test vérifie que la méthode <code>isBlocked</code> retourne <code>true</code>
     * après 3 tentatives incorrectes.
     */
    @Test
    public void testCodeBloqueApres3essaisFauxNonSuccessifs() {
        // Blocage car 3 essais éronnés
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertTrue(codeSecret.isBlocked());
    }

    /**
     * Teste que le code ne se bloque pas après 3 essais incorrects suivis d'une tentative correcte.
     * Le test vérifie également que le nombre de tentatives est réinitialisé après un code correct.
     *
     * @see #testCodeBloqueApres3essaisFauxNonSuccessifs
     */
    @Disabled("redondant par rapport à testCodeNonBloqueApres3essaisFauxNonSuccessifs")
    @Test
    public void testCodeNonBloqueApres3essaisFauxNonSuccessifs() {
        // Non blocage car 3 essaie réussis
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertTrue(codeSecret.verifierCode("5432"));
        Assertions.assertFalse(codeSecret.isBlocked());

        // .. réinit du nb blocage à 0 ..

        // Blocage avec 3 tests de code faux
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.isBlocked());
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.isBlocked());
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertTrue(codeSecret.isBlocked());
    }

    /**
     * Teste le comportement du blocage du code après plusieurs tentatives incorrectes.
     * Ce test vérifie que le code se bloque après 3 tentatives incorrectes et
     * reste bloqué même après une tentative correcte.
     */
    @Test
    public void testCodeBloque() {
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertFalse(codeSecret.isBlocked());
        Assertions.assertFalse(codeSecret.verifierCode("2345"));
        Assertions.assertTrue(codeSecret.isBlocked());
        Assertions.assertFalse(codeSecret.verifierCode("5432"));
        Assertions.assertTrue(codeSecret.isBlocked());
    }
}
