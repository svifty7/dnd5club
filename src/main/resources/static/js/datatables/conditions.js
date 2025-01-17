$(document).ready(function () {
    let pageInitiated = false;
    var scrollEventHeight = 0;
    var rowSelectIndex = 0;
    var table = $('#conditions').DataTable({
        ajax: '/data/conditions',
        dom: 'tiS',
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
                        var result = '<div class="wrapper"><div class="content"><h3 class="row_name"><span>' + row.name;
                        result += '</span><span class="books tip" title="' + row.book + '">' + row.book + '</span></h3>';
                        result += '<div class="two_row"><ename>' + row.englishName + '</ename></dv></dv></dv>';
                        return result;
                    }
                    return data;
                }
            },
            {
                data: 'englishName',
            },
            {
                data: "type",
                searchable: false
            },
        ],
        columnDefs: [
            {
                "targets": [ 1, 2 ],
                "visible": false
            },
        ],
        rowGroup: {
            dataSrc: 'type',
        },
        order: [ [ 2, 'asc' ], [ 0, 'asc' ] ],
        language: {
            processing: "Загрузка...",
            searchPlaceholder: "Поиск ",
            search: "_INPUT_",
            lengthMenu: "Показывать _MENU_ записей на странице",
            zeroRecords: "Ничего не найдено",
            info: "Показано _TOTAL_",
            infoEmpty: "Нет доступных записей",
            infoFiltered: "из _MAX_",
            loadingRecords: "Загрузка..."
        },
        initComplete: function (settings, json) {
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
            
            if (!$('#list_page_two_block').hasClass('block_information') && !selectedCondition) {
                return;
            }

            if (selectedCondition) {
                selectCondition(selectedCondition);
                var rowIndexes = [];
                table.rows(function (idx, data, node) {
                    if (data.id === selectedCondition.id) {
                        rowIndexes.push(idx);
                    }
                    return false;
                });
                rowSelectIndex = rowIndexes[0];
                $('#conditions tbody tr:eq(' + rowSelectIndex + ')').click();
                table.row(':eq(' + rowSelectIndex + ')', { page: 'current' }).select();
            }

            pageInitiated = true;
        }
    });

    $('#conditions tbody').on('click', 'tr', function (e) {
        if (!document.getElementById('list_page_two_block').classList.contains('block_information')) {
            document.getElementById('list_page_two_block').classList.add('block_information');
        }
        var tr = $(this).closest('tr');
        var table = $('#conditions').DataTable();
        var row = table.row(tr);
        var data = row.data();
        if (data === undefined) {
            return;
        }
        selectCondition(data);
        setTimeout(function () {
            e.target.closest('.simplebar-content-wrapper')
             .scrollTo({
                 top: e.target.closest('tr').offsetTop - 16,
                 behavior: "smooth"
             });
        }, 300)
    });
    $('#search').on('keyup click', function () {
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
    const element = $('#conditions');
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

function selectCondition(data) {
    selectedCondition = data;
    $('#condition_name').html(data.name);
    $('#english_name').html(data.englishName);

    document.title = data.name + ' (' + data.englishName + ')' + ' | Киниги D&D 5e';
    history.pushState('data to be passed', '', '/conditions/' + data.englishName.split(' ').join('_'));
    var url = '/conditions/fragment/' + data.id;
    $("#content_block").load(url);
}

$('#search').on('keyup click', function () {
    if ($(this).val()) {
        $('#text_clear').show();
    } else {
        $('#text_clear').hide();
    }
    const table = $('#conditions').DataTable();
    table.tables().search($(this).val()).draw();
});
$('#text_clear').on('click', function () {
    $('#search').val('');
    const table = $('#conditions').DataTable();
    table.tables().search($(this).val()).draw();
    $('#text_clear').hide();
});

$('#btn_close').on('click', function () {
    if (window.innerWidth < 1200) {
        $('#conditions').dataTable().api().rows().deselect();

        return;
    }

    closeHandler();
});

function closeHandler() {
    document.getElementById('list_page_two_block').classList.remove('block_information');
    selectedCondition = null;

    $.magnificPopup.close();

    history.pushState('data to be passed', '', '/conditions/');
}
