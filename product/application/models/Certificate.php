<?php
class Certificate extends CI_Model {

        public $ISIN = '';
       	public $trading_market = '';
		public $currency = '';
		public $issuer = '';
		public $issuing_price = 0;
		public $current_price = 0;
		public $price_hist = array();
		public $document_list = array();


        public function __construct()
        {
               $this->load->helper('file');
               $this->load->model('Price');
               $this->load->model('Document');
               $this->load->model('BonusCertificate');
               $this->load->model('GuaranteeCertificate');
        }

        public function get_certlist()
		{
			$data = file_get_contents('data.json');
			$jsonArray = json_decode($data,true);
			$certificateList = array();
			foreach ($jsonArray as $jsonOject) {
				$certificate;
				if (array_key_exists("barrier_level",$jsonOject))
				{
					$certificate = new BonusCertificate();
					$certificate->barrier_level = $jsonOject["barrier_level"];
					$certificate->hit = $jsonOject["hit"];
				}
				else if (array_key_exists("participation_rate",$jsonOject))
				{
					$certificate = new GuaranteeCertificate();
					$certificate->participation_rate = $jsonOject["participation_rate"];
				}
				else {
					$certificate = new Certificate();
				}
				$certificate->ISIN = $jsonOject["ISIN"];
				$certificate->trading_market = $jsonOject["trading_market"];
				$certificate->currency = $jsonOject["currency"];
				$certificate->issuer = $jsonOject["issuer"];
				$certificate->issuing_price = $jsonOject["issuing_price"];
				if (array_key_exists("price_hist",$jsonOject)){
					foreach ($jsonOject["price_hist"] as $jsonprice){
						$price = new Price();
						$price->timestamp = $jsonprice["timestamp"];
						$price->hist_price = $jsonprice["hist_price"];
						$certificate->price_hist[] = $price;
					}
				}
				$price = new Price();
				$price->timestamp = time();
				$price->hist_price = rand();
				$certificate->price_hist[] = $price;
				$certificate->current_price=$price->hist_price;
				if (array_key_exists("document_list",$jsonOject)){
					foreach ($jsonOject["document_list"] as $jsondoc){
						$document = new Document();
						$document->fileName = $jsondoc["fileName"];
						$document->fileFormat = $jsondoc["fileFormat"];
						$certificate->document_list[] = $document;
					}
				}
				$certificateList[] = $certificate;
			}


			
			/*$certificate = new Certificate();
			$certificate->ISIN = 'ABC';
			$certificate->trading_market = 'London';
			$certificate->currency = 'EURO';
			$certificate->issuer = 'DBS';
			$certificate->issuing_price = rand();
			
			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$certificate->current_price=$price->hist_price;			
			$certificateList[] = $certificate;

			$certificate = new Certificate();
			$certificate->ISIN = 'DDD';
			$certificate->trading_market = 'Brazil';
			$certificate->currency = 'BR';
			$certificate->issuer = 'DBS';
			$certificate->issuing_price = rand();
			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$certificate->current_price=$price->hist_price;			
			$certificateList[] = $certificate;

			$certificate = new BonusCertificate();
			$certificate->ISIN = 'Bonus Certificate';
			$certificate->trading_market = 'London';
			$certificate->currency = 'EURO';
			$certificate->issuer = 'DBS';
			$certificate->issuing_price = rand();
			$certificate->barrier_level = rand(0,10);
			$certificate->hit = true;

			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$certificate->current_price=$price->hist_price;			
			$certificateList[] = $certificate;

			$certificate = new GuaranteeCertificate();
			$certificate->ISIN = 'Guarantee Certificate';
			$certificate->trading_market = 'Brazil';
			$certificate->currency = 'BR';
			$certificate->issuer = 'DBS';
			$certificate->issuing_price = rand();
			$certificate->participation_rate = rand(0,10)/10.0;

			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$price = new Price();
			$price->timestamp = time();
			$price->hist_price = rand();
			$certificate->price_hist[] = $price;
			$certificate->current_price=$price->hist_price;			
			$certificateList[] = $certificate;*/
			$jsonString = json_encode($certificateList);
			file_put_contents('data.json', $jsonString);
			return $certificateList;
		}

		public function get_certItem($ISIN = NULL)
		{
		        if ($ISIN === NULL)
		        {
		        	return NULL;
		        }
				$certificateList = $this->get_certlist();
				foreach ($certificateList as $certificate){
					if (strcmp($certificate->ISIN,$ISIN) == 0)
						return $certificate;
				}
		        return NULL;
		}
}