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