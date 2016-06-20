<?php
  
  include("methods.php");

  //Variables
  //Data1 -> Ident (phone/username)
  //Data2 -> Authenticator (password)
  //Data3 -> Place (IP) / Ident searched
  //Data4 -> Action
  
  if(isset($_POST["data4"])){
    $action = clean(substr($_POST["data4"],0,20));
  }
  else if(isset($_GET["data4"])){
    $action = clean(substr($_GET["data4"],0,20));
  }
  else{
    $action = "menu";
  }

  if(isset($_POST["data1"])){
    $data1 = clean(substr($_POST["data1"],0,40));
  }
  else if(isset($_GET["data1"])){
    $data1 = clean(substr($_GET["data1"],0,40));
  }
  else{
    $action = "menu";
  }
  
  if(isset($_POST["data2"])){
    $data2 = clean(substr($_POST["data2"],0,40));
  }
  else if(isset($_GET["data2"])){
   $data2 = clean(substr($_GET["data2"],0,40));
  }
  else{
    $action = "menu";
  }

  if(isset($_POST["data3"])){
    $data3 = clean(substr($_POST["data3"],0,40));
  }
  else if(isset($_GET["data3"])){
    $data3 = clean(substr($_GET["data3"],0,40));
  }
  else{
    $action = "menu";
  }

  switch($action){
    case "login":
      
      drop(login($data1, $data2, $data3));
      break;

    case "register":
      if(checkUser($data1, $data2) == true){
        drop("Bad register");
      }
      else {
        drop(register($data1, $data2, $data3));
      }
      break;

    case "communicate":
      if(!checkUser($data1, $data2)){
        drop("Communication error");
      }
      else {
        drop(search($data3));
      }
      break;
    case "menu":
    default:
      drop("Not a valid petition");
      break;
  }
?>