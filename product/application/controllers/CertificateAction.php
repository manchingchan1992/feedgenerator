<?php
class CertificateAction extends CI_Controller {

        public function __construct()
        {
                parent::__construct();
                $this->load->model('Certificate');
        }

        public function index()
        {
                $data['certificateList'] = $this->Certificate->get_certlist();
                $data['title'] = 'Certificate List';

                $this->load->view('templates/header', $data);
                $this->load->view('certificateAction/index', $data);
                $this->load->view('templates/footer');
        }

        public function displayAsHtml($ISIN = NULL)
        {
                $data = $this->view($ISIN);
                $data['title'] = "Certificate Details";
                $this->load->view('templates/header', $data);
                $this->load->view('certificateAction/displayAsHtml', $data);
                $this->load->view('templates/footer');
        }
        public function view($ISIN = NULL)
        {
                $data['certificate'] = $this->Certificate->get_certItem(urldecode($ISIN));
                if (empty($data['certificate']))
                {
                        show_404();
                }      
                return $data;
        }
        public function displayAsXml($ISIN = NULL)
        {
                $data = $this->view($ISIN);
                if ($data['certificate'] instanceof GuaranteeCertificate){
                        show_error('GuaranteeCertificate Not allowed to be exported as xml', '501', $heading = 'An Error Was Encountered');
                }
                $data['title'] = $data['certificate']->ISIN;
                $this->output->set_content_type('text/xml');
                $this->load->view('certificateAction/displayAsXml', $data);
        }

}