sourceTypes = localStorage.getItem('first_visit');
$(document).ready(function () {
    function checkWidth() {
        let windowWidth = $('body').innerWidth();
        let elem = $("#body");

        const check = localStorage.getItem('compact_menu');

        if (check === 'true' && windowWidth > 1200) {
            elem.addClass('compact_menu');
            return;
        }

        // if (windowWidth < 1400) {
        //     elem.addClass('compact_menu');
        // } else {
        //     elem.removeClass('compact_menu');
        // }

        // if (windowWidth > 1200) {
        //     $('#list_page_two_block').addClass('block_information');
        // }

        if (windowWidth < 1200) {
            elem.removeClass('compact_menu');
            $('#body').removeClass('full_screen_right_block');
            // $('#list_page_two_block').removeClass('block_information');

            window.addEventListener('popstate', handlerBackBtnFromCard);
        }

        if (windowWidth > 1200) {
            window.removeEventListener('popstate', handlerBackBtnFromCard)
        }
    }

    checkWidth(); // проверит при загрузке страницы
    $(window).resize(function () {
        checkWidth(); // проверит при изменении размера окна клиента
    });

    Tipped.delegate('.tip', {
        skin: 'dnd5',
    });
    Tipped.delegate('.tip_scroll', {
        skin: 'dnd5',
        afterUpdate: function (content, element) {
            content.classList.add('tooltip_scroll');
        },
        onShow: function (content, element) {
            var simpleBar = new SimpleBar(content);
            simpleBar.recalculate();
        },
    });
    Tipped.delegate('.tip_spell', {
        ajax: {
            url: '/spells/id',
            type: 'get',
            success: function (data, textStatus, jqXHR) {
                return {
                    content: jqXHR.responseText
                };
            }
        },
        afterUpdate: function (content, element) {
            content.classList.add('tooltip_scroll');
        },
        onShow: function (content, element) {
            var simpleBar = new SimpleBar(content);
            simpleBar.recalculate();
        },
        skin: 'dnd5',
    });
    $.sidebarMenu($('.sidebar-menu'));
    $('.ajax-popup-link').magnificPopup({
        type: 'ajax',
        closeOnBgClick: true,
		modal: true,
    });
    $(document).on('click', '.popup-modal-dismiss', function (e) {
        e.preventDefault();
        $.magnificPopup.close();
    });
    let path = $(location).attr('pathname');
    if (path.startsWith('/classes')) {
        $('#charachter_item_menu_classes, #charachter_item_menu').addClass('active');
    } else if (path.startsWith('/races')) {
        $('#charachter_item_menu_races, #charachter_item_menu').addClass('active');
    } else if (path.startsWith('/traits')) {
        $('#charachter_item_menu_traits, #charachter_item_menu').addClass('active');
    } else if (path.startsWith('/options')) {
        $('#charachter_item_menu_options, #charachter_item_menu').addClass('active');
    } else if (path.startsWith('/backgrounds')) {
        $('#charachter_item_menu_backgrounds, #charachter_item_menu').addClass('active');
    } else if (path.startsWith('/spells')) {
        $('#spells_item_menu').addClass('active');
    } else if (path.startsWith('/treasures')) {
        $('#treasury_item_menu_treasures, #treasury_item_menu').addClass('active');
    } else if (path.startsWith('/items/magic')) {
        $('#treasury_item_menu_magic, #treasury_item_menu').addClass('active');
    } else if (path.startsWith('/weapons')) {
        $('#items_item_menu_weapons, #items_item_menu').addClass('active');
    } else if (path.startsWith('/armors')) {
        $('#items_item_menu_armors, #items_item_menu').addClass('active');
    } else if (path.startsWith('/items')) {
        $('#items_item_menu_items, #items_item_menu').addClass('active');
    } else if (path.startsWith('/bestiary')) {
        $('#bestiary_item_menu').addClass('active');
    } else if (path.startsWith('/screens')) {
        $('#screens_item_menu').addClass('active');
    } else if (path.startsWith('/gods')) {
        $('#workshop_item_menu, #workshop_item_menu_gods').addClass('active');
    } else if (path.startsWith('/rules')) {
        $('#workshop_item_menu, #workshop_item_menu_rules').addClass('active');
    } else if (path.startsWith('/books')) {
        $('#workshop_item_menu, #workshop_item_menu_books').addClass('active');
    } else if (path.startsWith('/tools/trader')) {
        $('#instruments_item_menu, #instruments_item_menu_trader').addClass('active');
    } else if (path.startsWith('/tools/encounters')) {
        $('#instruments_item_menu, #instruments_item_menu_encounters').addClass('active');
    } else if (path.startsWith('/tools/treasury')) {
        $('#instruments_item_menu, #instruments_item_menu_treasury').addClass('active');
    } else if (path.startsWith('/tools/wildmagic')) {
        $('#instruments_item_menu, #instruments_item_menu_wildmagic').addClass('active');
    } else if (path.startsWith('/tools/madness')) {
        $('#instruments_item_menu, #instruments_item_menu_madness').addClass('active');
    }

    const spoilers = document.querySelectorAll('.header')

    for (let spoiler of spoilers) {
        spoiler.addEventListener('click', function (event) {
            const div = spoiler.nextElementSibling;
            const classList = div.classList;

            const targetID = event.target.closest('button')
                ? event.target.closest('button').getAttribute('id')
                : event.target.getAttribute('id');

            if (!!targetID && targetID.match(/_clear_btn/gi)) {
                return;
            }

            classList.toggle('hide');
        })
    }
});

function handlerBackBtnFromCard() {
    let pathLevels = window.location.pathname
                           .split("/")
                           .filter(function (path) {
                               return !!path;
                           });

    if (pathLevels.length > 2 || !pathLevels.length) {
        return
    }

    let tableName;

    switch (pathLevels.length) {
        case 1:
            tableName = pathLevels[0];

            break;
        case 2:
            if (window.location.pathname.match(/^\/items\/magic/)) {
                tableName = 'items_magic';

                break;
            }

            break;
        default:
            return;
    }

    if (!tableName) {
        return;
    }

    switch (tableName) {
        case 'bestiary':
            tableName = 'creatures';

            break;
        default:
            break;
    }

    const table = $(`#${ tableName }`).dataTable().api();

    if (table.context.length > 1 || !table.context.length) {
        return
    }

    table.rows().deselect();
}

$("#btn_full_screen, #btn_exet_full_screen").click(function () {
    $("#body").toggleClass("full_screen_right_block");
});

$('li').click(function () {
    localStorage.setItem('selected_item_menu', this.id);
});
;(function ($, window, document, undefined) {
    'use strict';
    var $html = $('html');
    $html.on('click.ui.dropdown', '.js-dropdown', function (e) {
        e.preventDefault();
        if ($(this).hasClass('multiselect')) {
            $(this).addClass('is-open');
        } else {
            $(this).toggleClass('is-open');
        }
    });
    $html.on('click.ui.dropdown', '.js-dropdown [data-dropdown-value]', function (e) {
        e.preventDefault();
        var $item = $(this);
        var $dropdown = $item.parents('.js-dropdown');
        if ($dropdown.hasClass('multiselect')) {
            let $span = $dropdown.find('.js-dropdown__current');
            let $input = $dropdown.find('.js-dropdown__input');
            if (!$span.text().includes($item.text())) {
                if ($input.val() == '') {
                    $span.html('<span class="selected_item">' + $item.text() + '</span>');
                    $input.val($item.data('dropdown-value'));
                } else {
                    $span.html($span.html() + '<span class="selected_item">' + $item.text() + '</span>');
                    $input.val($input.val() + '|' + $item.data('dropdown-value'));
                }
            } else {
                $span.html($span.html().replace('<span class="selected_item">' + $item.text() + '</span>', ''));
                $input.val($input.val().replace('|' + $item.data('dropdown-value'), ''));
                $input.val($input.val().replace($item.data('dropdown-value'), ''));
                if ($input.val().trim() == '') {
                    $span.text($span.attr('title'));
                }
            }
            $item.toggleClass('selected');
        } else {
            $dropdown.find('.js-dropdown__current').text($item.text());
            $dropdown.find('.js-dropdown__input').val($item.data('dropdown-value'));
            $dropdown.find('li').removeClass('selected');
            $item.addClass('selected');
        }
        $dropdown.find('.js-dropdown__input').change();
    });
    $html.on('click.ui.dropdown', function (e) {
        var $target = $(e.target);
        if (!$target.parents().hasClass('js-dropdown')) {
            $('.js-dropdown').removeClass('is-open');
        }
    });
})(jQuery, window, document);

// Копирование ссылки в буфер
function copyToClipboard(text) {
    var inputc = document.body.appendChild(document.createElement("input"));
    inputc.value = window.location.href;
    inputc.focus();
    inputc.select();
    document.execCommand('copy');
    inputc.parentNode.removeChild(inputc);
    // alert("URL Copied.");
}

(function () {
    var cx = '';
    var $gcse = $('#script');
    $gcse.type = 'text/javascript';
    $gcse.async = true;
    $gcse.src = 'https://cse.google.com/cse.js?cx=' + cx;
    var s = document.getElementsByTagName('script')[0];
    if (s.parentNode === Node.ELEMENT_NODE) {
        s.parentNode.insertBefore($gcse, s);
    }
})();
window.onload = function () {
    if ($('#gsc-i-id1')) {
        $('#gsc-i-id1').attr("placeholder", "Поиск по сайту");
    }
};
