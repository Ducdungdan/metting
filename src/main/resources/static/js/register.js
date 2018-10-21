$(document).ready(function(){
$("#submit_register").submit(function(){
		return validate();
	});

// validate firstName
$('#ip_firstname').blur(function(){
	$('#status_firstname').text("");
	 validate_FirstName();
});

//validate lastName
$('#ip_lastname').blur(function(){
	$('#status_lastname').text("");
	 validate_LastName();
});

$('#ip_phone').blur(function(){
	$('#status_phone').text("");
	 validate_Phone();
});


$('#ip_address').blur(function(){
	$('#status_address').text("");
	 validate_Address();
});

$('#ip_username').blur(function(){
	$('#status_username').text("");
	checkTaiKhoanExist();
	 validate_Username();
});

$('#ip_password').blur(function(){
	$('#status_password').text("");
	 validate_Password();
});

$('#ip_repassword').blur(function(){
	$('#status_repassword').text("");
	 validate_Repassword();
});



});

// validate firstName

validate_FirstName = function() {
	var status = true;
	var firstname = $('#ip_firstname').val();
	if(firstname.trim().length == 0){
		$('#status_firstname').text('[Họ và tên đệm] không được bỏ trống');
		status = false;
	}
	return status;
}

//validate last name
validate_LastName = function (){
	var status = true;
	var lastname = $('#ip_lastname').val();
	if(lastname.trim().length == 0){
		$('#status_lastname').text('[Tên] không được bỏ trống');
		status = false;
	}
	return status;
}


// validate so dien thoai
validate_Phone = function(){
	var status = true;
	var phone = $('#ip_phone').val();
	if(phone.trim().length == 0){
		$('#status_phone').text('[Số điện thoại] không được bỏ trống');
		status = false;
	}else{
		if(isNaN(sdt)){
		$('#status_phone').text('Số điện thoại không không hợp lệ, hãy kiểm tra lại');
		status = false;
		}
	}
	return status;
}



//validate dia chi
validate_Address = function(){
	var status = true;
	var address = $('#ip_address').val();
	if(address.trim().length == 0){
		$('#status_address').text("[Địa chỉ] không được bỏ trống");
		status = false;
	}
	return status;
}

//validate ten dang nhap
validate_Username = function(){
	
	var status = true;
	var username = $('#ip_username').val();
	if(username.trim().length == 0){
		$('#status_username').text("[Tên đăng nhập] không được bỏ trống");
		status = false;
	}else if(username.trim().length < 4){
		$('#status_username').text("[Tên đăng nhập] phải ít nhất 4 kí tự");
		status = false;
	}else{
		if($('#statusUsername').val() == 1){
			
			status = false;
		}else{
			status = true;
		}
	}
	return status;
}

//validate mat khau
validate_Password = function(){
	var status = true;
	var matkhau = $('#ip_password').val();
	if(matkhau.trim().length == 0){
		$('#status_password').text("[Mật khẩu] không được bỏ trống");
		status = false;
	}else if(matkhau.trim().length <8){
		$('#status_password').text("[Mật khẩu] không được bé hơn 8 kí tự");
		status = false;
	}
	return status;
}

//validate nhap lai mat khau
validate_Repassword = function(){
	var status = true;
	var matkhaunhaplai = $('#ip_repassword').val();
	if(matkhaunhaplai.trim().length == 0){
		$('#status_repassword').text("Bạn chưa nhập lại mật khẩu");
		status = false;
	}else if(matkhaunhaplai != $('#ip_password').val()){
		$('#status_repassword').text("[Mật khẩu nhập lại] không chính xác");
		status = false;
	}

	return status;
}

validate = function(){
	$("small").text("");
	var status1 = validate_FirstName();
	
	var status2 = validate_LastName();
	
	var status3 = validate_Phone();
	
	var status4 = validate_Address();
	
	var status5 = validate_Username();
	
	var status6 = validate_Password();
	
	var status7 = validate_Repassword();
		
	return(status1 && status2 && status3 && status4 && status5 && status6 && status7);
	
}

checkTaiKhoanExist = function (){
	$.ajax({
		url:'findusername',
		type:'get',
		data:{taikhoan:$('#ip_username').val()},
		success: function(response){
			var status = response.status;
			$('#statusUsername').val(status);
			if(status == 1){
				$('#status_username').text("Tên đăng nhập này đã tồn tại trong hệ thống, vui lòng chọn tên đăng nhập khác");
			}
		}
	});
}



