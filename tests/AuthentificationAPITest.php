<?php
use PHPUnit\Framework\TestCase;

class AuthentificationAPITest extends TestCase {
    protected $pdo;

    protected function setUp(): void {
        $host='localhost'; // Serveur de BD
        $db='festiplan';   // Nom de la BD
        $user='root';      // User 
        $pass='root';      // Mot de passe
        $charset='utf8mb4';// charset utilisé
    
        // Réglage des options
        $options=[
            PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES=>false];
        
        // Constitution variable DSN
        $dsn="mysql:host=$host;dbname=$db;charset=$charset";
    
        try {
            $this->pdo = new PDO($dsn, $user, $pass, $options); // Connexion PDO
        } catch(PDOException $e) {
            // Erreur
        }
    }

    protected function tearDown(): void {
        // Rollback la transaction après chaque test
        if ($this->pdo !== null) {
            $this->pdo->rollBack();
        }
    }

    public function testAuthentificationWithValidCredentials() {
        $authLog = 'john';
        $authPwd = 'johndoe';

        // Insérer des données d'utilisateur dans la base de données pour le test
        $this->insertTestUser('John', 'Doe', $authLog, $authPwd);

        // Appel à la fonction d'authentification avec des identifiants valides
        $response = $this->callAuthentification($authLog, $authPwd);

        if ($response !== null && isset($response['statut'])) {
            $this->assertEquals('OK', $response['statut']);
            // Ajoutez d'autres assertions pour vérifier les détails de l'utilisateur, etc.
        } else {
            $this->fail("La réponse de l'API est invalide ou nulle." . $response);
        }
    }

    public function testAuthentificationWithInvalidCredentials() {
        $authLog = 'invalid_username';
        $authPwd = 'invalid_password';

        // Appel à la fonction d'authentification avec des identifiants invalides
        $response = $this->callAuthentification($authLog, $authPwd);

        if ($response !== null && isset($response['statut'])) {
            $this->assertEquals('KO', $response['statut']);
            // Ajoutez d'autres assertions pour vérifier le message d'erreur, etc.
        } else {
            $this->fail("La réponse de l'API est invalide ou nulle.");
        }
    }

    // Méthode pour insérer des données d'utilisateur dans la base de données pour les tests
    protected function insertTestUser($firstName, $lastName, $authLog, $authPwd) {
        if ($this->pdo !== null) {
            $stmt = $this->pdo->prepare("INSERT INTO users (nomUser, prenomUser, emailUser, loginUser, passwordUser) VALUES (?, ?, ?, ?, ?)");
            $stmt->execute([$firstName, $lastName, $firstName . "." . $lastName . "@iut-rodez.fr", $authLog, password_hash($authPwd, PASSWORD_DEFAULT)]);
        }
    }

    // Méthode pour appeler la fonction d'authentification de l'API
    protected function callAuthentification($authLog, $authPwd) {
        $apiUrl = 'http://localhost/api/authentification'; // URL de l'API à appeler

        // Données à envoyer en tant que POST
        $postData = http_build_query([
            'authLog' => $authLog,
            'authPwd' => $authPwd
        ]);

        $curl = curl_init(); // Initialisation de cURL

        // Configuration des options de cURL
        curl_setopt_array($curl, array(
            CURLOPT_URL => $apiUrl, // URL de l'API
            CURLOPT_RETURNTRANSFER => true, // Retourner la réponse dans une chaîne
            CURLOPT_POST => true, // Méthode de requête POST
            CURLOPT_POSTFIELDS => $postData, // Données POST
            CURLOPT_HTTPHEADER => array('Content-Type: application/x-www-form-urlencoded'), // En-têtes de la requête
            
            // Configuration du proxy
            CURLOPT_HTTPPROXYTUNNEL => true,
            CURLOPT_PROXY => 'http://cache.iut-rodez.fr:8080'
        ));

        $response = curl_exec($curl); // Exécution de la requête

        // Vérification des erreurs de cURL
        if ($response === false) {
            $error = curl_error($curl);
            // Gérer l'erreur de cURL ici
            // Par exemple, vous pouvez retourner une erreur ou enregistrer dans un fichier de journal
            return null;
        }

        curl_close($curl); // Fermeture de la session cURL

        // Décode la réponse JSON en tableau associatif
        $responseData = json_decode($response, true);

        return $responseData;
    }
}
