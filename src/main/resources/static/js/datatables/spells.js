$(document).ready(function() {
	var scrollEventHeight = 0;
	var rowSelectIndex = 0;
	var table = $('#spells').DataTable({
		ajax : '/data/spells?sourceTypes=' + sourceTypes,
		dom: 't',
		serverSide : true,
        deferRender: true,
        scrollCollapse: true,
		iDisplayLength : 40,
		order : [[0, 'asc'], [2, 'asc']],
		select: true,
		select: {
			style: 'single'
		},
        searchPanes: {
            initCollapsed: true,
            viewCount: false,
            dtOpts: {
                select: {
                    //style: 'multi'
                },
				searching: false,
            },
			orderable: false
        },
		columns : [
		{
			data : 'level',
		},
		{
			data : 'school',
		},
		{
			data : "name",
			render : function(data, type, row) {
				if (type === 'display') {
					var result ='<div class="tip spell_lvl" title="'+(row.level ===  0 ? 'Заговор' : row.level + ' уровень заклинания') +'">' + (row.level ===  0 ? '◐' : row.level) + '</div>';
					result+='<div class="spell_name">' + row.name;
					result+='<span>' + row.englishName + '</span></div>';
					result+='<div class="spell_school">' + row.school + '</div>';
					return result;
				}
				return data;
			}
		}, 
		{
			data : 'englishName',
		},
		{
			data : "classes",
			searchable: false,
		},
		{
			data : "damageType",
			searchable: false
		},
		{
			data : "ritual",
			searchable: false
		},
		{
			data : "concentration",
			searchable: false
		},
		],
		columnDefs : [
			{
				"targets": [ 0,1,3,4,5,6,7 ],
				"visible": false
			},
		],
		language : {
			processing : "Загрузка...",
			searchPlaceholder: "Поиск ",
			search : "_INPUT_",
			lengthMenu : "Показывать _MENU_ записей на странице",
			zeroRecords : "Ничего не найдено",
			info : "Показано _TOTAL_",
			infoEmpty : "Нет доступных записей",
			infoFiltered : "из _MAX_",
		    loadingRecords: "Загрузка...",
	        searchPanes: {
	            title: {
	                 _: 'Выбрано фильтров - %d',
	                 0: 'Фильтры не выбраны',
	                 1: 'Один фильтр выбран'
	            },
                collapseMessage: 'Свернуть все',
                showMessage: 'Развернуть все',
                clearMessage: 'Сбросить фильтры'
	        }
		},
		initComplete: function(settings, json) {
			scrollEventHeight = document.getElementById('scroll_load_simplebar').offsetHeight - 300;
		    const simpleBar = new SimpleBar(document.getElementById('scroll_load_simplebar'));
		    simpleBar.getScrollElement().addEventListener('scroll', function(event){
		    	if (simpleBar.getScrollElement().scrollTop > scrollEventHeight){
		    		  table.page.loadMore();
		    	      simpleBar.recalculate();
		    	      scrollEventHeight +=750;
		    	}
		    });
		    table.searchPanes.container().prependTo($('#searchPanes'));
		    table.searchPanes.container().hide();
		},
		createdRow: function (row, data, dataIndex) {
			if(data.homebrew){
				$(row).addClass('custom_source');
				if(localStorage.getItem('homebrew_source') != 'true'){
					$(row).addClass('hide_block');
				}
			}
		},
		drawCallback: function ( settings ) {
			if(localStorage.getItem('homebrew_source') == 'false'){
				for(; rowSelectIndex < table.data().count(); rowSelectIndex++){
					if(!table.rows(rowSelectIndex).data()[0].homebrew){
						$('#spells tbody tr:eq('+rowSelectIndex+')').click();
						break;
					}
				}
			}
			if(rowSelectIndex === 0 && selectedSpell === null){
				if (!$('#list_page_two_block').hasClass('block_information') && selectedSpell === null){
					return;
				}
				$('#spells tbody tr:eq('+rowSelectIndex+')').click();
			}
			if (selectedSpell) {
				selectSpell(selectedSpell);
				let rowIndexes = [];
				table.rows( function ( idx, data, node ) {
					if(data.id === selectedSpell.id){
						rowIndexes.push(idx);
					}
					return false;
				});
				rowSelectIndex = rowIndexes[0];
			}
			table.row(':eq('+rowSelectIndex+')', { page: 'current' }).select();
		}
	});

	$('#spells tbody').on('click', 'tr', function () {
		if(!document.getElementById('list_page_two_block').classList.contains('block_information')){
			document.getElementById('list_page_two_block').classList.add('block_information');
		}
		var tr = $(this).closest('tr');
		var table = $('#spells').DataTable();
		var row = table.row( tr );
		rowSelectIndex = row.index();
		selectSpell(row.data());
		selectedSpell = null;
	});
});
function selectSpell(data){
	$('#spell_name').html(data.name);
	$('#level').html((data.level ===  0 ? 'Заговор, ' : data.level +' уровень, ') + data.school + (data.ritual ? ' (ритуал)' : ''));
	$('#timecast').html(data.timeCast);
	$('#distance').html(data.distance);
	$('#components').html(data.components);
	$('#duration').html(data.duration);
	
	var source = (data.homebrew ? '<span class="tip homebrew_text" title="Homebrew - не является официальным.">Homebrew</span> - ' : '') + '<span class="tip" data-tipped-options="inline: \'inline-tooltip-source-' +data.id+'\'">' + data.bookshort + '</span>';
	source+= '<span id="inline-tooltip-source-'+ data.id + '" style="display: none">' + data.book + '</span>';
	$('#source_spell').html(source);

	const classIconsElement = document.getElementById('class_icons');
	while (classIconsElement.firstChild) {
		classIconsElement.removeChild(classIconsElement.firstChild);
	}
	data.classes.forEach(element => {
		var a = document.createElement("a");
		a.href = '/classes/' + element.englishName; 
		a.title = element.name;
		a.classList.add('tip', 'icon', 'icon_' + element.englishName.toLowerCase());
		classIconsElement.appendChild(a);
	});
	document.title = data.name;
	history.pushState('data to be passed', '', '/spells/' + data.englishName.split(' ').join('_'));
	const url = '/spells/fragment/' + data.id;
	$("#content_block").load(url);
}
var timer, delay = 300;
$('#search').bind('keydown blur change', function(e) {
    var _this = $(this);
    clearTimeout(timer);
    timer = setTimeout(function() {
		if($('#search').val()){
			$('#text_clear').show();
		}
		else {
			$('#text_clear').hide();
		}
		const table = $('#spells').DataTable();
		table.tables().search($('#search').val()).draw();
    }, delay );
});
$('#btn_filters').on('click', function() {
	var table = $('#spells').DataTable();
	table.searchPanes.container().toggle();
});
$('#text_clear').on('click', function () {
	$('#search').val('');
	const table = $('#spells').DataTable();
	table.tables().search($(this).val()).draw();
	$('#text_clear').hide();
});
$('#btn_close').on('click', function() {
	document.getElementById('list_page_two_block').classList.remove('block_information');
	localStorage.removeItem('selected_spell');
	history.pushState('data to be passed', 'Заклинания', '/spells/');
});