$(document).ready(function(){
	$("#users li").click(function(){
		removeActive();
		$(this).addClass("active");
		var lstChildren = $(this).children();
		var userName = lstChildren[0].value;
		var fullName = lstChildren[1].value;
		$("#hidUserName").val(userName);
		$("#hidFullName").val(fullName);
		$("#message").removeAttr('disabled');
		$("#message").val("");
		sendMessage();
		
	});
	
});

loadData = function (){
	loadData2Popup();
}
removeActive = function(){
	var lstLi = $("#users").children();
	for(var i=0; i< lstLi.length; i++){
		lstLi[i].classList.remove("active");
	}
}

var colors = [
'#2196F3', '#32c787', '#00BCD4', '#ff5652',
'#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

var roleRooms = 
[
{value: "READ", name: "Đọc"},
{value: "WRITE", name: "Ghi"},
{value: "EXPORT", name: "Xuất file"},
{value: "ADD_MEMBER", name: "Thêm người"},
{value: "DELETE_MEMBER", name: "Xóa người dùng"}
];

sendMessage = function(){
	var message = $("#message").val();
	$("#message").val("");
	if (message.trim().length > 0) {
		var fullName = $("#hidFullName").val();
		var lstFullNameSplit = fullName.split(" ");
		var nameDisplay = "";
		var lstMessage = $(".lstMessage");
		for(var i=0; i< lstFullNameSplit.length -1 ; i++){
			nameDisplay += lstFullNameSplit[i].charAt(0);
		}
		nameDisplay+="."+lstFullNameSplit[lstFullNameSplit.length - 1];
		var currentDate = getActualFullDate();
		var messageElement = document.createElement('li');
		messageElement.classList.add('message');
		var avatarElement = document.createElement('i');
		var avatarText = document.createTextNode(lstFullNameSplit[lstFullNameSplit.length - 1][0]);
		avatarElement.appendChild(avatarText);
		avatarElement.style['background-color'] = getAvatarColor(fullName);
		messageElement.appendChild(avatarElement);
		var usernameElement = document.createElement('span');
		var usernameText = document.createTextNode(nameDisplay);
		usernameElement.appendChild(usernameText);
		usernameElement.style['font-weight'] = 'bold';
		messageElement.appendChild(usernameElement);
		var timeElement= document.createElement('span');
		var timeText = document.createTextNode(currentDate);
		timeElement.appendChild(timeText);
		timeElement.style['margin-left'] ='25px';
		timeElement.style['font-style'] ='italic';
		messageElement.appendChild(timeElement);
		var textElement = document.createElement('p');
		var messageText = document.createTextNode(message);
		textElement.appendChild(messageText);
		messageElement.appendChild(textElement);
		var messageArea = document.querySelector('.lstMessage');
		messageArea.appendChild(messageElement);
		messageArea.scrollTop = messageArea.scrollHeight;

	}
	
	
	
}

sendMessage1 = function(){
	var message = "helelel";
	$("#message").val("");
	if (message.trim().length > 0) {
		var fullName = "Nguyen Dinh Thang"
		var lstFullNameSplit = fullName.split(" ");
		var nameDisplay = "";
		var lstMessage = $(".lstMessage");
		for(var i=0; i< lstFullNameSplit.length -1 ; i++){
			nameDisplay += lstFullNameSplit[i].charAt(0);
		}
		nameDisplay+="."+lstFullNameSplit[lstFullNameSplit.length - 1];
		var currentDate = getActualFullDate();
		var messageElement = document.createElement('li');
		messageElement.classList.add('message');
		var avatarElement = document.createElement('i');
		var avatarText = document.createTextNode(lstFullNameSplit[lstFullNameSplit.length - 1][0]);
		avatarElement.appendChild(avatarText);
		avatarElement.style['background-color'] = getAvatarColor(fullName);
		messageElement.appendChild(avatarElement);
		var usernameElement = document.createElement('span');
		var usernameText = document.createTextNode(nameDisplay);
		usernameElement.appendChild(usernameText);
		usernameElement.style['font-weight'] = 'bold';
		messageElement.appendChild(usernameElement);
		var timeElement= document.createElement('span');
		var timeText = document.createTextNode(currentDate);
		timeElement.appendChild(timeText);
		timeElement.style['margin-left'] ='25px';
		timeElement.style['font-style'] ='italic';
		messageElement.appendChild(timeElement);
		var textElement = document.createElement('textarea');
		var messageText = document.createTextNode(message);
		var icondelete = document.createElement('i');
		var iconsave = document.createElement('i');
		icondelete.setAttribute("class","fa fa-trash-o");
		icondelete.style['color']='red';
		icondelete.style['left']='unset';
		icondelete.style['cursor']='-webkit-grab';
		icondelete.setAttribute("aria-hidden","true");
		icondelete.setAttribute("onclick","onclickDelelte()");
		
		iconsave.setAttribute("class","fa fa-floppy-o");
		iconsave.style['color']='#4cae4c';
		iconsave.style['left']='unset';
		iconsave.style['cursor']='-webkit-grab';
		iconsave.style['margin-left']='35px';
		iconsave.setAttribute("aria-hidden","true");
		iconsave.setAttribute("onclick","onclickSave()");
		
		textElement.appendChild(messageText);
		textElement.setAttribute("class","form-control");
		textElement.style['display']='inline-block';
		textElement.style['width']='90%';
		messageElement.appendChild(textElement);
		messageElement.appendChild(icondelete);
		messageElement.appendChild(iconsave);
		var messageArea = document.querySelector('.lstMessage');
		messageArea.appendChild(messageElement);
		messageArea.scrollTop = messageArea.scrollHeight;

	}
	
	
	
}

getAvatarColor = function (fullName){
	var lengthFName = fullName.length;
	indexOfColor = lengthFName % 8;
	return colors[indexOfColor];
}

function getActualFullDate() {
	var d = new Date();
	var day = addZero(d.getDate());
	var month = addZero(d.getMonth()+1);
	var year = addZero(d.getFullYear());
	var h = addZero(d.getHours());
	var m = addZero(d.getMinutes());
	var s = addZero(d.getSeconds());
	return day + ". " + month + ". " + year + " (" + h + ":" + m + ":" + s +")";
}

function addZero(i) {
	if (i < 10) {
		i = "0" + i;
	}
	return i;
}

onclickDelelte = function(){
	alert("Bạn có chắc muốn xóa lời thoại này không");
}

onclickSave = function(){
	alert("Bạn có chắc muốn lưu lời thoại này không");
}


$("#message").keyup(function(event){
	if (event.keyCode == 13) {
		sendMessage();
	}
});

addPermissionSelectBox = function(value, name){
	var perOptionElement = document.createElement('option');
	perOptionElement.setAttribute("value",value);
	var perTextNode = document.createTextNode(name);
	perOptionElement.appendChild(perTextNode);
	var lstPer = document.querySelector('#selectreporterpermission');
	lstPer.appendChild(perOptionElement);
}

// load du lieu len popup them nguoi
loadData2Popup = function(){
	for (var i = 0; i < roleRooms.length; i++) {
		addPermissionSelectBox(roleRooms[i].value, roleRooms[i].name);
	}
	$('#selectreporterpermission').trigger("chosen:updated");
}

CLick = function(){
	var selected = $("#selectreporterpermission").chosen().val();
	console.log(selected);
}

var listObject = [];



var lstReporters = [];
var indexOfReporter = 1; // chi so cac reporter da them vao
addReporter = function(){
	var usrID =  $("#selectreporter").chosen().val();
	lstReporters.push({userId: usrID, roles: $("#selectreporterpermission").chosen().val() });
	var idRp = "reporter_" + $("#selectreporter").chosen().val();
	var name = $("#selectreporter option:selected").text()
	$(".lst_reporter").append(
		"<span class='user_span' id='"+idRp +"'>"+name+
		"<a onclick='closeReporter("+usrID+")'><i class='fa fa-times' aria-hidden='true' ></i></a>"+
		"</span>");
	indexOfReporter++;

}

removeReporter = function(userID){
	for(var i=0; i<lstReporters.length; i++){
		var uID = lstReporters[i].userID;
		if(uID == userID){
			lstReporters.splice(i,1);
		}
	}
}

closeReporter = function(id){
	var reporterid = "#reporter_"+id;
	$(reporterid).css("display","none");
	console.log(reporterid);
	removeReporter(id);
}

addListUser = function(){
	var roomID = GetURLParameter("roomID");
	var objRoomReq = {roomID: roomID, members:lstReporters};
	console.log(objRoomReq);
	var objectReq = {roomId: roomID, members:lstReporters};
	$.ajax({
		url:'/api/room/add-members',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				console.log("Success");
				loadData();
			}else {
				console.log("Faild");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});


}
/**
 * Lay tham so truyen tren duong dan
 * @param sParam ten tham so (roomID)
 * @returns gia tri roomID
 */

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

// danh sach cac id cua user bi remove
lstUserIDRemoved= [];

 // remove user khoi room
 removeUserFromRoom = function(userID){
 	var idRemoved = "#userremoved_"+userID;
 	$(idRemoved).css("display","none");
 	lstUserIDRemoved.push(userID);
 }

 addUserRemoved = function(userID, firstname, lastname, username){

 	var idRp = "userremoved_" + userID;
 	var name = firstname +" "+  lastname +" - " + username;

 	$("#lstUserExist").append(
 		"<span class='user_span' id='"+idRp +"'>"+name+
 		"<a onclick='removeUserFromRoom("+userID+")'><i class='fa fa-times' aria-hidden='true' ></i></a>"+
 		"</span>");

 }

 loadData2PopupRemove= function(){
 	var roomID = GetURLParameter("roomID");
 	var url = "/api/room/"+roomID;
 	$.ajax({
 		url:url,
 		type:'get',
 		contentType:'application/json',
 		dataType: 'json',
 		success: function(response){
 			var code = response.code;
 			if(code == 0){
 				console.log("Success");
 				loadData2LstUserRemove(response.data.members);
 			}else {
 				console.log("Faild");
 			}
 		},
 		error: function () {
 			console.log("Server error");
 		}
 	});
 }

 loadData2LstUserRemove = function(lstuser){
 	for(var i = 0; i<lstuser.length; i++){
 		var member = lstuser[i];
 		addUserRemoved(member.userId, member.firstName, member.lastName, member.username);
 	}
 }

 RemoveUsers = function(){
 	var roomID = GetURLParameter("roomID");
 	var dataRq = {roomId:roomID,members:lstUserIDRemoved};
 	$.ajax({
 		url:'/api/room/remove-members',
 		type:'post',
 		contentType:'application/json',
 		dataType: 'json',
 		data: JSON.stringify(dataRq),
 		success: function(response){
 			var code = response.code;
 			if(code == 0){
 				console.log("Success");
 			}else {
 				console.log("Faild");
 			}
 		},
 		error: function () {
 			console.log("Server error");
 		}
 	});
 }

 // Share ma code
 addPermissionShare = function(value, name){
 	var perOptionElement = document.createElement('option');
 	perOptionElement.setAttribute("value",value);
 	var perTextNode = document.createTextNode(name);
 	perOptionElement.appendChild(perTextNode);
 	var lstPer = document.querySelector('#permissionShare');
 	lstPer.appendChild(perOptionElement);
 }

 loadPermission2PopupShareCode = function(){
 	for (var i = 0; i < roleRooms.length; i++) {
 		addPermissionShare(roleRooms[i].value, roleRooms[i].name);
 	}
 	$('#permissionShare').trigger("chosen:updated");
 }

 createCode = function(){
 	var roomID =  GetURLParameter("roomID");
 	var roles = $("#permissionShare").chosen().val();
 	var objectReq = {roomId: roomID, roles: roles};
 	console.log(objectReq);
 	// var rolesText = $("#permissionShare option:selected").text().join(', ');

 	var rolesText = [];
 	$("#permissionShare option:selected").each(function () {
 		rolesText.push(this.text);
 	});
 	rolesText.join(',');
 	$.ajax({
 		url:'/api/room/createCode',
 		type:'post',
 		contentType:'application/json',
 		dataType: 'json',
 		data: JSON.stringify(objectReq),
 		success: function(response){
 			
 			if(code == 0){
 				console.log("Success");
 				var code = response.data.code;
 				addRowShareCode(rolesText,code);
 			}else {
 				console.log("Faild");
 			}
 		},
 		error: function () {
 			console.log("Server error");
 		}
 	});

 }

 addRowShareCode = function(lstRoles, code){
 	$("#tblstCodes").append(
 		"<tr>"+
 		"<td>" + lstRoles +"</td> <td>"+code+"</td>"+
 		"</tr>"
 		);
 }