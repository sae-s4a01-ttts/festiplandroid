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
            $putData = file_get_contents("php://input");
            $donneesPUT = json_decode($putData, true);

            if(isset($donneesPUT['authLog']) && isset($donneesPUT['authPwd'])) {
                authentification($donneesPUT['authLog'], $donneesPUT['authPwd']);
            } else {
                $res['statut'] = "KO";
                $res['message'] = "Données d'utilisateur invalides";
                sendJSON($res, 400);
            }
            break;

        case 'infosfestival':
                if (isset($url[1])) {
                    infosFestival($url[1]);
                } else {
                    $res['statut'] = "KO";
                    $res['message'] = "Donnée du festival invalide";
                    sendJSON($res, 400);
                }
            break;
        
        case 'listefestivalsfavoris':
            if (isset($url[1])) {
                getListeFestivalFavoris($url[1]);
            } else {
                $res['statut'] = "KO";
                $res['message'] = "ID utilisateur manquant";
                sendJSON($res, 400);
            }
        break;
            
        case 'listefestivals':
            getListeFestival();
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

function authentification($authLog, $authPwd) {
    $code = 500;
    $pdo = getPDO();

    try {
        $sql = "SELECT idUser, nomUser, prenomUser, loginUser, passwordUser
                FROM users 
                WHERE loginUser = :authLog";

        $stmt = $pdo->prepare($sql);

        $stmt->bindParam(":authLog", $authLog);

        $stmt->execute();

        $user = $stmt->fetch();

        if (!$user || !password_verify($authPwd, $user["passwordUser"])) {
            $res['statut'] = "KO";
            $res['message'] = "Identifiant ou mot de passe incorrect " . $authLog . ' 0 ' . $authPwd . $user["passwordUser"];
            $code = 404;
        } else {
            $res['statut'] = "OK";
            $res['id'] = $user["idUser"];
            $code = 200;
        }

    } catch (PDOException $e) {
        $res['statut'] = "KO";
        $res['message'] = "Problème connexion base de données";
    }

    sendJSON($res, $code);
}

function infosFestival($festivalId) {
    $pdo = getPDO();
    $code = 200;
    $res = [];

    try {
        // récupération des données de la table festival
        $requeteFestivalInfos = "SELECT nomFestival, descriptionFestival, images.nomImage, dateDebutFestival, dateFinFestival, idResponsable, ville, codePostal
                                 FROM festivals
                                 JOIN images ON images.idImage = festivals.idImage
                                 WHERE festivals.idFestival = :festivalId";

        $stmt = $pdo->prepare($requeteFestivalInfos);

        $stmt->bindParam(":festivalId", $festivalId);

        $stmt->execute();

        $festivalInfos = $stmt->fetch();

        if ($festivalInfos == false) {
            $code = 404;
            $res['statut'] = "KO";
            $res['message'] = "id du festival invalide";
            
        } else {
            // récupération des catégories du festival
            $requeteFestivalCategorie = "SELECT nomCategorie
                  FROM categories
                  JOIN categoriefestival ON categoriefestival.idCategorie = categories.idCategorie
                  WHERE categoriefestival.idFestival = :festivalId";
            
            $stmt2 = $pdo->prepare($requeteFestivalCategorie);
            $stmt2->bindParam("festivalId", $festivalId);
            $stmt2->execute();
            $nomCat = $stmt2->fetchAll();
    
            // récupération des spectacles du festival
            $requeteSpectacles = "SELECT titreSpectacle, dureeSpectacle
                                  FROM spectacles
                                  JOIN composer ON composer.idSpectacle = spectacles.idSpectacle
                                  WHERE composer.idFestival = :festivalId";
    
            $s = $pdo->prepare($requeteSpectacles);
            $s->bindParam(":festivalId", $festivalId);
            $s->execute();
            $nomSpec = $s->fetchAll();
    
            // Composition des données
            $res[] = $festivalInfos;
            $res[] = $nomCat;
            $res[] = $nomSpec;   
        }

    } catch (PDOException $e) {
        $code = 500;
        $res['statut'] = "KO";
        $res['message'] = "Problème connexion base de données";
    }
    
    sendJSON($res, $code);
} 

/**
 * Appel la base de données et récupère tous les festivals avec leurs informations
 */
function getListeFestival() {
    try {
        $pdo = getPDO();

        $requete = "SELECT f.idFestival, f.nomFestival, f.descriptionFestival, f.dateDebutFestival, f.dateFinFestival,
                           f.ville, f.codePostal, JSON_ARRAYAGG(c.nomCategorie) AS nomCategorie
                    FROM  festivals f
                    INNER JOIN categorieFestival cf ON f.idFestival = cf.idFestival
                    INNER JOIN categories c ON cf.idCategorie = c.idCategorie
                    GROUP BY f.idFestival, f.nomFestival, f.descriptionFestival, f.dateDebutFestival,
                             f.dateFinFestival, f.ville, f.codePostal;";

        $stmt = $pdo->prepare($requete);
        $stmt->execute();

        $reponse = $stmt->fetchALL();

        sendJSON($reponse,200);
    } catch (PDOException $e) {
        $reponse["statut"] = "KO";
        $reponse["message"] = $e->getMessage();
        sendJSON($reponse, 500);
    }
}

// /**
//  * Appel la base de données et récupère tous les festivals favoris avec leurs informations
//  */
// function getListeFestivalFavoris($utilisateur) {
//     try {
//         $pdo = getPDO();

//         $requeteIDFestival = "SELECT idFestival FROM festivals;";

//         $requeteFestivals = "SELECT nomFestival, descriptionFestival, idImage, dateDebutFestival, dateFinFestival, ville, codePostal
//         FROM festivals;";

//         $requeteCategorie = "SELECT nomCategorie FROM categories
//         INNER JOIN categorieFestival ON categorieFestival.idCategorie = categories.idCategorie
//         WHERE idFesitval = :id;";

//         $stmtFestivals = $pdo->prepare($requeteFestivals);
//         $stmtID = $pdo->prepare($requeteIDFestival);
//         $stmtCategorie = $pdo->prepare($requeteCategorie);

//         $stmtID->execute();
//         $stmtFestivals->execute();

//         // La liste des id de festivals permettant de récupérer leurs catégorie plus tard
//         $listeID = $stmtID->fetchALL();

//         // la liste de tous les festivals avec leurs données
//         $festivals = $stmtFestivals->fetchALL();

//         // Récupération des différentes catégories d'un festival
//         foreach ($listeID as $id) {
//             $stmtCategorie->execute();
//             $festivals["categories"] = $stmtCategorie->fetchALL();
//         }

//         sendJSON($festivals,200);
//     } catch (PDOException $e) {
//         $reponse["statut"] = "KO";
//         $reponse["message"] = $e->getMessage();
//         sendJSON($reponse, 500);
//     }
// }

function sendJSON($res, $code) {
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST, GET, DELETE, PUT");

    http_response_code($code);
    echo json_encode($res, JSON_UNESCAPED_UNICODE);
}

// $stmt = getPDO()->prepare("INSERT INTO users (nomUser, prenomUser, emailUser, loginUser, passwordUser) VALUES (?, ?, ?, ?, ?)");
// $stmt->execute(["Tom", "Jammes", "tom.jammes@iut-rodez.fr", "tom", password_hash("jammes", PASSWORD_DEFAULT)]);