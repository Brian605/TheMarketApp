<?php
require_once("constants.php");

$file="notifications.mkt";
$json = json_decode(file_get_contents($file),true);

echo json_encode($json);


?>