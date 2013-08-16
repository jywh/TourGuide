<?php
    /* This is a server file, which is responsible for connecting to the database, uploading a photo to the server and storing
     the file name in the database.*/
    
    $db_host = "localhost";
    $db_username = "root";
    $db_pass = "";
    $db_name = "servertest";
    
    @mysql_connect($db_host, $db_username, $db_pass) or die("Could not connect to mysql");
    @mysql_select_db($db_name) or die("No database");
    
    $targetPath = "uploads/";
    $thumbPath = "uploads/thumb/";
    
    /* Add the original filename to our target path.
     Result is "uploads/filename.extension" */
    $targetPath = $targetPath . basename($_FILES['uploadedfile']['name']);
    
    if (move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $targetPath)) {
        
        $fileName = basename($_FILES['uploadedfile']['name']);
        
        $thumbPath = $thumbPath . $fileName;
        makeThumbnail($targetPath, $thumbPath, 128);
        
        // this will get the last inserted rowId from database
        $last_insert_row = mysql_query("SELECT RowId FROM Memo ORDER BY RowId DESC LIMIT 0,1");
        $last = mysql_fetch_assoc($last_insert_row);
        $success = mysql_query("UPDATE Memo SET image='" . $fileName . "' WHERE RowId=" . $last['RowId']);
        
        if ($success)
            echo $last['RowId'];
        else
            echo "-1";
    } else {
        echo '0';
    }
    
    /* This function creates a thumbnail for the image and stores it in the server. */
    function makeThumbnail($src, $dest, $desired_width) {
        
        /* read the source image */
        $source_image = imagecreatefromjpeg($src);
        $width = imagesx($source_image);
        $height = imagesy($source_image);
        
        /* find the "desired height" of this thumbnail, relative to the desired width  */
        $desired_height = floor($height * ($desired_width / $width));
        
        /* create a new, "virtual" image */
        $virtual_image = imagecreatetruecolor($desired_width, $desired_height);
        
        /* copy source image at a resized size */
        imagecopyresized($virtual_image, $source_image, 0, 0, 0, 0, $desired_width, $desired_height, $width, $height);
        
        /* create the physical thumbnail image to its destination */
        imagejpeg($virtual_image, $dest, 100);
    }
    
    
    ?>
