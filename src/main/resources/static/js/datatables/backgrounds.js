$(document).ready(function () {
    $('#charachter_item_menu').addClass('showMenu');
    let pageInitiated = false;
    var scrollEventHeight = 0;
    var rowSelectIndex = 0;
    var table = $('#backgrounds').DataTable({
        ajax: '/data/backgrounds',
        dom: 't',
        serverSide: true,
        deferRender: true,
        iDisplayLength: 80,
        scrollCollapse: true,
        select: true,
        select: {
            style: 'single',
            toggleable: false
        },
        columns: [
            {
                data: "name",
                render: function (data, type, row) {
                    if (type === 'display') {
                        var result = '<h3 class="row_name"><span>' + row.name;
                        result += '</span> <ename>[' + row.englishName + ']</ename></h3>';
                        return result;
                    }
                    return data;
                }
            },
            {
                data: 'englishName',
            },
            {
                data: 'skills',
                searchable: false
            },
            {
                data: 'bookshort',
                searchable: false,
            },
        ],
        searchCols: [
            null,
            null,
            getSearchColumn('skill', 'backgrounds'),
            getSearchColumn('book', 'backgrounds'),
        ],
        columnDefs: [
            {
                "targets": [ 1, 2, 3 ],
                "visible": false
            },
        ],
        buttons: [
            {} ],
        order: [ [ 0, 'asc' ] ],
        language: {
            processing: "Загрузка...",
            searchPlaceholder: "Поиск ",
            search: "_INPUT_",
            lengthMenu: "Показывать _MENU_ записей на странице",
            zeroRecords: "Ничего не найдено",
            info: "Показано _TOTAL_",
            infoEmpty: "Нет доступных записей",
            infoFiltered: "из _MAX_",
            loadingRecords: "Загрузка...",
        },
        initComplete: function (settings, json) {
            restoreFilter('backgrounds');

            scrollEventHeight = document.getElementById('scroll_load_simplebar').offsetHeight - 400;
            const simpleBar = new SimpleBar(document.getElementById('scroll_load_simplebar'));
            simpleBar.getScrollElement().addEventListener('scroll', function (event) {
                if (simpleBar.getScrollElement().scrollTop > scrollEventHeight) {
                    table.page.loadMore();
                    simpleBar.recalculate();
                    scrollEventHeight += 750;

                    addEventListeners(true);
                }
            });
        },
        drawCallback: function (settings) {
            addEventListeners();

            if (!pageInitiated && window.innerWidth >= 1200) {
                $('#list_page_two_block').addClass('block_information');
            }

            if (rowSelectIndex === 0 && selectedBackground === null) {
                if (!$('#list_page_two_block').hasClass('block_information')) {
                    return;
                }
            }
            if (selectedBackground) {
                selectBackground(selectedBackground);
                var rowIndexes = [];
                table.rows(function (idx, data, node) {
                    if (data.id === selectedBackground.id) {
                        rowIndexes.push(idx);
                    }
                    return false;
                });
                rowSelectIndex = rowIndexes[0];
            }
            $('#backgrounds tbody tr:eq(' + rowSelectIndex + ')').click();
            table.row(':eq(' + rowSelectIndex + ')', { page: 'current' }).select();

            pageInitiated = true;
        },
        createdRow: function (row, data, dataIndex) {
            if (data.homebrew) {
                $(row).addClass('custom_source');
                if (!isHomebrewShowed('backgrounds')) {
                    $(row).addClass('hide_block');
                }
            } else if (data.setting) {
                $(row).addClass('setting_source');
                if (!isSettingsShowed('backgrounds')) {
                    $(row).addClass('hide_block');
                }
            }
        },
    });
    $('#backgrounds tbody').on('mouseup', 'tr', function (e) {
        if (e.which == 2) {
            var tr = $(this).closest('tr');
            var row = table.row(tr);
            rowSelectIndex = row.index();
            var data = row.data();
            window.open('/backgrounds/' + data.englishName.split(' ').join('_'));
        }
    });
    $('#backgrounds tbody').on('click', 'tr', function (e) {
        if (!document.getElementById('list_page_two_block').classList.contains('block_information')) {
            document.getElementById('list_page_two_block').classList.add('block_information');
        }
        var tr = $(this).closest('tr');
        var table = $('#backgrounds').DataTable();
        var row = table.row(tr);
        var data = row.data();
        if ((window.navigator.userAgent.indexOf("Mac") !== -1 && e.metaKey) || e.ctrlKey) {
            window.open('/backgrounds/' + data.englishName.split(' ').join('_'));
        }
        rowSelectIndex = row.index();
        selectBackground(data);
    });
    $('#search').on('keyup click', function () {
        if ($(this).val()) {
            $('#text_clear').show();
        } else {
            $('#text_clear').hide();
        }
        table.tables().search($(this).val()).draw();
    });
});

function addEventListeners(force = false) {
    $(document).ready(function () {
        onDeselectListener();
    });

    $(window).resize(function () {
        onDeselectListener();
    });

    if (force) {
        onDeselectListener();
    }
}

function onDeselectListener() {
    const element = $('#backgrounds');
    const table = element.dataTable().api();

    if (window.innerWidth < 1200 && !element.hasClass('has-deselect-handler')) {
        table.on('deselect.dt', closeHandler);
        element.addClass('has-deselect-handler');

        return
    }

    if (window.innerWidth >= 1200) {
        table.off('deselect.dt', closeHandler);
        element.removeClass('has-deselect-handler');
    }
}

function selectBackground(data) {
    $('#background_name').text(data.name);
    $('#english_name').html(data.englishName);

    document.title = data.name + ' (' + data.englishName + ')' + ' | Предыстории D&D 5e';
    history.pushState('data to be passed', '', '/backgrounds/' + data.englishName.split(' ').join('_'));
    var url = '/backgrounds/fragment/' + data.id;
    $("#content_block").load(url);
    selectedBackground = data;
}

$('#text_clear').on('click', function () {
    $('#search').val('');
    var table = $('#backgrounds').DataTable();
    table.tables().search($(this).val()).draw();
    $('#text_clear').hide();
});

$('#btn_close').on('click', function () {
    if (window.innerWidth < 1200) {
        $('#backgrounds').dataTable().api().rows().deselect();

        return;
    }

    closeHandler();
});

function closeHandler() {
    document.getElementById('list_page_two_block').classList.remove('block_information');
    selectedBackground = null;

    $.magnificPopup.close();

    history.pushState('data to be passed', '', '/backgrounds/');
}

$('#btn_filters').on('click', function () {
    $('#searchPanes').toggleClass('hide_block');

    $('#btn_filters').toggleClass('open');
});
$('.skill_checkbox').on('change', function (e) {
    let properties = $('input:checkbox[name="skill"]:checked').map(function () {
        return this.value;
    }).get().join('|');
    $('#backgrounds').DataTable().column(2).search(properties, true, false, false).draw();
    if (properties) {
        $('#skill_clear_btn').removeClass('hide_block');
    } else {
        $('#skill_clear_btn').addClass('hide_block');
    }

    saveFilter('backgrounds');
});
$('#skill_clear_btn').on('click', function () {
    $('#skill_clear_btn').addClass('hide_block');
    $('.skill_checkbox').prop('checked', false);
    $('#backgrounds').DataTable().column(2).search("", true, false, false).draw();

    saveFilter('backgrounds');
});
$('.book_checkbox').on('change', function (e) {
    let properties = $('input:checkbox[name="book"]:checked').map(function () {
        return this.value;
    }).get().join('|');
    $('#backgrounds').DataTable().column(3).search(properties, true, false, false).draw();
    if (properties) {
        $('#book_clear_btn').removeClass('hide_block');
    } else {
        $('#book_clear_btn').addClass('hide_block');
    }
    saveFilter('backgrounds');
});
$('#book_clear_btn').on('click', function () {
    $('#book_clear_btn').addClass('hide_block');
    $('.book_checkbox').prop('checked', true);
    $('#backgrounds').DataTable().column(3).search("", true, false, false).draw();
    saveFilter('backgrounds');
});

