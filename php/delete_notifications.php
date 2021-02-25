<?php
if(!empty($_POST['itemid']){
 $file="notifications.mkt";
 $json = json_decode(file_get_contents($file),true);
 $itemid=$__POST['itemid'];

 for($i=0;$i<count($json);$i++){
 $item=$json[$i];
 foreach($item as $key=>$value){
 if($value==$itemid){
 unset($item[$key]);
 }
 }
 }

 file_put_contents(json_encode($json));
 echo "success";

?>