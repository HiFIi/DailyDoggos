<?php 

    // Get Post Data
    $data = urldecode($_POST['data']);
      
	$jsonData      = array();
	$jsonTempData  = array();
	$url = "http://server.mbqonxda.net/Nightlies/Vanir/Samsung/Toro/2014/January%2010th/vanir_toro_4.4.2.011014.zip";

	   {
		  $jsonTempData = array();
		# $jsonTempData['Vanir-Changelog'] 	= $data.$i;
		  $jsonTempData['date'] 		= "January 10th";
		  $jsonTempData['build-status'] 	= "Success. :)";
		  
		  $jsonData[] = $jsonTempData;
	   }
	   	   {
		  $jsonTempData                         = array();
		# $jsonTempData['Vanir-Changelog'] 	= $data.$i;
		  $jsonTempData['date'] 		= "January 11th";
		  $jsonTempData['build-status'] 	= "TBD";
		  
		  $jsonData[] = $jsonTempData;
	   }
    
	 $outputArr = array();
	 $outputArr['Android'] = $jsonData;
	 
     // Encode Array To JSON Data
	 print_r( json_encode($outputArr));
	 

 ?>
