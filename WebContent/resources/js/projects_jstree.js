/**
 * 
 */


$(function () {
	
	/**/
	var jsonTree = document.querySelector("#jsonJstree").textContent;
	//ocultar o elemento
	document.querySelector("#jsonJstree").style.display = "none";
	
	var jsonObj = eval('({' +jsonTree+'})'); //
	/*http://www.devmedia.com.br/json-java-script-object-notation/6993*/
	var json = {	"types" : {	
		"pacote" : {},
		"classe" : {"icon" : "glyphicon glyphicon-ok"}
		},
		'plugins':["types"],
		'core' : jsonObj
	};
		
	$('#jstree3')
	.on('changed.jstree', 	function (e, data) {
								var i, j, r = [];
								for(i = 0, j = data.selected.length; i < j; i++) {
									r.push(data.instance.get_node(data.selected[i]).id);
								}
								//$('#event_result').html('Selected:<br /> ' + r.join(', '));
								$("#formHCharts\\:inputSelectedNode").val(r.join(', '));
								 /*O que devo fazer:
								  * criar um URL que contenha o id do nó, que será utilizados pelo lado do servidor
								  * inicializar um objeto XMLHttpRequest (através da função initRequest)
								  * solicitar que o objeto XMLHttpRequest envie uma solicitação assíncrona para o servidor.
								  * */
							}
	).jstree(json);
			
	drawHC();
});

function drawHC(){	
	
	var jsonHC = document.querySelector("#jsonHighcharts").textContent;
	document.querySelector("#jsonHighcharts").style.display = "none";
	
	var titleHC = document.querySelector("#titleHighcharts").textContent;
	document.querySelector("#titleHighcharts").style.display = "none";
	
	seriesHC_json = eval(jsonHC);
	

	Highcharts.chart('containerHC', {
		chart: {
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			type: 'pie'
		},
		title: {
			text: titleHC
		},
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					enabled: true,
					format: '<b>{point.name}</b>: {point.percentage:.1f} %',
					style: {
						color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
					}
				}
			}
		},
		series: seriesHC_json
	});
}

function verify_drawHC(data){
	if(data.status=="success"){
		drawHC();
	}
}