<?php
    function connect(){
        $mysqli = mysqli_connect("localhost", "root", "", "DataUsers");
        if (mysqli_connect_errno($mysqli)) {
            drop("Failed to connect to MySQL: " . mysqli_connect_error());
        }
        else{
            return $mysqli;
        }
    }

    function clean($string){
        $values = ["SELECT","COPY","DELETE","DROP","DUMP","OR","%","LIKE","-",";",":",
            "^","[","]","\\","!","°","?","=","&"," ","/","<",">","$","#","@","+","*","'","\""];
        return str_ireplace($values,"",$string);
    }

    //Temporal use
    function drop($string){
        print($string);
    }

    function checkUser($data1, $data2){
        $connection = connect();

        $result = $connection->query("SELECT Ident, Authenticator FROM General WHERE Ident = '".$data1."' AND Authenticator = '".$data2."'");
        if($result->num_rows == 1){
            return true;
        }
        else{
            return false;
        }
    }

    function search($data3){
        $connection = connect();
        $result = $connection->query("SELECT Place FROM General WHERE Ident = '".$data3."'");
        if($result->num_rows <> 1){
            return "Introduced callee error: ".$data3;
        }
        else{
            $row = $result->fetch_assoc();
            return "Callee: " . $row['Place'];
        }
    }

    function login($data1, $data2, $data3){
        $connection = connect();
        $mysqli_result = $connection->query("SELECT * FROM General WHERE Ident = '".$data1."' AND Authenticator = '".$data2."'");
        if($mysqli_result->num_rows > 0) {
            $row = $mysqli_result->fetch_assoc(); 
            if($data3 == $row['Place']){
                return "OK login without up";
            }
            else {
                $mysqli_result = $connection->query("UPDATE General SET Place = '".$data3."' WHERE Ident = '".$data1."' AND Authenticator = '".$data2."'");
                if($connection->affected_rows == 1){
                    return "OK login WITH update";
                }
                else return "OK login, ERROR update";
            }
        }
        else {
            return "Error login; bad parameters: ".$data1.", ".$data2;
        }
    }

    function register($data1, $data2, $data3) {
        $connection = connect();
        $result = $connection->query("INSERT INTO General VALUES ('".$data1."','".$data2."','".$data3."')");
        if ($result == TRUE) {
            return "OK register";
        } 
        else {
            return "Error register: " . $result;
        }
    }
?>