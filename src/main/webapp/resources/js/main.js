function exportCharts() {
	personelImg = PF('personelChart').exportAsImage();
	departmentImg = PF('departmentChart').exportAsImage();
	monthImg = PF('monthChart').exportAsImage();
	document.getElementById('performanceChartDlgForm:performanceHidden').value = personelImg.src;
	document.getElementById('performanceChartDlgForm:departmentHidden').value = departmentImg.src;
	document.getElementById('performanceChartDlgForm:monthHidden').value = monthImg.src;
}

function exportMemzucCharts(){
	chart0 = PF('chart0').exportAsImage();
	chart1 = PF('chart1').exportAsImage();
	chart2 = PF('chart2').exportAsImage();
	chart3 = PF('chart3').exportAsImage();
	chart4 = PF('chart4').exportAsImage();
	chart5 = PF('chart5').exportAsImage();
	chart6 = PF('chart6').exportAsImage();
	
	document.getElementById('chartDlgForm:chart0Hidden').value = chart0.src;
	document.getElementById('chartDlgForm:chart1Hidden').value = chart1.src;
	document.getElementById('chartDlgForm:chart2Hidden').value = chart2.src;
	document.getElementById('chartDlgForm:chart3Hidden').value = chart3.src;
	document.getElementById('chartDlgForm:chart4Hidden').value = chart4.src;
	document.getElementById('chartDlgForm:chart5Hidden').value = chart5.src;
	document.getElementById('chartDlgForm:chart6Hidden').value = chart6.src;
}