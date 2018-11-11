var listUser ;

$(document).ready(function(){
	var roomid = GetURLParameter('roomid');
	var url = "/api/room/members/"+roomid;
	console.log("roomid: "+roomid);
	console.log("url: "+ url);
	$.ajax({
		url:url,
		type:'get',
		success: function(response){
			var code = response.code;
			if(code == 1){
				listUser = response.data;
				console.log("Success");
			}else {
				console.log("Faild");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
	loadData();
});

loadData = function(){
	for (var i = 0; i < listUser.length; i++) {
		var user = listUser[i];
		var username = user.username;
		var firstName = user.firstName;
		var lastName = user.lastName;
		addUserMember(firstName,lastName,username);
		addUserMemberToSelectBox(firstName,lastName,username);
	}
}



addUserMember = function(firstName, lastName, username){
	var nameShow = firstName+" "+ lastName + " - "+ username;
	var userSpanElement = document.createElement('span');
	userSpanElement.classList.add('user_span');
	var userTextNode = document.createTextNode(nameShow);
	userSpanElement.appendChild(userTextNode);
	var lstUserArea = document.querySelector('.lst_user');
	lstUserArea.appendChild(userSpanElement);
}

addUserMemberToSelectBox = function(firstName, lastName, username){
	var nameShow = firstName+" "+ lastName + " - "+ username;
	var userOptionElement = document.createElement('option');
	var userTextNode = document.createTextNode(nameShow);
	userOptionElement.appendChild(userTextNode);
	var lstUserSelect = document.querySelector('#selectuser');
	lstUserSelect.appendChild(userOptionElement);
}

function GetURLParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
	var sURLVariables = sPageURL.split('&');
	for (var i = 0; i < sURLVariables.length; i++){
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam)
		{
			return sParameterName[1];
		}
	}
}