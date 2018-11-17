$(document).ready(function(){
	showUserFullName();
	$("#users li").click(function(){
		removeActive();
		$(this).addClass("active");
		var lstChildren = $(this).children();
		var speakerID = lstChildren[0].value;
		var fullName = lstChildren[1].value;
		$("#hidSpeakerID").val(speakerID);
		$("#hidFullName").val(fullName);
		$("#message").removeAttr('disabled');
		sendMessage();
	
		
	});
	var roomID = GetURLParameter("roomID");
	getRoomInfor(roomID);
	$("#meeting_name").text(roomInfor.name);
	var meetingStartTime = roomInfor.createdDTG;
	$("#meeting_time").text(meetingStartTime);
	
});

// trang thai cuoc hop dang dien ra hay da ket thuc
var active = 0;

loadData = function (){
	loadData2Popup();
}
removeActive = function(){
	var lstLi = $("#users").children();
	for(var i=0; i< lstLi.length; i++){
		lstLi[i].classList.remove("active");
	}
}

showUserFullName = function(){
	var fullName = getCookiebyName("fullname");
	$("#spFullName").text(fullName);
}

getCookiebyName = function(name){
	var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
	return !!pair ? pair[1] : null;
};

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

addReporterSelectBox = function(userid, firstname, lastname, username){
	var fullname = firstname+" "+lastname +" - " + username;
	var optionElement = document.createElement('option');
	optionElement.setAttribute("value",userid);
	var textNode = document.createTextNode(fullname);
	optionElement.appendChild(textNode);
	var lstReporter = document.querySelector('#selectreporter');
	lstReporter.appendChild(optionElement);
}


// load du lieu len popup them nguoi
loadData2Popup = function(){
	for (var i = 0; i < roleRooms.length; i++) {
		addPermissionSelectBox(roleRooms[i].value, roleRooms[i].name);
	}
	$('#selectreporterpermission').trigger("chosen:updated");
	loadData2SelectReporter();
}

// loaddata vao selectbox danh sach reporter
loadData2SelectReporter = function(){
	var roomID = GetURLParameter("roomID");
	$.ajax({
		url:'/api/room/get-reporters',
		type:'get',
		data:{roomId:roomID},
		success: function(response){
			var code = response.code;
			var token = response.token;
			if(code == 0){
				var lstReporters = response.data;
				for (var i = 0; i < lstReporters.length; i++) {
					var rpt = lstReporters[i];
					addReporterSelectBox(rpt.userId,rpt.firstName, rpt.lastName,rpt.username);
					$('#selectreporter').trigger("chosen:updated");
				}
			}else {
				$("#status_login").text("Tên đăng nhập hoặc mật khẩu không chính xác");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
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
lstSpeaker = [];
var indexOfSpeaker = 0;
addSpeaker = function(){
	var firstName = $("#firstName_Speaker").val();
	var lastName = $("#lastName_Speaker").val();

	lstSpeaker.push({index: indexOfSpeaker, firstName: firstName, lastName: lastName});

	var idSp = "speaker_" + indexOfSpeaker;
	var name = firstName +" " + lastName;
	$(".lstSpeaker").append(
		"<span class='user_span' id='"+idSp +"'>"+name+
		"<a onclick='closeSpeaker("+indexOfSpeaker+")'><i class='fa fa-times' aria-hidden='true' ></i></a>"+
		"</span>");
	indexOfSpeaker++;
	console.log(lstSpeaker);
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

closeSpeaker = function(id){
	var spID = "#speaker_"+id;
	$(spID).css("display","none");
	console.log(spID);
	removeSpeaker(id);
}

removeSpeaker = function(id){
	for(var i=0; i<lstSpeaker.length; i++){
		if(id == lstSpeaker[i].index){
			lstSpeaker.splice(i,1);
		}
	}
}

addListUser = function(){
	var spk=  addListSpeaker();
	if (spk) {
		var rpk= addListReporter();
		if(!rpk){
			alert("Đã xảy ra lỗi trong quá trình thêm reporter");
		}else{
			alert("Đã thêm người thành công");
			var url = "/meeting?roomID="+  GetURLParameter("roomID");
			window.location.replace(url);
		}
	}else{
		alert("Đã xảy ra lỗi trong quá trình thêm speaker");
	}
	

}

addListReporter = function(){
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
				return true;
			}else {
				console.log("Faild");
				return false;
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
}

addListSpeaker = function(){
	var roomId = GetURLParameter("roomID");
	var listSpeakerAdd =[];
	for (var i = 0; i < lstSpeaker.length; i++) {
		var spk = lstSpeaker[i];
		listSpeakerAdd.push({firstName:spk.firstName, lastName: spk.lastName});
	}
	var objectReq = {roomId: roomId, speakers:listSpeakerAdd};
	$.ajax({
		url:'/api/room/add-speakers',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				console.log("Success");
				return true;
			}else {
				console.log("Faild");
				return false;
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
 	if(lstUserIDRemoved.length > 0){
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
 	}else{
 		alert("Chưa chọn người dùng để xóa");
 	}
 	
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

 // lay thong tin cua phong vua tao

 var roomInfor ={};
 getRoomInfor = function(roomID){
 var url = "/api/room/"+roomID;
 	$.ajax({
		url:url,
		type:'get',
		success: function(response){
			var code = response.code;
			var token = response.token;
			if(code == 0){
				roomInfor = response.data;
			}else {
				console.log("Error trong get thong tin room theo roomid");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
 }

 // kết thúc cuộc họp
 finishMeeting = function(){
 	var roomID = GetURLParameter("roomID");
 	var objectReq = {roomId: roomID};
 	$.ajax({
		url:'/api/room/finish-room',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				console.log("Success");
				var url = "/meetingdetail?roomid="+roomID;
				window.location.replace(url);
			}else {
				console.log("Faild");
				
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
 }

 // ------------------------------------------- SEND MESSAGE SOCKET -----------------------------------------------------

var stompClient = null; 

 // connect to socket
 function connect(event) {

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
        event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    var urlSocket = "/topic/"+GetURLParameter("roomID");
    stompClient.subscribe(urlSocket, onMessageReceived);

    // Tell your username to the server
    var authorization = getCookiebyName("authorization");
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({data: {"authorization": authorization}, type: 'JOIN'})
    )
}

function onError(error) {
    alert("Không thể kết nối với server, vui lòng refresh trang để thử lại");
}

function sendMessage(event) {
    var speakerID = $("hidSpeakerID").val();
    var roomID = GetURLParameter("roomID");
    var content = $("#message").val().trim();
    if(messageContent && stompClient) {
        var chatMessage = {
        		data: {
        			roomId: roomID,
        			speakerId: speakerID,
        			content: content,
        			startTime: new Date().getTime(),
        			endTime: new Date().getTime() + 3600
        		},
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
	console.log(payload);
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else if (message.type === 'CHAT') {
    	messageElement.classList.add('chat-message');

    	var reporter = message.data.reporter;
    	var speaker = message.data.speaker
    	
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(reporter.lastName[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(reporter.firstName + " " + reporter.lastName);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(reporter.firstName + " " + reporter.lastName);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    } 

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.data.startTime + " " + message.data.endTime + " " + message.data.speaker.firstName + message.data.speaker.lastName + ": " + message.data.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

