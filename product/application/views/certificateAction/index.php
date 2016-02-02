<style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
}
</style>
<table>
	<tr>
		<th>
			ISIN
		</th>
		<th>
			Trading Market
		</th>
		<th>
			Currency
		</th>
		<th>
			Issuer
		</th>
		<th>
			Issuing Price
		</th>
		<th>
			Current Price
		</th>
		<!-- <th>
			Price history
		</th> -->
		<th>
			
		</th>
	</tr>
	<?php foreach ($certificateList as $certificate): ?>
	<tr>
		<td>
			<?php echo $certificate->ISIN ?>
		</td>
		<td>
			<?php echo $certificate->trading_market ?>
		</td>
		<td>
			<?php echo $certificate->currency ?>
		</td>
		<td>
			<?php echo $certificate->issuer ?>
		</td>
		<td>
			<?php echo $certificate->issuing_price ?>
		</td>
		<td>
			<?php echo $certificate->current_price ?>
		</td>
		<!--<td>
			<ul>
			<?php foreach ($certificate->price_hist as $price): ?>
                <li>
                	Timestamp:<?php echo $price->timestamp ?>
                	Price:<?php echo $price->hist_price ?>
            	</li>
            <?php endforeach ?>
		</td>-->
		<td>
			<p><a href="certificateAction/displayAsHtml/<?php echo $certificate->ISIN ?>">Click here to display as HTML</a></p>
			<p><a href="certificateAction/displayAsXml/<?php echo $certificate->ISIN ?>">Click here to display as XML</a></p>
		</td>
	</tr>
	<?php endforeach ?>
</table>       
<script>
	var time = new Date().getTime();
	function refresh() {
         if(new Date().getTime() - time >= 10000) 
             window.location.reload(true);
         else 
             setTimeout(refresh, 10000);
     }

     setTimeout(refresh, 10000);
</script>
