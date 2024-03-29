<?php
use PHPUnit\Framework\TestCase;
require __DIR__ . "/../api/index.php";

class APITest extends TestCase {
    protected $pdo;

    protected function setUp(): void {
        // Initialisation de la connexion PDO et début de la transaction
        $this->pdo = getPDO();
        $this->pdo->beginTransaction();
    }

    protected function tearDown(): void {
        // Rollback de la transaction à la fin des tests
        $this->pdo->rollBack();
    }

    // Méthode pour insérer des données d'utilisateur dans la base de données pour les tests
    protected function insertTestUser($firstName, $lastName, $authLog, $authPwd) {
        $stmt = $this->pdo->prepare("INSERT INTO users (nomUser, prenomUser, emailUser, loginUser, passwordUser) VALUES (?, ?, ?, ?, ?)");
        $stmt->execute([$firstName, $lastName, $firstName . "." . $lastName . "@iut-rodez.fr", $authLog, password_hash($authPwd, PASSWORD_DEFAULT)]);
    }

    // Tests unitaires
    public function testAuthentificationWithValidCredentials() {
        // Création d'un utilisateur de test
        $authLog = 'john';
        $authPwd = 'johndoe';
        $this->insertTestUser("John", "Doe", $authLog, $authPwd);

        // Appel à la fonction d'authentification avec des identifiants valides
        $response = authentification($authLog, $authPwd, $this->pdo);
        $this->assertEquals('OK', $response['statut']);
    }
}