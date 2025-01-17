$(document).ready(function () {
	$('#instruments_item_menu').addClass('showMenu');
});
$('#generate').on('click', function() {
	var parent = document.createElement("div");
	parent.classList.add("tools_block_result");
	var element = document.createElement("p");
	parent.appendChild(element);
	let type='';
	if (document.getElementById('short').checked){
		type = '?type=SHORT';
	} else if (document.getElementById('long').checked){
		type = '?type=LONG';
	} else if (document.getElementById('unlimited').checked){
		type = '?type=UNLIMITED';
	}
	fetch('/tools/madness/random' + type).then(data => data.text()) .then(html => element.innerHTML = html);
    document.getElementById('content_block').prepend(parent);
});
$('#clear').on('click', function() {
	  const myNode = document.getElementById("content_block");
	  while (myNode.firstChild) {
	    myNode.removeChild(myNode.lastChild);
	  }
});