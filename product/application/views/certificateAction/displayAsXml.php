<Certificate>
	<Property>
		<name>
			ISIN
		</name>
		<value>
			<?php echo $certificate->ISIN ?>
		</value>
	</Property>
	<Property>
		<name>
			Trading Market
		</name>
		<value>
			<?php echo $certificate->trading_market ?>
		</value>
	</Property>
	<Property>
		<name>
			Currency
		</name>
		<value>
			<?php echo $certificate->currency ?>
		</value>
	</Property>
	<Property>
		<name>
			Issuer
		</name>
		<value>
			<?php echo $certificate->issuer ?>
		</value>
	</Property>
	<Property>
		<name>
			Issuing Price
		</name>
		<value>
			<?php echo $certificate->issuing_price ?>
		</value>
	</Property>
	<Property>
		<name>
			Current Price
		</name>
		<value>
			<?php echo $certificate->current_price ?>
		</value>
	</Property>
	<Property>
		<name>
			Price history
		</name>
		<value>
			<?php foreach ($certificate->price_hist as $price): ?>
                <Price Timestamp="<?php echo $price->timestamp ?>" value="<?php echo $price->hist_price ?>"/>
            <?php endforeach ?>
		</value>
	</Property>
<?php if ($certificate instanceof BonusCertificate) { ?>
	<Property>
		<name>
			Barrier Level
		</name>
		<value>
			<?php echo $certificate->barrier_level ?>
		</value>
	</Property>
	<Property>
		<name>
			Hit
		</name>
		<value>
			<?php echo $certificate->hit ? 'true' : 'false' ?>
		</value>
	</Property>
<?php } ?>
<?php if ($certificate instanceof GuaranteeCertificate) { ?>
	<Property>
		<name>
			Participation Rate
		</name>
		<value>
			<?php echo $certificate->participation_rate ?>
		</value>
	</Property>
<?php } ?>
	<Property>
		<name>
			Documents
		</name>
		<value>
			<?php foreach ($certificate->document_list as $document): ?>
			<File name="<?php echo $document->fileName ?>" type="<?php echo $document->fileFormat ?>"/>
			<?php endforeach ?>
		</value>
	</Property>
</Certificate>