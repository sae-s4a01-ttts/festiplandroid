<?php
function getPDO() {
    $host='localhost';	// Serveur de BD
    $db='festiplan';    // Nom de la BD
    $user='root';		// User 
    $pass='root';		// Mot de passe
    $charset='utf8mb4';	// charset utilisé

    // Réglage des options
    $options=[
        PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES=>false];
    
    // Constitution variable DSN
    $dsn="mysql:host=$host;dbname=$db;charset=$charset";

    try {
        $pdo = new PDO($dsn,$user,$pass,$options);    // Connexion PDO
        return $pdo;
    } catch(PDOException $e) {
        $res['statut'] = "KO";
        $res['message'] = "Problème connexion base de données";
        sendJSON($res, 500);
    }
}

// Traitement de la demande si elle existe
if(!empty($_GET['req'])) {
    $url = explode('/', filter_var($_GET['req'], FILTER_SANITIZE_URL));

    switch ($url[0]) {
        case 'authentification':
            // TODO : Faire le code de l'authentification
            authentification();
            break;
        
        default:
            $res["statut"] = "KO";
            $res["message"] = $url[0] . " inexistant";
            sendJSON($res, 404);
            break;
    }
} else {
    $res["statut"] = "KO";
    $res["message"] = "URL non valide";
    sendJSON($res, 404);
}

function authentification() {

}

function sendJSON($res, $code) {
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST, GET, DELETE, PUT");

    http_response_code($code);
    echo json_encode($res, JSON_UNESCAPED_UNICODE);
}