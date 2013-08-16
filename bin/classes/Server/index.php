<?php
    /* This is a server file, which is responsible for connecting to the database, executing the queries that are requested by
     the client through different commands and sending the results back to the client.*/
    
    $db_host = "localhost";
    $db_username = "root";
    $db_pass = "";
    $db_name = "servertest";
    
    @mysql_connect($db_host, $db_username, $db_pass) or die("Could not connect to mysql");
    @mysql_select_db($db_name) or die("No database");
    
    //Get the command that is sent by the client
    $command = $_REQUEST["command"];
    
    $response = "";
    
    /*Determine which command to execute, send the appropriate query to the database, receive and process the result and send
     it back to the client*/
    
    if ($command == "getNearby") { //get the notes that are within a certain bound
        $latitudeBound1 = $_REQUEST["latitudebound1"];
        $latitudeBound2 = $_REQUEST["latitudebound2"];
        $longitudeBound1 = $_REQUEST["longitudebound1"];
        $longitudeBound2 = $_REQUEST["longitudebound2"];
        if ($latitudeBound1 != NULL && $latitudeBound2 != NULL &&
            $longitudeBound1 != NULL && $longitudeBound2 != NULL) {
            $result = mysql_query("SELECT RowId, Latitude, Longitude FROM Memo
                                  WHERE Latitude > '$latitudeBound1' AND Latitude <'$latitudeBound2'
                                  AND   Longitude > '$longitudeBound1'AND Longitude <'$longitudeBound2'");
                                  
                                  while ($row = mysql_fetch_array($result)) {
                                  $rowId = $row["RowId"];
                                  $latitude = $row["Latitude"];
                                  $longitude = $row["Longitude"];
                                  $response = $response . $rowId . "%%%" . $latitude . "&&&" . $longitude . "@@@";
                                  }
                                  echo $response;
                                  } else {
                                  echo "";
                                  }
                                  } else if ($command == "getMemo") { //get the notes information when a user taps on a note icon
                                  $rowId = $_REQUEST['rowId'];
                                  if ($rowId != NULL) {
                                  $result = mysql_query("SELECT Author, Body, Date, image FROM Memo
                                                        WHERE RowId = '$rowId'");
                                                        while ($row = mysql_fetch_array($result)) {
                                                        $author = $row["Author"];
                                                        $date = $row["Date"];
                                                        $body = $row["Body"];
                                                        $image = ($row['image'] != NULL) ? $row['image'] : "0";
                                                        $response = $response . $author . "%%%" . $date . "&&&" . $body . "###".$image."@@@";
                                                        }
                                                        echo $response;
                                                        }
                                                        }else if ($command == "insertMemo") { //insert a new note created by a user to the database
                                                        $author = $_REQUEST['author'];
                                                        $body = $_REQUEST['body'];
                                                        $date = $_REQUEST['date'];
                                                        $latitude=$_REQUEST['latitude'];
                                                        $longitude=$_REQUEST['longitude'];
                                                        $body = mysql_real_escape_string($body);
                                                        $author = mysql_real_escape_string($author);
                                                        $result=mysql_query("INSERT INTO Memo (Author, Body, Date, Latitude, Longitude) VALUES ('"
                                                                            . $author . "', '" . $body . "','" . $date . "','".$latitude."','".$longitude."')");
                                                        if($result)
                                                        echo mysql_insert_id();
                                                        else
                                                        echo '-1';
                                                        } else if($command == "editMemo") // update the note that the user has edited
                                                        {
                                                        $rowId = $_REQUEST['rowId'];
                                                        $author = $_REQUEST['author'];
                                                        $body = $_REQUEST['body'];
                                                        $body = mysql_real_escape_string($body);
                                                        $author = mysql_real_escape_string($author);
                                                        $result=mysql_query("UPDATE Memo SET Author='".$author."', Body='".$body."' WHERE RowId=".$rowId);
                                                        if($result)
                                                        echo '1';
                                                        else
                                                        echo '0';
                                                        }
                                                        else if($command == "deleteMemo") // remove the note that the user has deleted
                                                        {
                                                        $rowId = $_REQUEST['rowId'];
                                                        $result = mysql_query("DELETE FROM Memo WHERE RowId=".$rowId);
                                                        if($result)
                                                        echo '1';
                                                        else
                                                        echo '0';
                                                        }
                                                        else {
                                                        echo "";
                                                        } 
    ?>




