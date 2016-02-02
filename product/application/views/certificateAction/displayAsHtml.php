
		<div>
			<p>ISIN: <?php echo $certificate->ISIN ?></p>
		</div>
		<div>
			<p>Trading Market: <?php echo $certificate->trading_market ?></p>
		</div>
		<div>
			<p>Currency: <?php echo $certificate->currency ?></p>
		</div>	
		<div>
			<p>Issuer: <?php echo $certificate->issuer ?></p>
		</div>	
		<div>
			<p>Issuing Price: <?php echo $certificate->issuing_price ?></p>
		</div>
		<div>
			<p>Current Price: <?php echo $certificate->current_price ?></p>
		</div>
		<div>
			<p>Price history:</p>
			<ul>
			<?php foreach ($certificate->price_hist as $price): ?>
                <li>
                	Timestamp:<?php echo $price->timestamp ?>
                	Price:<?php echo $price->hist_price ?>
            	</li>
            <?php endforeach ?>
        	</ul>
		</div>
		

<?php if ($certificate instanceof BonusCertificate) { ?>
	<p>Barrier level: <?php echo $certificate->barrier_level?></p>
	<p>Hit: <?php echo $certificate->hit ? 'true' : 'false' ?></p>
<?php } ?>
<?php if ($certificate instanceof GuaranteeCertificate) { ?>
	<p>Participation Rate: <?php echo $certificate->participation_rate?></p>
<?php } ?>
<p>Documents:</p>
<ul>
	<?php foreach ($certificate->document_list as $document): ?>
	<li>
		File Name:<?php echo $document->fileName ?>
		Type:<?php echo $document->fileFormat ?>
	</li>
<?php endforeach ?>
</ul>
<p><a href="#" onClick="window.history.back();">Back to Certificate List</a></p>