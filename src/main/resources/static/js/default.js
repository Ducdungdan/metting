
var listRoom= [];

var colors = [
'#2196F3', '#32c787', '#00BCD4', '#ff5652',
'#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

$(document).ready(function(){

	showUserFullName();

	$.ajax({
		url:'/api/room/joined',
		type:'get',
		success: function(response){
			var code = response.code;
			if(code == 0){
				listRoom = response.data;
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
	
	loadData();
});

showUserFullName = function(){
	var fullName = getCookiebyName("fullname");
	$("#spFullName").text(fullName);
}

getCookiebyName = function(name){
	var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
	return !!pair ? pair[1] : null;
};


loadData = function(){
	// bind data to listmeetingroom
	for (var i = 0; i < listRoom.length; i++) {
		var id = listRoom[i].id;
		var number = listRoom[i].number;
		var time = listRoom[i].time;
		var name = listRoom[i].name;
		addMettingRoom(id,number,time,name);
	}
}

searchRoom= function(){
	var key = $("#txtSearch").val();
	for (var i = 0; i < listRoom.length; i++) {
		var idMeetingRoomLi = "li_mr_" + listRoom[i].id;
		$("#"+idMeetingRoomLi).css("display","list-item");
		if (listRoom[i].name.includes(key) == false) {
			$("#"+idMeetingRoomLi).css("display","none");
		}
	}
}

addMettingRoom = function(idroom, numberOfUser, time, name){

	var roomElement = document.createElement('li');
	roomElement.setAttribute("onclick","navigateToDetail(" +idroom+")");
	var idMeetingRoomLi = "li_mr_" + idroom;
	roomElement.setAttribute("id",idMeetingRoomLi);
	roomElement.classList.add('meetingroom');
	var avatarElement = document.createElement('i');
	var avatarText = document.createTextNode(numberOfUser);
	avatarElement.appendChild(avatarText);
	avatarElement.style['background-color'] = getAvatarColor(numberOfUser);
	roomElement.appendChild(avatarElement);
	var timeElement = document.createElement('span');
	timeElement.classList.add('style_time');
	var timeText= document.createTextNode(time);
	timeElement.appendChild(timeText);
	roomElement.appendChild(timeElement);
	var meetingNameElement = document.createElement('p');
	var meetingNameText = document.createTextNode(name);
	meetingNameElement.appendChild(meetingNameText);
	roomElement.appendChild(meetingNameElement);
	var lstRoomArea = document.querySelector('#listRoom');
	lstRoomArea.appendChild(roomElement);
}


getAvatarColor = function (numberOfUser){
	indexOfColor = numberOfUser % 8;
	return colors[indexOfColor];
}


closeReporter = function(id){
	var reporterid = "#reporter_"+id;
	$(reporterid).css("display","none");
	console.log(reporterid);
	
}


navigateToDetail = function(idroom){
	
	var url = "/meetingdetail?roomid="+idroom;
	window.location.replace(url);
}

// tao mot cuoc hop moi
addRoom = function(){
	$.ajax({
		url:'/api/room/create',
		type:'post',
		data:{name:$('#txtMeetingName').val(), description:$('#txtMeetingDescription').val(), maxUser:$('#txtMaxUser').val()},
		success: function(response){
			var code = response.code;
			if(code == 1){
				alert("Không được để trống tên, mô tả và số người sử dụng tối đa");
			}else{
				alert("Tạo mới cuộc họp thành công");
			}
			
		},
		error: function () {
			console.log("Server error");
		}
	});
}

joinMeeting = function(){
	var code = $("#txtCode").val();
	var objectReq = {code: code};
	var status = true;
	if(code.trim().length == 0) {
		status = false;
		alert("Bạn chưa nhập mã code");
	}

	if(status){
		$.ajax({
			url:'/api/room/joinByCode',
			type:'post',
			contentType:'application/json',
			dataType: 'json',
			data: JSON.stringify(objectReq),
			success: function(response){

				if(code == 0){
					console.log("Success");
					var roomid = response.data.id;
					var url = "meeting?roomID="+ roomid;
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
}