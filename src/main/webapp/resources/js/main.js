function exportCharts() {
	personelImg = PF('personelChart').exportAsImage();
	departmentImg = PF('departmentChart').exportAsImage();
	document.getElementById('performanceChartDlgForm:performanceHidden').value = personelImg.src;
	document.getElementById('performanceChartDlgForm:departmentHidden').value = departmentImg.src;
}