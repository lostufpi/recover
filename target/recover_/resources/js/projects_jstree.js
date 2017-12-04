/**
 * 
 */
$(function () {
	$('#jstree3')
	.on('changed.jstree', 	function (e, data) {
								var i, j, r = [];
								for(i = 0, j = data.selected.length; i < j; i++) {
									r.push(data.instance.get_node(data.selected[i]).text);
								}
								$('#event_result').html('Selected:<br /> ' + r.join(', '));
							}
	).jstree({	"types" : {	
		"pacote" : {},
		"classe" : {"icon" : "glyphicon glyphicon-ok"}
	},
	'plugins':["types"],
	'core' : {
		'data' : [
		          {
		        	  "text" : "dolphin (am, ce e pi)", "type": "pacote", "state" : {"opened" : true},
		        	  "children" : [
		        	                {	"text" : "procedimentos (am e ce)",
		        	                	"children" : [
		        	                	              {"text" : "IncidenciaCalculo.java (am e ce)", "type" : "classe"},
		        	                	              {"text" : "ProcedimentoInterface.java (am e ce)"},
		        	                	              {"text" : "SituacoesProcedimentoRealizado.java (am e ce)"}
		        	                	              ]
		        	                },
		        	                {	"text" : "procedimento (pi)", "type" : "pacote",
		        	                	"children" : [
		        	                	              {	"text" : "calculo (pi)", "type" : "pacote",
		        	                	            	  "children" : [
		        	                	            	                {	"text" : "CalculoFilme.java (pi)"}]
		        	                	              }]
		        	                },
		        	                {"text" : "resumo (am, ce e pi)", "type" : "pacote"},
		        	                {"text" : "financeiro (am, ce e pi)",		"type" : "pacote",	"state" : {"opened" : true},
		        	                	"children" : [
		        	                	              {"text" : "imposto (pi)", "type" : "pacote",
		        	                	            	  "children" : [
		        	                	            	                {"text" : "InformacaoImposto.java (pi)", "type" : "classe"}
		        	                	            	                ]
		        	                	              },
		        	                	              {"text" : "FluxoFinanceiro.java (am, ce e pi)", "type" : "classe"},
		        	                	              {"text" : "sefip (am, ce e pi)",	"type" : "pacote",
		        	                	            	  "children" : [
		        	                	            	                {"text" : "Empresa.java (am, ce e pi)", "type" : "classe"}
		        	                	            	                ]
		        	                	              },
		        	                	              {"text" : "repasse (am e ce)", "type" : "pacote", "state" : {"opened" : true},
		        	                	            	  "children" : [
		        	                	            	                {"text" : "SituacoesRepasse.java* (am, ce e pi)", "type" : "classe"}
		        	                	            	                ]
		        	                	              },
		        	                	              {"text" : "repasse (pi)", "type" : "pacote", "state" : {"opened" : true},
		        	                	            	  "children" : [
		        	                	            	                {"text" : "SituacoesRepasse.java* (am, ce e pi)", "type" : "classe"}
		        	                	            	                ]
		        	                	              },
		        	                	              {"text" : "faturamento (am, ce e pi)", "type" : "pacote",
		        	                	            	  "children" : [
		        	                	            	                {"text" : "CartaRemessa.java (am, ce e pi)", "type" : "classe", "state" : {"selected" : true}}
		        	                	            	                ]
		        	                	              }
		        	                	              ]
		        	                },
		        	                {"text" : "enums (am, ce e pi)", "type" : "pacote",
		        	                	"children" : [
		        	                	              {"text" : "guias (am, ce e pi)", "type" : "pacote",
		        	                	            	  "children" : [
		        	                	            	                {"text" : "GrauParticipacaoEnum.java (am, ce e pi)", "type" : "classe"},
		        	                	            	                {"text" : "CommandCalculoProcedimento.java (am e pi)", "type" : "classe"}
		        	                	            	                ]
		        	                	              },
		        	                	              {"text" : "SituacaoValidacaoEnum.java (pi)", "type" : "classe"}
		        	                	              ]
		        	                }
		        	                ]
		          }

		          ]
	}
	});

	Highcharts.chart('container', {
		chart: {
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			type: 'pie'
		},
		title: {
			text: 'CartaRemessa.java'
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
		series: [{
			name: 'Participação',
			colorByPoint: true,
			data: [{
				name: 'AM',
				y: 4.55
			}, {
				name: 'AM e CE',
				y: 3.03
			}, {
				name: 'PI',
				y: 7.58
			}, {
				name: 'AM, CE e PI',
				y: 84.84
			}]
		}]
	});
});