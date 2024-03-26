<?php
use PHPUnit\Framework\TestCase;

class AuthentificationAPITest extends TestCase {
    protected $pdo;

    protected function setUp(): void {
        $host = 'localhost'; // Serveur de BD
        $db = 'festiplan';   // Nom de la BD
        $user = 'root';      // User 
        $pass = 'root';      // Mot de passe
        $charset = 'utf8mb4';// charset utilisé
    
        // Réglage des options
        $options = [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false
        ];
        
        // Constitution variable DSN
        $dsn = "mysql:host=$host;dbname=$db;charset=$charset";
    
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
        $authLog = 'tom';
        $authPwd = 'jammes';

        // Insérer des données d'utilisateur dans la base de données pour le test
        $this->insertTestUser('Tom', 'Jammes', $authLog, $authPwd);

        // Appel à la fonction d'authentification avec des identifiants valides
        $response = $this->callAuthentification($authLog, $authPwd);

        if ($response !== null && isset($response['statut'])) {
            $this->assertEquals('OK', $response['statut']);
            // Ajoutez d'autres assertions pour vérifier les détails de l'utilisateur, etc.
        } else {
            $this->fail("La réponse de l'API est invalide ou nulle.");
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
        $apiUrl = 'https://salon-eureka.fr/api/authentification'; // URL de l'API à appeler
        $http_status = 200;

        // Appel de la fonction d'API
        return $this->appelAPI($apiUrl, "", $http_status, 'POST', json_encode(['authLog' => $authLog, 'authPwd' => $authPwd]));
    }

    // Fonction pour appeler l'API
    protected function appelAPI($apiUrl, $apiKey, &$http_status, $typeRequete = "GET", $donnees = null) {
        // Interrogation de l'API
        // $apiUrl Url d'appel de l'API
        // $http_status Retourne le statut HTTP de la requete
        // $typeRequete = GET / POST / DELETE / PUT, GET par défaut si non précisé
        // $donnees = données envoyées au format JSON en PUT ET POST, rien si GET ou DELETE
        // La fonction retourne le résultat en format JSON
        
        $curl = curl_init();									// Initialisation

        curl_setopt($curl, CURLOPT_URL, $apiUrl);				// Url de l'API à appeler
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);			// Retour dans une chaine au lieu de l'afficher
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false); 		// Désactive test certificat
        curl_setopt($curl, CURLOPT_FAILONERROR, true);
        
        // Parametre pour le type de requete
        curl_setopt($curl, CURLOPT_CUSTOMREQUEST, $typeRequete); 
        
        // Si des données doivent être envoyées
        if (!empty($donnees)) {
            curl_setopt($curl, CURLOPT_POSTFIELDS, $donnees);
            curl_setopt($curl, CURLOPT_POST, true);
        }
        
        $httpheader []= "Content-Type:application/json";
        
        if (!empty($apiKey)) {
            // Ajout de la clé API dans l'entete si elle existe (pour tous les appels sauf login)
            $httpheader = ['APIKEYDEMONAPPLI: '.$apiKey];
        }
        curl_setopt($curl, CURLOPT_HTTPHEADER, $httpheader);
        
        // A utiliser sur le réseau des PC IUT, pas en WIFI, pas sur une autre connexion
        $proxy="http://cache.iut-rodez.fr:8080";
        curl_setopt($curl, CURLOPT_HTTPPROXYTUNNEL, true);
        curl_setopt($curl, CURLOPT_PROXY,$proxy ) ;
        /////////////////////////////////////////////////////////////////////////////
        
        $result = curl_exec($curl);								// Exécution
        $http_status = curl_getinfo($curl, CURLINFO_HTTP_CODE);	// Récupération statut 
        
        curl_close($curl);										// Cloture curl

        if ($http_status=="200" or $http_status=="201" ) {		// OK, l'appel s'est bien passé
            return json_decode($result,true); 					// Retourne la collection 
        } else {
            $result=[]; 										// retourne une collection Vide
            return $result;
        }
    }
}

