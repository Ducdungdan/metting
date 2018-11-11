
var listRoom;

var colors = [
'#2196F3', '#32c787', '#00BCD4', '#ff5652',
'#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

$(document).ready(function(){

	$.ajax({
		url:'/api/room/joined',
		type:'get',
		success: function(response){
			var code = response.code;
			if(code == 1){
				listRoom = response.data;
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

addRoomJoin = function(){
	addMettingRoom(1,10,"10:12:20","Cuộc họp thành viên hội đồng quản trị");
	addMettingRoom(2,12,"10:12:20","Cuộc họp thành viên hội đồng quản trị");
}

navigateToDetail = function(idroom){
	
	var url = "/meetingdetail?roomid="+idroom;
	window.location.replace(url);
}
